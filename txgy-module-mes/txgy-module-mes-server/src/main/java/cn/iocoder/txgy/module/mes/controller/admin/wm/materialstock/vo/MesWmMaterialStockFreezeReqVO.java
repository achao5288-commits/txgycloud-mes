package cn.iocoder.txgy.module.mes.controller.admin.wm.materialstock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 库存台账冻结/解冻 Request VO")
@Data
public class MesWmMaterialStockFreezeReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "是否冻结", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "冻结状态不能为空")
    private Boolean frozen;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填）。必填：本操作会改动环保数据，"
            + "缺签名后端直接拒绝（错误码 1_040_703_015）")
    private String signImg;

    @Schema(description = "签署意见（随签字记录留档）")
    private String opinion;

}

