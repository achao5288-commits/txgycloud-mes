package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-应急事件 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEmergencyEventPageReqVO extends PageParam {

    @Schema(description = "事件编号")
    private String eventNo;

    @Schema(description = "事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)")
    private String eventType;

    @Schema(description = "发生地点")
    private String location;

    @Schema(description = "状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)")
    private String status;

}
