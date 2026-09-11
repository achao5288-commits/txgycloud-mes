package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-应急预案 Response VO")
@Data
public class MesSetEmergencyPlanRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "预案编号")
    private String planNo;

    @Schema(description = "预案名称")
    private String planName;

    @Schema(description = "预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)")
    private String planType;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "发布日期（备案时限与评估周期的起算点）")
    private LocalDate publishDate;

    @Schema(description = "备案截止日（发布 + 20 个工作日，派生值不进表单）")
    private LocalDate filingDeadline;

    @Schema(description = "备案号（报生态环境部门后登记）")
    private String filingNo;

    @Schema(description = "备案日期")
    private LocalDate filingDate;

    @Schema(description = "预案附件地址")
    private String attachUrl;

    @Schema(description = "上次评估修订日期")
    private LocalDate lastReviewDate;

    @Schema(description = "下次评估修订日期（发布或上次修订 + 3 年，派生值不进表单）")
    private LocalDate nextReviewDate;

    @Schema(description = "修订原因（工艺/物料/法规变化）")
    private String reviewReason;

    @Schema(description = "状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
