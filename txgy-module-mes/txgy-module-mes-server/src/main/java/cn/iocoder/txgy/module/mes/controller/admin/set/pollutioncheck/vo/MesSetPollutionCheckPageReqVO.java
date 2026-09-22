package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPollutionCheckPageReqVO extends PageParam {

    @Schema(description = "环节：PURCHASE_INBOUND/MATERIAL_ISSUE/WASTE_INTERMEDIATE/FINISHED_PRODUCT", example = "PURCHASE_INBOUND")
    private String stage;

    @Schema(description = "关联单号(采购入库/领料/产品入库等真实单据号)", example = "IR20260329000001")
    private String bizNo;

    @Schema(description = "批次编号（mes_wm_batch.id），在库/入库检测按此精确取该批全部判定", example = "9")
    private Long batchId;

    @Schema(description = "批次关联状态：null=全部 true=已关联批次 false=未关联（历史手工录入，无法回到库存）", example = "false")
    private Boolean linked;

    @Schema(description = "批次号（快照字段，模糊匹配）", example = "B20260903-001")
    private String batchNo;

    @Schema(description = "物料/产品名称(模糊)", example = "涂料")
    private String itemName;

    @Schema(description = "AI 初筛结果：CLEAN/POLLUTED/UNCERTAIN", example = "UNCERTAIN")
    private String aiResult;

    @Schema(description = "是否已复核：null=全部 true=已复核 false=待复核", example = "false")
    private Boolean reviewed;

    @Schema(description = "人工复核分类：CLEAN/POLLUTED", example = "POLLUTED")
    private String reviewResult;

    @Schema(description = "成品达标分支：QUALIFIED/REWORK/SCRAPPED", example = "SCRAPPED")
    private String finishedResult;

    @Schema(description = "复核人(昵称)", example = "刘洋")
    private String reviewBy;

    @Schema(description = "创建时间区间(判定时间)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] createTime;

}
