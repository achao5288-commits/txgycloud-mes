package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * MES 在库环保视图分页 Request VO
 *
 * 只读视图：行是「在库的库存行」，服务端一次性 join 出批次污染投影 + 最近一次污染判定 + 四项体检判据。
 */
@Schema(description = "管理后台 - MES 在库环保视图分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesInventoryEnvPageReqVO extends PageParam {

    @Schema(description = "批次号（模糊）", example = "B20260911")
    private String batchCode;

    @Schema(description = "物料编码/名称（模糊）", example = "钢板")
    private String itemName;

    @Schema(description = "物料分类编号（含子分类）", example = "1")
    private Long itemTypeId;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    /**
     * 见 {@link cn.iocoder.txgy.module.mes.service.set.inventoryenv.MesInventoryEnvService} 的 INSPECT_STATUS_* 常量
     */
    @Schema(description = "检测状态：CHECKED 已检 / NOT_CHECKED 未检 / OVERDUE 超期未检 / STOCKPILE 积压", example = "NOT_CHECKED")
    private String inspectStatus;

    @Schema(description = "污染投影：POLLUTED 污染 / CLEAN 无污染 / UNKNOWN 未判定（NULL）", example = "POLLUTED")
    private String pollutionStatus;

    @Schema(description = "在库天数大于", example = "90")
    private Integer minStockDays;

    @Schema(description = "只看混放（同库位既有污染批次又有非污染批次）", example = "true")
    private Boolean mixed;

    /**
     * 体检周期。V1 是全局口径（设计 F8/F9：不做每物料分类一套），
     * 留成请求参数只是为了运营能临时调，不是配置项。
     * ponytail: 全局周期，物料周转差异大时会误报；A1 验证后再改按 item_type_id 配表。
     */
    @Schema(description = "检测周期（天）：距上次判定（无判定则按入库时间）超过该天数即「超期未检」", example = "90")
    private Integer cycleDays = 90;

    @Schema(description = "积压阈值（天）：入库超过该天数即「积压」", example = "180")
    private Integer stockpileDays = 180;

}
