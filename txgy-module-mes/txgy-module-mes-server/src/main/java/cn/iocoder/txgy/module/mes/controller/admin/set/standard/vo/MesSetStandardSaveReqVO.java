package cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-检测标准 新增/修改 Request VO")
@Data
public class MesSetStandardSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标准编号")
    @NotEmpty(message = "标准编号不能为空")
    private String standardNo;

    @Schema(description = "标准名称")
    @NotEmpty(message = "标准名称不能为空")
    private String standardName;

    @Schema(description = "检测域：SAFETY/ENV/HEALTH")
    private String domain;

    @Schema(description = "检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等")
    private String testType;

    @Schema(description = "引用国标编号（GBZ/GB/T）")
    private String refStandard;

    @Schema(description = "限值配置(JSON文本，如 {CO:{mac,pcTWA,unit}})")
    private String limitsConfig;

    @Schema(description = "检测方法描述")
    private String method;

    @Schema(description = "周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT")
    private String periodType;

    @Schema(description = "事件触发配置(JSON文本)")
    private String triggerConfig;

    @Schema(description = "适用区域/工序(JSON文本)")
    private String applicableArea;

    @Schema(description = "状态：DRAFT/ACTIVE/OBSOLETE")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
