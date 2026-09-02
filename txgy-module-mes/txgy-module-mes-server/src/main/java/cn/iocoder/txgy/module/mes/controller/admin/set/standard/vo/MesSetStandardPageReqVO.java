package cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 检测标准 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetStandardPageReqVO extends PageParam {

    @Schema(description = "标准名称", example = "气体检测")
    private String standardName;

    @Schema(description = "检测域：SAFETY/ENV/HEALTH", example = "SAFETY")
    private String domain;

    @Schema(description = "检测类型", example = "GAS")
    private String testType;

    @Schema(description = "状态：DRAFT/ACTIVE/OBSOLETE", example = "ACTIVE")
    private String status;

}
