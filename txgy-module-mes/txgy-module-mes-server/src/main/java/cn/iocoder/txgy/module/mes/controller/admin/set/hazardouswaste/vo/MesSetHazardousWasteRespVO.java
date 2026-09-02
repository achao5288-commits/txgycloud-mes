package cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危废台账 Response VO")
@Data
public class MesSetHazardousWasteRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "转移联单编号")
    private String manifestNo;

    @Schema(description = "危废代码（HW代码，如 HW08）")
    private String wasteCode;

    @Schema(description = "危废名称")
    private String wasteName;

    @Schema(description = "数量", example = "100.5")
    private BigDecimal quantity;

    @Schema(description = "数量单位", example = "kg")
    private String quantityUnit;

    @Schema(description = "状态阶段：GENERATED/STORED/TRANSFERRED/DISPOSED")
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

    @Schema(description = "状态：DRAFT/APPROVED/REJECTED")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
