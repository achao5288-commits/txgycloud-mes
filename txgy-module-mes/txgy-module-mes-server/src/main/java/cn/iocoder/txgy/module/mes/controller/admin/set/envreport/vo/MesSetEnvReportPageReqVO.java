package cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-环保检测报告 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEnvReportPageReqVO extends PageParam {

    @Schema(description = "报告编号 EP-YYYYMMDD-NNN")
    private String reportNo;

    @Schema(description = "报告名称")
    private String reportName;

    @Schema(description = "报告来源：THIRD_PARTY/INTERNAL")
    private String reportType;

    @Schema(description = "类别：EXHAUST_GAS/WASTEWATER/NOISE/SOLID_WASTE/AMBIENT/COMPREHENSIVE等")
    private String reportCategory;

    @Schema(description = "状态：DRAFT/APPROVED/REJECTED/ARCHIVED")
    private String status;

}
