package cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-危废台账 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetHazardousWastePageReqVO extends PageParam {

    @Schema(description = "危废代码（HW代码，如 HW08）", example = "HW08")
    private String wasteCode;

    @Schema(description = "危废名称", example = "废矿物油")
    private String wasteName;

    @Schema(description = "状态阶段：GENERATED/STORED/TRANSFERRED/DISPOSED", example = "GENERATED")
    private String stage;

    @Schema(description = "状态：DRAFT/APPROVED/REJECTED", example = "DRAFT")
    private String status;

}
