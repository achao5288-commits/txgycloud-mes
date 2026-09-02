package cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 检测标准 Response VO")
@Data
public class MesSetStandardRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "标准编号", example = "SET-STD-20260001")
    private String standardNo;

    @Schema(description = "标准名称", example = "受限空间作业气体检测")
    private String standardName;

    @Schema(description = "检测域", example = "SAFETY")
    private String domain;

    @Schema(description = "检测类型", example = "GAS")
    private String testType;

    @Schema(description = "引用国标编号", example = "GBZ 2.1-2019")
    private String refStandard;

    @Schema(description = "限值配置(JSON 文本)")
    private String limitsConfig;

    @Schema(description = "检测方法描述")
    private String method;

    @Schema(description = "周期类型", example = "EVENT")
    private String periodType;

    @Schema(description = "事件触发配置(JSON 文本)")
    private String triggerConfig;

    @Schema(description = "适用区域/工序(JSON 文本)")
    private String applicableArea;

    @Schema(description = "状态", example = "ACTIVE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
