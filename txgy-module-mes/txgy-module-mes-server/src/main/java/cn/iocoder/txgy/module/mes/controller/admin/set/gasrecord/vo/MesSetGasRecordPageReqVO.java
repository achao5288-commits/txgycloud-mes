package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-气体检测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetGasRecordPageReqVO extends PageParam {

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "检测位置")
    private String location;

    @Schema(description = "气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2")
    private String gasType;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL")
    private String collectionMode;

}
