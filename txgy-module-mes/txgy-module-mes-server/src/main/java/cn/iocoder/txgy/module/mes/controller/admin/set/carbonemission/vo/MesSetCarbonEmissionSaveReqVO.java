package cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-碳排放核算记录 新增/修改 Request VO")
@Data
public class MesSetCarbonEmissionSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "核算批次号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "核算批次号不能为空")
    private String calcNo;

    @Schema(description = "核算周期：DAILY/MONTHLY/YEARLY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "核算周期不能为空")
    private String periodType;

    @Schema(description = "周期开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "周期开始日期不能为空")
    private LocalDate periodStart;

    @Schema(description = "周期结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "周期结束日期不能为空")
    private LocalDate periodEnd;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "关联能耗记录编号", example = "1")
    private Long sourceRecordId;

    @Schema(description = "能源类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "能源类型不能为空")
    private String energyType;

    @Schema(description = "能源消耗量")
    private BigDecimal consumption;

    @Schema(description = "排放因子")
    private BigDecimal emissionFactor;

    @Schema(description = "碳排放量=consumption*factor")
    private BigDecimal carbonEmission;

    @Schema(description = "单位 tCO2")
    private String unit;

    @Schema(description = "工艺排放")
    private BigDecimal processEmission;

    @Schema(description = "合计")
    private BigDecimal totalEmission;

}
