package cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-环保检测报告 Response VO")
@Data
public class MesSetEnvReportRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "报告编号", example = "ENV-RPT-2026Q3-001")
    private String reportNo;

    @Schema(description = "报告名称", example = "2026年第三季度自行监测报告")
    private String reportName;

    @Schema(description = "报告类型：MONTHLY(月度)/QUARTERLY(季度)/ANNUAL(年度)/OTHER(其他)", example = "QUARTERLY")
    private String reportType;

    @Schema(description = "报告类别：SELF_MONITOR(自行监测)/COMPLIANCE(合规性)/OTHER(其他)", example = "SELF_MONITOR")
    private String reportCategory;

    @Schema(description = "关联报告模板编号", example = "1")
    private Long templateId;

    @Schema(description = "报告周期开始日期")
    private LocalDate periodStart;

    @Schema(description = "报告周期结束日期")
    private LocalDate periodEnd;

    @Schema(description = "报告生成日期")
    private LocalDate reportDate;

    @Schema(description = "数据汇总摘要", example = "废水总排口COD均值42mg/L，达标排放")
    private String dataSummary;

    @Schema(description = "报告文件 URL", example = "/prod-api/1.pdf")
    private String fileUrl;

    @Schema(description = "签章文件 URL", example = "/prod-api/sign/1.png")
    private String signUrl;

    @Schema(description = "关联表单编号", example = "1")
    private Long formId;

    @Schema(description = "状态：DRAFT(草稿)/SUBMITTED(已提交)/APPROVED(已审批)/REJECTED(已驳回)", example = "APPROVED")
    private String status;

    @Schema(description = "审核人", example = "李四")
    private String auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
