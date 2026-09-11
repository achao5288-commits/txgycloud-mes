package cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-电气安全检查 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetElectricalRecordPageReqVO extends PageParam {

    @Schema(description = "记录编号 ELEC-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "检测位置(配电柜/线路区域)")
    private String location;

    @Schema(description = "检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE")
    private String checkItem;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
