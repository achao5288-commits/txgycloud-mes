package cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 安全环保检测-电气安全检测记录 新增/修改 Request VO")
@Data
public class MesSetElectricalRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ELEC-20260902-001")
    @NotEmpty(message = "记录编号不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联设备不能为空")
    private Long deviceId;

    @Schema(description = "检测位置", example = "配电室A")
    private String location;

    @Schema(description = "检测项目（接地电阻/绝缘电阻/漏电保护/等电位等）", requiredMode = Schema.RequiredMode.REQUIRED, example = "GROUND_RESISTANCE")
    @NotEmpty(message = "检测项目不能为空")
    private String checkItem;

    @Schema(description = "测量值", example = "1.2")
    private BigDecimal measuredValue;

    @Schema(description = "单位：Ω/MΩ/mA/V", example = "Ω")
    private String unit;

    @Schema(description = "限值", example = "4")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "检测仪器编号", example = "INS-003")
    private String instrumentNo;

    @Schema(description = "仪器校准状态：0-未校准 1-已校准", example = "1")
    private Integer instrumentCalibOk;

    @Schema(description = "检测人", example = "张三")
    private String inspector;

    @Schema(description = "检测时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片 URL（逗号分隔）", example = "/prod-api/1.png,/prod-api/2.png")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
