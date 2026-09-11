package cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-废气监测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetExhaustGasPageReqVO extends PageParam {

    @Schema(description = "记录编号 EXGAS-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "关联排放口编号")
    private Long outletId;

    @Schema(description = "污染物：SO2/NOX/PM/VOCs/HCL/HF等")
    private String pollutantCode;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：CEMS_AUTO/MANUAL")
    private String collectionMode;

}
