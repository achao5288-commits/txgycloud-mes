package cn.iocoder.txgy.module.mes.service.set.hazwaste;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.json.JsonUtils;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.infra.api.file.FileApi;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazwasteManifestDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste.MesSetHazardousWasteMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste.MesSetHazwasteManifestMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.signrecord.MesSetSignRecordMapper;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 安全环保检测-危废链 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
@Slf4j
public class MesSetHazwasteServiceImpl implements MesSetHazwasteService {

    /**
     * 常见 HW 类别的危险特性（HJ1276 标签用）。只覆盖设计文档锚定的天信真实清单，
     * 未收录的代码一律留空 —— 危险特性写错比留空更危险，宁可不填等人工补。
     */
    private static final Map<String, String> HW_HAZARD_TRAITS = Map.of(
            "HW06", "毒性/易燃性",
            "HW08", "毒性/易燃性",
            "HW12", "毒性",
            "HW49", "毒性");

    @Resource
    private MesSetHazardousWasteMapper hazardousWasteMapper;

    @Resource
    private MesSetHazwasteManifestMapper manifestMapper;

    @Resource
    private MesSetSignRecordMapper signRecordMapper;

    @Resource
    private MesSetSignRecordService signRecordService;

    @Resource
    private FileApi fileApi;

    // ==================== 危废台账 ====================

