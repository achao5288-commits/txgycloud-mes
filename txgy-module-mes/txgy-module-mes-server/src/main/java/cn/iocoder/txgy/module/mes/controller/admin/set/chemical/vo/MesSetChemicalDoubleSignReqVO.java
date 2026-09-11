package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 危化品五双双人签字 Request VO")
@Data
public class MesSetChemicalDoubleSignReqVO {

    @Schema(description = "作业单号（危化品档案编号 / 收发单号 / 领料单号）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "作业单号不能为空")
    private String bizNo;

    @Schema(description = "五双作业类型：RECEIVE/KEEP/LOCK/ISSUE/TRANSPORT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "作业类型不能为空")
    private String signAction;

    @Schema(description = "签字意见")
    private String opinion;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填；留空则不存签名图）")
    private String signImg;

}
