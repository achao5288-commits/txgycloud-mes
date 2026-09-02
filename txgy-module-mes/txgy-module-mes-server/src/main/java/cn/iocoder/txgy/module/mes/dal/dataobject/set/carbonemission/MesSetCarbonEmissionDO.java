package cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MES 安全环保检测-碳排放核算记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_carbon_emission")
@KeySequence("mes_set_carbon_emission_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetCarbonEmissionDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 核算批次号 CARBON-YYYYMM
     */
    private String calcNo;
    /**
     * 核算周期：DAILY/MONTHLY/YEARLY
     */
    private String periodType;
    /**
     * 周期开始日期
     */
    private LocalDate periodStart;
    /**
     * 周期结束日期
     */
    private LocalDate periodEnd;
    /**
     * 关联工单编号(工单级碳足迹可空)
     */
    private Long woId;
    /**
     * 关联能耗记录编号(能源台账)
     */
    private Long sourceRecordId;
    /**
     * 能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM
     */
    private String energyType;
    /**
     * 能源消耗量
     */
    private BigDecimal consumption;
    /**
     * 排放因子
     */
    private BigDecimal emissionFactor;
    /**
     * 碳排放量=consumption*factor
     */
    private BigDecimal carbonEmission;
    /**
     * 单位 tCO2
     */
    private String unit;
    /**
     * 工艺排放
     */
    private BigDecimal processEmission;
    /**
     * 合计
     */
    private BigDecimal totalEmission;

}