    @Override
    public Long createHazardousWaste(MesSetHazardousWasteSaveReqVO createReqVO) {
        // manifest_no 是全库唯一键(不带租户)，撞了就直接抛，不给 500
        if (hazardousWasteMapper.selectByManifestNo(createReqVO.getManifestNo()) != null) {
            throw exception(SET_HAZWASTE_MANIFEST_NO_DUPLICATE);
        }
        MesSetHazardousWasteDO obj = BeanUtils.toBean(createReqVO, MesSetHazardousWasteDO.class);
        obj.setStage(STAGES[0]);
        obj.setStatus("DRAFT");
        hazardousWasteMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateHazardousWaste(MesSetHazardousWasteSaveReqVO updateReqVO) {
        MesSetHazardousWasteDO exist = validateHazardousWasteExists(updateReqVO.getId());
        MesSetHazardousWasteDO dup = hazardousWasteMapper.selectByManifestNo(updateReqVO.getManifestNo());
        if (dup != null && !dup.getId().equals(exist.getId())) {
            throw exception(SET_HAZWASTE_MANIFEST_NO_DUPLICATE);
        }
        // 环节只能由 advanceStage 推进，改单不许改 stage
        MesSetHazardousWasteDO updateObj = BeanUtils.toBean(updateReqVO, MesSetHazardousWasteDO.class);
        updateObj.setStage(null);
        hazardousWasteMapper.updateById(updateObj);
    }

    @Override
    public void deleteHazardousWaste(Long id) {
        validateHazardousWasteExists(id);
        hazardousWasteMapper.deleteById(id);
    }

    @Override
    public MesSetHazardousWasteDO getHazardousWaste(Long id) {
        return validateHazardousWasteExists(id);
    }

    @Override
    public PageResult<MesSetHazardousWasteDO> getHazardousWastePage(MesSetHazardousWastePageReqVO pageReqVO) {
        return hazardousWasteMapper.selectPage(pageReqVO);
    }

    @Override
    public void advanceStage(Long id, String stage, String handler) {
        MesSetHazardousWasteDO waste = validateHazardousWasteExists(id);
        int from = stageIndex(waste.getStage());
        int to = stageIndex(stage);
        if (to < 0) {
            throw exception(SET_HAZWASTE_STAGE_INVALID);
        }
        if (to <= from) {
            // 危废台账是负法律责任的流水，环节不能回退、也不能原地重复推进
            throw exception(SET_HAZWASTE_STAGE_TRANSITION_INVALID);
        }
        MesSetHazardousWasteDO updateObj = new MesSetHazardousWasteDO();
        updateObj.setId(id);
        updateObj.setStage(stage);
        updateObj.setHandleTime(LocalDateTime.now());
        updateObj.setHandler(StrUtil.blankToDefault(handler, currentUserName()));
        if ("TRANSFERRED".equals(stage)) {
            updateObj.setStatus("APPROVED");
        }
        hazardousWasteMapper.updateById(updateObj);
    }

    // ==================== 危废转移联单 ====================

    @Override
    public Long createManifest(MesSetHazwasteManifestSaveReqVO createReqVO) {
        if (manifestMapper.selectByManifestNo(createReqVO.getManifestNo()) != null) {
            throw exception(SET_HAZWASTE_MANIFEST_NO_DUPLICATE);
        }
        MesSetHazwasteManifestDO obj = BeanUtils.toBean(createReqVO, MesSetHazwasteManifestDO.class);
        obj.setStatus(MANIFEST_STATUSES[0]);
        manifestMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateManifest(MesSetHazwasteManifestSaveReqVO updateReqVO) {
        MesSetHazwasteManifestDO exist = validateManifestExists(updateReqVO.getId());
        MesSetHazwasteManifestDO dup = manifestMapper.selectByManifestNo(updateReqVO.getManifestNo());
        if (dup != null && !dup.getId().equals(exist.getId())) {
            throw exception(SET_HAZWASTE_MANIFEST_NO_DUPLICATE);
        }
        // 五方确认时间/状态由 declare/confirm/close/gate 推进，改单不许覆盖
        MesSetHazwasteManifestDO updateObj = BeanUtils.toBean(updateReqVO, MesSetHazwasteManifestDO.class);
        updateObj.setStatus(null);
        manifestMapper.updateById(updateObj);
    }

    @Override
    public void deleteManifest(Long id) {
        validateManifestExists(id);
        manifestMapper.deleteById(id);
    }

    @Override
    public MesSetHazwasteManifestDO getManifest(Long id) {
        return validateManifestExists(id);
    }

    @Override
    public MesSetHazwasteManifestDO getManifestByNo(String manifestNo) {
        return manifestMapper.selectByManifestNo(manifestNo);
    }

    @Override
    public PageResult<MesSetHazwasteManifestDO> getManifestPage(MesSetHazwasteManifestPageReqVO pageReqVO) {
        return manifestMapper.selectPage(pageReqVO);
    }

    @Override
    public void declareManifest(String manifestNo) {
        MesSetHazwasteManifestDO manifest = validateManifestByNoExists(manifestNo);
        ensureStatusAdvance(manifest.getStatus(), "DECLARED");
        MesSetHazwasteManifestDO updateObj = new MesSetHazwasteManifestDO();
        updateObj.setId(manifest.getId());
        updateObj.setStatus("DECLARED");
        updateObj.setDeclaredTime(LocalDateTime.now());
        manifestMapper.updateById(updateObj);
    }

    @Override
    public void confirmParty(String manifestNo, String partyRole) {
        MesSetHazwasteManifestDO manifest = validateManifestByNoExists(manifestNo);
        if (!Arrays.asList(PARTY_ROLES).contains(partyRole)) {
            throw exception(SET_HAZWASTE_PARTY_ROLE_INVALID);
        }
        MesSetHazwasteManifestDO updateObj = new MesSetHazwasteManifestDO();
        updateObj.setId(manifest.getId());
        LocalDateTime now = LocalDateTime.now();
        switch (partyRole) {
            case "CARRIER" -> updateObj.setCarrierConfirmTime(now);
            case "RECEIVER_PARTY" -> updateObj.setReceiveConfirmTime(now);
            case "STORAGE" -> updateObj.setStorageConfirmTime(now);
            case "DISPOSER" -> updateObj.setDisposeConfirmTime(now);
            default -> {
                // GENERATOR 的确认就是申报本身，走 declareManifest
                throw exception(SET_HAZWASTE_PARTY_ROLE_INVALID);
            }
        }
        // 生效判据：产生方已申报，且运输方与接收方都已确认（对齐国家固废系统"三方确认后联单生效"）
        LocalDateTime carrier = "CARRIER".equals(partyRole) ? now : manifest.getCarrierConfirmTime();
        LocalDateTime receive = "RECEIVER_PARTY".equals(partyRole) ? now : manifest.getReceiveConfirmTime();
        if (manifest.getDeclaredTime() != null && carrier != null && receive != null
                && stageIndexOfManifest(manifest.getStatus()) < stageIndexOfManifest("EFFECTIVE")) {
            updateObj.setStatus("EFFECTIVE");
        }
        manifestMapper.updateById(updateObj);
    }

    @Override
    public void closeManifest(String manifestNo) {
        MesSetHazwasteManifestDO manifest = validateManifestByNoExists(manifestNo);
        // 归档不是"往前挪一格"就算：没真出过厂就没有回执可归档，只认已出厂
        if (!"TRANSFERRED".equals(manifest.getStatus())) {
            throw exception(SET_HAZWASTE_MANIFEST_STATUS_TRANSITION_INVALID);
        }
        MesSetHazwasteManifestDO updateObj = new MesSetHazwasteManifestDO();
        updateObj.setId(manifest.getId());
        updateObj.setStatus("CLOSED");
        manifestMapper.updateById(updateObj);
    }

    @Override
    public void signManifest(String manifestNo, String signRole, String opinion, String signImg) {
        validateManifestByNoExists(manifestNo);
        if (!Arrays.asList(SIGN_ROLES).contains(signRole)) {
            throw exception(SET_HAZWASTE_SIGN_ROLE_INVALID);
        }
        signRecordService.createSignRecord(MesSetSignRecordDO.builder()
                .bizType(SIGN_BIZ_TYPE)
                .bizNo(manifestNo)
                .signRole(signRole)
                .signUser(currentUserName())
                .signUserId(getLoginUserId())
                .signTime(LocalDateTime.now())
                .opinion(opinion)
                .signImg(signImg)
                .build());
    }

    @Override
    public List<MesSetHazwasteManifestDO> listDueSoon(int days) {
        return manifestMapper.selectDueSoon(LocalDateTime.now().plusDays(days));
    }

    // ==================== 门卫硬放行 ====================

    @Override
    public MesSetHazwasteGateCheckRespVO checkGate(MesSetHazwasteGateCheckReqVO reqVO) {
        MesSetHazwasteManifestDO manifest = manifestMapper.selectByManifestNo(reqVO.getManifestNo());
        if (manifest == null) {
            // 放行前必须能出示生效联单——查不到就是最严重的拦截，连车牌都不用比
            return MesSetHazwasteGateCheckRespVO.builder()
                    .passed(false).manifestNo(reqVO.getManifestNo()).vehicleMatched(false)
                    .missingSignRoles(new ArrayList<>())
                    .reasons(List.of("联单号不存在，无有效转移联单不得出厂"))
                    .build();
        }
        boolean effective = stageIndexOfManifest(manifest.getStatus()) >= stageIndexOfManifest("EFFECTIVE");
        boolean vehicleMatched = StrUtil.isBlank(reqVO.getVehicleNo()) || StrUtil.isBlank(manifest.getVehicleNo())
                || reqVO.getVehicleNo().equals(manifest.getVehicleNo());

        Set<String> signed = signRecordMapper.selectListByBiz(SIGN_BIZ_TYPE, reqVO.getManifestNo()).stream()
                .map(MesSetSignRecordDO::getSignRole).collect(Collectors.toSet());
        List<String> missing = Arrays.stream(REQUIRED_SIGN_ROLES)
                .filter(r -> !signed.contains(r)).collect(Collectors.toList());

        List<String> reasons = new ArrayList<>();
        if (!effective) {
            reasons.add("联单尚未生效（须已申报且运输方、接收方均已确认）");
        }
        if (!vehicleMatched) {
            reasons.add("车牌号与联单登记不一致（登记：" + manifest.getVehicleNo() + "，实扫：" + reqVO.getVehicleNo() + "）");
        }
        if (!missing.isEmpty()) {
            reasons.add("四方签字不齐，缺：" + String.join("/", missing));
        }

        return MesSetHazwasteGateCheckRespVO.builder()
                .passed(reasons.isEmpty())
                .manifestNo(manifest.getManifestNo())
                .wasteName(manifest.getWasteName())
                .status(manifest.getStatus())
                .effective(effective)
                .vehicleMatched(vehicleMatched)
                .missingSignRoles(missing)
                .reasons(reasons)
                .gateReleaseTime(manifest.getGateReleaseTime())
                .gateGuard(manifest.getGateGuard())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesSetHazwasteGateCheckRespVO releaseGate(MesSetHazwasteGateCheckReqVO reqVO) {
        MesSetHazwasteManifestDO manifest = manifestMapper.selectByManifestNo(reqVO.getManifestNo());
        if (manifest == null) {
            throw exception(SET_HAZWASTE_GATE_MANIFEST_MISSING);
        }
        MesSetHazwasteGateCheckRespVO check = checkGate(reqVO);
        if (!check.getPassed()) {
            // 逐条给出具体拦截码：门卫看到的必须是"为什么不让走"，不是一句笼统的失败
            if (!check.getEffective()) {
                throw exception(SET_HAZWASTE_GATE_NOT_EFFECTIVE);
            }
            if (!check.getVehicleMatched()) {
                throw exception(SET_HAZWASTE_GATE_VEHICLE_MISMATCH);
            }
            throw exception(SET_HAZWASTE_GATE_SIGN_INCOMPLETE);
        }

        String guard = currentUserName();
        // 已出厂的单再扫一次不该改史：复扫只回放原放行信息
        if (stageIndexOfManifest(manifest.getStatus()) >= stageIndexOfManifest("TRANSFERRED")) {
            return check;
        }

        signRecordService.createSignRecord(MesSetSignRecordDO.builder()
                .bizType(SIGN_BIZ_TYPE).bizNo(manifest.getManifestNo()).signRole("GUARD")
                .signUser(guard).signUserId(getLoginUserId())
                .signTime(LocalDateTime.now()).opinion("门卫核验放行")
                .signImg(reqVO.getSignImg()).build());

        LocalDateTime now = LocalDateTime.now();
        MesSetHazwasteManifestDO updateObj = new MesSetHazwasteManifestDO();
        updateObj.setId(manifest.getId());
        updateObj.setStatus("TRANSFERRED");
        updateObj.setTransferTime(now);
        updateObj.setGateReleaseTime(now);
        updateObj.setGateGuard(guard);
        manifestMapper.updateById(updateObj);

        // 同单号的台账同步推到"转移"：联单生效放行 = 台账上的危废确实出厂了
        MesSetHazardousWasteDO waste = hazardousWasteMapper.selectByManifestNo(manifest.getManifestNo());
        if (waste != null && stageIndex(waste.getStage()) < stageIndex("TRANSFERRED")) {
            advanceStage(waste.getId(), "TRANSFERRED", guard);
        }

        check.setStatus("TRANSFERRED");
        check.setGateReleaseTime(now);
        check.setGateGuard(guard);
        return check;
    }

    // ==================== HJ1276 标签 ====================

    @Override
    public MesSetHazwasteLabelRespVO getLabel(Long id) {
        MesSetHazardousWasteDO waste = validateHazardousWasteExists(id);
        return buildLabel(waste);
    }

    @Override
    public String archiveLabel(Long id) {
        MesSetHazardousWasteDO waste = validateHazardousWasteExists(id);
        String json = JsonUtils.toJsonString(buildLabel(waste));
        String url;
        try {
            url = fileApi.createFile(json.getBytes(StandardCharsets.UTF_8),
                    "HJ1276-" + StrUtil.blankToDefault(waste.getContainerCode(), waste.getManifestNo()) + ".json",
                    "mes/hazwaste-label", "application/json");
        } catch (Exception e) {
            log.error("[archiveLabel][台账({}) 标签归档失败]", id, e);
            throw exception(SET_HAZWASTE_LABEL_ARCHIVE_FAILED);
        }
        if (StrUtil.isBlank(url)) {
            throw exception(SET_HAZWASTE_LABEL_ARCHIVE_FAILED);
        }
        MesSetHazardousWasteDO updateObj = new MesSetHazardousWasteDO();
        updateObj.setId(id);
        updateObj.setLabelUrl(url);
        hazardousWasteMapper.updateById(updateObj);
        return url;
    }

    private MesSetHazwasteLabelRespVO buildLabel(MesSetHazardousWasteDO waste) {
        // 二维码内容＝容器码（一桶一码），没登记容器码时退回联单号，保证扫得出唯一对象
        String qr = StrUtil.blankToDefault(waste.getContainerCode(), waste.getManifestNo());
        return MesSetHazwasteLabelRespVO.builder()
                .title("危险废物")
                .wasteName(waste.getWasteName())
                .wasteCode(waste.getWasteCode())
                .hazardTraits(HW_HAZARD_TRAITS.get(waste.getWasteCode()))
                .generateUnit(waste.getCounterparty())
                .containerCode(waste.getContainerCode())
                .quantity(waste.getQuantity())
                .quantityUnit(waste.getQuantityUnit())
                .labelDate(LocalDate.now().toString())
                .qrContent(qr)
                .labelUrl(waste.getLabelUrl())
                .build();
    }

    // ==================== 校验与工具 ====================

    private MesSetHazardousWasteDO validateHazardousWasteExists(Long id) {
        MesSetHazardousWasteDO obj = id == null ? null : hazardousWasteMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_HAZWASTE_NOT_EXISTS);
        }
        return obj;
    }

    private MesSetHazwasteManifestDO validateManifestExists(Long id) {
        MesSetHazwasteManifestDO obj = id == null ? null : manifestMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_HAZWASTE_MANIFEST_NOT_EXISTS);
        }
        return obj;
    }

    private MesSetHazwasteManifestDO validateManifestByNoExists(String manifestNo) {
        MesSetHazwasteManifestDO obj = StrUtil.isBlank(manifestNo) ? null
                : manifestMapper.selectByManifestNo(manifestNo);
        if (obj == null) {
            throw exception(SET_HAZWASTE_MANIFEST_NOT_EXISTS);
        }
        return obj;
    }

    private int stageIndex(String stage) {
        return stage == null ? -1 : Arrays.asList(STAGES).indexOf(stage);
    }

    private int stageIndexOfManifest(String status) {
        return status == null ? -1 : Arrays.asList(MANIFEST_STATUSES).indexOf(status);
    }

    /**
     * 联单状态只进不退。目标状态非法 → 005；回退或原地重复 → 006。
     */
    private void ensureStatusAdvance(String current, String target) {
        int to = stageIndexOfManifest(target);
        if (to < 0) {
            throw exception(SET_HAZWASTE_MANIFEST_STATUS_INVALID);
        }
        if (to <= stageIndexOfManifest(current)) {
            throw exception(SET_HAZWASTE_MANIFEST_STATUS_TRANSITION_INVALID);
        }
    }

    private String currentUserName() {
        String nickname = getLoginUserNickname();
        if (nickname == null || nickname.isEmpty()) {
            return String.valueOf(getLoginUserId());
        }
        return nickname;
    }

}
