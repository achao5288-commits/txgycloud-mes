package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 治污设施耗材更换（换炭）Request VO")
@Data
public class MesSetFacilityReplaceReqVO {

    @Schema(description = "设施编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "设施编号不能为空")
    private Long id;

    @Schema(description = "换下耗材数量（kg）", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    @NotNull(message = "换下耗材数量不能为空")
    private BigDecimal quantity;

    @Schema(description = "容器码（一桶一码，产出的废活性炭入哪只桶）", example = "CTN-ACT-001")
    private String containerCode;

    @Schema(description = "贮存地点", example = "危废暂存间A")
    private String storageLocation;

    @Schema(description = "更换日期（留空取今天）")
    private LocalDate replaceDate;

    @Schema(description = "经办人（留空取当前登录人昵称）")
    private String handler;

}
