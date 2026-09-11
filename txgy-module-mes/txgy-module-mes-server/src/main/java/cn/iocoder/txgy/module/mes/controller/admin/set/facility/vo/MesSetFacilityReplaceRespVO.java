package cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 治污设施耗材更换结果 Response VO")
@Data
public class MesSetFacilityReplaceRespVO {

    @Schema(description = "设施编号")
    private Long facilityId;

    @Schema(description = "设施名称")
    private String facilityName;

    @Schema(description = "自动产生的危废台账编号（换炭即产生废活性炭 HW49）")
    private Long wasteLedgerId;

    @Schema(description = "自动产生的危废台账联单号")
    private String wasteManifestNo;

    @Schema(description = "危废代码（固定 HW49）", example = "HW49")
    private String wasteCode;

    @Schema(description = "换下数量")
    private java.math.BigDecimal quantity;

    @Schema(description = "本次更换日期")
    private LocalDate replaceDate;

    @Schema(description = "下次更换日期（=本次+周期）")
    private LocalDate nextReplaceDate;

    @Schema(description = "提示语")
    private String message;

}
