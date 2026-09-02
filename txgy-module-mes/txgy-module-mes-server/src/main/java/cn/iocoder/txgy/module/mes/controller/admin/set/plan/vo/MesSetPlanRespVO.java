package cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 检测计划 Response VO")
@Data
public class MesSetPlanRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "计划编号", example = "SET-PLAN-20260001")
    private String planNo;

    @Schema(description = "计划名称", example = "受限空间作业前气体检测")
    private String planName;

    @Schema(description = "触发类型", example = "EVENT")
    private String planType;

    @Schema(description = "周期类型", example = "MONTHLY")
    private String periodType;

    @Schema(description = "生效开始日期")
    private LocalDate startDate;

    @Schema(description = "生效结束日期")
    private LocalDate endDate;

    @Schema(description = "关联设备编号", example = "1")
    private Long machineryId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "关联检测标准编号", example = "1")
    private Long standardId;

    @Schema(description = "责任人(执行人)编号", example = "1")
    private Long assigneeId;

    @Schema(description = "状态", example = "ACTIVE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
