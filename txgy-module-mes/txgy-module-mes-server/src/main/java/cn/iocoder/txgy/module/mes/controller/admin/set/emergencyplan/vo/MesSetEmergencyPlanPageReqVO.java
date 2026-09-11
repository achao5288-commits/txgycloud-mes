package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-应急预案 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEmergencyPlanPageReqVO extends PageParam {

    @Schema(description = "预案编号")
    private String planNo;

    @Schema(description = "预案名称")
    private String planName;

    @Schema(description = "预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)")
    private String planType;

    @Schema(description = "状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)")
    private String status;

}
