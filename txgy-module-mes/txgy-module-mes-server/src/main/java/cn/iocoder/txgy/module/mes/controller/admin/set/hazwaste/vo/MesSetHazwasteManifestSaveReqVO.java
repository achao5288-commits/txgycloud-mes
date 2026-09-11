package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危废转移联单 新增/修改 Request VO")
@Data
public class MesSetHazwasteManifestSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "国家固废系统电子转移联单号")
    @NotEmpty(message = "联单号不能为空")
    private String manifestNo;

    @Schema(description = "危险废物类别代码(HW08/HW49等)")
    private String wasteCode;

    @Schema(description = "危险废物名称")
    private String wasteName;

    @Schema(description = "申报转移量")
    private BigDecimal quantity;

    @Schema(description = "数量单位")
    private String quantityUnit;

    @Schema(description = "产生单位")
    private String generateUnit;

    @Schema(description = "运输单位")
    private String carrierUnit;

    @Schema(description = "接收单位")
    private String receiveUnit;

    @Schema(description = "贮存单位")
    private String storageUnit;

    @Schema(description = "处置单位")
    private String disposeUnit;

    @Schema(description = "申报/确认时限")
    private LocalDateTime declareDeadline;

    @Schema(description = "运输车牌号")
    private String vehicleNo;

    @Schema(description = "地磅净重(吨)")
    private BigDecimal netWeight;

    @Schema(description = "备注")
    private String remark;

}
