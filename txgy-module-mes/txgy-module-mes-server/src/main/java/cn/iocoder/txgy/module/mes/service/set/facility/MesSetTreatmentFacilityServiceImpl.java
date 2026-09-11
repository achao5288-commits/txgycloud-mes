package cn.iocoder.txgy.module.mes.service.set.facility;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo.*;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.MesSetHazardousWasteSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.facility.MesSetTreatmentFacilityMapper;
import cn.iocoder.txgy.module.mes.service.set.hazwaste.MesSetHazwasteService;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 安全环保检测-治污设施运行管控 Service 实现（设计文档 §6.3）
 *
 * @author OPENLAB BS
 */
@Service
@Slf4j
public class MesSetTreatmentFacilityServiceImpl implements MesSetTreatmentFacilityService {

    private static final DateTimeFormatter NO_TIME_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private MesSetTreatmentFacilityMapper facilityMapper;

    /**
     * 换炭要落成危废台账，复用危废链的 Service —— 不自己拼一条 DO 插进去。
     * 这样危废那边的编号查重、默认环节/状态，全都跟着它那套口径走。
     */
    @Resource
    private MesSetHazwasteService hazwasteService;

    @Resource
    private MesSetSignRecordService signRecordService;

    // ==================== 台账 ====================

    @Override
    public Long createFacility(MesSetTreatmentFacilitySaveReqVO createReqVO) {
        if (facilityMapper.selectByFacilityNo(createReqVO.getFacilityNo()) != null) {
            throw exception(SET_FACILITY_NO_DUPLICATE);
        }
        validateType(createReqVO.getFacilityType());
        MesSetTreatmentFacilityDO obj = BeanUtils.toBean(createReqVO, MesSetTreatmentFacilityDO.class);
        // 新建设施就是"在运行"，要停运得走申报审批 —— 不给"建的时候就建成停运"的后门
        obj.setRunStatus(RUN_STATUSES[0]);
        obj.setShutdownStatus(SHUTDOWN_STATUSES[0]);
        obj.setNextReplaceDate(calcNextReplace(createReqVO.getLastReplaceDate(),
                createReqVO.getReplaceCycleDays()));
        if (obj.getStatus() == null) {
            obj.setStatus("ENABLED");
        }
        facilityMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateFacility(MesSetTreatmentFacilitySaveReqVO updateReqVO) {
        MesSetTreatmentFacilityDO exist = validateFacilityExists(updateReqVO.getId());
        MesSetTreatmentFacilityDO dup = facilityMapper.selectByFacilityNo(updateReqVO.getFacilityNo());
        if (dup != null && !dup.getId().equals(exist.getId())) {
            throw exception(SET_FACILITY_NO_DUPLICATE);
        }
        validateType(updateReqVO.getFacilityType());
        MesSetTreatmentFacilityDO updateObj = BeanUtils.toBean(updateReqVO, MesSetTreatmentFacilityDO.class);
        // 运行状态与停运申报不是"档案属性"：只能走 申报→审批 / 复运 这两个动作改。
        // 否则改一次档案就等于绕过了审批，停运审批也就白设了。
        updateObj.setRunStatus(null);
        updateObj.setShutdownStatus(null);
        updateObj.setShutdownDeclaredAt(null);
        updateObj.setShutdownApprover(null);
        updateObj.setShutdownApprovedAt(null);
        updateObj.setNextReplaceDate(calcNextReplace(updateReqVO.getLastReplaceDate(),
                updateReqVO.getReplaceCycleDays()));
        facilityMapper.updateById(updateObj);
    }

    @Override
    public void deleteFacility(Long id) {
        validateFacilityExists(id);
        facilityMapper.deleteById(id);
    }

    @Override
    public MesSetTreatmentFacilityDO getFacility(Long id) {
        return validateFacilityExists(id);
    }

    @Override
    public PageResult<MesSetTreatmentFacilityDO> getFacilityPage(MesSetTreatmentFacilityPageReqVO pageReqVO) {
        return facilityMapper.selectPage(pageReqVO);
    }

    // ==================== 换炭 → HW49 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesSetFacilityReplaceRespVO replaceConsumable(MesSetFacilityReplaceReqVO reqVO) {
        MesSetTreatmentFacilityDO facility = validateFacilityExists(reqVO.getId());
        if (reqVO.getQuantity() == null || reqVO.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            // 设计文档 §6.3 的原话是"杜绝换炭不记危废"：不填量就没法登记危废，
            // 与其放行一个 0 让危废台账多一条空行，不如直接拒。
            throw exception(SET_FACILITY_CONSUMABLE_QUANTITY_INVALID);
        }
        LocalDate replaceDate = reqVO.getReplaceDate() == null ? LocalDate.now() : reqVO.getReplaceDate();
        String handler = StrUtil.blankToDefault(reqVO.getHandler(), currentUserName());

        // ① 换下即登记废活性炭 HW49 入桶 —— 与本次换炭同一个事务，要么都成要么都不成
        MesSetHazardousWasteSaveReqVO waste = new MesSetHazardousWasteSaveReqVO();
        waste.setManifestNo(buildWasteManifestNo(facility));
        waste.setWasteCode(CONSUMABLE_WASTE_CODE);
        waste.setWasteName(CONSUMABLE_WASTE_NAME);
        waste.setQuantity(reqVO.getQuantity());
        waste.setQuantityUnit("kg");
        waste.setStorageLocation(reqVO.getStorageLocation());
        waste.setContainerCode(reqVO.getContainerCode());
        waste.setCounterparty(facility.getFacilityName());
        waste.setHandleTime(replaceDate.atStartOfDay());
        waste.setHandler(handler);
        waste.setRemark(String.format("治污设施 %s(%s) 换炭自动登记：耗材→危废转化（设计文档 §6.3）",
                facility.getFacilityName(), facility.getFacilityNo()));
        Long wasteId = hazwasteService.createHazardousWaste(waste);

        // ② 顺延下次更换日期
        MesSetTreatmentFacilityDO updateObj = new MesSetTreatmentFacilityDO();
        updateObj.setId(facility.getId());
        updateObj.setLastReplaceDate(replaceDate);
        LocalDate next = calcNextReplace(replaceDate, facility.getReplaceCycleDays());
        updateObj.setNextReplaceDate(next);
        facilityMapper.updateById(updateObj);

        MesSetFacilityReplaceRespVO resp = new MesSetFacilityReplaceRespVO();
        resp.setFacilityId(facility.getId());
        resp.setFacilityName(facility.getFacilityName());
        resp.setWasteLedgerId(wasteId);
        resp.setWasteManifestNo(waste.getManifestNo());
        resp.setWasteCode(CONSUMABLE_WASTE_CODE);
        resp.setQuantity(reqVO.getQuantity());
        resp.setReplaceDate(replaceDate);
        resp.setNextReplaceDate(next);
        resp.setMessage(StrUtil.format("已换炭 {} kg，同时登记废活性炭 {} 入桶（台账 #{}）",
                reqVO.getQuantity().stripTrailingZeros().toPlainString(),
                CONSUMABLE_WASTE_CODE, wasteId));
        return resp;
    }

