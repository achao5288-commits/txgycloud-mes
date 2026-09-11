package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 治污设施新增/修改 Request VO")
@Data
public class MesSetTreatmentFacilitySaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "TF-001")
    @NotEmpty(message = "设施编号不能为空")
    private String facilityNo;

    @Schema(description = "设施名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "喷涂线活性炭吸附塔")
    @NotEmpty(message = "设施名称不能为空")
    private String facilityName;

    @Schema(description = "设施类型：ACTIVATED_CARBON/CATALYTIC_COMBUSTION/BAG_FILTER")
    private String facilityType;

    @Schema(description = "关联排放口编号")
    private String outletCode;

    @Schema(description = "所属产线/工序")
    private String lineCode;

    @Schema(description = "设计风量 m³/h")
    private BigDecimal designAirVolume;

    @Schema(description = "耗材名称")
    private String consumableName;

    @Schema(description = "更换周期(天)")
    private Integer replaceCycleDays;

    @Schema(description = "上次更换日期")
    private LocalDate lastReplaceDate;

    @Schema(description = "档案状态：ENABLED/DISABLED")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
