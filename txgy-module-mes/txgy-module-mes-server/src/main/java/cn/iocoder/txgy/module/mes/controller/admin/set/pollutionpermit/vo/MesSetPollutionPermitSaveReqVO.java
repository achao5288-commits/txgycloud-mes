package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - MES 安全环保检测-排污许可管理 新增/修改 Request VO")
@Data
public class MesSetPollutionPermitSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "排污许可证编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "91450100-2025-001")
    @NotEmpty(message = "排污许可证编号不能为空")
    private String permitNo;

    @Schema(description = "企业名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "广西XX环保科技有限公司")
    @NotEmpty(message = "企业名称不能为空")
    private String enterpriseName;

    @Schema(description = "发证机关", example = "XX市生态环境局")
    private String issuingAuthority;

    @Schema(description = "发证日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate issueDate;

    @Schema(description = "有效期开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "有效期截止日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "排污口编码（多个以逗号分隔）", example = "DW001,DW002")
    private String outletCodes;

    @Schema(description = "年度许可排放量（各污染物限值描述）", example = "SO2≤100t/a；NOx≤80t/a")
    private String annualLimits;

    @Schema(description = "年度执行报告记录", example = "2025年度执行报告已于2026-01-15提交")
    private String annualReports;

    @Schema(description = "状态：VALID(有效)/EXPIRING(即将到期)/EXPIRED(已失效)/REVOKED(已注销)", example = "VALID")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
