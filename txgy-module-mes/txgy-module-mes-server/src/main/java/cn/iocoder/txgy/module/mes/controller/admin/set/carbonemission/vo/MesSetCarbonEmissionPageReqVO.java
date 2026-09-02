package cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-碳排放核算记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetCarbonEmissionPageReqVO extends PageParam {

    @Schema(description = "核算周期：DAILY/MONTHLY/YEARLY", example = "MONTHLY")
    private String periodType;

    @Schema(description = "能源类型", example = "ELECTRICITY")
    private String energyType;

}
