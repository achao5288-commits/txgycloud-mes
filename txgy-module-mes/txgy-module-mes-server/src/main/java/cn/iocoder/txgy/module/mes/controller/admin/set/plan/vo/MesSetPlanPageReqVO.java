package cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 检测计划 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPlanPageReqVO extends PageParam {

    @Schema(description = "计划名称", example = "气体检测")
    private String planName;

    @Schema(description = "触发类型：PERIODIC/EVENT", example = "EVENT")
    private String planType;

    @Schema(description = "状态：DRAFT/ACTIVE/STOPPED", example = "ACTIVE")
    private String status;

}
