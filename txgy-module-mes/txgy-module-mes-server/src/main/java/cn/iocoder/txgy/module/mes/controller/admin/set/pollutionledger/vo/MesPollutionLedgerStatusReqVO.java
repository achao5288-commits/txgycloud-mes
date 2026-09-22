package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-污染/危废暂存台账 处置流转 Request VO")
@Data
public class MesPollutionLedgerStatusReqVO {

    @Schema(description = "台账编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "台账编号不能为空")
    private Long id;

    @Schema(description = "流转目标状态：PROCESSING(处置中)/REUSED(已回用)/DISCHARGED(已排放)/DISPOSED(已处置)",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "DISPOSED")
    @NotEmpty(message = "流转目标状态不能为空")
    private String status;

    @Schema(description = "流转备注")
    private String remark;

    @Schema(description = "排放去向（目标状态=已排放 DISCHARGED 时填；留空回退台账去向/库位）", example = "厂区污水处理站")
    private String destination;

    @Schema(description = "执行排放标准/达标口径（目标状态=已排放时填）", example = "GB8978-1996 三级")
    private String standard;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填）。必填：处置流转会改动环保数据，"
            + "缺签名后端直接拒绝（错误码 1_040_819_005）")
    private String signImg;

    @Schema(description = "签署意见（随签字记录留档）")
    private String opinion;

}
