package cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo;

import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 安全环保检测-危废反向查来源 Response VO
 *
 * 设计文档 §9「物料衡算」的原话是：**危废可由源头工单反推投料批，否则报"无源"**。
 * 所以这个 VO 的核心不是"列出关联数据"，而是 `sourced` 这个判断题：
 * 每一行台账都必须能答出"这只桶是哪来的"，答不出就明写 {@code unsourcedReason}，
 * 不许用空数组冒充"查过了没问题"——无源本身就是需要人去补录的异常。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 危废反向查来源 Response VO")
@Data
public class MesSetTraceReverseRespVO {

    @Schema(description = "查询用的桶码（原样回显，便于前端对上是哪一次查询）")
    private String containerCode;

    @Schema(description = "查询用的联单号")
    private String manifestNo;

    @Schema(description = "台账里有没有这只桶/这张联单")
    private Boolean found;

    @Schema(description = "是否每一行台账都查到了源头。false 时看 unsourcedReason")
    private Boolean sourced;

    @Schema(description = "无源的说明；有源时为 null")
    private String unsourcedReason;

    @Schema(description = "命中的危废台账行（同一只桶多次进出会有多行）")
    private List<Ledger> ledgers;

    @Schema(description = "该桶的过秤证据。**为空不等于没称过**——治理设施换炭走的是直接建台账，"
            + "没有过秤环节，这点在 detail 里说明")
    private List<MesSetWeighRecordDO> weighRecords;

    @Schema(description = "该桶的时间轴（追溯链节点，按时间正序）")
    private List<MesSetTraceChainDO> traceNodes;

    @Schema(description = "该桶的签字记录（会签/门卫放行等凭据）")
    private List<MesSetSignRecordDO> signRecords;

    @Schema(description = "危废台账行 + 它反查出来的源头")
    @Data
    public static class Ledger {

        @Schema(description = "台账行编号")
        private Long id;

        @Schema(description = "联单号")
        private String manifestNo;

        @Schema(description = "危废类别代码")
        private String wasteCode;

        @Schema(description = "危废名称")
        private String wasteName;

        @Schema(description = "数量")
        private BigDecimal quantity;

        @Schema(description = "单位")
        private String quantityUnit;

        @Schema(description = "去向环节")
        private String stage;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "暂存库位")
        private String storageLocation;

        @Schema(description = "桶码")
        private String containerCode;

        @Schema(description = "对方单位/来源设施名")
        private String counterparty;

        @Schema(description = "关联工单编号（**只回 ID，不假装能解开**：工单侧目前没有环保联结字段，"
                + "反推投料批要等工单侧补上关联列）")
        private Long woId;

        @Schema(description = "处理时间")
        private LocalDateTime handleTime;

        @Schema(description = "经手人")
        private String handler;

        @Schema(description = "源头类型：EMERGENCY(应急处置产生) / FACILITY_CARBON(治理设施换炭) / UNSOURCED(无源)")
        private String sourceType;

        @Schema(description = "源头单据号：应急事件编号 / 治理设施编号")
        private String sourceDocNo;

        @Schema(description = "源头名称：应急事件类型+地点 / 设施名称")
        private String sourceName;

        @Schema(description = "源头说明或**无源原因**，直接给人看")
        private String sourceDetail;

        @Schema(description = "源头单据的主键（应急事件 id / 设施 id），便于前端跳转")
        private Long sourceId;

    }

}
