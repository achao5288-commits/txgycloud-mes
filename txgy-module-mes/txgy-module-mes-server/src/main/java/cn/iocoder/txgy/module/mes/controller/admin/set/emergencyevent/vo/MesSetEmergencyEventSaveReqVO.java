package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-应急事件 新增/修改 Request VO")
@Data
public class MesSetEmergencyEventSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "事件编号")
    @NotEmpty(message = "事件编号不能为空")
    private String eventNo;

    @Schema(description = "事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)")
    @NotEmpty(message = "事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)不能为空")
    private String eventType;

    @Schema(description = "泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK")
    private String scenario;

    @Schema(description = "发生时间")
    private LocalDateTime occurTime;

    @Schema(description = "发生地点")
    private String location;

    @Schema(description = "涉事化学品编码（关联危化品档案取 MSDS/禁配）")
    private String chemicalCode;

    @Schema(description = "泄漏量")
    private BigDecimal leakQuantity;

    @Schema(description = "影响范围")
    private String impactScope;

    @Schema(description = "上报人")
    private String reportUser;

    @Schema(description = "上报时间（PDA 一键上报）")
    private LocalDateTime reportTime;

    @Schema(description = "处置人（派发时指定）")
    private String handler;

    @Schema(description = "派发时间")
    private LocalDateTime dispatchTime;

    @Schema(description = "处置说明")
    private String disposeNote;

    @Schema(description = "处置照片")
    private String disposePhotoUrl;

    @Schema(description = "应急废物桶数（处置时按明细回填，非人工填写）")
    private Integer wasteCount;

    @Schema(description = "应急废物合计净重 kg（处置时按明细回填，非人工填写）")
    private BigDecimal wasteQuantity;

    @Schema(description = "事件报告（原因/数量/处置/整改）")
    private String reportContent;

    @Schema(description = "负责人")
    private String approver;

    @Schema(description = "签字时间")
    private LocalDateTime approveTime;

    @Schema(description = "闭环时间")
    private LocalDateTime closedTime;

    @Schema(description = "状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
