package cn.iocoder.txgy.module.mes.dal.dataobject.set.monitor;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-污染物超标事件 DO
 *
 * <p>看板的「超标明细」不是查出来的、是**算出来存下来的**：读数表里只有逐条的实测值，
 * 而口径要的是「按小时均值判定、同一小时只计 1 次」——同一小时的第 2 条读数不该再算一次超标。
 * 于是本表按 (排放口, 因子, 小时) 唯一地固化一条事件，看板读它、派单/闭环写它。
 *
 * <p>只存判定结果与处置状态，**不复制浓度**：读数原文仍在 mes_set_exhaust_gas / mes_set_wastewater，
 * 两处都存浓度迟早会不同步。
 *
 * <p>表上没有业务唯一键（本仓已统一移除业务唯一键），
 * 「同一小时只 1 条」由 Service 层幂等 upsert 保证。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_monitor_exceed")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetMonitorExceedDO extends BaseDO {

    /**
     * 待处置
     */
    public static final String STATUS_PENDING = "PENDING";
    /**
     * 处置中
     */
    public static final String STATUS_PROCESSING = "PROCESSING";
    /**
     * 已闭环
     */
    public static final String STATUS_CLOSED = "CLOSED";

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 排放口编号(mes_set_emission_outlet.id)
     */
    private Long outletId;

    /**
     * 排放口编号快照(排口改名/停用后历史仍可读)
     */
    private String outletCode;

    /**
     * 排放口名称快照
     */
    private String outletName;

    /**
     * 监控因子编码(与读数表 pollutant_code 同域)
     */
    private String pollutantCode;

    /**
     * 监控因子名称
     */
    private String pollutantName;

    /**
     * 小时均值
     */
    private BigDecimal monitorValue;

    /**
     * 限值(取自排放口 permit_limits)
     */
    private BigDecimal limitValue;

    /**
     * 单位 mg/m3 或 mg/L
     */
    private String unit;

    /**
     * 超标倍数 = 小时均值 / 限值
     */
    private BigDecimal multiple;

    /**
     * 超标发生时间(取整到小时，即小时起点)
     */
    private LocalDateTime occurTime;

    /**
     * 持续时长(分钟)，同因子连续超标小时数 × 60
     */
    private Integer durationMin;

    /**
     * 处置状态：PENDING/PROCESSING/CLOSED
     */
    private String handleStatus;

    /**
     * 处置人(派单时按当前登录人回填，前端不猜)
     */
    private String handler;

    /**
     * 派单时间
     */
    private LocalDateTime dispatchTime;

    /**
     * 闭环时间
     */
    private LocalDateTime closedTime;

    /**
     * 备注
     */
    private String remark;

}
