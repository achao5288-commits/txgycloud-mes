package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-排污许可证 新增/修改 Request VO")
@Data
public class MesSetPollutionPermitSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "排污许可证编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "P-110108-2026-0001")
    @NotEmpty(message = "排污许可证编号不能为空")
    private String permitNo;

    @Schema(description = "持证单位名称(排放单位/企业)", requiredMode = Schema.RequiredMode.REQUIRED, example = "华瀚节能建材")
    @NotEmpty(message = "持证单位名称不能为空")
    private String enterpriseName;

    @Schema(description = "发证机关", example = "北京市生态环境局")
    private String issuingAuthority;

    @Schema(description = "发证日期", example = "2026-01-01")
    private LocalDate issueDate;

    @Schema(description = "许可有效期起始", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    private LocalDate startDate;

    @Schema(description = "许可有效期止", requiredMode = Schema.RequiredMode.REQUIRED, example = "2030-12-31")
    private LocalDate endDate;

    @Schema(description = "绑定的排放口编号(逗号分隔, 如 DA001,DA002)", example = "DA001,DA002")
    private String outletCodes;

    @Schema(description = "许可年排放总量 JSON：[{pollutantCode,pollutantName,annualLimitT,annualUsedT}]")
    private String annualLimits;

    @Schema(description = "执行报告配置 JSON：[{reportType:QUARTERLY/ANNUAL,periodStart,periodEnd}]")
    private String annualReports;

    @Schema(description = "状态：ACTIVE(有效)/EXPIRED(已过期)/REVOKED(已注销)", example = "ACTIVE")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
