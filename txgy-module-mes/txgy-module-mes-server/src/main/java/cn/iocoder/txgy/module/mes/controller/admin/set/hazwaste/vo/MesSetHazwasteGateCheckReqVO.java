package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 危废出厂门卫校验 Request VO")
@Data
public class MesSetHazwasteGateCheckReqVO {

    @Schema(description = "国家固废系统电子转移联单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "联单号不能为空")
    private String manifestNo;

    @Schema(description = "运输车牌号（门卫扫牌核对，留空则跳过车牌核对）")
    private String vehicleNo;

    @Schema(description = "手写签名图片地址（门卫手写板上传 infra 文件服务后回填；留空则不存签名图）")
    private String signImg;

}
