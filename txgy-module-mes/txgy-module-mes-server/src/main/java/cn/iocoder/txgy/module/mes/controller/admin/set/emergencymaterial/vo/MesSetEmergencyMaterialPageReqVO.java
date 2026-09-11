package cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-应急物资 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetEmergencyMaterialPageReqVO extends PageParam {

    @Schema(description = "物资编号")
    private String materialNo;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)")
    private String materialType;

    @Schema(description = "状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单")
    private String status;

}
