package cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-应急演练 新增/修改 Request VO")
@Data
public class MesSetEmergencyDrillSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "演练编号")
    @NotEmpty(message = "演练编号不能为空")
    private String drillNo;

    @Schema(description = "演练名称")
    @NotEmpty(message = "演练名称不能为空")
    private String drillName;

    @Schema(description = "关联预案编号（须为已发布/已备案的预案）")
    private Long planId;

    @Schema(description = "演练时的预案版本快照")
    private String planVersion;

    @Schema(description = "演练类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)")
    @NotEmpty(message = "演练类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)不能为空")
    private String drillType;

    @Schema(description = "演练日期")
    @NotNull(message = "演练日期不能为空")
    private LocalDate drillDate;

    @Schema(description = "参加人数")
    private Integer participantCount;

    @Schema(description = "参加人员")
    private String participants;

    @Schema(description = "签到表附件")
    private String signSheetUrl;

    @Schema(description = "演练照片")
    private String photoUrl;

    @Schema(description = "演练视频")
    private String videoUrl;

    @Schema(description = "演练评估")
    private String evaluation;

    @Schema(description = "整改要求（填写后整改状态转 PENDING）")
    private String rectifyRequirement;

    @Schema(description = "整改状态：NONE(无需整改)/PENDING(待整改)/DONE(已整改)")
    private String rectifyStatus;

    @Schema(description = "整改完成日期")
    private LocalDate rectifyDoneDate;

    @Schema(description = "闭环日期")
    private LocalDate closedDate;

    @Schema(description = "状态：PLANNED(已计划)/DONE(已演练)/CLOSED(已闭环)")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
