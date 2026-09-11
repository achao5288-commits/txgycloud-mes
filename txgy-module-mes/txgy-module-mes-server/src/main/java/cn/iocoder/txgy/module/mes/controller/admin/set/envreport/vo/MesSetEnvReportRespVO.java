package cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-环保检测报告 Response VO")
@Data
public class MesSetEnvReportRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "报告编号 EP-YYYYMMDD-NNN")
    private String reportNo;

    @Schema(description = "报告名称")
    private String reportName;

    @Schema(description = "报告来源：THIRD_PARTY/INTERNAL")
    private String reportType;

    @Schema(description = "类别：EXHAUST_GAS/WASTEWATER/NOISE/SOLID_WASTE/AMBIENT/COMPREHENSIVE等")
    private String reportCategory;

    @Schema(description = "复用质检报告模板编号")
    private Long templateId;

    @Schema(description = "报告统计期起")
    private LocalDate periodStart;

    @Schema(description = "报告统计期止")
    private LocalDate periodEnd;

    @Schema(description = "报告日期")
    private LocalDate reportDate;

    @Schema(description = "检测结果摘要JSON文本")
    private String dataSummary;

    @Schema(description = "报告文件URL(第三方导入PDF)")
    private String fileUrl;

    @Schema(description = "电子签名文件URL")
    private String signUrl;

    @Schema(description = "自定义表单配置id")
    private Long formId;

    @Schema(description = "状态：DRAFT/APPROVED/REJECTED/ARCHIVED")
    private String status;

    @Schema(description = "审核人(终审)")
    private String auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
