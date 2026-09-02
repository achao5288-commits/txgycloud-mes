package cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 检测计划 新增/修改 Request VO")
@Data
public class MesSetPlanSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "SET-PLAN-20260001")
    @NotEmpty(message = "计划编号不能为空")
    private String planNo;

    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "受限空间作业前气体检测")
    @NotEmpty(message = "计划名称不能为空")
    private String planName;

    @Schema(description = "触发类型：PERIODIC(周期)/EVENT(事件)", example = "EVENT")
    private String planType;

    @Schema(description = "周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY", example = "MONTHLY")
    private String periodType;

    @Schema(description = "生效开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "生效结束日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "关联设备编号", example = "1")
    private Long machineryId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "关联检测标准编号", example = "1")
    private Long standardId;

    @Schema(description = "责任人(执行人)编号", example = "1")
    private Long assigneeId;

    @Schema(description = "状态：DRAFT/ACTIVE/STOPPED", example = "ACTIVE")
    private String status;

    @Schema(description = "备注", example = "每日班次开始前执行")
    private String remark;

}
