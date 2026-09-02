package cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 安全环保检测-排放口 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emission_outlet")
@KeySequence("mes_set_emission_outlet_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmissionOutletDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 排放口编号(如 DA001/DW001)
     */
    private String outletCode;
    /**
     * 排放口名称
     */
    private String outletName;
    /**
     * 排放类型：GAS/WASTEWATER/NOISE
     */
    private String outletType;
    /**
     * 主要污染物列表(JSON/CSV文本)
     */
    private String pollutantCodes;
    /**
     * 位置描述
     */
    private String location;
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 排气筒高度 m
     */
    private BigDecimal stackHeight;
    /**
     * 在线监测方式：CEMS/MANUAL/NONE
     */
    private String monitorMethod;
    /**
     * 关联排污许可证号
     */
    private String permitNo;
    /**
     * 许可排放限值JSON文本
     */
    private String permitLimits;
    /**
     * 是否重点/国控排放口：1是/0否
     */
    private Integer isKeyOutlet;
    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
