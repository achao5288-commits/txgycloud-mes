package cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-应急演练 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEmergencyDrillPageReqVO extends PageParam {

    @Schema(description = "演练编号")
    private String drillNo;

    @Schema(description = "演练名称")
    private String drillName;

    @Schema(description = "演练类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)")
    private String drillType;

    @Schema(description = "状态：PLANNED(已计划)/DONE(已演练)/CLOSED(已闭环)")
    private String status;

}
