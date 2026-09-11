package cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-追溯链节点 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetTraceChainPageReqVO extends PageParam {

    @Schema(description = "追溯对象码(模糊, PC-.../批次码)", example = "PC-2026")
    private String traceCode;

    @Schema(description = "业务关联单号(模糊)", example = "PC-20260907101530001")
    private String bizNo;

    @Schema(description = "业务关联类型：CHECK/LEDGER", example = "CHECK")
    private String bizType;

    @Schema(description = "环节：PURCHASE_INBOUND/MATERIAL_ISSUE/WASTE_INTERMEDIATE/FINISHED_PRODUCT/DISPOSAL", example = "WASTE_INTERMEDIATE")
    private String nodeStage;

    @Schema(description = "批次号（批次全链追溯：按批次聚合该批全部判定的追溯链；给出时优先于 bizNo/traceCode）", example = "BATCH_ITEM_1")
    private String batchNo;

}
