package cn.iocoder.txgy.module.mes.service.set.chemical;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo.*;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemical.MesSetChemicalProfileMapper;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 安全环保检测-危化品档案 Service 实现（设计文档 §5 主线A）
 *
 * 本类只做**档案与规则**，不碰库存账：stock_quantity 是档案上的一个计数（由 stockIn 累加），
 * 真正的批次效期/FIFO 冻结在 mes_wm_batch.expireDate 与库存投影侧（§5.3）。
 *
 * @author OPENLAB BS
 */
@Service
@Slf4j
public class MesSetChemicalProfileServiceImpl implements MesSetChemicalProfileService {

    /**
     * 相容组中文名。
     *
     * 只收设计文档 §5.2 / §130 点名的物料族（黑料/白料/稀释剂/环氧粉末/水/醇/胺）；
     * **没收录的按原值回显、不猜**——与 HW_HAZARD_TRAITS 同一口径，编错了比留空危险。
     */
    private static final Map<String, String> GROUP_NAMES = Map.of(
            "ISOCYANATE", "异氰酸酯(黑料)",
            "POLYOL", "组合聚醚(白料)",
            "THINNER", "稀释剂",
            "EPOXY", "环氧粉末",
            "WATER", "水",
            "ALCOHOL", "醇类",
            "AMINE", "胺类");

    /**
     * 禁配矩阵（设计文档 §5.2 锚定）：
     * 「异氰酸酯(黑料)…远离水醇胺（放热剧反应）」、「组合聚醚(白料)…与黑料剧烈反应 → 分间、双锁分开」。
     *
     * 只列文档点名的组合。其余组合**不判**——禁配是有化学依据的事，宁可漏判让人工确认，
     * 也不能凭"看起来像"就拦货。
     */
    private static final String[][] INCOMPATIBLE_PAIRS = {
            {"ISOCYANATE", "POLYOL"},
            {"ISOCYANATE", "WATER"},
            {"ISOCYANATE", "ALCOHOL"},
            {"ISOCYANATE", "AMINE"},
    };

    /**
     * 必须存放于防爆区的相容组（设计文档 §5.2「稀释剂：易燃液体专柜」）。
     */
    private static final Set<String> EXPLOSION_PROOF_GROUPS = Set.of("THINNER");

    @Resource
    private MesSetChemicalProfileMapper profileMapper;

    @Resource
    private MesSetSignRecordService signRecordService;

    // ==================== 档案 CRUD ====================

