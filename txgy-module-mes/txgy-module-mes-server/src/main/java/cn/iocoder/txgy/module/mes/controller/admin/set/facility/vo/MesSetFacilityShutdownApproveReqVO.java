package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 治污设施停运审批 Request VO")
@Data
public class MesSetFacilityShutdownApproveReqVO {

    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "设施编号不能为空")
    private Long id;

    @Schema(description = "是否批准：true 批准停运 / false 驳回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审批结论不能为空")
    private Boolean approved;

    @Schema(description = "审批意见")
    private String opinion;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填；留空则不存签名图）")
    private String signImg;

}
