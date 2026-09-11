package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 治污设施同开同停校验 Request VO")
@Data
public class MesSetFacilityCoRunReqVO {

    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "设施编号不能为空")
    private Long id;

    /**
     * 产线是否正在运行。
     *
     * 为什么是入参而不是系统自己查：设计文档 §六「设施未运行 → 工单禁止开工」想挂到工单上，
     * 但 mes_pro_work_order 表**没有产线/设施/排放口列**（已核），自动取不到这个信号。
     * 与其编一个假的产线状态，不如让调用方明确给进来——页面按实际开停情况勾一下，
     * 或者将来补了工单侧的联结字段后改由服务端自动取。**没配就别编。**
     */
    @Schema(description = "产线是否正在运行（来自产线侧，当前需调用方给出）",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产线运行状态不能为空")
    private Boolean productionRunning;

}
