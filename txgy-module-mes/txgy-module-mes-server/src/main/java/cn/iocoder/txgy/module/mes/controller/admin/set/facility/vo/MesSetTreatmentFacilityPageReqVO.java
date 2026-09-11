package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 治污设施分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetTreatmentFacilityPageReqVO extends PageParam {

    @Schema(description = "设施编号", example = "TF-001")
    private String facilityNo;

    @Schema(description = "设施名称")
    private String facilityName;

    @Schema(description = "设施类型")
    private String facilityType;

    @Schema(description = "关联排放口编号")
    private String outletCode;

    @Schema(description = "运行状态：RUNNING/STOPPED")
    private String runStatus;

    @Schema(description = "档案状态：ENABLED/DISABLED")
    private String status;

}
