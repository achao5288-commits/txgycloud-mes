package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MES 合规看板 Response VO
 *
 * 全库口径，不接受筛选：看板是「现在全厂什么状态」，下钻靠点数字跳去在库环保视图带筛选，
 * 而不是让看板自己变成可筛选的第二个视图（两个口径迟早会对不上）。
 */
@Schema(description = "管理后台 - MES 合规看板 Response VO")
@Data
public class MesInventoryEnvDashboardRespVO {

    @Schema(description = "全库体检汇总（与体检接口同一套判据，同一时刻同结果）")
    private MesInventoryEnvInspectSummaryRespVO summary;

    @Schema(description = "检测覆盖率：已复核批次 / 总批次，0~1，无批次时为 0")
    private Double coverRate;

    @Schema(description = "异常率：复核为「有污染」的批次 / 已复核批次，0~1，无已复核批次时为 0")
    private Double pollutedRate;

    @Schema(description = "待复核积压")
    private MesInventoryEnvPendingReviewRespVO pendingReview;

    @Schema(description = "处置时效")
    private MesInventoryEnvLedgerDurationRespVO ledgerDuration;

    @Schema(description = "按仓库分档")
    private List<MesInventoryEnvDashboardItemRespVO> byWarehouse;

    @Schema(description = "按物料分类分档")
    private List<MesInventoryEnvDashboardItemRespVO> byItemType;

}
