package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - MES 安全环保检测-台账标记品终审 Request VO
 *
 * 标记品管理（需求 5.6 环保专员「终审」权限）：标记/解除标记只由环保专员定夺，
 * 终审结果同步投影到批次污染戳（marked 即禁止正常出库/销售）。
 */
@Schema(description = "管理后台 - MES 安全环保检测-台账标记品终审 Request VO")
@Data
public class MesPollutionLedgerMarkReqVO {

    @Schema(description = "台账编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "台账编号不能为空")
    private Long id;

    @Schema(description = "终审结论：true=标记（禁止正常出库），false=解除标记", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "终审结论不能为空")
    private Boolean marked;

    @Schema(description = "终审意见")
    private String remark;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填）。必填：终审会改动环保数据，"
            + "缺签名后端直接拒绝（错误码 1_040_819_004）")
    private String signImg;

}
