package cn.iocoder.txgy.module.mes.controller.admin.safetyenvstatistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - MES 安全环保检测统计 Response VO")
@Data
public class MesSafetyEnvStatisticsRespVO {

    @Schema(description = "检测记录总数（11 类检测记录表合计）", example = "42")
    private Integer recordTotalCount;

    @Schema(description = "本月新增检测记录数", example = "5")
    private Integer monthCount;

    @Schema(description = "合格条数（result=PASS）", example = "38")
    private Integer passCount;

    @Schema(description = "异常条数（result=FAIL）", example = "4")
    private Integer failCount;

    @Schema(description = "合格率（0-100）", example = "90")
    private Integer passRate;

    @Schema(description = "检测标准数量", example = "4")
    private Integer standardCount;

    @Schema(description = "检测计划数量", example = "4")
    private Integer planCount;

    @Schema(description = "执行中检测计划数量", example = "2")
    private Integer planActiveCount;

    @Schema(description = "排放口数量", example = "3")
    private Integer outletCount;

    @Schema(description = "排污许可证数量", example = "1")
    private Integer permitCount;

    @Schema(description = "危险废物台账条数", example = "3")
    private Integer wasteCount;

    @Schema(description = "环保检测报告数量", example = "2")
    private Integer envReportCount;

    @Schema(description = "碳排放核算条数", example = "3")
    private Integer carbonCount;

    @Schema(description = "碳排放核算总量(tCO2e)", example = "12.5")
    private BigDecimal carbonEmissionSum;

    @Schema(description = "各检测类型统计")
    private List<RecordTypeVO> recordStats;

    @Schema(description = "近 6 个月检测记录趋势")
    private List<MonthTrendVO> monthTrend;

    @Schema(description = "某检测类型统计")
    @Data
    public static class RecordTypeVO {

        @Schema(description = "类型编码", example = "gas")
        private String type;

        @Schema(description = "类型名称", example = "气体检测")
        private String typeName;

        @Schema(description = "总条数", example = "5")
        private Integer total;

        @Schema(description = "异常条数", example = "1")
        private Integer fail;

    }

    @Schema(description = "月度趋势点")
    @Data
    public static class MonthTrendVO {

        @Schema(description = "月份 yyyy-MM", example = "2026-04")
        private String month;

        @Schema(description = "检测条数", example = "3")
        private Integer count;

    }

}
