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

    @Schema(description = "报告类型：MONTHLY(月度)/QUARTERLY(季度)/ANNUAL(年度)/OTHER(其他)", example = "QUARTERLY")
    private String reportType;

    @Schema(description = "状态：DRAFT(草稿)/SUBMITTED(已提交)/APPROVED(已审批)/REJECTED(已驳回)", example = "APPROVED")
    private String status;

}
