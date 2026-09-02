package cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-废气排放检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_exhaust_gas")
@KeySequence("mes_set_exhaust_gas_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetExhaustGasDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录编号 EXGAS-YYYYMMDD-NNN
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
     * 污染物：SO2/NOX/PM/VOCs/HCL/HF等
     */
    private String pollutantCode;
    /**
     * 排放浓度 mg/m3
     */
    private BigDecimal concentration;
    /**
     * 单位 mg/m3等
     */
    private String unit;
    /**
     * 标态干烟气流量 m3/h
     */
    private BigDecimal flowRate;
    /**
     * 折算排放速率 kg/h
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
     * 采集方式：CEMS_AUTO/MANUAL
     */
    private String collectionMode;
    /**
     * 监测时间
     */
    private LocalDateTime monitorTime;
    /**
     * CEMS设备编号/采样仪器号
     */
    private String instrumentNo;
    /**
     * 监测人(手工时)
     */
    private String inspector;
    /**
     * 备注
     */
    private String remark;

}
