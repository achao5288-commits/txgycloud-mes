package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产废登记入参（治理设施换炭、设备维保换油等「确定性危废」称重入暂存台账，不经过污染判定）
 *
 * 与判定侧的 SaveReqVO 字段口径一致：重量用 weight/unitName，不另立"数量"列。
 */
@Schema(description = "管理后台 - MES 产废登记 Request VO")
@Data
public class MesPollutionLedgerWasteRegisterReqVO {

    @Schema(description = "物料/产品名称（如 废活性炭）", requiredMode = Schema.RequiredMode.REQUIRED, example = "废活性炭")
    @NotEmpty(message = "物料名称不能为空")
    private String itemName;

    @Schema(description = "物料/产品编码", example = "TX-FW-CAR-005")
    private String itemCode;

    @Schema(description = "规格/废物类别代码", example = "HW49 900-039-49")
    private String itemSpec;

    @Schema(description = "重量", example = "120.000")
    private BigDecimal weight;

    @Schema(description = "重量单位：KG 表示已换算的质量，其余为物料原单位", example = "KG")
    private String unitName;

    @Schema(description = "暂存去向/库位名称")
    private String location;

    @Schema(description = "来源单号（如换炭作业票号），可供反查本行从哪张单来的")
    private String sourceRecordNo;

    @Schema(description = "备注")
    private String remark;

}
