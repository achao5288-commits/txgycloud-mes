package cn.iocoder.txgy.module.mes.service.set.envreport;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.json.JsonUtils;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.infra.api.file.FileApi;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill.MesSetEmergencyDrillDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport.MesSetEnvReportDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencydrill.MesSetEmergencyDrillMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent.MesSetEmergencyEventMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.envreport.MesSetEnvReportMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas.MesSetExhaustGasMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater.MesSetWastewaterMapper;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_ARCHIVE_FAILED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_PERIOD_MISSING;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_REPORT_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_STATUS_INVALID;
import static cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService.RESULT_FAIL;

/**
 * MES 安全环保检测-环保检测报告 Service 实现类
 *
 * 自动取数汇总（设计文档 §六-3）里**两种口径并存**，这是本类最容易埋雷的地方（§15.1）：
 * <ul>
 *   <li>{@code exhaustGas} / {@code wastewater} 段按**统计期**闭区间 {@code [start 00:00, end+1 00:00)}；</li>
 *   <li>{@code quota} 段按**自然年**（{@code periodEnd} 所在年），复用 {@link MesSetPermitComplianceService#checkAnnualQuota}。</li>
 * </ul>
 * 夹具里故意放"统计期外但同年"和"跨年"两种行，就是为了让串口径立刻显形。
 *
 * @author OPENLAB BS
 */
@Slf4j
@Service
@Validated
public class MesSetEnvReportServiceImpl implements MesSetEnvReportService {

    /**
     * 报告来源：THIRD_PARTY/INTERNAL
     */
    private static final Set<String> REPORT_TYPES = new HashSet<>(Arrays.asList("MONTHLY", "QUARTERLY", "THIRD_PARTY"));

    /**
     * 状态：DRAFT/APPROVED/REJECTED/ARCHIVED
     */
    private static final Set<String> STATUSS = new HashSet<>(Arrays.asList("APPROVED", "DRAFT"));

    /**
     * 报告快照里的时间一律用可读字符串，**不用**接口 DTO 那套 epoch 毫秒（§15.2）。
     * 这是写给人看的归档件，不是前端绑定值；两边照抄会得到一个 1970 年的报告。
     */
    private static final DateTimeFormatter SNAP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 归档件在文件服务里的目录（§16.6：目录按业务分）
     */
    private static final String ARCHIVE_DIR = "mes/env-report";

    /**
     * 应急段只认这两个"已收口"的状态：计划过的演练、上报了没处置的事件，都不算可报监管的数。
     */
    private static final String DRILL_STATUS_CLOSED = "CLOSED";
    private static final String EVENT_STATUS_CLOSED = "CLOSED";

    @Resource
    private MesSetEnvReportMapper envreportMapper;

    @Resource
    private MesSetExhaustGasMapper exhaustGasMapper;

    @Resource
    private MesSetWastewaterMapper wastewaterMapper;

    @Resource
    private MesSetEmergencyDrillMapper drillMapper;

    @Resource
    private MesSetEmergencyEventMapper eventMapper;

    @Resource
    private MesSetEmissionOutletMapper outletMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Resource
    private FileApi fileApi;

