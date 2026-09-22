package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 受控库位调整 Request VO")
@Data
public class MesSetPollutionCheckControlLocationReqVO {

    @Schema(description = "判定记录编号（必须已复核）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "判定记录编号不能为空")
    private Long id;

    @Schema(description = "目标库位编号（mes_wm_warehouse_area.id）：有污染必须选污染管控库位，无污染不得选受控库位",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "目标库位不能为空")
    private Long locationId;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填）。必填：本操作会改动环保数据，"
            + "缺签名后端直接拒绝（错误码 1_040_818_019）")
    private String signImg;

    @Schema(description = "签署意见（随签字记录留档）")
    private String opinion;

}