    @Override
    public Long createProfile(MesSetChemicalProfileSaveReqVO createReqVO) {
        if (profileMapper.selectByProfileNo(createReqVO.getProfileNo()) != null) {
            throw exception(SET_CHEMICAL_PROFILE_NO_DUPLICATE);
        }
        MesSetChemicalProfileDO obj = BeanUtils.toBean(createReqVO, MesSetChemicalProfileDO.class);
        obj.setStockQuantity(BigDecimal.ZERO);
        if (obj.getStatus() == null) {
            obj.setStatus("ENABLED");
        }
        profileMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateProfile(MesSetChemicalProfileSaveReqVO updateReqVO) {
        MesSetChemicalProfileDO exist = validateProfileExists(updateReqVO.getId());
        MesSetChemicalProfileDO dup = profileMapper.selectByProfileNo(updateReqVO.getProfileNo());
        if (dup != null && !dup.getId().equals(exist.getId())) {
            throw exception(SET_CHEMICAL_PROFILE_NO_DUPLICATE);
        }
        MesSetChemicalProfileDO updateObj = BeanUtils.toBean(updateReqVO, MesSetChemicalProfileDO.class);
        // 存量只由 stockIn 累加，改档案不许覆盖——那是账，不是属性
        updateObj.setStockQuantity(null);
        if (updateReqVO.getStatus() != null && !List.of("ENABLED", "DISABLED").contains(updateReqVO.getStatus())) {
            throw exception(SET_CHEMICAL_PROFILE_STATUS_INVALID);
        }
        profileMapper.updateById(updateObj);
    }

    @Override
    public void deleteProfile(Long id) {
        validateProfileExists(id);
        profileMapper.deleteById(id);
    }

    @Override
    public MesSetChemicalProfileDO getProfile(Long id) {
        return validateProfileExists(id);
    }

    @Override
    public MesSetChemicalProfileDO getProfileByNo(String profileNo) {
        MesSetChemicalProfileDO obj = profileMapper.selectByProfileNo(profileNo);
        if (obj == null) {
            throw exception(SET_CHEMICAL_PROFILE_NOT_EXISTS);
        }
        return obj;
    }

    @Override
    public PageResult<MesSetChemicalProfileDO> getProfilePage(MesSetChemicalProfilePageReqVO pageReqVO) {
        return profileMapper.selectPage(pageReqVO);
    }

    // ==================== 分级储存：四道校验 ====================

    @Override
    public MesSetChemicalStorageCheckRespVO checkStorage(MesSetChemicalStorageCheckReqVO reqVO) {
        MesSetChemicalProfileDO profile = getProfileByNo(reqVO.getProfileNo());
        String location = StrUtil.blankToDefault(reqVO.getStorageLocation(), profile.getStorageLocation());

        List<String> reasons = new ArrayList<>();
        MesSetChemicalStorageCheckRespVO resp = new MesSetChemicalStorageCheckRespVO();
        resp.setProfileNo(profile.getProfileNo());
        resp.setChemicalName(profile.getChemicalName());
        resp.setCompatGroup(profile.getCompatGroup());
        resp.setCompatGroupName(GROUP_NAMES.getOrDefault(profile.getCompatGroup(), profile.getCompatGroup()));
        resp.setStorageLocation(location);
        resp.setStockQuantity(profile.getStockQuantity());
        resp.setStorageLimit(profile.getStorageLimit());

        // ① MSDS 硬挂载：设计文档「无 MSDS 的危化品不下采购单」
        boolean msdsOk = StrUtil.isNotBlank(profile.getMsdsUrl());
        resp.setMsdsOk(msdsOk);
        if (!msdsOk) {
            reasons.add("未挂载 MSDS：无 MSDS 的危化品不得入库，请先上传 MSDS 再放行");
        }

        // ② 专区要求
        boolean zoneOk = checkZone(profile);
        resp.setZoneOk(zoneOk);
        if (!zoneOk) {
            reasons.add(String.format("专区不符：%s 须存放于防爆区（EXPLOSION_PROOF），当前为 %s",
                    resp.getCompatGroupName(), profile.getStorageZone()));
        }

        // ③ 同库位禁配
        List<String> incompatibleWith = new ArrayList<>();
        if (StrUtil.isNotBlank(location)) {
            for (MesSetChemicalProfileDO other : profileMapper.selectEnabledByLocation(location, profile.getId())) {
                if (isIncompatible(profile, other)) {
                    incompatibleWith.add(String.format("%s（%s，组 %s）",
                            other.getChemicalName(), other.getProfileNo(),
                            GROUP_NAMES.getOrDefault(other.getCompatGroup(), other.getCompatGroup())));
                }
            }
        }
        resp.setIncompatibleWith(incompatibleWith);
        for (String s : incompatibleWith) {
            reasons.add(String.format("禁配：库位「%s」已有 %s，与本物料禁同区（须分间存放）", location, s));
        }

        // ④ 储量上限（只在给定了本次入库量时判）
        Boolean quotaOk = null;
        if (reqVO.getQuantity() != null) {
            quotaOk = checkQuota(profile, reqVO.getQuantity());
            if (!quotaOk) {
                BigDecimal after = nullSafe(profile.getStockQuantity()).add(reqVO.getQuantity());
                reasons.add(String.format("超量：现有 %s + 本次 %s = %s %s，超过储量上限 %s %s",
                        nullSafe(profile.getStockQuantity()).stripTrailingZeros().toPlainString(),
                        reqVO.getQuantity().stripTrailingZeros().toPlainString(),
                        after.stripTrailingZeros().toPlainString(),
                        StrUtil.blankToDefault(profile.getStorageUnit(), ""),
                        profile.getStorageLimit().stripTrailingZeros().toPlainString(),
                        StrUtil.blankToDefault(profile.getStorageUnit(), "")));
            }
        }
        resp.setQuotaOk(quotaOk);

        resp.setReasons(reasons);
        resp.setPassed(reasons.isEmpty());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesSetChemicalStorageCheckRespVO stockIn(MesSetChemicalStockInReqVO reqVO) {
        if (reqVO.getQuantity() == null || reqVO.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(SET_CHEMICAL_QUANTITY_INVALID);
        }
        MesSetChemicalStorageCheckReqVO checkReqVO = new MesSetChemicalStorageCheckReqVO();
        checkReqVO.setProfileNo(reqVO.getProfileNo());
        checkReqVO.setStorageLocation(reqVO.getStorageLocation());
        checkReqVO.setQuantity(reqVO.getQuantity());
        MesSetChemicalStorageCheckRespVO check = checkStorage(checkReqVO);

        // 逐条给出具体拦截码：库管看到的必须是"为什么不让入库"，不是一句笼统的失败
        if (!check.getPassed()) {
            if (Boolean.FALSE.equals(check.getMsdsOk())) {
                throw exception(SET_CHEMICAL_MSDS_MISSING);
            }
            if (Boolean.FALSE.equals(check.getZoneOk())) {
                throw exception(SET_CHEMICAL_ZONE_MISMATCH);
            }
            if (check.getIncompatibleWith() != null && !check.getIncompatibleWith().isEmpty()) {
                throw exception(SET_CHEMICAL_INCOMPATIBLE);
            }
            throw exception(SET_CHEMICAL_QUOTA_EXCEEDED);
        }

        MesSetChemicalProfileDO profile = getProfileByNo(reqVO.getProfileNo());
        MesSetChemicalProfileDO updateObj = new MesSetChemicalProfileDO();
        updateObj.setId(profile.getId());
        updateObj.setStockQuantity(nullSafe(profile.getStockQuantity()).add(reqVO.getQuantity()));
        profileMapper.updateById(updateObj);

        check.setStockQuantity(updateObj.getStockQuantity());
        return check;
    }

    private boolean checkZone(MesSetChemicalProfileDO profile) {
        boolean needExplosionProof = Boolean.TRUE.equals(profile.getExplosionProof())
                || EXPLOSION_PROOF_GROUPS.contains(profile.getCompatGroup());
        return !needExplosionProof || "EXPLOSION_PROOF".equals(profile.getStorageZone());
    }

    private boolean checkQuota(MesSetChemicalProfileDO profile, BigDecimal quantity) {
        if (profile.getStorageLimit() == null) {
            return true;
        }
        return nullSafe(profile.getStockQuantity()).add(quantity).compareTo(profile.getStorageLimit()) <= 0;
    }

    /**
     * 两物料是否禁配：命中矩阵（对无序），或任一方在 incompatibleGroups 里人工点名了对方。
     */
    private boolean isIncompatible(MesSetChemicalProfileDO a, MesSetChemicalProfileDO b) {
        String ga = a.getCompatGroup();
        String gb = b.getCompatGroup();
        if (ga != null && gb != null) {
            for (String[] pair : INCOMPATIBLE_PAIRS) {
                if ((pair[0].equals(ga) && pair[1].equals(gb)) || (pair[1].equals(ga) && pair[0].equals(gb))) {
                    return true;
                }
            }
        }
        return inExtraList(a.getIncompatibleGroups(), gb) || inExtraList(b.getIncompatibleGroups(), ga);
    }

    private boolean inExtraList(String extra, String group) {
        if (StrUtil.isBlank(extra) || group == null) {
            return false;
        }
        for (String s : extra.split(",")) {
            if (group.equals(s.trim())) {
                return true;
            }
        }
        return false;
    }

    // ==================== 五双双人签字 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesSetChemicalDoubleSignRespVO doubleSign(MesSetChemicalDoubleSignReqVO reqVO) {
        validateSignAction(reqVO.getSignAction());
        Long userId = getLoginUserId();
        List<MesSetSignRecordDO> exist = listSigns(reqVO.getBizNo(), reqVO.getSignAction());

        // 双人制的核心：同一个人签两次不算双人。设计文档 §5.3「双账号各签一次」
        for (MesSetSignRecordDO r : exist) {
            if (userId != null && userId.equals(r.getSignUserId())) {
                throw exception(SET_CHEMICAL_DOUBLE_SIGN_SAME_USER);
            }
        }

        signRecordService.createSignRecord(MesSetSignRecordDO.builder()
                .bizType(DOUBLE_SIGN_BIZ_TYPE)
                .bizNo(reqVO.getBizNo())
                .signRole(reqVO.getSignAction())
                .signUser(currentUserName())
                .signUserId(userId)
                .signTime(LocalDateTime.now())
                .opinion(reqVO.getOpinion())
                .signImg(reqVO.getSignImg())
                .build());
        return buildSignStatus(reqVO.getBizNo(), reqVO.getSignAction(), listSigns(reqVO.getBizNo(), reqVO.getSignAction()));
    }

    @Override
    public MesSetChemicalDoubleSignRespVO getDoubleSignStatus(String bizNo, String signAction) {
        validateSignAction(signAction);
        return buildSignStatus(bizNo, signAction, listSigns(bizNo, signAction));
    }

    private void validateSignAction(String signAction) {
        if (!Arrays.asList(DOUBLE_SIGN_ACTIONS).contains(signAction)) {
            throw exception(SET_CHEMICAL_DOUBLE_SIGN_ACTION_INVALID);
        }
    }

    /** 取该作业单该动作下的全部签字（复用签字表的分页查询，不另开 Mapper 方法）。 */
    private List<MesSetSignRecordDO> listSigns(String bizNo, String signAction) {
        MesSetSignRecordPageReqVO reqVO = new MesSetSignRecordPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(100);
        reqVO.setBizType(DOUBLE_SIGN_BIZ_TYPE);
        reqVO.setBizNo(bizNo);
        return signRecordService.getSignRecordPage(reqVO).getList().stream()
                .filter(r -> signAction.equals(r.getSignRole()))
                .collect(Collectors.toList());
    }

    private MesSetChemicalDoubleSignRespVO buildSignStatus(String bizNo, String signAction,
                                                           List<MesSetSignRecordDO> signs) {
        MesSetChemicalDoubleSignRespVO resp = new MesSetChemicalDoubleSignRespVO();
        resp.setBizNo(bizNo);
        resp.setSignAction(signAction);
        resp.setSignCount(signs.size());
        // 按账号去重展示：同一个账号签两次时，去重后仍只有一个人，一眼能看出没满足双人制
        List<String> signers = signs.stream()
                .map(r -> r.getSignUserId() == null ? r.getSignUser() : String.valueOf(r.getSignUserId()))
                .distinct()
                .collect(Collectors.toList());
        resp.setSigners(signers);
        resp.setComplete(signers.size() >= 2);
        resp.setLastSignTime(signs.stream().map(MesSetSignRecordDO::getSignTime)
                .filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null));
        return resp;
    }

