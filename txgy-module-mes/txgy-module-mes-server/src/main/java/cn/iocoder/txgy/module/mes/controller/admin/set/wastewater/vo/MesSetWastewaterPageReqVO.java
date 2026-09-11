package cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-废水监测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetWastewaterPageReqVO extends PageParam {

    @Schema(description = "记录编号 WW-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "关联排放口编号")
    private Long outletId;

    @Schema(description = "污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等")
    private String pollutantCode;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：ONLINE_AUTO/LAB_MANUAL")
    private String collectionMode;

}
