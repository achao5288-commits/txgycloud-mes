package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-排污许可证 Response VO")
@Data
public class MesSetPollutionPermitRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "排污许可证编号", example = "P-110108-2026-0001")
    private String permitNo;

    @Schema(description = "持证单位名称", example = "华瀚节能建材")
    private String enterpriseName;

    @Schema(description = "发证机关")
    private String issuingAuthority;

    @Schema(description = "发证日期")
    private LocalDate issueDate;

    @Schema(description = "许可有效期起始")
    private LocalDate startDate;

    @Schema(description = "许可有效期止")
    private LocalDate endDate;

    @Schema(description = "绑定的排放口编号(逗号分隔)")
    private String outletCodes;

    @Schema(description = "许可年排放总量 JSON")
    private String annualLimits;

    @Schema(description = "执行报告配置 JSON")
    private String annualReports;

    @Schema(description = "状态：ACTIVE/EXPIRED/REVOKED")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
