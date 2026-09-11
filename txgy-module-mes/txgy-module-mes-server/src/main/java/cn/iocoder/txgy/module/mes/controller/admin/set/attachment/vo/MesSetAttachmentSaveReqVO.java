package cn.iocoder.txgy.module.mes.controller.admin.set.attachment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-业务附件 登记 Request VO")
@Data
public class MesSetAttachmentSaveReqVO {

    @Schema(description = "业务关联类型：CHECK/LEDGER/MANIFEST/FACILITY/EMERGENCY 等",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "CHECK")
    @NotEmpty(message = "业务关联类型不能为空")
    @Size(max = 32, message = "业务关联类型长度不能超过 32")
    private String bizType;

    @Schema(description = "业务关联单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PC-20260907101530001")
    @NotEmpty(message = "业务关联单号不能为空")
    @Size(max = 64, message = "业务关联单号长度不能超过 64")
    private String bizNo;

    @Schema(description = "原始文件名（展示用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "排污许可证.pdf")
    @NotEmpty(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过 255")
    private String fileName;

    @Schema(description = "文件访问地址（infra 文件服务 /infra/file/upload 返回）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "http://localhost:48082/admin-api/infra/file/4/get/xxx.pdf")
    @NotEmpty(message = "文件地址不能为空")
    @Size(max = 512, message = "文件地址长度不能超过 512")
    private String fileUrl;

    @Schema(description = "备注", example = "危废转移联单扫描件")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;

}
