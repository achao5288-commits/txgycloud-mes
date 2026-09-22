package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 在库环保视图 Response VO
 *
 * 一个在库库存行 = 一行。判据（未检/超期/积压/混放）由服务端算好，前端只渲染不重算，
 * 避免前后端时钟与口径打架。
 */
@Schema(description = "管理后台 - MES 在库环保视图 Response VO")
@Data
public class MesInventoryEnvRespVO {

    @Schema(description = "库存行编号（前端行键）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    // ==================== 物料 ====================

    @Schema(description = "物料分类编号", example = "1")
    private Long itemTypeId;

    @Schema(description = "物料编号", example = "1")
    private Long itemId;

    @Schema(description = "物料编码", example = "M001")
    private String itemCode;

    @Schema(description = "物料名称", example = "钢板")
    private String itemName;

    @Schema(description = "规格型号", example = "10mm*100mm")
    private String specification;

    @Schema(description = "计量单位名称", example = "千克")
    private String unitMeasureName;

    // ==================== 库存位置 ====================

    @Schema(description = "批次编号（为空则该行无批次，无法发起在库检测）", example = "1")
    private Long batchId;

    @Schema(description = "批次号", example = "B20260911")
    private String batchCode;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "原料仓")
    private String warehouseName;

    @Schema(description = "库区编号", example = "1")
    private Long locationId;

    @Schema(description = "库区名称", example = "A 区")
    private String locationName;

    @Schema(description = "库位编号", example = "1")
    private Long areaId;

    @Schema(description = "库位名称", example = "A-01")
    private String areaName;

    @Schema(description = "库位是否受控（mes_wm_warehouse_area.pollution_control）", example = "true")
    private Boolean areaPollutionControl;

    @Schema(description = "在库数量", example = "100.0000")
    private BigDecimal quantity;

    @Schema(description = "入库时间")
    private LocalDateTime receiptTime;

    @Schema(description = "在库天数（服务端按入库时间算）", example = "120")
    private Integer stockDays;

    @Schema(description = "是否冻结", example = "false")
    private Boolean frozen;

    // ==================== 污染投影（批次主数据，由判定/冻结流程回写） ====================

    @Schema(description = "批次污染状态：POLLUTED / 其它 / NULL(未判定，见 F16)", example = "POLLUTED")
    private String pollutionStatus;

    @Schema(description = "污染物隔离位置", example = "危废暂存间")
    private String pollutionLocation;

    @Schema(description = "批次是否标记（禁止正常出库）", example = "false")
    private Boolean pollutionMarked;

    @Schema(description = "源污染判定记录编号", example = "PC-20260911-001")
    private String pollutionSrcRecord;

    // ==================== 最近一次判定 ====================

    @Schema(description = "判定记录编号（为空 = 该批次从未检测）", example = "1")
    private Long checkId;

    @Schema(description = "判定记录号", example = "PC-20260911-001")
    private String checkRecordNo;

    @Schema(description = "AI 初筛结论：CLEAN / POLLUTED / UNCERTAIN", example = "CLEAN")
    private String aiResult;

    @Schema(description = "AI 置信度", example = "90")
    private Integer aiConfidence;

    @Schema(description = "人工复核结论（为空 = 待复核，不算有效判定）", example = "CLEAN")
    private String reviewResult;

    @Schema(description = "人工复核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "去向/处置方式", example = "CONTROLLED_STORAGE")
    private String disposition;

    @Schema(description = "受控库位名称快照", example = "危废库 A-01")
    private String checkLocation;

    @Schema(description = "受控库位编号", example = "1")
    private Long checkLocationId;

    @Schema(description = "判定是否标记", example = "false")
    private Boolean checkMarked;

    @Schema(description = "台账行编号（标记终审走该行）", example = "1")
    private Long ledgerId;

    @Schema(description = "台账是否已标记终审", example = "false")
    private Boolean ledgerMarked;

    /**
     * true = 这条判定是靠 batch_no 字符串对上的历史记录（batch_id 为空）。
     * 存量 100% 是这种（phase 1 之前建的记录都没有 batch_id），页面上要显形，
     * 不能让人以为「已检测」都是锚得住批次的。
     */
    @Schema(description = "判定是否只能按批次号字符串匹配（历史记录无 batch_id）", example = "true")
    private Boolean checkByBatchNo;

    // ==================== 四项体检判据（服务端算） ====================

    @Schema(description = "判据：未检测", example = "false")
    private Boolean notChecked;

    @Schema(description = "判据：超期未检（距上次判定/入库超过检测周期，且无有效判定）", example = "true")
    private Boolean overdue;

    @Schema(description = "判据：积压（入库超过积压阈值）", example = "false")
    private Boolean stockpiled;

    @Schema(description = "判据：混放（同库位既有 POLLUTED 批次又有非 POLLUTED 批次）", example = "false")
    private Boolean mixed;

}
