package cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理后台 - MES 安全环保检测-称重记录（手工登记） Request VO
 *
 * 电子秤直采（AUTO）未接入前，先支持作业现场手工登记（MANUAL）：
 * 毛重/皮重人工录入，净重由服务端计算，避免前端算错或漏算。
 */
@Schema(description = "管理后台 - MES 安全环保检测-称重记录 手工登记 Request VO")
@Data
public class MesSetWeighRecordSaveReqVO {

    @Schema(description = "称重类型：PRODUCE(产废)/FACTORY(出厂)", requiredMode = Schema.RequiredMode.REQUIRED, example = "PRODUCE")
    @NotEmpty(message = "称重类型不能为空")
    private String weighType;

    @Schema(description = "业务类型（如 CHECK/LEDGER，可空）", example = "LEDGER")
    private String bizType;

    @Schema(description = "业务单号（判定记录号/台账来源单号，可空）", example = "PC-20260907101530001")
    private String bizNo;

    @Schema(description = "容器/包装编号", example = "BIN-0001")
    private String containerCode;

    @Schema(description = "批次号", example = "BATCH_ITEM_1")
    private String batchCode;

    @Schema(description = "设备编号（直采时留痕，手工登记可空）", example = "SCALE-01")
    private String deviceCode;

    @Schema(description = "毛重(kg)", requiredMode = Schema.RequiredMode.REQUIRED, example = "125.50")
    @NotNull(message = "毛重不能为空")
    private BigDecimal grossWeight;

    @Schema(description = "皮重(kg，留空按 0)", example = "5.50")
    private BigDecimal tareWeight;

    @Schema(description = "车牌号（出厂称重常用）", example = "京A12345")
    private String plateNo;

    @Schema(description = "现场照片地址", example = "http://...")
    private String photo;

    @Schema(description = "备注/异常说明", example = "手工登记：电子秤未联网")
    private String reason;

    @Schema(description = "称重时间（留空取当前时间）")
    private LocalDateTime weighTime;

}
