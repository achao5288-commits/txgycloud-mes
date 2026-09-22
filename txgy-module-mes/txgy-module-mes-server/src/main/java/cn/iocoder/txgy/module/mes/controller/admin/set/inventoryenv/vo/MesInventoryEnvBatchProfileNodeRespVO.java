package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 批次档案 - 链上单个节点 Response VO
 *
 * 批次主数据的污染戳（pollution_*）是本模块只读借用的字段：它在污染管控里是「该批是否
 * 仍有未闭环台账行」的投影缓存，正是档案要展示的东西，所以直接读、不回写。
 */
@Schema(description = "管理后台 - MES 批次档案链节点 Response VO")
@Data
public class MesInventoryEnvBatchProfileNodeRespVO {

    @Schema(description = "批次编号", example = "1")
    private Long batchId;

    @Schema(description = "批次号", example = "B20260901001")
    private String batchCode;

    @Schema(description = "物料编号", example = "1")
    private Long itemId;

    @Schema(description = "物料名称", example = "钢板")
    private String itemName;

    @Schema(description = "生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期")
    private LocalDateTime expireDate;

    @Schema(description = "质量状态", example = "1")
    private Integer qualityStatus;

    // ==================== 环保档案（本模块新增的注解） ====================

    @Schema(description = "污染投影：POLLUTED=仍有未闭环污染台账；NULL=无污染管控", example = "POLLUTED")
    private String pollutionStatus;

    @Schema(description = "污染去向（台账最早开放行的存放去向）", example = "危废暂存间")
    private String pollutionLocation;

    @Schema(description = "终审标记（标记后禁止正常出库）", example = "false")
    private Boolean pollutionMarked;

    @Schema(description = "污染来源判定单号", example = "PC20260911001")
    private String pollutionSrcRecord;

    @Schema(description = "最近一次环保判定的复核结果：NULL=未判/待复核", example = "CLEAN")
    private String lastReviewResult;

    @Schema(description = "最近一次环保判定的单号", example = "PC20260911001")
    private String lastCheckRecordNo;

    @Schema(description = "最近一次环保判定的时间")
    private LocalDateTime lastCheckTime;

    @Schema(description = "是否已判（有已复核判定）", example = "true")
    private Boolean judged;

    @Schema(description = "是否当前在库（链上有在库库存行）", example = "true")
    private Boolean inStock;

    @Schema(description = "是否本次档案的查询起点", example = "false")
    private Boolean focus;

}
