package cn.iocoder.txgy.module.mes.service.set.envboard;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.module.mes.controller.admin.set.envboard.vo.MesSetEnvBoardRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial.MesSetEmergencyMaterialDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazwasteManifestDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.facility.MesSetTreatmentFacilityMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste.MesSetHazwasteManifestMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionpermit.MesSetPollutionPermitMapper;
import cn.iocoder.txgy.module.mes.service.set.emergencymaterial.MesSetEmergencyMaterialService;
import cn.iocoder.txgy.module.mes.service.set.hazwaste.MesSetHazwasteService;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService.LEVEL_RED;
import static cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService.LEVEL_WARN;

/**
 * MES 安全环保检测-环保看板 Service 实现类
 *
 * <p>五块内容各自复用**既有服务**判定，本类不新造阈值：
 * 年总量水位来自 {@link MesSetPermitComplianceService#checkAnnualQuota}、证载水位来自
 * {@code checkPermitValidity}、物资待办来自 {@link MesSetEmergencyMaterialService#getAlerts}；
 * 设施与联单直接用各自 Mapper 上已有的待办查询（换炭到期 / 未批先停 / 申报时限临近）。
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEnvBoardServiceImpl implements MesSetEnvBoardService {

    /**
     * 危废贮存期限（天）。GB18597 规定贮存一般不超过一年，故取 365 作为**本看板的默认口径**。
     * <p>
     * ponytail: 这是硬编码在水位计算里的值，而设计文档把这类阈值列为「字典项，需配置页」——
     * 配置页落地后应改为从字典读，届时这个常量降级为兜底默认值。之所以现在敢写：看板把它原样回给
     * 前端（{@code storage.limitDays}），界面会显示「按 365 天口径」，不会伪装成配过的东西。
     */
    private static final int STORAGE_LIMIT_DAYS = 365;

    /**
     * 暂存预警提前量：剩余 30 天内进待办。
     */
    private static final int STORAGE_SOON_DAYS = 30;

    /**
     * 联单申报时限的**展示窗口**，不是法规时限——真正的时限落在联单的 declare_deadline 列上，
     * 这里只决定「提前多久开始出现在看板上」。
     */
    private static final int MANIFEST_DUE_HORIZON_DAYS = 7;

    private static final String STORAGE_OVERDUE = "OVERDUE";
    private static final String STORAGE_SOON = "SOON";
    private static final String STORAGE_NORMAL = "NORMAL";

    @Resource
    private MesSetEmissionOutletMapper outletMapper;

    @Resource
    private MesSetPollutionPermitMapper permitMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Resource
    private MesSetTreatmentFacilityMapper facilityMapper;

    @Resource
    private MesPollutionLedgerMapper ledgerMapper;

    @Resource
    private MesSetHazwasteManifestMapper manifestMapper;

    @Resource
    private MesSetEmergencyMaterialService materialService;

    @Override
    public MesSetEnvBoardRespVO getBoard() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        MesSetEnvBoardRespVO board = new MesSetEnvBoardRespVO();
        board.setGeneratedAt(now);
        board.setQuota(buildQuota(today));
        board.setFacility(buildFacility(today));
        board.setStorage(buildStorage(now));
        board.setManifest(buildManifest(now));
        board.setMaterial(buildMaterial());
        return board;
    }

    // ==================== 许可余量 ====================

    /**
     * 年总量水位按**排放口自己申报的污染物**枚举，而不是按当年监测行反推：
     * 一个口今年还没监测过，它的余量照样该在板上——按监测行推会把「没测过」显示成「没问题」。
     * 该口未配年总量时 annualLimit 为 null，前端据此显示「未配置」，不冒充绿灯。
     */
    private MesSetEnvBoardRespVO.QuotaVO buildQuota(LocalDate today) {
        List<MesSetEmissionOutletDO> outlets = outletMapper.selectList();

        List<MesSetEnvBoardRespVO.QuotaItem> items = new ArrayList<>();
        Set<String> permitNos = new LinkedHashSet<>();
        int warn = 0;
        int red = 0;
        for (MesSetEmissionOutletDO outlet : outlets) {
            if (StrUtil.isNotBlank(outlet.getPermitNo())) {
                permitNos.add(outlet.getPermitNo());
            }
            for (String pollutantCode : splitCodes(outlet.getPollutantCodes())) {
                MesSetPermitComplianceService.AnnualQuota q =
                        permitComplianceService.checkAnnualQuota(outlet.getId(), pollutantCode, today);
                MesSetEnvBoardRespVO.QuotaItem item = new MesSetEnvBoardRespVO.QuotaItem();
                item.setOutletId(outlet.getId());
                item.setOutletCode(outlet.getOutletCode());
                item.setPollutantCode(pollutantCode);
                item.setAnnualLimit(q.annualLimit());
                item.setUsed(q.used());
                item.setRatio(q.ratio());
                item.setLevel(q.level());
                items.add(item);
                if (LEVEL_RED.equals(q.level())) {
                    red++;
                } else if (LEVEL_WARN.equals(q.level())) {
                    warn++;
                }
            }
        }
        // 红线排前面：看板最上面必须是最该被看见的
        items.sort(Comparator.comparingInt((MesSetEnvBoardRespVO.QuotaItem i) -> levelOrder(i.getLevel()))
                .thenComparing(MesSetEnvBoardRespVO.QuotaItem::getOutletCode,
                        Comparator.nullsLast(Comparator.naturalOrder())));

        MesSetEnvBoardRespVO.QuotaVO quota = new MesSetEnvBoardRespVO.QuotaVO();
        quota.setItems(items);
        quota.setWarnCount(warn);
        quota.setRedCount(red);
        quota.setPermits(buildPermits(permitNos, today));
        return quota;
    }

    /**
     * 证载水位。余量是按证算的，证过期了余量就没意义，故两者同屏——但**不合并成一条结论**：
     * 证过期不等于超总量，两件事得分开看。
     */
    private List<MesSetEnvBoardRespVO.PermitItem> buildPermits(Set<String> permitNos, LocalDate today) {
        List<MesSetEnvBoardRespVO.PermitItem> permits = new ArrayList<>();
        for (String permitNo : permitNos) {
            MesSetPollutionPermitDO permit = permitMapper.selectByPermitNo(permitNo);
            if (permit == null) {
                continue;
            }
            // 排放口挂着证号、证却查不到：证被删了而口还留着。这不是看板该静默吞掉的事，
            // 但也不该让整块看板挂掉——按 OVERDUE 报出来，等人工去对。
            MesSetPermitComplianceService.PermitValidity v =
                    permitComplianceService.checkPermitValidity(permit.getId(), today);
            if (v == null) {
                continue;
            }
            MesSetEnvBoardRespVO.PermitItem item = new MesSetEnvBoardRespVO.PermitItem();
            item.setPermitNo(v.permitNo());
            item.setEndDate(v.endDate());
            item.setDaysLeft(v.daysLeft());
            item.setLevel(v.level());
            item.setEnterpriseName(permit.getEnterpriseName());
            permits.add(item);
        }
        permits.sort(Comparator.comparing(MesSetEnvBoardRespVO.PermitItem::getDaysLeft,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return permits;
    }

    private int levelOrder(String level) {
        if (LEVEL_RED.equals(level)) {
            return 0;
        }
        if (LEVEL_WARN.equals(level)) {
            return 1;
        }
        return 2;
    }

    /** 排放口的 pollutantCodes 是逗号分隔的中文名（如「颗粒物,SO2,NOx」），空段丢掉 */
    private List<String> splitCodes(String codes) {
        if (StrUtil.isBlank(codes)) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        for (String s : codes.split(",")) {
            if (StrUtil.isNotBlank(s)) {
                list.add(s.trim());
            }
        }
        return list;
    }

    // ==================== 设施运行 ====================

    private MesSetEnvBoardRespVO.FacilityVO buildFacility(LocalDate today) {
        List<MesSetTreatmentFacilityDO> all = facilityMapper.selectList();

        MesSetEnvBoardRespVO.FacilityVO vo = new MesSetEnvBoardRespVO.FacilityVO();
        vo.setTotal(all.size());
        vo.setRunning((int) all.stream().filter(f -> "RUNNING".equals(f.getRunStatus())).count());
        vo.setStopped((int) all.stream().filter(f -> "STOPPED".equals(f.getRunStatus())).count());
        vo.setShutdownDeclared((int) all.stream()
                .filter(f -> StrUtil.isNotBlank(f.getShutdownStatus())
                        && !"NONE".equals(f.getShutdownStatus())).count());

        List<MesSetEnvBoardRespVO.FacilityItem> due = new ArrayList<>();
        for (MesSetTreatmentFacilityDO f : facilityMapper.selectDueReplace(today)) {
            due.add(toFacilityItem(f, today));
        }
        vo.setDueReplace(due);

        List<MesSetEnvBoardRespVO.FacilityItem> unapproved = new ArrayList<>();
        for (MesSetTreatmentFacilityDO f : facilityMapper.selectStoppedWithoutApproval()) {
            unapproved.add(toFacilityItem(f, today));
        }
        vo.setStoppedWithoutApproval(unapproved);
        return vo;
    }

    private MesSetEnvBoardRespVO.FacilityItem toFacilityItem(MesSetTreatmentFacilityDO f, LocalDate today) {
        MesSetEnvBoardRespVO.FacilityItem item = new MesSetEnvBoardRespVO.FacilityItem();
        item.setId(f.getId());
        item.setFacilityNo(f.getFacilityNo());
        item.setFacilityName(f.getFacilityName());
        item.setRunStatus(f.getRunStatus());
        item.setShutdownStatus(f.getShutdownStatus());
        item.setNextReplaceDate(f.getNextReplaceDate());
        item.setDaysToReplace(f.getNextReplaceDate() == null
                ? null : ChronoUnit.DAYS.between(today, f.getNextReplaceDate()));
        return item;
    }

    // ==================== 暂存倒计时 ====================

    /**
     * 在库暂存行按「进库那天起算」倒计时。起算点用台账的 status_time（落 STORED 那一刻），
     * 不用 create_time——台账是先建单后流转的，拿建单日算会把贮存期算长、把已超期的显示成没超。
     * status_time 为空的行不猜，直接跳过（宁可少一行，不给一个编出来的天数）。
     */
    private MesSetEnvBoardRespVO.StorageVO buildStorage(LocalDateTime now) {
        List<MesPollutionLedgerDO> stored =
                ledgerMapper.selectList(MesPollutionLedgerDO::getStatus, MesPollutionLedgerDO.STATUS_STORED);

        List<MesSetEnvBoardRespVO.StorageItem> items = new ArrayList<>();
        int overdue = 0;
        int soon = 0;
        for (MesPollutionLedgerDO l : stored) {
            if (l.getStatusTime() == null) {
                continue;
            }
            long daysStored = ChronoUnit.DAYS.between(l.getStatusTime().toLocalDate(), now.toLocalDate());
            long daysLeft = STORAGE_LIMIT_DAYS - daysStored;
            MesSetEnvBoardRespVO.StorageItem item = new MesSetEnvBoardRespVO.StorageItem();
            item.setId(l.getId());
            item.setSourceRecordNo(l.getSourceRecordNo());
            item.setItemName(l.getItemName());
            item.setWeight(l.getWeight());
            item.setLocation(l.getLocation());
            item.setStoredAt(l.getStatusTime());
            item.setDaysStored(daysStored);
            item.setDaysLeft(daysLeft);
            item.setLevel(daysLeft < 0 ? STORAGE_OVERDUE
                    : (daysLeft <= STORAGE_SOON_DAYS ? STORAGE_SOON : STORAGE_NORMAL));
            if (STORAGE_OVERDUE.equals(item.getLevel())) {
                overdue++;
            } else if (STORAGE_SOON.equals(item.getLevel())) {
                soon++;
            }
            items.add(item);
        }
        // 最急的排最前：daysLeft 升序
        items.sort(Comparator.comparing(MesSetEnvBoardRespVO.StorageItem::getDaysLeft,
                Comparator.nullsLast(Comparator.naturalOrder())));

        MesSetEnvBoardRespVO.StorageVO vo = new MesSetEnvBoardRespVO.StorageVO();
        vo.setLimitDays(STORAGE_LIMIT_DAYS);
        vo.setOverdueCount(overdue);
        vo.setSoonCount(soon);
        vo.setItems(items);
        return vo;
    }

    // ==================== 联单状态 ====================

    private MesSetEnvBoardRespVO.ManifestVO buildManifest(LocalDateTime now) {
        List<MesSetHazwasteManifestDO> all = manifestMapper.selectList();

        Map<String, Integer> byStatus = new LinkedHashMap<>();
        // 按状态机顺序初始化，避免空状态不显示、前端图例每次都不一样
        for (String s : MesSetHazwasteService.MANIFEST_STATUSES) {
            byStatus.put(s, 0);
        }
        for (MesSetHazwasteManifestDO m : all) {
            if (StrUtil.isNotBlank(m.getStatus())) {
                byStatus.merge(m.getStatus(), 1, Integer::sum);
            }
        }

        List<MesSetEnvBoardRespVO.ManifestItem> dueSoon = new ArrayList<>();
        for (MesSetHazwasteManifestDO m :
                manifestMapper.selectDueSoon(now.plusDays(MANIFEST_DUE_HORIZON_DAYS))) {
            MesSetEnvBoardRespVO.ManifestItem item = new MesSetEnvBoardRespVO.ManifestItem();
            item.setId(m.getId());
            item.setManifestNo(m.getManifestNo());
            item.setWasteName(m.getWasteName());
            item.setQuantity(m.getQuantity());
            item.setStatus(m.getStatus());
            item.setDeclareDeadline(m.getDeclareDeadline());
            item.setHoursLeft(m.getDeclareDeadline() == null
                    ? null : Duration.between(now, m.getDeclareDeadline()).toHours());
            dueSoon.add(item);
        }

        MesSetEnvBoardRespVO.ManifestVO vo = new MesSetEnvBoardRespVO.ManifestVO();
        vo.setTotal(all.size());
        vo.setCountByStatus(byStatus);
        vo.setDueSoon(dueSoon);
        return vo;
    }

    // ==================== 应急物资 ====================

    private MesSetEnvBoardRespVO.MaterialVO buildMaterial() {
        List<MesSetEmergencyMaterialDO> alerts = materialService.getAlerts();

        List<MesSetEnvBoardRespVO.MaterialItem> items = new ArrayList<>();
        for (MesSetEmergencyMaterialDO m : alerts) {
            MesSetEnvBoardRespVO.MaterialItem item = new MesSetEnvBoardRespVO.MaterialItem();
            item.setId(m.getId());
            item.setMaterialNo(m.getMaterialNo());
            item.setMaterialName(m.getMaterialName());
            item.setQuantity(m.getQuantity());
            item.setUnit(m.getUnit());
            item.setExpireDate(m.getExpireDate());
            item.setStatus(m.getStatus());
            items.add(item);
        }

        MesSetEnvBoardRespVO.MaterialVO vo = new MesSetEnvBoardRespVO.MaterialVO();
        vo.setAlertCount(items.size());
        vo.setAlerts(items);
        return vo;
    }

}
