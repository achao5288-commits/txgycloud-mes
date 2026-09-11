package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

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

    @Schema(description = "危废联单号")
    private String manifestNo;

    @Schema(description = "危废代码(如 HW08)")
    private String wasteCode;

    @Schema(description = "危废名称")
    private String wasteName;

    @Schema(description = "台账环节：GENERATED/STORED/TRANSFERRED/DISPOSED")
    private String stage;

    @Schema(description = "容器码(一桶一码)")
    private String containerCode;

    @Schema(description = "审批状态：DRAFT/APPROVED/REJECTED")
    private String status;

}
