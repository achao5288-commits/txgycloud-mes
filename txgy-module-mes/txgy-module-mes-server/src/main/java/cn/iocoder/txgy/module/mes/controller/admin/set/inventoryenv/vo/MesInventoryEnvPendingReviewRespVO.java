package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MES 合规看板 - 待复核积压 Response VO
 *
 * 全表口径（不限在库）：待复核的判定会冻结批次在库，不只会挡住出库，积压一天就多冻一天。
 */
@Schema(description = "管理后台 - MES 待复核积压 Response VO")
@Data
public class MesInventoryEnvPendingReviewRespVO {

    @Schema(description = "待复核判定条数（review_result 为空）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer count;

    @Schema(description = "最长已等待天数（从建单算起）", example = "9")
    private Integer maxWaitDays;

}
