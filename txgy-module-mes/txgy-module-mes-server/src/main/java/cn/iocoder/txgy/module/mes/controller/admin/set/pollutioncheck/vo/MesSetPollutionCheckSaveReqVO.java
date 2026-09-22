package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 新增/修改 Request VO")
@Data
public class MesSetPollutionCheckSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "环节：PURCHASE_INBOUND(采购入库)/MATERIAL_ISSUE(生产领用)/WASTE_INTERMEDIATE(中间废弃物)/FINISHED_PRODUCT(成品)/IN_STOCK(在库复查)",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "PURCHASE_INBOUND")
    @NotEmpty(message = "环节不能为空")
    private String stage;

    @Schema(description = "关联单号")
    private String bizNo;

    @Schema(description = "批次编号（mes_wm_batch.id）。传了则由服务端反查批次回填批次号/物料编码/名称/规格，" +
            "请求里的同名字段会被覆盖——避免人工录入的文本与库存对不上。在库/入库检测必须传。", example = "9")
    private Long batchId;

    @Schema(description = "批次号（batchId 存在时为显示快照，可空）", example = "B20260903-001")
    private String batchNo;

    @Schema(description = "物料/产品编码", example = "MAT-0001")
    private String itemCode;

    // 不再是 @NotEmpty：传了 batchId 时名称由服务端从批次反查回填，请求方不必知道名称。
    // 「批次编号或物料名称至少其一」由 Service 在解析批次之后统一校验（否则这条约束会互相打架）。
    @Schema(description = "物料/产品名称。传了 batchId 则由服务端回填并覆盖本字段", example = "含铅涂料")
    private String itemName;

    @Schema(description = "规格")
    private String itemSpec;

    @Schema(description = "现场观察到的污染特征（多选）：存 SIGN_* code，换行分隔。供复核时收窄法条候选，可空")
    private String fieldSigns;

    @Schema(description = "重量(kg)", example = "12.500")
    private BigDecimal weight;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填）。创建判定（含批量发起检测）必填："
            + "发起检测是人为主张的判定，缺签名后端直接拒绝（错误码 1_040_818_019）；AI 预筛(/prescreen)与改单不校验")
    private String signImg;

    @Schema(description = "签署意见（随签字记录留档）")
    private String opinion;

}
