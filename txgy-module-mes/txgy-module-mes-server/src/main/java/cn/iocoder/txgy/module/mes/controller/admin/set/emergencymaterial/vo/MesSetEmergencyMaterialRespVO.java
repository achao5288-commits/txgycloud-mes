package cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-应急物资 Response VO")
@Data
public class MesSetEmergencyMaterialRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "物资编号")
    private String materialNo;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)")
    private String materialType;

    @Schema(description = "规格型号")
    private String spec;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "在库数量")
    private BigDecimal quantity;

    @Schema(description = "定点存放位置")
    private String storageLocation;

    @Schema(description = "生产日期")
    private LocalDate produceDate;

    @Schema(description = "有效期至（临期预警依据）")
    private LocalDate expireDate;

    @Schema(description = "最近检查日期")
    private LocalDate lastCheckDate;

    @Schema(description = "状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
