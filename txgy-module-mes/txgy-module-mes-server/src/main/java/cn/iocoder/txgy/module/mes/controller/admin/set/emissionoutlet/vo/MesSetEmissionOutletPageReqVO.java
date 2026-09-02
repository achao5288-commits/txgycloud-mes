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

    @Schema(description = "排放口名称", example = "废气排放口1")
    private String outletName;

    @Schema(description = "排放类型：GAS/WASTEWATER/NOISE", example = "GAS")
    private String outletType;

    @Schema(description = "状态：ACTIVE/INACTIVE", example = "ACTIVE")
    private String status;

}
