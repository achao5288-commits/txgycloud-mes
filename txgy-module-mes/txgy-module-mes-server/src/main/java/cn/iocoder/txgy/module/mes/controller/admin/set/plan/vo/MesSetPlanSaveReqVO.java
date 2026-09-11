package cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-检测计划 新增/修改 Request VO")
@Data
public class MesSetPlanSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "计划编号")
    @NotEmpty(message = "计划编号不能为空")
    private String planNo;

    @Schema(description = "计划名称")
    @NotEmpty(message = "计划名称不能为空")
    private String planName;

    @Schema(description = "触发类型：PERIODIC(周期)/EVENT(事件)")
    private String planType;

    @Schema(description = "周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY")
    private String periodType;

    @Schema(description = "生效开始日期")
    private LocalDate startDate;

    @Schema(description = "生效结束日期")
    private LocalDate endDate;

    @Schema(description = "关联设备编号(事件/设备型)")
    private Long machineryId;

    @Schema(description = "关联工序编号(事件/工单型)")
    private Long operationId;

    @Schema(description = "关联检测标准编号")
    private Long standardId;

    @Schema(description = "责任人/执行人编号")
    private Long assigneeId;

    @Schema(description = "状态：DRAFT/ACTIVE/STOPPED")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
