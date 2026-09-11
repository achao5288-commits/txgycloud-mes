package cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-噪声检测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetNoiseRecordPageReqVO extends PageParam {

    @Schema(description = "记录编号 NOISE-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "监测类型：STATIONARY(固定式声级计)/PERSONAL(个体剂量计)")
    private String sourceType;

    @Schema(description = "检测位置/区域")
    private String location;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL")
    private String collectionMode;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
