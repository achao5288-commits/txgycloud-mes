package cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-电气安全检查 新增/修改 Request VO")
@Data
public class MesSetElectricalRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号 ELEC-YYYYMMDD-NNN")
    @NotEmpty(message = "记录编号 ELEC-YYYYMMDD-NNN不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联设备/配电设施编号")
    @NotNull(message = "关联设备/配电设施编号不能为空")
    private Long deviceId;

    @Schema(description = "检测位置(配电柜/线路区域)")
    private String location;

    @Schema(description = "检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE")
    @NotEmpty(message = "检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE不能为空")
    private String checkItem;

    @Schema(description = "实测值")
    private BigDecimal measuredValue;

    @Schema(description = "单位 MΩ/Ω/mA/ms/V")
    private String unit;

    @Schema(description = "标准限值")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "检测仪器编号")
    private String instrumentNo;

    @Schema(description = "仪器校准状态快照：1已校准/0未校准")
    private Boolean instrumentCalibOk;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测时间")
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片URL(逗号分隔)")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