    // ==================== 预警 ====================

    @Override
    public MesSetChemicalAlertRespVO getAlerts(Integer days) {
        int warnDays = days == null || days <= 0 ? 30 : days; // §5 资质到期前 30 天预警
        LocalDate deadline = LocalDate.now().plusDays(warnDays);

        List<MesSetChemicalProfileDO> enabled = profileMapper.selectList(
                new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                        .eq(MesSetChemicalProfileDO::getStatus, "ENABLED"));

        MesSetChemicalAlertRespVO resp = new MesSetChemicalAlertRespVO();
        resp.setOverQuota(enabled.stream()
                .filter(p -> p.getStorageLimit() != null
                        && nullSafe(p.getStockQuantity()).compareTo(p.getStorageLimit()) > 0)
                .collect(Collectors.toList()));
        resp.setMsdsMissing(enabled.stream()
                .filter(p -> StrUtil.isBlank(p.getMsdsUrl()))
                .collect(Collectors.toList()));
        resp.setMsdsExpiring(enabled.stream()
                .filter(p -> p.getMsdsExpireDate() != null && !p.getMsdsExpireDate().isAfter(deadline))
                .collect(Collectors.toList()));
        return resp;
    }

    // ==================== 内部工具 ====================

    private MesSetChemicalProfileDO validateProfileExists(Long id) {
        if (id == null) {
            throw exception(SET_CHEMICAL_PROFILE_NOT_EXISTS);
        }
        MesSetChemicalProfileDO obj = profileMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_CHEMICAL_PROFILE_NOT_EXISTS);
        }
        return obj;
    }

    private static BigDecimal nullSafe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String currentUserName() {
        String nickname = getLoginUserNickname();
        if (nickname == null || nickname.isEmpty()) {
            return String.valueOf(getLoginUserId());
        }
        return nickname;
    }

}
