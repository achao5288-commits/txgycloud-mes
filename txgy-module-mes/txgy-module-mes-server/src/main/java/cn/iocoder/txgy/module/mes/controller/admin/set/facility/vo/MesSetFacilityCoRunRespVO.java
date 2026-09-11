package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 治污设施同开同停校验结果 Response VO")
@Data
public class MesSetFacilityCoRunRespVO {

    @Schema(description = "是否通过")
    private Boolean passed;

    @Schema(description = "设施编号")
    private Long facilityId;

    @Schema(description = "设施名称")
    private String facilityName;

    @Schema(description = "所属产线")
    private String lineCode;

    @Schema(description = "设施运行状态")
    private String runStatus;

    @Schema(description = "产线是否运行")
    private Boolean productionRunning;

    @Schema(description = "拦截原因")
    private List<String> reasons;

}
