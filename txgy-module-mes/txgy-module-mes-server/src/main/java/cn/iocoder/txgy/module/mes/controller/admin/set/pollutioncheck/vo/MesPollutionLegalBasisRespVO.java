package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - MES 污染判定 可引用法规依据选项 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesPollutionLegalBasisRespVO {

    @Schema(description = "展示标签：法规简称 + 条款号 + 要点摘要", example = "固废法（2020修订） 第七十九条 · 按环保标准贮存利用处置危废")
    private String label;

    @Schema(description = "落库值：法规全称 + 条款号 + 要点全文")
    private String value;

}
