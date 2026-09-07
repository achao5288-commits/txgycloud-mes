package cn.iocoder.txgy.module.mes.dal.mysql.safetyenvstatistics;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MES 安全环保检测统计 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSafetyEnvStatisticsMapper {

    /**
     * 按类型统计各检测记录表的总数与异常数
     *
     * @return [{ "type": "gas", "total": 5, "fail": 1 }, ...]
     */
    List<Map<String, Object>> selectRecordCountGroupByType();

    /**
     * 汇总台账类数据（标准/计划/排放口/许可/危废/报告/碳排放）
     *
     * @return [{standardCount, planCount, planActiveCount, outletCount, permitCount, wasteCount, envReportCount, carbonCount, carbonEmissionSum}]
     */
    Map<String, Object> selectLedgerSummary();

    /**
     * 按月份聚合近 N 个月各检测记录表的记录数
     *
     * @param beginTime >= 开始时间
     * @return [{ "month": "2026-04", "count": 3 }, ...]
     */
    List<Map<String, Object>> selectMonthTrend(@Param("beginTime") LocalDateTime beginTime);

}
