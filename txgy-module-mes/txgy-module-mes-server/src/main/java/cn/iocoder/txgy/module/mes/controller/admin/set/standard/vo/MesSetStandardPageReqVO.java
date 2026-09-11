package cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-检测标准 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetStandardPageReqVO extends PageParam {

    @Schema(description = "标准编号")
    private String standardNo;

    @Schema(description = "标准名称")
    private String standardName;

    @Schema(description = "检测域：SAFETY/ENV/HEALTH")
    private String domain;

    @Schema(description = "检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等")
    private String testType;

    @Schema(description = "周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT")
    private String periodType;

    @Schema(description = "状态：DRAFT/ACTIVE/OBSOLETE")
    private String status;

}
