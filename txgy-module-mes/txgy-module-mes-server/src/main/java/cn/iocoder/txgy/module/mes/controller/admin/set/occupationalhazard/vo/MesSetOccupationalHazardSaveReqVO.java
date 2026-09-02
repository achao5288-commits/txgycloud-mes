package cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 安全环保检测-职业病危害因素检测记录 新增/修改 Request VO")
@Data
public class MesSetOccupationalHazardSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "OCC-HZ-20260901-001")
    @NotEmpty(message = "记录编号不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联员工编号", example = "1")
    private Long empId;

    @Schema(description = "危害因素类别：CHEMICAL(化学)/DUST(粉尘)/PHYSICAL(物理)/BIOLOGICAL(生物)/OTHER(其他)", requiredMode = Schema.RequiredMode.REQUIRED, example = "CHEMICAL")
    @NotEmpty(message = "危害因素类别不能为空")
    private String factorCategory;

    @Schema(description = "危害因素编码（如 GBZ 2.1-2019 对应代号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "PC-TWA-苯")
    @NotEmpty(message = "危害因素编码不能为空")
    private String factorCode;

    @Schema(description = "检测岗位/工作场所", requiredMode = Schema.RequiredMode.REQUIRED, example = "涂装车间喷漆岗")
    @NotEmpty(message = "检测岗位/工作场所不能为空")
    private String workplace;

    @Schema(description = "来源关联类型：PLAN(检测计划)/TASK(检测任务)", example = "PLAN")
    private String sourceRefType;

    @Schema(description = "来源记录编号", example = "1")
    private Long sourceRecordId;

    @Schema(description = "测量值", example = "12.5")
    private BigDecimal measuredValue;

    @Schema(description = "单位（如 mg/m3 / dB(A)）", example = "mg/m3")
    private String unit;

    @Schema(description = "限值类型：MAC(最高容许)/PC-TWA(时间加权平均)/PC-STEL(短时间接触)/NOISE(噪声限值)", example = "PC-TWA")
    private String limitType;

    @Schema(description = "职业接触限值(OEL)", example = "50")
    private BigDecimal oelValue;

    @Schema(description = "参考标准（如 GBZ 2.1-2019）", example = "GBZ 2.1-2019")
    private String refStandard;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "检测人", example = "张三")
    private String inspector;

    @Schema(description = "检测时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

}