    @Override
    public List<MesSetTreatmentFacilityDO> listDueReplace(Integer days) {
        int warnDays = days == null || days <= 0 ? 30 : days;
        return facilityMapper.selectDueReplace(LocalDate.now().plusDays(warnDays));
    }

    // ==================== 停运申报 + 审批 ====================

    @Override
    public MesSetTreatmentFacilityDO declareShutdown(MesSetFacilityShutdownDeclareReqVO reqVO) {
        MesSetTreatmentFacilityDO facility = validateFacilityExists(reqVO.getId());
        if ("PENDING".equals(facility.getShutdownStatus())) {
            throw exception(SET_FACILITY_SHUTDOWN_ALREADY_PENDING);
        }
        MesSetTreatmentFacilityDO updateObj = new MesSetTreatmentFacilityDO();
        updateObj.setId(facility.getId());
        updateObj.setShutdownStatus("PENDING");
        updateObj.setShutdownReason(reqVO.getShutdownReason());
        updateObj.setShutdownPlanStart(reqVO.getShutdownPlanStart());
        updateObj.setShutdownPlanEnd(reqVO.getShutdownPlanEnd());
        updateObj.setShutdownDeclaredAt(LocalDateTime.now());
        // 上一次审批的人/时间清掉，免得跟本次申报混在一起看不出谁审的
        updateObj.setShutdownApprover("");
        updateObj.setShutdownApprovedAt(null);
        facilityMapper.updateById(updateObj);
        return facilityMapper.selectById(facility.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesSetTreatmentFacilityDO approveShutdown(MesSetFacilityShutdownApproveReqVO reqVO) {
        MesSetTreatmentFacilityDO facility = validateFacilityExists(reqVO.getId());
        if (!"PENDING".equals(facility.getShutdownStatus())) {
            throw exception(SET_FACILITY_SHUTDOWN_NOT_PENDING);
        }
        boolean approved = Boolean.TRUE.equals(reqVO.getApproved());
        String approver = currentUserName();

        MesSetTreatmentFacilityDO updateObj = new MesSetTreatmentFacilityDO();
        updateObj.setId(facility.getId());
        updateObj.setShutdownStatus(approved ? "APPROVED" : "REJECTED");
        updateObj.setShutdownApprover(approver);
        updateObj.setShutdownApprovedAt(LocalDateTime.now());
        // 只有批准才真的停 —— 驳回的设施还在转，run_status 不动
        updateObj.setRunStatus(approved ? "STOPPED" : "RUNNING");
        facilityMapper.updateById(updateObj);

        // 审批是合规动作，留痕（复用签字表，白拿一份历史）
        signRecordService.createSignRecord(MesSetSignRecordDO.builder()
                .bizType(SHUTDOWN_SIGN_BIZ_TYPE)
                .bizNo(facility.getFacilityNo())
                .signRole(approved ? "APPROVE" : "REJECT")
                .signUser(approver)
                .signUserId(getLoginUserId())
                .signTime(LocalDateTime.now())
                .opinion(reqVO.getOpinion())
                .signImg(reqVO.getSignImg())
                .build());
        return facilityMapper.selectById(facility.getId());
    }

    @Override
    public MesSetTreatmentFacilityDO resumeFacility(Long id) {
        MesSetTreatmentFacilityDO facility = validateFacilityExists(id);
        MesSetTreatmentFacilityDO updateObj = new MesSetTreatmentFacilityDO();
        updateObj.setId(facility.getId());
        updateObj.setRunStatus("RUNNING");
        // 复运是往安全方向走，不需要审批；顺手把本次停运申报归档掉，
        // 否则下次再要停运时 PENDING 判定会拿着上一轮的历史挡路
        updateObj.setShutdownStatus("NONE");
        facilityMapper.updateById(updateObj);
        return facilityMapper.selectById(facility.getId());
    }

    @Override
    public List<MesSetTreatmentFacilityDO> listStoppedWithoutApproval() {
        return facilityMapper.selectStoppedWithoutApproval();
    }

    // ==================== 同开同停 ====================

    @Override
    public MesSetFacilityCoRunRespVO checkCoRun(MesSetFacilityCoRunReqVO reqVO) {
        MesSetTreatmentFacilityDO facility = validateFacilityExists(reqVO.getId());
        boolean productionRunning = Boolean.TRUE.equals(reqVO.getProductionRunning());
        boolean facilityStopped = "STOPPED".equals(facility.getRunStatus());

        List<String> reasons = new ArrayList<>();
        if (productionRunning && facilityStopped) {
            reasons.add(StrUtil.format(
                    "同开同停：产线「{}」运行中，配套治污设施 {}({}) 处于停运，产能不能开",
                    StrUtil.blankToDefault(facility.getLineCode(), "(未填)"),
                    facility.getFacilityName(), facility.getFacilityNo()));
            // 停运有没有走过审批，是两种不同性质的违规，分开说
            if (!"APPROVED".equals(facility.getShutdownStatus())) {
                reasons.add(StrUtil.format("未批先停：该设施停运申报状态为 {}（须已批准）",
                        facility.getShutdownStatus()));
            }
        }

        MesSetFacilityCoRunRespVO resp = new MesSetFacilityCoRunRespVO();
        resp.setPassed(reasons.isEmpty());
        resp.setFacilityId(facility.getId());
        resp.setFacilityName(facility.getFacilityName());
        resp.setLineCode(facility.getLineCode());
        resp.setRunStatus(facility.getRunStatus());
        resp.setProductionRunning(productionRunning);
        resp.setReasons(reasons);
        return resp;
    }

    // ==================== 内部工具 ====================

    private MesSetTreatmentFacilityDO validateFacilityExists(Long id) {
        if (id == null) {
            throw exception(SET_FACILITY_NOT_EXISTS);
        }
        MesSetTreatmentFacilityDO obj = facilityMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_FACILITY_NOT_EXISTS);
        }
        return obj;
    }

    private void validateType(String type) {
        // 类型可以为空（先建档后补），但填了就必须是点名的三类之一
        if (StrUtil.isNotBlank(type) && !Arrays.asList(FACILITY_TYPES).contains(type)) {
            throw exception(SET_FACILITY_TYPE_INVALID);
        }
    }

    /** 下次更换 = 本次 + 周期；周期没填就返回 null（没周期就没法算到期，不拿建档日凑）。 */
    private LocalDate calcNextReplace(LocalDate base, Integer cycleDays) {
        if (base == null || cycleDays == null || cycleDays <= 0) {
            return null;
        }
        return base.plusDays(cycleDays);
    }

    /** 换炭产生的危废联单号：设施号 + 时间戳，天然不撞。 */
    private String buildWasteManifestNo(MesSetTreatmentFacilityDO facility) {
        return StrUtil.format("HW49-{}-{}", facility.getFacilityNo(),
                LocalDateTime.now().format(NO_TIME_FMT));
    }

    private String currentUserName() {
        String nickname = getLoginUserNickname();
        if (StrUtil.isBlank(nickname)) {
            return String.valueOf(getLoginUserId());
        }
        return nickname;
    }

}
