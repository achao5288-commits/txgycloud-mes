package cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-粉尘检测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetDustRecordPageReqVO extends PageParam {

    @Schema(description = "记录编号 DUST-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "检测位置/作业区域")
    private String location;

    @Schema(description = "检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2")
    private String dustType;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL")
    private String collectionMode;

}