    @Override
    public Long createEnvReport(MesSetEnvReportSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetEnvReportDO obj = BeanUtils.toBean(createReqVO, MesSetEnvReportDO.class);
        // 建单静默降级（§15.3）：统计期已填且调用方没给摘要时才自动取数。
        // 缺统计期**不报错**——草稿阶段统计期常常后补，报告本身得先能建出来。
        if (StrUtil.isBlank(obj.getDataSummary())
                && obj.getPeriodStart() != null && obj.getPeriodEnd() != null) {
            obj.setDataSummary(buildSummaryQuietly(obj.getPeriodStart(), obj.getPeriodEnd()));
        }
        envreportMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEnvReport(MesSetEnvReportSaveReqVO updateReqVO) {
        MesSetEnvReportDO exist = validateEnvReportExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getReportNo());
        envreportMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetEnvReportDO.class));
    }

    @Override
    public void deleteEnvReport(Long id) {
        validateEnvReportExists(id);
        envreportMapper.deleteById(id);
    }

    @Override
    public MesSetEnvReportDO getEnvReport(Long id) {
        return envreportMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEnvReportDO> getEnvReportPage(MesSetEnvReportPageReqVO pageReqVO) {
        return envreportMapper.selectPage(pageReqVO);
    }

    @Override
    public String autoSummary(Long id) {
        MesSetEnvReportDO report = validateEnvReportExists(id);
        // 显式补数：缺前提必须响（与建单那步的静默降级相对，§15.3）
        if (report.getPeriodStart() == null || report.getPeriodEnd() == null) {
            throw exception(SET_ENV_REPORT_PERIOD_MISSING);
        }
        String summary = buildSummary(report.getPeriodStart(), report.getPeriodEnd());
        MesSetEnvReportDO update = new MesSetEnvReportDO();
        update.setId(id);
        update.setDataSummary(summary);
        envreportMapper.updateById(update);
        return summary;
    }

    @Override
    public String archiveSummary(Long id) {
        MesSetEnvReportDO report = validateEnvReportExists(id);
        String summary = report.getDataSummary();
        if (StrUtil.isBlank(summary)) {
            if (report.getPeriodStart() == null || report.getPeriodEnd() == null) {
                // 没摘要也没统计期 —— 不产出空壳文件（§16.4）
                throw exception(SET_ENV_REPORT_PERIOD_MISSING);
            }
            summary = buildSummary(report.getPeriodStart(), report.getPeriodEnd());
        }
        String url;
        try {
            url = fileApi.createFile(summary.getBytes(StandardCharsets.UTF_8),
                    report.getReportNo() + ".json", ARCHIVE_DIR, "application/json");
        } catch (Exception e) {
            log.error("[archiveSummary][归档失败，文件服务不可用，reportNo={}]", report.getReportNo(), e);
            throw exception(SET_ENV_REPORT_ARCHIVE_FAILED);
        }
        if (StrUtil.isBlank(url)) {
            // 返回了空 URL 与抛异常同性质：说"已归档"却取不回，比当场报错危险
            log.error("[archiveSummary][文件服务返回空 URL，reportNo={}]", report.getReportNo());
            throw exception(SET_ENV_REPORT_ARCHIVE_FAILED);
        }
        MesSetEnvReportDO update = new MesSetEnvReportDO();
        update.setId(id);
        update.setFileUrl(url);
        envreportMapper.updateById(update);
        return url;
    }

    private MesSetEnvReportDO validateEnvReportExists(Long id) {
        MesSetEnvReportDO obj = envreportMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_ENV_REPORT_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、报告来源：THIRD_PARTY/INTERNAL枚举、状态：DRAFT/APPROVED/REJECTED/ARCHIVED枚举
     */
    private void validateBase(MesSetEnvReportSaveReqVO reqVO, String origin) {
        MesSetEnvReportDO exist = envreportMapper.selectByReportNo(reqVO.getReportNo());
        if (exist != null && !exist.getReportNo().equals(origin)) {
            throw exception(SET_ENV_REPORT_NO_DUPLICATE);
        }
        if (reqVO.getReportType() != null && !REPORT_TYPES.contains(reqVO.getReportType())) {
            throw exception(SET_ENV_REPORT_REPORT_TYPE_INVALID);
        }
        if (reqVO.getStatus() != null && !STATUSS.contains(reqVO.getStatus())) {
            throw exception(SET_ENV_REPORT_STATUS_INVALID);
        }
    }

    // ==================== 自动取数汇总 ====================

    /**
     * 汇总失败不阻断建单：留空待人工补数即可（统计期后补时本来就会再跑一遍）。
     */
    private String buildSummaryQuietly(LocalDate start, LocalDate end) {
        try {
            return buildSummary(start, end);
        } catch (Exception e) { // noqa: 建单是主流程，摘要只是附产品
            log.error("[buildSummaryQuietly][建单自动取数失败，data_summary 留空待补，period={} ~ {}]",
                    start, end, e);
            return null;
        }
    }

    private String buildSummary(LocalDate start, LocalDate end) {
        // 统计期取**闭区间**：periodEnd 当天 23:00 的行必须计入，故止点用 end+1 的 00:00
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay();

        List<Mon> gas = gasMons(exhaustGasMapper.selectByMonitorRange(from, to));
        List<Mon> water = waterMons(wastewaterMapper.selectByMonitorRange(from, to));
        Map<Long, String> outletCodes = outletCodes();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("periodStart", start.toString());
        root.put("periodEnd", end.toString());
        root.put("generatedAt", LocalDateTime.now().format(SNAP_FORMAT));
        root.put("exhaustGas", section(gas, outletCodes));
        root.put("wastewater", section(water, outletCodes));
        root.put("quota", quotaSection(gas, water, outletCodes, end));
        root.put("emergency", emergencySection(start, end, from, to));
        return JsonUtils.toJsonString(root);
    }

    /**
     * 应急段（§八.2 / §八.4 入执行报告）：期内的演练（含评估结论）与事件（含报告全文）。
     * <p>
     * 演练按 {@code drillDate} 自然日**闭区间**（与年度覆盖统计同口径）；事件按 {@code occurTime}
     * 用 {@code [start 00:00, end+1 00:00)} 半开区间——两者都是"期内发生"这一件事，别看着写法不同以为口径不同。
     * <p>
     * 事件报告全文（reportContent）原样带出：报告是给人签字报监管的，改一个字都要能追到人的原话。
     */
    private Map<String, Object> emergencySection(LocalDate start, LocalDate end,
                                                 LocalDateTime from, LocalDateTime to) {
        List<MesSetEmergencyDrillDO> drills = drillMapper.selectListByDateRange(start, end);
        List<MesSetEmergencyEventDO> events = eventMapper.selectListByOccurRange(from, to);

        Map<String, Object> sec = new LinkedHashMap<>();
        sec.put("drillCount", drills.size());
        sec.put("drillClosedCount", drills.stream()
                .filter(d -> DRILL_STATUS_CLOSED.equals(d.getStatus())).count());
        sec.put("drills", drills.stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("drillNo", d.getDrillNo());
            m.put("drillName", d.getDrillName());
            m.put("drillType", d.getDrillType());
            m.put("drillDate", d.getDrillDate() == null ? null : d.getDrillDate().toString());
            m.put("participantCount", d.getParticipantCount());
            m.put("status", d.getStatus());
            m.put("evaluation", d.getEvaluation());
            m.put("rectifyRequirement", d.getRectifyRequirement());
            m.put("rectifyStatus", d.getRectifyStatus());
            m.put("closedDate", d.getClosedDate() == null ? null : d.getClosedDate().toString());
            return m;
        }).collect(Collectors.toList()));

        sec.put("eventCount", events.size());
        sec.put("eventClosedCount", events.stream()
                .filter(e -> EVENT_STATUS_CLOSED.equals(e.getStatus())).count());
        sec.put("emergencyWasteQuantity", events.stream()
                .map(MesSetEmergencyEventDO::getWasteQuantity).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        sec.put("events", events.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("eventNo", e.getEventNo());
            m.put("eventType", e.getEventType());
            m.put("scenario", e.getScenario());
            m.put("occurTime", e.getOccurTime() == null ? null : e.getOccurTime().format(SNAP_FORMAT));
            m.put("location", e.getLocation());
            m.put("chemicalCode", e.getChemicalCode());
            m.put("leakQuantity", e.getLeakQuantity());
            m.put("impactScope", e.getImpactScope());
            m.put("status", e.getStatus());
            m.put("handler", e.getHandler());
            m.put("wasteCount", e.getWasteCount());
            m.put("wasteQuantity", e.getWasteQuantity());
            m.put("reportContent", e.getReportContent());
            m.put("approver", e.getApprover());
            m.put("closedTime", e.getClosedTime() == null ? null : e.getClosedTime().format(SNAP_FORMAT));
            return m;
        }).collect(Collectors.toList()));
        return sec;
    }

    /**
     * 一个介质（废气/废水）的整体情况：总行数/超标数/排放量 + 按污染物细分 + 超标明细。
     */
    private Map<String, Object> section(List<Mon> rows, Map<Long, String> outletCodes) {
        Map<String, Object> sec = new LinkedHashMap<>();
        sec.put("records", rows.size());
        sec.put("exceedCount", countExceed(rows));
        sec.put("emissionAmount", sumAmount(rows));

        Map<String, List<Mon>> grouped = rows.stream()
                .filter(m -> StrUtil.isNotBlank(m.pollutantCode()))
                .collect(Collectors.groupingBy(Mon::pollutantCode, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> pollutants = new ArrayList<>();
        for (Map.Entry<String, List<Mon>> e : grouped.entrySet()) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("pollutantCode", e.getKey());
            p.put("records", e.getValue().size());
            p.put("exceedCount", countExceed(e.getValue()));
            p.put("emissionAmount", sumAmount(e.getValue()));
            pollutants.add(p);
        }
        sec.put("pollutants", pollutants);

        // 原始监测行只存 outletId，报告给人看要能认出是哪个口
        List<Map<String, Object>> exceeds = rows.stream().filter(this::isExceed)
                .map(m -> {
                    Map<String, Object> x = new LinkedHashMap<>();
                    x.put("recordNo", m.recordNo());
                    x.put("outletId", m.outletId());
                    x.put("outletCode", outletCodes.get(m.outletId()));
                    x.put("pollutantCode", m.pollutantCode());
                    x.put("concentration", m.concentration());
                    x.put("limitValue", m.limitValue());
                    x.put("monitorTime", m.monitorTime() == null ? null : m.monitorTime().format(SNAP_FORMAT));
                    return x;
                }).collect(Collectors.toList());
        sec.put("exceeds", exceeds);
        return sec;
    }

    /**
     * 年累计段：对统计期内**出现过的每个 (排放口,污染物)** 出一条，但累计量按**自然年**算——
     * 与上面的统计期口径分开，混起来就是 §15.1 那个雷。
     * 没配年总量时不编水位：annualLimit/ratio 为 null、level 仍 NORMAL，只如实报累计量（§14.1 同口径）。
     */
    private List<Map<String, Object>> quotaSection(List<Mon> gas, List<Mon> water,
                                                   Map<Long, String> outletCodes, LocalDate periodEnd) {
        Set<Long> seenOutlets = new LinkedHashSet<>();
        Map<Long, Set<String>> pairs = new LinkedHashMap<>();
        for (Mon m : concat(gas, water)) {
            if (m.outletId() == null || StrUtil.isBlank(m.pollutantCode())) {
                continue;
            }
            if (seenOutlets.add(m.outletId())) {
                pairs.put(m.outletId(), new LinkedHashSet<>());
            }
            pairs.get(m.outletId()).add(m.pollutantCode());
        }

        List<Map<String, Object>> quota = new ArrayList<>();
        for (Map.Entry<Long, Set<String>> e : pairs.entrySet()) {
            for (String pollutantCode : e.getValue()) {
                MesSetPermitComplianceService.AnnualQuota q =
                        permitComplianceService.checkAnnualQuota(e.getKey(), pollutantCode, periodEnd);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("outletId", e.getKey());
                item.put("outletCode", outletCodes.get(e.getKey()));
                item.put("pollutantCode", pollutantCode);
                item.put("annualLimit", q.annualLimit());
                item.put("used", q.used());
                item.put("ratio", q.ratio());
                item.put("level", q.level());
                quota.add(item);
            }
        }
        return quota;
    }

    private List<Mon> concat(List<Mon> a, List<Mon> b) {
        List<Mon> all = new ArrayList<>(a);
        all.addAll(b);
        return all;
    }

    private boolean isExceed(Mon m) {
        return RESULT_FAIL.equals(m.result());
    }

    private long countExceed(List<Mon> rows) {
        return rows.stream().filter(this::isExceed).count();
    }

    private BigDecimal sumAmount(List<Mon> rows) {
        return rows.stream().map(Mon::emissionAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Long, String> outletCodes() {
        return outletMapper.selectList().stream()
                .filter(o -> o.getId() != null)
                .collect(Collectors.toMap(MesSetEmissionOutletDO::getId,
                        o -> StrUtil.nullToEmpty(o.getOutletCode()), (a, b) -> a, LinkedHashMap::new));
    }

    private List<Mon> gasMons(List<MesSetExhaustGasDO> rows) {
        List<Mon> list = new ArrayList<>(rows.size());
        for (MesSetExhaustGasDO r : rows) {
            list.add(new Mon(r.getRecordNo(), r.getOutletId(), r.getPollutantCode(), r.getConcentration(),
                    r.getEmissionAmount(), r.getLimitValue(), r.getResult(), r.getMonitorTime()));
        }
        return list;
    }

    private List<Mon> waterMons(List<MesSetWastewaterDO> rows) {
        List<Mon> list = new ArrayList<>(rows.size());
        for (MesSetWastewaterDO r : rows) {
            list.add(new Mon(r.getRecordNo(), r.getOutletId(), r.getPollutantCode(), r.getConcentration(),
                    r.getEmissionAmount(), r.getLimitValue(), r.getResult(), r.getMonitorTime()));
        }
        return list;
    }

    /**
     * 汇总期间用到的监测行快照。**两类介质字段一致**，故共用一条，免得报告里废气废水两套写法漂移。
     */
    private record Mon(String recordNo, Long outletId, String pollutantCode, BigDecimal concentration,
                       BigDecimal emissionAmount, BigDecimal limitValue, String result,
                       LocalDateTime monitorTime) {
    }

}
