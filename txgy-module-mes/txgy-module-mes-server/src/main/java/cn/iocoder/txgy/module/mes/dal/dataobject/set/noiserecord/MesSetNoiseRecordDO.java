package cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-噪声检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_noise_record")
@KeySequence("mes_set_noise_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetNoiseRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录编号
     */
    private String recordNo;
    /**
     * 关联检测计划编号
     */
    private Long planId;
    /**
     * 关联工单编号
     */
    private Long woId;
    /**
     * 关联工序编号
     */
    private Long operationId;
    /**
     * 关联设备编号
     */
    private Long deviceId;
    /**
     * 关联作业人员编号
     */
    private Long empId;
    /**
     * 噪声源类型（固定声源/流动声源/冲击噪声等）
     */
    private String sourceType;
    /**
     * 检测位置
     */
    private String location;
    /**
     * 采集方式：IOT_AUTO/MANUAL
     */
    private String collectionMode;
    /**
     * 8小时等效声级 Lex8h(dB(A))
     */
    private BigDecimal lex8h;
    /**
     * 峰值声级 Lpeak(dB(C))
     */
    private BigDecimal lpeak;
    /**
     * 8小时等效声级限值(dB(A))
     */
    private BigDecimal limitLex8h;
    /**
     * 峰值声级限值(dB(C))
     */
    private BigDecimal limitLpeak;
    /**
     * 频谱分析结果
     */
    private String spectrum;
    /**
     * 关联执行标准编号
     */
    private Long standardId;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 检测仪器编号
     */
    private String instrumentNo;
    /**
     * 检测人
     */
    private String inspector;
    /**
     * 检测时间
     */
    private LocalDateTime inspectTime;
    /**
     * 检测照片 URL（逗号分隔）
     */
    private String photoUrls;
    /**
     * 备注
     */
    private String remark;

}
