package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 Response VO")
@Data
public class MesSetPollutionCheckRespVO {

    @Schema(description = "编号", example = "1")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "记录编号")
    @ExcelProperty("记录编号")
    private String recordNo;

    @Schema(description = "环节")
    @ExcelProperty("环节")
    private String stage;

    @Schema(description = "关联单号")
    @ExcelProperty("关联单号")
    private String bizNo;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchNo;

    @Schema(description = "物料/产品编码")
    @ExcelProperty("物料编码")
    private String itemCode;

    @Schema(description = "物料/产品名称")
    @ExcelProperty("物料名称")
    private String itemName;

    @Schema(description = "规格")
    @ExcelProperty("规格")
    private String itemSpec;

    @Schema(description = "重量(kg)", example = "12.500")
    @ExcelProperty("重量(kg)")
    private BigDecimal weight;

    @Schema(description = "AI 初筛结果：CLEAN/POLLUTED/UNCERTAIN")
    @ExcelProperty("AI初筛")
    private String aiResult;

    @Schema(description = "AI 置信度(%)")
    @ExcelProperty("AI置信度")
    private Integer aiConfidence;

    @Schema(description = "AI 判定依据")
    @ExcelProperty("AI依据")
    private String aiReason;

    @Schema(description = "AI 推荐存储方法")
    @ExcelProperty("AI推荐存储")
    private String suggestedStorage;

    @Schema(description = "人工复核结果：CLEAN/POLLUTED，空=待复核")
    @ExcelProperty("复核结果")
    private String reviewResult;

    @Schema(description = "成品达标分支：QUALIFIED/REWORK/SCRAPPED，仅成品环节有值")
    @ExcelProperty("达标分支")
    private String finishedResult;

    @Schema(description = "最终存储方法")
    @ExcelProperty("存储方法")
    private String storageMethod;

    @Schema(description = "处置方式")
    @ExcelProperty("处置方式")
    private String disposition;

    @Schema(description = "去向/库位")
    @ExcelProperty("去向/库位")
    private String location;

    @Schema(description = "受控库位编号(mes_wm_warehouse_area.id)")
    @ExcelIgnore
    private Long locationId;

    @Schema(description = "是否标记")
    @ExcelProperty("是否标记")
    private Boolean marked;

    @Schema(description = "复核人")
    @ExcelProperty("复核人")
    private String reviewBy;

    @Schema(description = "复核时间")
    @ExcelProperty("复核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
