package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 危废联单四方会签 Request VO")
@Data
public class MesSetHazwasteSignReqVO {

    @Schema(description = "联单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "联单号不能为空")
    private String manifestNo;

    @Schema(description = "会签角色：HANDOVER(移交环保员)/DRIVER(押运司机)/RECEIVER(接收经手人)/GUARD(门卫)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "会签角色不能为空")
    private String signRole;

    @Schema(description = "签署意见")
    private String opinion;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填；留空则不存签名图）")
    private String signImg;

}
