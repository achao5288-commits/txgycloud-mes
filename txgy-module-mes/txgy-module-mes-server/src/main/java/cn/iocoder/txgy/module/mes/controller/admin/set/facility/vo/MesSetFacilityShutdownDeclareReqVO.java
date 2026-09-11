package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 治污设施停运申报 Request VO")
@Data
public class MesSetFacilityShutdownDeclareReqVO {

    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "设施编号不能为空")
    private Long id;

    @Schema(description = "停运事由", example = "活性炭更换，计划停机 4 小时")
    private String shutdownReason;

    @Schema(description = "计划停运开始")
    private LocalDateTime shutdownPlanStart;

    @Schema(description = "计划停运结束")
    private LocalDateTime shutdownPlanEnd;

}
