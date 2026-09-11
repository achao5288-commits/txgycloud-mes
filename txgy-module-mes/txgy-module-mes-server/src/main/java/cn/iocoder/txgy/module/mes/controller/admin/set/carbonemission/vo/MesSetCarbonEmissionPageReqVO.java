package cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-碳排放核算 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetCarbonEmissionPageReqVO extends PageParam {

    @Schema(description = "核算批次号 CARBON-YYYYMM")
    private String calcNo;

    @Schema(description = "核算周期：DAILY/MONTHLY/YEARLY")
    private String periodType;

    @Schema(description = "周期开始日期")
    private LocalDate periodStart;

    @Schema(description = "能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM")
    private String energyType;

}
