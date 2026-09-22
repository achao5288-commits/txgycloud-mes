package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - MES 污染判定 现场污染特征选项 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesFieldSignRespVO {

    @Schema(description = "展示文案（现场语言，非条款语言）", example = "包装破损 / 容器渗漏")
    private String label;

    @Schema(description = "特征 code（落库值）", example = "SIGN_LEAK")
    private String value;

    @Schema(description = "该特征命中的法规依据候选，供人工复核收窄选项；无对应法条时为空")
    private List<MesPollutionLegalBasisRespVO> articles;

}
