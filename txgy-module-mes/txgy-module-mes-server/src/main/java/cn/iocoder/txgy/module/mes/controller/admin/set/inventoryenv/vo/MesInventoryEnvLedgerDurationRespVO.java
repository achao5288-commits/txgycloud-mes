package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MES 合规看板 - 处置时效 Response VO
 *
 * 口径：台账从登记(STORED)到离开「已暂存」的天数。只统计已闭环的行，
 * 仍挂在暂存的行不计入（否则平均时效会随积压越拖越长，看不出真实处置速度）。
 */
@Schema(description = "管理后台 - MES 污染台账处置时效 Response VO")
@Data
public class MesInventoryEnvLedgerDurationRespVO {

    @Schema(description = "已闭环台账行数", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
    private Integer closedCount;

    @Schema(description = "平均处置天数（无闭环数据时为 0）", example = "4")
    private Integer avgDays;

    @Schema(description = "最长处置天数（无闭环数据时为 0）", example = "15")
    private Integer maxDays;

}
