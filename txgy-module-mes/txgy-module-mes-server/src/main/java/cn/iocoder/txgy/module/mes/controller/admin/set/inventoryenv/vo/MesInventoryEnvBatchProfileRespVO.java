package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 批次档案 Response VO
 *
 * 前向（这批货去了哪）+ 后向（这批货由什么来）两条链，每个节点带上该批的环保档案。
 * 链本身复用仓储既有的追溯实现，本模块只做注解——环保不反向侵入 wm。
 */
@Schema(description = "管理后台 - MES 批次档案 Response VO")
@Data
public class MesInventoryEnvBatchProfileRespVO {

    @Schema(description = "查询起点批次的环保档案")
    private MesInventoryEnvBatchProfileNodeRespVO focus;

    @Schema(description = "前向链：本批流向的下游批次（含起点）")
    private List<MesInventoryEnvBatchProfileNodeRespVO> forward;

    @Schema(description = "后向链：本批由来的上游批次（含起点）")
    private List<MesInventoryEnvBatchProfileNodeRespVO> backward;

    @Schema(description = "链上存在未判定批次的个数（未判定=没人判过，不等于无污染）", example = "2")
    private Integer unjudgedCount;

    @Schema(description = "链上存在污染批次的个数", example = "1")
    private Integer pollutedCount;

}
