package cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MES 安全环保检测-应急物资 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emergency_material")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmergencyMaterialDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 物资编号
     */
    private String materialNo;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)
     */
    private String materialType;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 在库数量
     */
    private BigDecimal quantity;

    /**
     * 定点存放位置
     */
    private String storageLocation;

    /**
     * 生产日期
     */
    private LocalDate produceDate;

    /**
     * 有效期至（临期预警依据）
     */
    private LocalDate expireDate;

    /**
     * 最近检查日期
     */
    private LocalDate lastCheckDate;

    /**
     * 状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
