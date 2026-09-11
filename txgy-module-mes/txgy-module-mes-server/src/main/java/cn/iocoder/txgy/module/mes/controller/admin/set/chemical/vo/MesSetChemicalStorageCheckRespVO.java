package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 危化品库位/入库校验 Response VO")
@Data
public class MesSetChemicalStorageCheckRespVO {

    @Schema(description = "是否准予存放")
    private Boolean passed;

    @Schema(description = "危化品档案编号")
    private String profileNo;

    @Schema(description = "危化品名称")
    private String chemicalName;

    @Schema(description = "相容组")
    private String compatGroup;

    @Schema(description = "相容组中文名（未收录的按原值回显，不编）")
    private String compatGroupName;

    @Schema(description = "库位")
    private String storageLocation;

    @Schema(description = "MSDS 是否已挂载")
    private Boolean msdsOk;

    @Schema(description = "专区是否符合要求")
    private Boolean zoneOk;

    @Schema(description = "储量是否在上限内（未传 quantity 时为 null，表示未判）")
    private Boolean quotaOk;

    @Schema(description = "与同库位哪些物料禁配")
    private List<String> incompatibleWith;

    @Schema(description = "当前存量")
    private BigDecimal stockQuantity;

    @Schema(description = "储量上限")
    private BigDecimal storageLimit;

    @Schema(description = "拦截/提示原因（逐条，供人看的）")
    private List<String> reasons;

}
