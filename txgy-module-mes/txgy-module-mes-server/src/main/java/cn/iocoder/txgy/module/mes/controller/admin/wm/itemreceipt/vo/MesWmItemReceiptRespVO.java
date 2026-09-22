package cn.iocoder.txgy.module.mes.controller.admin.wm.itemreceipt.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 采购入库单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MesWmItemReceiptRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "IR2026020001")
    @ExcelProperty("入库单编码")
    private String code;

    @Schema(description = "入库单名称", example = "钢板入库单")
    @ExcelProperty("入库单名称")
    private String name;

    @Schema(description = "来料检验单编号", example = "1")
    private Long iqcId;

    @Schema(description = "来料检验单编码", example = "IQC20260201")
    @ExcelProperty("来料检验单")
    private String iqcCode;

    @Schema(description = "到货通知单编号", example = "1")
    private Long noticeId;

    @Schema(description = "到货通知单编码", example = "AN2026020001")
    @ExcelProperty("到货通知单")
    private String noticeCode;

    @Schema(description = "采购订单号", example = "PO20260201")
    @ExcelProperty("采购订单号")
    private String purchaseOrderCode;

    @Schema(description = "供应商编号", example = "1")
    private Long vendorId;

    @Schema(description = "供应商名称", example = "某供应商")
    @ExcelProperty("供应商")
    private String vendorName;

    @Schema(description = "入库日期")
    @ExcelProperty("入库日期")
    private LocalDateTime receiptDate;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    /**
     * 以下四项是「整单环保判定」投影，非本表字段，由 Service 按 biz_no=入库单编码 聚合判定表回填。
     * 不加 @ExcelProperty：导出列已定型，加进去会动到别人对表头的预期。
     */
    @Schema(description = "环保判定状态：NOT_JUDGED 未判定 / PENDING 待复核 / PARTIAL 部分判定 / DONE_CLEAN 已完成·无污染 / DONE_POLLUTED 已完成·有污染")
    private String pollutionStatus;

    @Schema(description = "已判定物料行数（同一行的重复判定只算一行）")
    private Integer pollutionJudgedLines;

    @Schema(description = "物料行总数")
    private Integer pollutionLineCount;

    @Schema(description = "待复核行数")
    private Integer pollutionPendingLines;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
