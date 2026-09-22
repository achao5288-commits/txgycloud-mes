package cn.iocoder.txgy.module.mes.controller.admin.set.monitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * MES 安全环保检测-污染源在线监控看板 Response VO
 *
 * <p>看板接口与实时大屏接口**共用一个形状**：页面上「看板」和「大屏」是同一份数据的两种画法，
 * 后端若分两个 VO，迟早会出现大屏说超标、看板说达标。大屏轮询只取其中四项，缺的段落前端自动沿用上一屏。
 *
 * <p>设计约束（来自对接说明 §二）：**只下发原始值与时间，不下发判定结论**。
 * 「是否超期」「是否逼近限值」这类判断由前端按 {@code value/limit} 与 {@code occurTime+24h} 自行推算，
 * 免得前后端各算一份、屏幕上两个数字打架。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 在线监控看板 Response VO")
@Data
public class MesSetMonitorBoardRespVO {

    @Schema(description = "排放口总数")
    private Integer outletTotal;

    @Schema(description = "在线排放口数")
    private Integer outletOnline;

    @Schema(description = "当前超标排放口数（最新一条有效读数越限）")
    private Integer exceedCount;

    @Schema(description = "今日超标次数（按小时均值判定，同一小时计 1 次）")
    private Integer todayExceedTimes;

    @Schema(description = "数据完整率 %（仅 CEMS 连续监测口径）")
    private BigDecimal dataCompleteRate;

    @Schema(description = "排放口在线率 %")
    private BigDecimal onlineRate;

    @Schema(description = "环比（较昨日同期）")
    private Delta delta;

    @Schema(description = "口径说明（展示在看板底部）")
    private Meta meta;

    @Schema(description = "近 24 小时趋势")
    private List<HourTrend> hourTrend;

    @Schema(description = "各排放口达标率")
    private List<OutletRate> outletRates;

    @Schema(description = "因子实时值")
    private List<Metric> metrics;

    @Schema(description = "超标明细")
    private List<ExceedRecord> exceedRecords;

    @Schema(description = "排放口实时状态")
    private List<OutletLive> outletLive;

    @Schema(description = "环比：本值与昨日同期值之差，正数表示变差（超标类）或变好（比率类由前端按语义着色）")
    @Data
    public static class Delta {

        @Schema(description = "当前超标排放口数环比")
        private Integer exceedCount;

        @Schema(description = "今日超标次数环比")
        private Integer todayExceedTimes;

        @Schema(description = "数据完整率环比（百分点）")
        private BigDecimal dataCompleteRate;

        @Schema(description = "在线率环比（百分点）")
        private BigDecimal onlineRate;

    }

    @Schema(description = "口径说明：看板底部那三句话，给看看板的人看")
    @Data
    public static class Meta {

        @Schema(description = "数据来源")
        private String source;

        @Schema(description = "采集频率")
        private String freq;

        @Schema(description = "判定口径")
        private String caliber;

    }

    @Schema(description = "24 小时趋势点")
    @Data
    public static class HourTrend {

        @Schema(description = "小时标签，如 09:00")
        private String hour;

        @Schema(description = "全厂该因子小时均值；该小时无有效读数为 null（前端断线处理，不画成 0）")
        private BigDecimal value;

        @Schema(description = "该因子限值")
        private BigDecimal limit;

    }

    @Schema(description = "排放口达标率")
    @Data
    public static class OutletRate {

        @Schema(description = "排放口编号")
        private String outletNo;

        @Schema(description = "排放口名称")
        private String outletName;

        @Schema(description = "达标率 %（分子分母均排除掉线/数据延迟的时段）")
        private BigDecimal passRate;

        @Schema(description = "今日超标次数")
        private Integer exceedCount;

    }

    @Schema(description = "因子实时值")
    @Data
    public static class Metric {

        @Schema(description = "监控因子")
        private String pollutantName;

        @Schema(description = "实测值")
        private BigDecimal value;

        @Schema(description = "限值")
        private BigDecimal limit;

    }

    @Schema(description = "超标明细")
    @Data
    public static class ExceedRecord {

        @Schema(description = "编号，派单/闭环要用")
        private Long id;

        @Schema(description = "排放口编号")
        private String outletNo;

        @Schema(description = "排放口名称")
        private String outletName;

        @Schema(description = "监控因子")
        private String pollutantName;

        @Schema(description = "小时均值")
        private BigDecimal value;

        @Schema(description = "限值")
        private BigDecimal limit;

        @Schema(description = "超标倍数 = value / limit")
        private BigDecimal multiple;

        @Schema(description = "发生时间（毫秒时间戳，由 Jackson 序列化 LocalDateTime）")
        private java.time.LocalDateTime occurTime;

        @Schema(description = "持续时长（分钟）")
        private Integer durationMin;

        @Schema(description = "处置状态：PENDING/PROCESSING/CLOSED")
        private String handleStatus;

        @Schema(description = "处置人，未派单为 -")
        private String handler;

        @Schema(description = "处置轨迹")
        private List<ExceedEvent> events;

    }

    @Schema(description = "处置轨迹节点")
    @Data
    public static class ExceedEvent {

        @Schema(description = "发生时间")
        private java.time.LocalDateTime t;

        @Schema(description = "节点类型：DISPATCH/CLOSE")
        private String kind;

        @Schema(description = "标题")
        private String title;

        @Schema(description = "说明")
        private String desc;

    }

    @Schema(description = "排放口实时状态")
    @Data
    public static class OutletLive {

        @Schema(description = "排放口编号")
        private String outletNo;

        @Schema(description = "排放口名称")
        private String outletName;

        @Schema(description = "主监控因子")
        private String pollutantName;

        @Schema(description = "最新实测值；掉线/数据延迟必须为 null，不能填 0")
        private BigDecimal value;

        @Schema(description = "限值")
        private BigDecimal limit;

        @Schema(description = "今日超标次数")
        private Integer todayExceed;

        @Schema(description = "最近上报时间（服务端时钟）")
        private String lastReport;

        @Schema(description = "ONLINE / STALE 数据延迟 / OFFLINE 掉线")
        private String status;

    }

}
