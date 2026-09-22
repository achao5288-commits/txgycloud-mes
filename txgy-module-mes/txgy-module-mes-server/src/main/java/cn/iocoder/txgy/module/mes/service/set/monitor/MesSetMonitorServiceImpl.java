package cn.iocoder.txgy.module.mes.service.set.monitor;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.util.json.JsonUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.monitor.vo.MesSetMonitorBoardRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.monitor.MesSetMonitorExceedDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas.MesSetExhaustGasMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.monitor.MesSetMonitorExceedMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.tracechain.MesSetTraceChainMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater.MesSetWastewaterMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 安全环保检测-污染源在线监控看板 Service 实现类
 *
 * <p><b>一、为什么看板要写库。</b>读数表里只有逐条实测值，而口径要的是「按小时均值判定、
 * 同一小时只计 1 次」。本项目 XXL-Job 未运行，没有定时任务能做这个汇总，于是把汇总动作挂在
 * 读看板上（{@link #materializeExceed}），幂等 upsert（已派单/已闭环不回退）。量级：10 个排口 ×
 * 8 个因子 × 24 小时 × 30 天 ≈ 6k 行读数，内存聚合一次几十毫秒，够用。
 * ponytail: 读时计算，定时任务跑起来后把这动作挪进 Job 即可。
 *
 * <p><b>二、判定口径全在这里，前端不重算</b>（对接说明 §四）：超标按小时均值、超标倍数后端算、
 * 环比按昨日同期。唯独「是否超期（occurTime+24h）」故意不下发——那是展示逻辑，前端按时间戳自己推。
 *
 * <p><b>三、没数据 ≠ 达标。</b>掉线/数据延迟的排口 value 下发 {@code null} 并被排除在达标率分子分母之外，
 * 宁可让看板少一行，也不给一个「看起来达标」的假数。
 *
 * @author OPENLAB BS
 */
@Slf4j
@Service
@Validated
public class MesSetMonitorServiceImpl implements MesSetMonitorService {

    /**
     * 每次读看板时回算多少天的超标事件。取 30 天是为了让「超标明细」列表有历史可看，
     * 不是判定窗口——判定窗口永远是「今天」与「昨日同期」。
     */
    private static final int MATERIALIZE_DAYS = 30;

    /**
     * CEMS 连续监测：3 小时内有数即在线，24 小时内有过数即「数据延迟」。
     */
    private static final int CEMS_ONLINE_HOURS = 3;
    private static final int CEMS_STALE_HOURS = 24;

    /**
     * 手工监测频次本来就低（1~2 次/天），拿 3 小时当在线线会把所有手工口都判成掉线。
     * 36 小时 ≈ 隔天补录一次仍算在线，一周没数才算掉线。
     */
    private static final int MANUAL_ONLINE_HOURS = 36;
    private static final int MANUAL_STALE_HOURS = 168;

    /**
     * 超标明细一次下发多少条。看板是「今天该干什么」的屏，不是台账。
     */
    private static final int EXCEED_RECORD_LIMIT = 20;

    private static final int TREND_HOURS = 24;

    /**
     * 追溯链业务类型：与 {@code TRACE_TYPE_LEDGER}/{@code TRACE_TYPE_EMERGENCY} 同一惯例，
     * traceType 与 bizType 取同一个短名。
     */
    private static final String TRACE_TYPE_MONITOR = "MONITOR";

    private static final String STATUS_ONLINE = "ONLINE";
    private static final String STATUS_STALE = "STALE";
    private static final String STATUS_OFFLINE = "OFFLINE";

    private static final String FREQ_CEMS = "1 次/小时";
    private static final String FREQ_MANUAL = "1~2 次/天";

    private static final DateTimeFormatter HOUR_LABEL = DateTimeFormatter.ofPattern("HH:00");
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter LAST_REPORT = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    private static final DateTimeFormatter TRACE_CODE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHH");

    /**
     * 时间范围档位：只影响**看多长的历史**（趋势的桶宽、达标率与超标明细的回看窗口），
     * 不影响**判定口径**——超标永远按小时均值判、同一小时永远只计 1 次。
     * 把判定口径跟着档位走，等于同一个排口换个下拉框就换一套标准。
     */
    private record TrendWindow(int buckets, int stepHours, DateTimeFormatter label) {
    }

    private static final Map<String, TrendWindow> RANGES = Map.of(
            "7d", new TrendWindow(7, 24, DAY_LABEL),
            "30d", new TrendWindow(30, 24, DAY_LABEL));
    private static final TrendWindow RANGE_DEFAULT = new TrendWindow(TREND_HOURS, 1, HOUR_LABEL);

    private static TrendWindow windowOf(String range) {
        // Map.of 造出来的是 ImmutableCollections.MapN，它的 get(null) 是**抛 NPE**而不是返回 null，
        // getOrDefault 也救不回来。不带 range 的请求（大屏轮询、首屏）天天都有，必须先挡空值。
        return range == null ? RANGE_DEFAULT : RANGES.getOrDefault(range, RANGE_DEFAULT);
    }

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Resource
    private MesSetMonitorExceedMapper exceedMapper;

    @Resource
    private MesSetEmissionOutletMapper outletMapper;

    @Resource
    private MesSetExhaustGasMapper exhaustGasMapper;

    @Resource
    private MesSetWastewaterMapper wastewaterMapper;

    @Resource
    private MesSetTraceChainMapper traceChainMapper;

    // ==================== 看板 ====================

    @Override
    public MesSetMonitorBoardRespVO getBoard(String range, String factor) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();

        Map<Long, OutletCtx> ctxs = loadOutlets();
        // 一次把 30 天读数取回来：以下所有聚合（趋势/达标率/在线率/完整率/固化超标）都从这一份内存数据推，
        // 免得同一次请求里对同一张表查五遍、五遍之间还可能不一致。
        List<Reading> readings = loadReadings(todayStart.minusDays(MATERIALIZE_DAYS), now.plusHours(1));
        pickPrimaryCode(ctxs, readings);
        List<MesSetMonitorExceedDO> exceeds = materializeExceed(ctxs, readings, now, todayStart);

        LocalDateTime sameTimeYesterday = now.minusDays(1);
        Snapshot today = snapshot(ctxs, readings, exceeds, now);
        Snapshot yesterday = snapshot(ctxs, readings, exceeds, sameTimeYesterday);

        // 档位决定回看窗口；四个 KPI 不跟着走——它们的名字就是「今日」「当前」，
        // 换个档位就把「今日超标次数」变成「近 30 天超标次数」是改口径，不是改视图。
        TrendWindow window = windowOf(range);
        LocalDateTime windowStart = trendStart(now, window);

        MesSetMonitorBoardRespVO vo = new MesSetMonitorBoardRespVO();
        vo.setOutletTotal(ctxs.size());
        vo.setOutletOnline(today.onlineOutlets);
        vo.setExceedCount(today.exceedOutlets);
        vo.setTodayExceedTimes(today.exceedTimes);
        vo.setDataCompleteRate(today.completeRate);
        vo.setOnlineRate(rate(today.onlineOutlets, ctxs.size()));
        vo.setDelta(buildDelta(today, yesterday));
        vo.setMeta(buildMeta(ctxs, window));
        vo.setHourTrend(buildHourTrend(ctxs, readings, now, factor, window));
        vo.setOutletRates(buildOutletRates(ctxs, readings, windowStart, now));
        vo.setMetrics(buildMetrics(ctxs, readings, now));
        vo.setExceedRecords(buildExceedRecords(exceeds, windowStart));
        vo.setOutletLive(buildOutletLive(ctxs, readings, exceeds, now, todayStart));
        return vo;
    }

    // ==================== 指标快照（今日 / 昨日同期共用一套算法） ====================

    /**
     * 四个 KPI 都是「截止某时刻」的量，所以做成时间参数的函数：今天传 now、昨天传 now-1d。
     * <p>
     * 环比之所以要这么算：前端拿今日值和历史全天值硬比，早上 9 点必然显示「大幅改善」——那只是
     * 今天还没过完。后端按昨日**同一时刻**取，比较的才是同一段时长。
     */
    private Snapshot snapshot(Map<Long, OutletCtx> ctxs, List<Reading> readings,
                              List<MesSetMonitorExceedDO> exceeds, LocalDateTime asOf) {
        LocalDateTime dayStart = asOf.toLocalDate().atStartOfDay();
        Snapshot s = new Snapshot();
        s.outletTotal = ctxs.size();

        // 1. 今日超标次数：已固化的事件里落在 [今日0点, asOf) 的条数
        s.exceedTimes = (int) exceeds.stream()
                .filter(e -> !e.getOccurTime().isBefore(dayStart) && e.getOccurTime().isBefore(asOf))
                .count();

        // 2. 当前超标排口数：各排口**最新一条**有效读数越限即算（不论因子）
        Set<Long> over = new HashSet<>();
        for (Map.Entry<Long, OutletCtx> en : ctxs.entrySet()) {
            for (Reading r : latestByOutletAndCode(readings, en.getKey()).values()) {
                if (r.time.isAfter(asOf)) {
                    continue;
                }
                BigDecimal limit = en.getValue().limitOf(r.code);
                if (limit != null && r.value.compareTo(limit) > 0) {
                    over.add(en.getKey());
                    break;
                }
            }
        }
        s.exceedOutlets = over.size();

        // 3. 在线排口数
        int online = 0;
        for (OutletCtx ctx : ctxs.values()) {
            if (STATUS_ONLINE.equals(statusOf(ctx, readings, asOf))) {
                online++;
            }
        }
        s.onlineOutlets = online;

        // 4. 数据完整率：只算 CEMS 排口。手工监测一天 1~2 次，"每小时应有 1 条"对它根本不成立，
        //    掺进来算完整率会得出一个恒低且毫无意义的数字。
        Set<Long> cemsOutlets = ctxs.values().stream().filter(c -> c.cems).map(c -> c.id)
                .collect(Collectors.toSet());
        long hoursElapsed = Duration.between(dayStart, asOf).toHours();
        if (!cemsOutlets.isEmpty() && hoursElapsed > 0) {
            // 分子分母都取整点：asOf 落在半小时上，那半小时不该算"应有"
            Set<String> present = new HashSet<>();
            for (Reading r : readings) {
                if (!cemsOutlets.contains(r.outletId) || r.time.isBefore(dayStart) || !r.time.isBefore(asOf)) {
                    continue;
                }
                present.add(r.outletId + "@" + r.time.toLocalDate() + "T" + r.time.getHour());
            }
            s.completeRate = rate(present.size(), cemsOutlets.size() * hoursElapsed);
        }
        return s;
    }

    private MesSetMonitorBoardRespVO.Delta buildDelta(Snapshot today, Snapshot yesterday) {
        MesSetMonitorBoardRespVO.Delta d = new MesSetMonitorBoardRespVO.Delta();
        d.setExceedCount(today.exceedOutlets - yesterday.exceedOutlets);
        d.setTodayExceedTimes(today.exceedTimes - yesterday.exceedTimes);
        d.setDataCompleteRate(diff(today.completeRate, yesterday.completeRate));
        d.setOnlineRate(diff(rate(today.onlineOutlets, today.outletTotal), rate(yesterday.onlineOutlets, yesterday.outletTotal)));
        return d;
    }

    // ==================== 超标事件固化 ====================

    /**
     * 把窗口内的读数按「排放口 + 因子 + 小时」聚成小时均值，越限的 upsert 成事件。
     *
     * <p>幂等靠 (outletId, pollutantCode, occurTime) 三元组：库里已有的事件只更新
     * 均值/限值/倍数/时长四个**事实列**，绝不碰 handle_status / handler / dispatch_time——
     * 否则看板每刷一次，处置中的单子就自己回到待处置，闭环的单子自己复活。
     *
     * @return 窗口内的事件（含本次新插入的，带 id），新的在前
     */
    private List<MesSetMonitorExceedDO> materializeExceed(Map<Long, OutletCtx> ctxs, List<Reading> readings,
                                                          LocalDateTime now, LocalDateTime todayStart) {
        LocalDateTime windowStart = todayStart.minusDays(MATERIALIZE_DAYS);
        // 只固化已经过完的小时：当前这一小时还没走完，此刻的均值不是小时均值
        LocalDateTime lastFullHour = now.truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        Map<Key, List<BigDecimal>> buckets = new LinkedHashMap<>();
        for (Reading r : readings) {
            OutletCtx ctx = ctxs.get(r.outletId);
            if (ctx == null || ctx.limitOf(r.code) == null) {
                continue;   // 没配限值的因子不可判定，不进事件（也不当作达标）
            }
            LocalDateTime hour = r.time.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
            if (hour.isBefore(windowStart) || !hour.isBefore(lastFullHour)) {
                continue;
            }
            buckets.computeIfAbsent(new Key(r.outletId, r.code, hour), k -> new ArrayList<>()).add(r.value);
        }

        // 越限的小时集合，供算连续时长用
        Map<Key, BigDecimal> meanOfExceed = new LinkedHashMap<>();
        Set<String> exceedHourSet = new HashSet<>();
        for (Map.Entry<Key, List<BigDecimal>> en : buckets.entrySet()) {
            BigDecimal mean = mean(en.getValue());
            BigDecimal limit = ctxs.get(en.getKey().outletId).limitOf(en.getKey().code);
            if (mean.compareTo(limit) > 0) {
                meanOfExceed.put(en.getKey(), mean);
                exceedHourSet.add(en.getKey().outletId + "@" + en.getKey().code + "@" + en.getKey().hour);
            }
        }
        // 库里已有的：三元组建索引（一次取回，内存比对——逐条 selectOne 会变成几百次往返）
        List<MesSetMonitorExceedDO> existing = exceedMapper.selectByOccurRange(windowStart, now.plusHours(1));
        Map<String, MesSetMonitorExceedDO> index = new HashMap<>();
        for (MesSetMonitorExceedDO e : existing) {
            index.put(e.getOutletId() + "@" + e.getPollutantCode() + "@" + e.getOccurTime(), e);
        }
        if (meanOfExceed.isEmpty()) {
            return sortDesc(index.values());
        }

        for (Map.Entry<Key, BigDecimal> en : meanOfExceed.entrySet()) {
            Key k = en.getKey();
            OutletCtx ctx = ctxs.get(k.outletId);
            BigDecimal mean = en.getValue();
            BigDecimal limit = ctx.limitOf(k.code);
            BigDecimal multiple = mean.divide(limit, 3, RoundingMode.HALF_UP);
            int duration = consecutiveHours(exceedHourSet, k) * 60;

            MesSetMonitorExceedDO old = index.get(k.outletId + "@" + k.code + "@" + k.hour);
            if (old == null) {
                MesSetMonitorExceedDO fresh = MesSetMonitorExceedDO.builder()
                        .outletId(k.outletId)
                        .outletCode(ctx.outlet.getOutletCode())
                        .outletName(ctx.outlet.getOutletName())
                        .pollutantCode(k.code)
                        .pollutantName(ctx.nameOf(k.code))
                        .monitorValue(mean)
                        .limitValue(limit)
                        .unit(ctx.unitOf(k.code))
                        .multiple(multiple)
                        .occurTime(k.hour)
                        .durationMin(duration)
                        .handleStatus(MesSetMonitorExceedDO.STATUS_PENDING)
                        .handler("")
                        .build();
                exceedMapper.insert(fresh);
                index.put(k.outletId + "@" + k.code + "@" + k.hour, fresh);
            } else {
                // 只更新事实列。builder 出来的 DO 里其余字段是 null，MyBatis-Plus 默认
                // 不更新 null 字段，正好把处置状态原样留着。
                exceedMapper.updateById(MesSetMonitorExceedDO.builder()
                        .id(old.getId())
                        .monitorValue(mean)
                        .limitValue(limit)
                        .unit(ctx.unitOf(k.code))
                        .multiple(multiple)
                        .durationMin(duration)
                        .build());
                old.setMonitorValue(mean);
                old.setLimitValue(limit);
                old.setMultiple(multiple);
                old.setDurationMin(duration);
            }
        }

        return sortDesc(index.values());
    }

    private List<MesSetMonitorExceedDO> sortDesc(Collection<MesSetMonitorExceedDO> rows) {
        return rows.stream()
                .sorted(Comparator.comparing(MesSetMonitorExceedDO::getOccurTime).reversed()
                        .thenComparing(MesSetMonitorExceedDO::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 主因子 = 窗口内读数最多的那个因子。看板的「实时状态」一行一个排口，
     * 而一个排口可能同时测颗粒物/SO2/VOCs，必须有确定的选法：取测点最密的那条，
     * 它最能代表这个口开没开、通不通。库里没有任何读数时保留许可配置里的顺序。
     */
    private void pickPrimaryCode(Map<Long, OutletCtx> ctxs, List<Reading> readings) {
        Map<Long, Map<String, Long>> counts = new HashMap<>();
        for (Reading r : readings) {
            counts.computeIfAbsent(r.outletId, k -> new HashMap<>())
                    .merge(r.code, 1L, Long::sum);
        }
        for (OutletCtx ctx : ctxs.values()) {
            Map<String, Long> c = counts.get(ctx.id);
            if (c == null || c.isEmpty()) {
                continue;
            }
            c.entrySet().stream().max(Map.Entry.comparingByValue())
                    .ifPresent(e -> ctx.primaryCode = e.getKey());
        }
    }

    /**
     * 从该小时起，同排口同因子连续越限了几个小时。
     * <p>
     * 事件是按小时逐条落的（「同一小时计 1 次」是防重复计数，不是把连续几小时并成一条），
     * 所以 durationMin 表示的是「从这次超标算起，往后持续了多久」——一条 3 小时的连续超标
     * 会落 3 条事件，时长分别是 180 / 120 / 60 分钟。
     */
    private int consecutiveHours(Set<String> exceedHourSet, Key k) {
        int n = 0;
        LocalDateTime h = k.hour;
        while (exceedHourSet.contains(k.outletId + "@" + k.code + "@" + h)) {
            n++;
            h = h.plusHours(1);
        }
        return n;
    }

    // ==================== 趋势 / 达标率 / 因子值 / 实时状态 ====================

    /**
     * 窗口起点。按小时看就对齐到整点，按天看就对齐到自然日——「09-01」应该指那一整天，
     * 而不是「9/1 10:00 到 9/2 10:00」这种滚动 24 小时，那样没人能拿它跟台账对。
     */
    private static LocalDateTime trendStart(LocalDateTime now, TrendWindow w) {
        return w.stepHours() >= 24
                ? now.toLocalDate().plusDays(1).atStartOfDay()
                        .minusDays((long) w.buckets() * w.stepHours() / 24)
                : now.truncatedTo(ChronoUnit.HOURS).plusHours(1).minusHours(w.buckets());
    }

    /**
     * 浓度趋势：一个因子一条线，值为**全厂该因子在桶内的均值**。桶宽随档位变
     * （近 24 小时 = 24 个整点桶，近 7/30 天 = 7/30 个自然日桶）。
     * <p>
     * 因子怎么选：请求指定优先，否则取窗口内读数最多的那个——一条线上不能混两个因子，
     * 而看板没有「当前因子」这个概念之前，得有个确定的默认。
     * 限值取各排口该因子的**最小值**（最严口径），并在 meta.caliber 里说明。
     */
    private List<MesSetMonitorBoardRespVO.HourTrend> buildHourTrend(Map<Long, OutletCtx> ctxs,
                                                                    List<Reading> readings,
                                                                    LocalDateTime now, String factor,
                                                                    TrendWindow w) {
        int step = w.stepHours();
        LocalDateTime start = trendStart(now, w);
        // 按小时看时 end 是下一个整点（当前这个不完整的小时不算已经过完，但要看得到）；
        // 按天看时 end 是明天 0 点，最后一桶是今天——今天没走完，趋势上照样画出来
        LocalDateTime end = step >= 24 ? start.plusDays(w.buckets()) : start.plusHours(w.buckets());
        List<Reading> win = readings.stream()
                .filter(r -> !r.time.isBefore(start) && r.time.isBefore(end))
                .collect(Collectors.toList());
        if (win.isEmpty()) {
            return List.of();
        }

        String code = StrUtil.isNotBlank(factor) ? factor
                : win.stream().collect(Collectors.groupingBy(r -> r.code, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        if (code == null) {
            return List.of();
        }

        // 桶键用下标而不是时间：下标算出来就一定落在 [0, buckets) 里，
        // 用时间当键还得处理「读数落在 end 之后」这种边界，多一个静默丢数的口子
        Map<Long, List<BigDecimal>> byBucket = new LinkedHashMap<>();
        for (Reading r : win) {
            if (!code.equals(r.code)) {
                continue;
            }
            long idx = Duration.between(start, r.time.truncatedTo(ChronoUnit.HOURS)).toHours() / step;
            if (idx >= 0 && idx < w.buckets()) {
                byBucket.computeIfAbsent(idx, k -> new ArrayList<>()).add(r.value);
            }
        }

        BigDecimal limit = ctxs.values().stream()
                .map(c -> c.limitOf(code)).filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);

        List<MesSetMonitorBoardRespVO.HourTrend> trend = new ArrayList<>();
        for (int i = 0; i < w.buckets(); i++) {
            LocalDateTime bucket = start.plusHours((long) i * step);
            MesSetMonitorBoardRespVO.HourTrend point = new MesSetMonitorBoardRespVO.HourTrend();
            point.setHour(bucket.format(w.label()));
            List<BigDecimal> vals = byBucket.get((long) i);
            // 该桶没有读数就下发 null：前端据此断线，画成 0 等于凭空报了一个「浓度为零」
            point.setValue(vals == null || vals.isEmpty() ? null : mean(vals));
            point.setLimit(limit);
            trend.add(point);
        }
        return trend;
    }

    /**
     * 各排口达标率：窗口内已过完的小时里，小时均值不越限的比例。
     * <p>
     * 桶永远是**小时**，不随档位变粗——「超标按小时均值判定、同一小时只计 1 次」是判定口径，
     * 换成按天聚合会让同一个排口换个下拉框就换一套标准。
     * <p>
     * **掉线/数据延迟的排口整行不出现**——「没数据」和「达标」是两件事，
     * 把没数据的排口按 0 次超标算进分母，等于替它宣布达标。
     */
    private List<MesSetMonitorBoardRespVO.OutletRate> buildOutletRates(Map<Long, OutletCtx> ctxs,
                                                                      List<Reading> readings,
                                                                      LocalDateTime windowStart,
                                                                      LocalDateTime now) {
        LocalDateTime lastFullHour = now.truncatedTo(ChronoUnit.HOURS);
        List<MesSetMonitorBoardRespVO.OutletRate> list = new ArrayList<>();
        for (OutletCtx ctx : ctxs.values()) {
            // 只考核 ONLINE：数据延迟的排口今天的数据本身就是残缺的，
            // 拿残缺的一天去算达标率，算出来的数是"采集质量"而不是"排放合规"
            if (!STATUS_ONLINE.equals(statusOf(ctx, readings, now))) {
                continue;
            }
            Map<Key, List<BigDecimal>> buckets = new LinkedHashMap<>();
            for (Reading r : readings) {
                if (!Objects.equals(r.outletId, ctx.id) || ctx.limitOf(r.code) == null) {
                    continue;
                }
                LocalDateTime hour = r.time.truncatedTo(ChronoUnit.HOURS);
                if (hour.isBefore(windowStart) || !hour.isBefore(lastFullHour)) {
                    continue;
                }
                buckets.computeIfAbsent(new Key(ctx.id, r.code, hour), k -> new ArrayList<>()).add(r.value);
            }
            if (buckets.isEmpty()) {
                continue;
            }
            int pass = 0;
            int fail = 0;
            for (Map.Entry<Key, List<BigDecimal>> en : buckets.entrySet()) {
                BigDecimal limit = ctx.limitOf(en.getKey().code);
                if (mean(en.getValue()).compareTo(limit) > 0) {
                    fail++;
                } else {
                    pass++;
                }
            }
            MesSetMonitorBoardRespVO.OutletRate vo = new MesSetMonitorBoardRespVO.OutletRate();
            vo.setOutletNo(ctx.outlet.getOutletCode());
            vo.setOutletName(ctx.outlet.getOutletName());
            vo.setPassRate(rate(pass, pass + fail));
            vo.setExceedCount(fail);
            list.add(vo);
        }
        // 达标率低的排前面，另外排除掉线排口之后还剩几个，就是这一屏实际能考核的排口数
        list.sort(Comparator.comparing(MesSetMonitorBoardRespVO.OutletRate::getPassRate,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    /**
     * 因子实时值：每个因子取「最新有效读数里负荷率最高的那个排口」的值。
     * <p>
     * 不是平均值——平均值会把一个已经顶到限值 108% 的排口稀释成一个看着没事的数，
     * 而这块卡片的用途恰恰是「哪个因子正在逼近红线」。
     */
    private List<MesSetMonitorBoardRespVO.Metric> buildMetrics(Map<Long, OutletCtx> ctxs,
                                                               List<Reading> readings, LocalDateTime now) {
        Map<String, Reading> worst = new LinkedHashMap<>();
        for (OutletCtx ctx : ctxs.values()) {
            if (STATUS_OFFLINE.equals(statusOf(ctx, readings, now))) {
                continue;
            }
            for (Reading r : latestByOutletAndCode(readings, ctx.id).values()) {
                if (ctx.limitOf(r.code) == null) {
                    continue;
                }
                Reading cur = worst.get(r.code);
                if (cur == null || load(r, ctx).compareTo(load(cur, ctxs.get(cur.outletId))) > 0) {
                    worst.put(r.code, r);
                }
            }
        }
        List<MesSetMonitorBoardRespVO.Metric> list = new ArrayList<>();
        for (Reading r : worst.values()) {
            MesSetMonitorBoardRespVO.Metric m = new MesSetMonitorBoardRespVO.Metric();
            m.setPollutantName(ctxs.get(r.outletId).nameOf(r.code));
            m.setValue(r.value);
            m.setLimit(ctxs.get(r.outletId).limitOf(r.code));
            list.add(m);
        }
        list.sort(Comparator.comparing(MesSetMonitorBoardRespVO.Metric::getValue,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    /**
     * 排放口实时状态：一行一个排口，展示它的**主因子**（窗口内读数最多的那个）。
     * <p>
     * 掉线时 value 下发 null 而不是 0——0 是一个合法的实测值，用它表示"没测到"，
     * 前端既画不出断线，也没法把它排除在达标率之外。
     */
    private List<MesSetMonitorBoardRespVO.OutletLive> buildOutletLive(Map<Long, OutletCtx> ctxs,
                                                                     List<Reading> readings,
                                                                     List<MesSetMonitorExceedDO> exceeds,
                                                                     LocalDateTime now, LocalDateTime dayStart) {
        List<MesSetMonitorBoardRespVO.OutletLive> list = new ArrayList<>();
        for (OutletCtx ctx : ctxs.values()) {
            String code = ctx.primaryCode;
            MesSetMonitorBoardRespVO.OutletLive vo = new MesSetMonitorBoardRespVO.OutletLive();
            vo.setOutletNo(ctx.outlet.getOutletCode());
            vo.setOutletName(ctx.outlet.getOutletName());
            vo.setPollutantName(ctx.nameOf(code));
            vo.setLimit(ctx.limitOf(code));
            vo.setTodayExceed((int) exceeds.stream()
                    .filter(e -> Objects.equals(e.getOutletId(), ctx.id) && !e.getOccurTime().isBefore(dayStart))
                    .count());

            Reading latest = latestByOutletAndCode(readings, ctx.id).get(code);
            String status = statusOf(ctx, readings, now);
            vo.setStatus(status);
            if (latest == null || STATUS_OFFLINE.equals(status)) {
                vo.setValue(null);
                vo.setLastReport(latest == null ? "-" : latest.time.format(LAST_REPORT));
            } else {
                vo.setValue(latest.value);
                vo.setLastReport(latest.time.format(LAST_REPORT));
            }
            list.add(vo);
        }
        // 出问题的排最前：掉线 → 数据延迟 → 超标 → 其余
        list.sort(Comparator.comparingInt((MesSetMonitorBoardRespVO.OutletLive v) -> liveOrder(v))
                .thenComparing(MesSetMonitorBoardRespVO.OutletLive::getOutletNo,
                        Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    private int liveOrder(MesSetMonitorBoardRespVO.OutletLive v) {
        if (STATUS_OFFLINE.equals(v.getStatus())) {
            return 0;
        }
        if (STATUS_STALE.equals(v.getStatus())) {
            return 1;
        }
        if (v.getTodayExceed() != null && v.getTodayExceed() > 0) {
            return 2;
        }
        return 3;
    }

    // ==================== 超标明细 ====================

    private List<MesSetMonitorBoardRespVO.ExceedRecord> buildExceedRecords(List<MesSetMonitorExceedDO> exceeds,
                                                                          LocalDateTime windowStart) {
        List<MesSetMonitorExceedDO> top = exceeds.stream()
                .filter(e -> !e.getOccurTime().isBefore(windowStart))
                .limit(EXCEED_RECORD_LIMIT).collect(Collectors.toList());
        if (top.isEmpty()) {
            return List.of();
        }
        // 处置轨迹一次查完：逐条查会变成 N+1
        Map<String, List<MesSetTraceChainDO>> traces = traceChainMapper
                .selectListByBizNos(top.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList()))
                .stream().filter(t -> TRACE_TYPE_MONITOR.equals(t.getBizType()))
                .collect(Collectors.groupingBy(MesSetTraceChainDO::getBizNo, LinkedHashMap::new, Collectors.toList()));

        List<MesSetMonitorBoardRespVO.ExceedRecord> list = new ArrayList<>();
        for (MesSetMonitorExceedDO e : top) {
            MesSetMonitorBoardRespVO.ExceedRecord vo = new MesSetMonitorBoardRespVO.ExceedRecord();
            vo.setId(e.getId());
            vo.setOutletNo(e.getOutletCode());
            vo.setOutletName(e.getOutletName());
            vo.setPollutantName(e.getPollutantName());
            vo.setValue(e.getMonitorValue());
            vo.setLimit(e.getLimitValue());
            vo.setMultiple(e.getMultiple());
            vo.setOccurTime(e.getOccurTime());
            vo.setDurationMin(e.getDurationMin());
            vo.setHandleStatus(e.getHandleStatus());
            // 未派单时下发 "-"：前端不猜"是不是我"，也让"处置人为空"这件事看得见
            vo.setHandler(StrUtil.isBlank(e.getHandler()) ? "-" : e.getHandler());
            List<MesSetMonitorBoardRespVO.ExceedEvent> events = new ArrayList<>();
            for (MesSetTraceChainDO t : traces.getOrDefault(String.valueOf(e.getId()), List.of())) {
                MesSetMonitorBoardRespVO.ExceedEvent ev = new MesSetMonitorBoardRespVO.ExceedEvent();
                ev.setT(t.getNodeTime());
                ev.setKind(StrUtil.contains(t.getNodeAction(), "闭环") ? "CLOSE" : "DISPATCH");
                ev.setTitle(t.getNodeAction());
                ev.setDesc(t.getExtra());
                events.add(ev);
            }
            vo.setEvents(events);
            list.add(vo);
        }
        return list;
    }

    // ==================== 派单 / 闭环 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispatchExceed(Long id) {
        MesSetMonitorExceedDO e = exceedMapper.selectById(id);
        if (e == null) {
            throw exception(SET_MONITOR_EXCEED_NOT_EXISTS);
        }
        if (MesSetMonitorExceedDO.STATUS_CLOSED.equals(e.getHandleStatus())) {
            throw exception(SET_MONITOR_EXCEED_STATUS_INVALID);
        }
        if (MesSetMonitorExceedDO.STATUS_PROCESSING.equals(e.getHandleStatus())) {
            throw exception(SET_MONITOR_EXCEED_ALREADY_DISPATCHED);
        }
        String who = currentUserName();
        exceedMapper.updateById(MesSetMonitorExceedDO.builder()
                .id(id)
                .handleStatus(MesSetMonitorExceedDO.STATUS_PROCESSING)
                .handler(who)
                .dispatchTime(LocalDateTime.now())
                .build());
        writeTrace(e, "派单处置", who, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeExceed(Long id) {
        MesSetMonitorExceedDO e = exceedMapper.selectById(id);
        if (e == null) {
            throw exception(SET_MONITOR_EXCEED_NOT_EXISTS);
        }
        if (MesSetMonitorExceedDO.STATUS_CLOSED.equals(e.getHandleStatus())) {
            throw exception(SET_MONITOR_EXCEED_ALREADY_CLOSED);
        }
        String who = currentUserName();
        // 没派单直接闭环是允许的（现场先处理完再补录是常态），但处置人得落到人头上：
        // 已派单就保留原处置人，闭环人记在追溯链里，不覆盖派单人。
        String handler = StrUtil.isBlank(e.getHandler()) ? who : e.getHandler();
        exceedMapper.updateById(MesSetMonitorExceedDO.builder()
                .id(id)
                .handleStatus(MesSetMonitorExceedDO.STATUS_CLOSED)
                .handler(handler)
                .closedTime(LocalDateTime.now())
                .build());
        writeTrace(e, "闭环确认", who, handler);
    }

    private void writeTrace(MesSetMonitorExceedDO e, String action, String who, String handler) {
        String extra = handler == null
                ? StrUtil.format("{} 由 {} 派单", e.getOutletName(), who)
                : StrUtil.format("{} 处置人 {}，闭环确认人 {}", e.getOutletName(), handler, who);
        traceChainMapper.insert(MesSetTraceChainDO.builder()
                .traceCode(e.getOutletCode() + "-" + e.getOccurTime().format(TRACE_CODE_TIME))
                .traceType(TRACE_TYPE_MONITOR)
                .bizType(TRACE_TYPE_MONITOR)
                .bizNo(String.valueOf(e.getId()))
                .nodeStage(TRACE_TYPE_MONITOR)
                .nodeAction(action)
                .batchStatus(e.getHandleStatus())
                .operatorName(who)
                .nodeTime(LocalDateTime.now())
                .extra(StrUtil.maxLength(extra, 250))
                .build());
    }

    // ==================== 元信息 ====================

    private MesSetMonitorBoardRespVO.Meta buildMeta(Map<Long, OutletCtx> ctxs, TrendWindow w) {
        long cems = ctxs.values().stream().filter(c -> c.cems).count();
        long manual = ctxs.size() - cems;
        MesSetMonitorBoardRespVO.Meta meta = new MesSetMonitorBoardRespVO.Meta();
        meta.setSource("CEMS 在线监测" + (manual > 0 ? " + 手工补录" : ""));
        meta.setFreq(StrUtil.format("CEMS {} 口 {} · 手工 {} 口 {}", cems, FREQ_CEMS, manual, FREQ_MANUAL));
        // 口径里要写清档位只改窗口、不改判定——否则"近 30 天"这个下拉框会让人以为标准也放宽了
        meta.setCaliber(StrUtil.format("判定窗口 {}，口径不随档位变；", w.stepHours() >= 24
                        ? "近 " + w.buckets() + " 天（趋势按自然日聚合）" : "近 24 小时（趋势按整点聚合）")
                + "按小时均值判定，同一小时连续超标计 1 次；超标倍数 = 小时均值 / 限值；"
                // 完整率必须写明是 CEMS 口径：手工口一天 1~2 条，把它们拉进"每小时应有 1 条"的
                // 分母里会得出一个恒低且毫无意义的数——不写清楚，这个数就会被当成排口的问题
                + "数据完整率仅按 CEMS 口径（手工监测频次本就是 1~2 次/天，不进这个分母），< 90% 视为采集缺失；"
                + "达标率与完整率均排除掉线/数据延迟的排口；环比按昨日同期。"
                + "趋势线取全厂该因子均值，限值取各排口最小值（最严口径）。");
        return meta;
    }

    // ==================== 读数装载与索引 ====================

    /** 废气 + 废水合成一条时间轴：两张表的字段除样本号外完全同构，各自聚合等于把口径写两遍 */
    private List<Reading> loadReadings(LocalDateTime start, LocalDateTime end) {
        List<Reading> all = new ArrayList<>();
        for (MesSetExhaustGasDO r : exhaustGasMapper.selectByMonitorRange(start, end)) {
            all.add(new Reading(r.getOutletId(), r.getPollutantCode(), r.getConcentration(), r.getMonitorTime()));
        }
        for (MesSetWastewaterDO r : wastewaterMapper.selectByMonitorRange(start, end)) {
            all.add(new Reading(r.getOutletId(), r.getPollutantCode(), r.getConcentration(), r.getMonitorTime()));
        }
        all.removeIf(r -> r.outletId == null || StrUtil.isBlank(r.code) || r.value == null || r.time == null);
        all.sort(Comparator.comparing(r -> r.time));
        return all;
    }

    private Map<Long, OutletCtx> loadOutlets() {
        Map<Long, OutletCtx> map = new LinkedHashMap<>();
        List<MesSetEmissionOutletDO> outlets = outletMapper.selectList();
        for (MesSetEmissionOutletDO o : outlets) {
            // 停用的排口不进看板：它已经不该有数了，留在板上只会显示成"掉线"
            if (StrUtil.isNotBlank(o.getStatus()) && !"ACTIVE".equals(o.getStatus())) {
                continue;
            }
            map.put(o.getId(), new OutletCtx(o));
        }
        return map;
    }

    /** 每个排口每个因子的最新一条读数 */
    private Map<String, Reading> latestByOutletAndCode(List<Reading> readings, Long outletId) {
        Map<String, Reading> map = new HashMap<>();
        for (Reading r : readings) {
            if (!Objects.equals(r.outletId, outletId)) {
                continue;
            }
            Reading cur = map.get(r.code);
            if (cur == null || r.time.isAfter(cur.time)) {
                map.put(r.code, r);
            }
        }
        return map;
    }

    /**
     * 排口的在线状态：按监测方式取不同阈值。没有任何读数的排口直接 OFFLINE——
     * 「从来没上报过」比「很久没上报」更严重，不该因为"没有时间戳可比"而漏掉。
     */
    private String statusOf(OutletCtx ctx, List<Reading> readings, LocalDateTime asOf) {
        LocalDateTime last = null;
        for (Reading r : readings) {
            if (!Objects.equals(r.outletId, ctx.id) || r.time.isAfter(asOf)) {
                continue;
            }
            if (last == null || r.time.isAfter(last)) {
                last = r.time;
            }
        }
        if (last == null) {
            return STATUS_OFFLINE;
        }
        long hours = Duration.between(last, asOf).toHours();
        long online = ctx.cems ? CEMS_ONLINE_HOURS : MANUAL_ONLINE_HOURS;
        long stale = ctx.cems ? CEMS_STALE_HOURS : MANUAL_STALE_HOURS;
        if (hours <= online) {
            return STATUS_ONLINE;
        }
        return hours <= stale ? STATUS_STALE : STATUS_OFFLINE;
    }

    // ==================== 小工具 ====================

    private BigDecimal load(Reading r, OutletCtx ctx) {
        BigDecimal limit = ctx == null ? null : ctx.limitOf(r.code);
        return limit == null || limit.signum() == 0 ? BigDecimal.ZERO
                : r.value.multiply(HUNDRED).divide(limit, 1, RoundingMode.HALF_UP);
    }

    private static BigDecimal mean(List<BigDecimal> values) {
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 3, RoundingMode.HALF_UP);
    }

    /** 百分率，一位小数；分母为 0 返回 null —— 不返回 0，前端要把"没得算"和"算出来是 0"分开显示 */
    private static BigDecimal rate(long numerator, long denominator) {
        return denominator <= 0 ? null
                : BigDecimal.valueOf(numerator).multiply(HUNDRED)
                .divide(BigDecimal.valueOf(denominator), 1, RoundingMode.HALF_UP);
    }

    private static BigDecimal diff(BigDecimal a, BigDecimal b) {
        return a == null || b == null ? null : a.subtract(b).setScale(1, RoundingMode.HALF_UP);
    }

    private static String currentUserName() {
        String nickname = getLoginUserNickname();
        return StrUtil.isBlank(nickname) ? String.valueOf(getLoginUserId()) : nickname;
    }

    // ==================== 内部结构 ====================

    /** (排放口, 因子, 小时) 三元组 */
    private record Key(Long outletId, String code, LocalDateTime hour) {
    }

    /** 一条读数（废气/废水同构后的形状） */
    private record Reading(Long outletId, String code, BigDecimal value, LocalDateTime time) {
    }

    /** KPI 快照：同一套算法喂不同时刻，就是"今日"与"昨日同期" */
    private static class Snapshot {
        int outletTotal;
        int onlineOutlets;
        int exceedOutlets;
        int exceedTimes;
        BigDecimal completeRate;
    }

    /**
     * 排放口的上板上下文：限值/因子名/单位来自排污许可配置，主因子按读数多少动态定。
     */
    private static class OutletCtx {
        final MesSetEmissionOutletDO outlet;
        final Long id;
        final boolean cems;
        final Map<String, LimitItem> limits = new HashMap<>();
        String primaryCode;

        OutletCtx(MesSetEmissionOutletDO outlet) {
            this.outlet = outlet;
            this.id = outlet.getId();
            this.cems = "CEMS".equals(outlet.getMonitorMethod());
            if (StrUtil.isNotBlank(outlet.getPermitLimits())) {
                try {
                    for (LimitItem item : JsonUtils.parseArray(outlet.getPermitLimits(), LimitItem.class)) {
                        if (StrUtil.isNotBlank(item.getPollutantCode())) {
                            limits.put(item.getPollutantCode(), item);
                        }
                    }
                } catch (Exception e) {
                    // 解析失败按"未配置"处理：配置期已由 validateOutletLimits 拦下格式错误，
                    // 能走到这里是直接改库或历史脏数据，不该让整块看板 500
                    log.error("[OutletCtx][排放口 {} 许可限值 JSON 解析失败，该口不做限值判定, config={}]",
                            outlet.getId(), outlet.getPermitLimits(), e);
                }
            }
            // 主因子兜底：许可里配了限值的第一个（顺序稳定，不随查询计划变）；
            // 有读数时会被 pickPrimaryCode 换成读数最多的那个
            this.primaryCode = limits.keySet().stream().findFirst().orElse(null);
        }

        BigDecimal limitOf(String code) {
            LimitItem item = limits.get(code);
            return item == null ? null : item.getLimitValue();
        }

        String nameOf(String code) {
            LimitItem item = code == null ? null : limits.get(code);
            return item == null || StrUtil.isBlank(item.getPollutantName()) ? code : item.getPollutantName();
        }

        String unitOf(String code) {
            LimitItem item = limits.get(code);
            return item == null || StrUtil.isBlank(item.getUnit()) ? "" : item.getUnit();
        }
    }

    /**
     * 排放口 permit_limits JSON 条目（格式见 MesSetPermitComplianceService）。
     */
    @Data
    public static class LimitItem {
        private String pollutantCode;
        private String pollutantName;
        private BigDecimal limitValue;
        private String unit;
    }

}
