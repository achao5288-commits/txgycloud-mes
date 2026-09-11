package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-危化品档案 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetChemicalProfilePageReqVO extends PageParam {

    @Schema(description = "档案编号")
    private String profileNo;

    @Schema(description = "危化品名称")
    private String chemicalName;

    @Schema(description = "危化品代码")
    private String chemicalCode;

    @Schema(description = "相容组")
    private String compatGroup;

    @Schema(description = "储存专区")
    private String storageZone;

    @Schema(description = "状态：ENABLED/DISABLED")
    private String status;

}
