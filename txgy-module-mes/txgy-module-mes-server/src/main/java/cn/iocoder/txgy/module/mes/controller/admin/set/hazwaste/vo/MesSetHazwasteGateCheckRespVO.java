package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 危废出厂门卫校验 Response VO")
@Data
@Builder
public class MesSetHazwasteGateCheckRespVO {

    @Schema(description = "是否放行（四项校验全过才是 true）")
    private Boolean passed;

    @Schema(description = "联单号")
    private String manifestNo;

    @Schema(description = "危废名称")
    private String wasteName;

    @Schema(description = "联单状态")
    private String status;

    @Schema(description = "联单是否已生效")
    private Boolean effective;

    @Schema(description = "车牌是否核对通过")
    private Boolean vehicleMatched;

    @Schema(description = "缺失的签字角色（HANDOVER/DRIVER/RECEIVER）")
    private List<String> missingSignRoles;

    @Schema(description = "拦截原因清单（为空即放行）")
    private List<String> reasons;

    @Schema(description = "门卫放行时间（已放行时非空）")
    private LocalDateTime gateReleaseTime;

    @Schema(description = "放行门卫")
    private String gateGuard;

}
