package cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-签字记录 手工签字 Request VO")
@Data
public class MesSetSignRecordSaveReqVO {

    @Schema(description = "业务关联类型：CHECK(污染判定)/LEDGER(台账处置)", requiredMode = Schema.RequiredMode.REQUIRED, example = "CHECK")
    @NotBlank(message = "业务关联类型不能为空")
    private String bizType;

    @Schema(description = "业务关联单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PC-20260907101530001")
    @NotBlank(message = "业务关联单号不能为空")
    private String bizNo;

    @Schema(description = "签字角色：REVIEWER(复核)/OPERATOR(录入)/APPROVER(审批)", requiredMode = Schema.RequiredMode.REQUIRED, example = "REVIEWER")
    @NotBlank(message = "签字角色不能为空")
    private String signRole;

    @Schema(description = "签字账号ID（下拉选的签字人）。姓名由服务端按账号查，不收前端传的名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "80370")
    @NotNull(message = "签字账号不能为空")
    private Long signUserId;

    @Schema(description = "签字地点/去向", example = "危废暂存间A-01")
    private String location;

    @Schema(description = "签署意见", example = "现场核对无误")
    private String opinion;

    @Schema(description = "手写签名图片URL（infra 文件服务；未签则空）")
    private String signImg;

}
