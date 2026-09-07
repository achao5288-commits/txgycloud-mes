package cn.iocoder.txgy.module.mes.service.safetyenvstatistics;

import cn.iocoder.txgy.module.mes.controller.admin.safetyenvstatistics.vo.MesSafetyEnvStatisticsRespVO;
import cn.iocoder.txgy.module.mes.dal.mysql.safetyenvstatistics.MesSafetyEnvStatisticsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MES 安全环保检测统计 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
public class MesSafetyEnvStatisticsServiceImpl implements MesSafetyEnvStatisticsService {

    /** 各检测记录表类型 -> 展示名称 */
    private static final Map<String, String> TYPE_NAMES = createTypeNames();

    private static Map<String, String> createTypeNames() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("gas", "气体检测");
        map.put("dust", "粉尘检测");
        map.put("noise", "噪声检测");
        map.put("wastewater", "废水检测");
        map.put("exhaust", "废气检测");
        map.put("electrical", "电气检测");
        map.put("fire", "消防检查");
        map.put("chemical", "危化品检查");
        map.put("ppe", "防护用品检查");
        map.put("occupational", "职业危害检测");
        map.put("pressure", "压力容器检测");
        return map;
    }

    @Resource
    private MesSafetyEnvStatisticsMapper safetyEnvStatisticsMapper;

    @Override
    public MesSafetyEnvStatisticsRespVO getMesSafetyEnvStatistics() {
        MesSafetyEnvStatisticsRespVO vo = new MesSafetyEnvStatisticsRespVO();

        // 1. 台账类计数
        Map<String, Object> ledger = safetyEnvStatisticsMapper.selectLedgerSummary();
        vo.setStandardCount(toInteger(ledger.get("standardCount")));
        vo.setPlanCount(toInteger(ledger.get("planCount")));
        vo.setPlanActiveCount(toInteger(ledger.get("planActiveCount")));
        vo.setOutletCount(toInteger(ledger.get("outletCount")));
        vo.setPermitCount(toInteger(ledger.get("permitCount")));
        vo.setWasteCount(toInteger(ledger.get("wasteCount")));
        vo.setEnvReportCount(toInteger(ledger.get("envReportCount")));
        vo.setCarbonCount(toInteger(ledger.get("carbonCount")));
        vo.setCarbonEmissionSum(toBigDecimal(ledger.get("carbonEmissionSum")));

        // 2. 各检测类型统计 + 汇总
        List<Map<String, Object>> typeRows = safetyEnvStatisticsMapper.selectRecordCountGroupByType();
        int recordTotal = 0;
        int pass = 0;
        int fail = 0;
        List<MesSafetyEnvStatisticsRespVO.RecordTypeVO> recordStats = new ArrayList<>();
        for (Map<String, Object> row : typeRows) {
            String type = (String) row.get("type");
            int total = toInteger(row.get("total"));
            int failCount = toInteger(row.get("fail"));
            recordTotal += total;
            pass += total - failCount;
            fail += failCount;
            MesSafetyEnvStatisticsRespVO.RecordTypeVO recordVO = new MesSafetyEnvStatisticsRespVO.RecordTypeVO();
            recordVO.setType(type);
            recordVO.setTypeName(TYPE_NAMES.getOrDefault(type, type));
            recordVO.setTotal(total);
            recordVO.setFail(failCount);
            recordStats.add(recordVO);
        }
        vo.setRecordTotalCount(recordTotal);
        vo.setPassCount(pass);
        vo.setFailCount(fail);
        vo.setPassRate(recordTotal == 0 ? 0 : (int) Math.round(pass * 100.0 / recordTotal));
        vo.setRecordStats(recordStats);

        // 3. 近 6 个月趋势（无数据月份补 0）
        YearMonth beginMonth = YearMonth.now().minusMonths(5);
        List<Map<String, Object>> trendRows = safetyEnvStatisticsMapper.selectMonthTrend(
                LocalDateTime.of(beginMonth.atDay(1), java.time.LocalTime.MIN));
        Map<String, Integer> trendMap = new HashMap<>();
        for (Map<String, Object> row : trendRows) {
            trendMap.put((String) row.get("month"), toInteger(row.get("count")));
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<MesSafetyEnvStatisticsRespVO.MonthTrendVO> monthTrend = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth month = beginMonth.plusMonths(i);
            MesSafetyEnvStatisticsRespVO.MonthTrendVO trendVO = new MesSafetyEnvStatisticsRespVO.MonthTrendVO();
            trendVO.setMonth(month.format(fmt));
            trendVO.setCount(trendMap.getOrDefault(trendVO.getMonth(), 0));
            monthTrend.add(trendVO);
        }
        vo.setMonthTrend(monthTrend);
        vo.setMonthCount(monthTrend.get(monthTrend.size() - 1).getCount());
        return vo;
    }

    private int toInteger(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return new BigDecimal(obj.toString()).intValue();
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        return new BigDecimal(obj.toString()).setScale(2, RoundingMode.HALF_UP);
    }

}
