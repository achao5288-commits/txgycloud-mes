package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危废台账 新增/修改 Request VO")
@Data
public class MesSetHazardousWasteSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "危废联单号")
    @NotEmpty(message = "危废联单号不能为空")
    private String manifestNo;

    @Schema(description = "危废代码(如 HW08)")
    private String wasteCode;

    @Schema(description = "危废名称")
    @NotEmpty(message = "危废名称不能为空")
    private String wasteName;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "数量单位（吨等）")
    private String quantityUnit;

    @Schema(description = "贮存地点")
    private String storageLocation;

    @Schema(description = "容器码(一桶一码)")
    private String containerCode;

    @Schema(description = "交接方/接收单位")
    private String counterparty;

    @Schema(description = "来源工单编号")
    private Long woId;

    @Schema(description = "交接/处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "经办人")
    private String handler;

    @Schema(description = "备注")
    private String remark;

}
