package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-危废转移联单 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetHazwasteManifestPageReqVO extends PageParam {

    @Schema(description = "联单号")
    private String manifestNo;

    @Schema(description = "危险废物类别代码")
    private String wasteCode;

    @Schema(description = "危险废物名称")
    private String wasteName;

    @Schema(description = "运输车牌号")
    private String vehicleNo;

    @Schema(description = "状态：DRAFT/DECLARED/EFFECTIVE/TRANSFERRED/CLOSED")
    private String status;

}
