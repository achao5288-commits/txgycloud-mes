package cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-排放口 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEmissionOutletPageReqVO extends PageParam {

    @Schema(description = "排放口编号(如 DA001/DW001)")
    private String outletCode;

    @Schema(description = "排放口名称")
    private String outletName;

    @Schema(description = "排放类型：GAS/WASTEWATER/NOISE")
    private String outletType;

    @Schema(description = "在线监测方式：CEMS/MANUAL/NONE")
    private String monitorMethod;

    @Schema(description = "状态：ACTIVE/INACTIVE")
    private String status;

}
