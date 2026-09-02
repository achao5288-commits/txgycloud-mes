package cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-废水排放检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_wastewater")
@KeySequence("mes_set_wastewater_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetWastewaterDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录编号 WW-YYYYMMDD-NNN
     */
    private String recordNo;
    /**
     * 关联排放口编号
     */
    private Long outletId;
    /**
     * 关联工单编号
     */
    private Long woId;
    /**
     * 实验室样品编号(手工检测时)
     */
    private String sampleNo;
    /**
     * 污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等
     */
    private String pollutantCode;
    /**
     * 检测浓度 mg/L(pH无量纲)
     */
    private BigDecimal concentration;
    /**
     * 单位 mg/L(pH留空)
     */
    private String unit;
    /**
     * 排放流量 m3/h
     */
    private BigDecimal flowRate;
    /**
     * 排放量 kg/h
     */
    private BigDecimal emissionAmount;
    /**
     * 限值
     */
    private BigDecimal limitValue;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 采集方式：ONLINE_AUTO/LAB_MANUAL
     */
    private String collectionMode;
    /**
     * 监测时间
     */
    private LocalDateTime monitorTime;
    /**
     * 在线监测仪/化验设备编号
     */
    private String instrumentNo;
    /**
     * 化验员(手工时)
     */
    private String inspector;
    /**
     * 备注
     */
    private String remark;

}
