package cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-检测计划 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPlanPageReqVO extends PageParam {

    @Schema(description = "计划编号")
    private String planNo;

    @Schema(description = "计划名称")
    private String planName;

    @Schema(description = "触发类型：PERIODIC(周期)/EVENT(事件)")
    private String planType;

    @Schema(description = "周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY")
    private String periodType;

    @Schema(description = "状态：DRAFT/ACTIVE/STOPPED")
    private String status;

}
