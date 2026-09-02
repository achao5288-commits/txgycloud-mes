package cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 检测标准 新增/修改 Request VO")
@Data
public class MesSetStandardSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "标准编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "SET-STD-20260001")
    @NotEmpty(message = "标准编号不能为空")
    private String standardNo;

    @Schema(description = "标准名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "受限空间作业气体检测")
    @NotEmpty(message = "标准名称不能为空")
    private String standardName;

    @Schema(description = "检测域：SAFETY/ENV/HEALTH", example = "SAFETY")
    private String domain;

    @Schema(description = "检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE 等", example = "GAS")
    private String testType;

    @Schema(description = "引用国标编号", example = "GBZ 2.1-2019")
    private String refStandard;

    @Schema(description = "限值配置(JSON 文本)", example = "{\"CO\":{\"mac\":20,\"unit\":\"mg/m3\"}}")
    private String limitsConfig;

    @Schema(description = "检测方法描述", example = "便携式气体检测仪现场测定")
    private String method;

    @Schema(description = "周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT", example = "EVENT")
    private String periodType;

    @Schema(description = "事件触发配置(JSON 文本)", example = "{\"triggerType\":\"WO_START\"}")
    private String triggerConfig;

    @Schema(description = "适用区域/工序(JSON 文本)", example = "[\"受限空间\"]")
    private String applicableArea;

    @Schema(description = "状态：DRAFT/ACTIVE/OBSOLETE", example = "ACTIVE")
    private String status;

    @Schema(description = "备注", example = "受限空间作业开工前强制检测")
    private String remark;

}
