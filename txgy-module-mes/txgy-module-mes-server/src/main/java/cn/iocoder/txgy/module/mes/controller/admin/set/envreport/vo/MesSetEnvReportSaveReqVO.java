package cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - MES 安全环保检测-环保检测报告 新增/修改 Request VO")
@Data
public class MesSetEnvReportSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "报告编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENV-RPT-2026Q3-001")
    @NotEmpty(message = "报告编号不能为空")
    private String reportNo;

    @Schema(description = "报告名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026年第三季度自行监测报告")
    @NotEmpty(message = "报告名称不能为空")
    private String reportName;

    @Schema(description = "报告类型：MONTHLY(月度)/QUARTERLY(季度)/ANNUAL(年度)/OTHER(其他)", requiredMode = Schema.RequiredMode.REQUIRED, example = "QUARTERLY")
    @NotEmpty(message = "报告类型不能为空")
    private String reportType;

    @Schema(description = "报告类别：SELF_MONITOR(自行监测)/COMPLIANCE(合规性)/OTHER(其他)", example = "SELF_MONITOR")
    private String reportCategory;

    @Schema(description = "关联报告模板编号", example = "1")
    private Long templateId;

    @Schema(description = "报告周期开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate periodStart;

    @Schema(description = "报告周期结束日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate periodEnd;

    @Schema(description = "报告生成日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate reportDate;

    @Schema(description = "数据汇总摘要", example = "废水总排口COD均值42mg/L，达标排放")
    private String dataSummary;

    @Schema(description = "报告文件 URL", example = "/prod-api/1.pdf")
    private String fileUrl;

    @Schema(description = "签章文件 URL", example = "/prod-api/sign/1.png")
    private String signUrl;

    @Schema(description = "关联表单编号", example = "1")
    private Long formId;

    @Schema(description = "状态：DRAFT(草稿)/SUBMITTED(已提交)/APPROVED(已审批)/REJECTED(已驳回)", example = "DRAFT")
    private String status;

    @Schema(description = "审核人", example = "李四")
    private String auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "备注")
    private String remark;

}
