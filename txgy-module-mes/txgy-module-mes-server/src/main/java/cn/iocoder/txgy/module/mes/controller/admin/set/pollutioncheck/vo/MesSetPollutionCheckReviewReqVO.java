package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 人工复核 Request VO")
@Data
public class MesSetPollutionCheckReviewReqVO {

    @Schema(description = "判定记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "判定记录编号不能为空")
    private Long id;

    @Schema(description = "人工复核结果（终态，二值）：CLEAN(无污染)/POLLUTED(有污染)",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "POLLUTED")
    @NotEmpty(message = "人工复核结果不能为空")
    private String reviewResult;

    @Schema(description = "成品达标分支(仅成品环节必填)：QUALIFIED(达标)/REWORK(局部缺陷返工)/SCRAPPED(整体报废)")
    private String finishedResult;

    @Schema(description = "最终存储方法（留空则按复核结果取默认/沿用 AI 建议）")
    private String storageMethod;

    @Schema(description = "处置方式：NORMAL_INBOUND/CONTROLLED_STORAGE/ISSUE_ALLOWED/REJECT_ISSUE/REUSE/DISCHARGE/ISOLATE_STORAGE/MARKED_STORAGE（留空按环节+结果取默认）")
    private String disposition;

    @Schema(description = "去向/库位（库位名称，与 locationId 二选一；给了 locationId 则以其名称为准）")
    private String location;

    @Schema(description = "受控库位编号（mes_wm_warehouse_area.id）：有污染必填且库位须为污染管控库位；无污染可留空")
    private Long locationId;

    @Schema(description = "是否标记（留空则：有污染标记，无污染不标记）")
    private Boolean marked;

    @Schema(description = "复核备注")
    private String remark;

    @Schema(description = "手写签名图片地址（前端手写板上传 infra 文件服务后回填；留空则不存签名图）")
    private String signImg;

}
