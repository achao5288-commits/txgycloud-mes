package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MES 合规看板 - 单个维度的分档 Response VO
 *
 * 按仓库 / 物料分类分组的同一套判据，点一行即带着该维度下钻到在库环保视图。
 */
@Schema(description = "管理后台 - MES 合规看板分维度 Response VO")
@Data
public class MesInventoryEnvDashboardItemRespVO {

    @Schema(description = "维度编号（仓库编号或物料分类编号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long dimId;

    @Schema(description = "维度名称（仓库名或分类名；主数据缺失时为空，前端显示「未知」）", example = "原料仓")
    private String dimName;

    @Schema(description = "在库库存行数", example = "20")
    private Integer stockRows;

    @Schema(description = "涉及批次数", example = "18")
    private Integer batchCount;

    @Schema(description = "已复核批次数", example = "3")
    private Integer coveredBatchCount;

    @Schema(description = "复核为「有污染」的批次数", example = "1")
    private Integer pollutedBatchCount;

    @Schema(description = "未检测行数", example = "15")
    private Integer notChecked;

    @Schema(description = "超期未检行数", example = "4")
    private Integer overdue;

    @Schema(description = "积压行数", example = "2")
    private Integer stockpiled;

    @Schema(description = "混放行数", example = "0")
    private Integer mixed;

    @Schema(description = "污染投影为 POLLUTED 的行数", example = "2")
    private Integer pollutedRows;

}
