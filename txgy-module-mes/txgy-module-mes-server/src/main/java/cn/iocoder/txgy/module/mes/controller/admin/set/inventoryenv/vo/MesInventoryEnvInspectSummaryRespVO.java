package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MES 全库环保体检汇总 Response VO
 *
 * 只读重算，不落「体检批次」表：这些数字全都能从在库数据当场导出来，存一份只会多一份会过期的副本。
 * 各判据是**并列命中数**（一行可同时未检+积压），加起来大于 stockRows 不是 bug。
 */
@Schema(description = "管理后台 - MES 全库环保体检汇总 Response VO")
@Data
public class MesInventoryEnvInspectSummaryRespVO {

    @Schema(description = "在库库存行总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    private Integer stockRows;

    @Schema(description = "涉及批次数（库存行 ≥ 批次，同批多行只算一次）", example = "54")
    private Integer batchCount;

    @Schema(description = "已复核批次数（有有效判定的批次）", example = "6")
    private Integer coveredBatchCount;

    @Schema(description = "复核为「有污染」的批次数", example = "1")
    private Integer pollutedBatchCount;

    @Schema(description = "未检测的库存行数", example = "54")
    private Integer notChecked;

    @Schema(description = "超期未检的库存行数", example = "12")
    private Integer overdue;

    @Schema(description = "积压的库存行数", example = "5")
    private Integer stockpiled;

    @Schema(description = "混放的库存行数", example = "0")
    private Integer mixed;

    @Schema(description = "污染投影为 POLLUTED 的库存行数（看板卡片值；批次数见 pollutedBatchCount）", example = "3")
    private Integer pollutedRows;

    @Schema(description = "待检行数（命中未检/超期/积压/混放任一项，去重后）", example = "54")
    private Integer pending;

}
