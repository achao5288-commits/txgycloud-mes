package cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo;

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

    @Schema(description = "转移联单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "转移联单编号不能为空")
    private String manifestNo;

    @Schema(description = "危废代码（HW代码，如 HW08）")
    private String wasteCode;

    @Schema(description = "危废名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "危废名称不能为空")
    private String wasteName;

    @Schema(description = "数量", example = "100.5")
    private BigDecimal quantity;

    @Schema(description = "数量单位", example = "kg")
    private String quantityUnit;

    @Schema(description = "状态阶段：GENERATED/STORED/TRANSFERRED/DISPOSED", example = "GENERATED")
    private String stage;

    @Schema(description = "储存地点")
    private String storageLocation;

    @Schema(description = "接收方/处置方")
    private String counterparty;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理人")
    private String handler;

    @Schema(description = "状态：DRAFT/APPROVED/REJECTED", example = "DRAFT")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
