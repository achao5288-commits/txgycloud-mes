package cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
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
     * 记录编号 NOISE-YYYYMMDD-NNN
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
     * 关联人员编号(个体剂量计佩戴人)
     */
    private Long empId;

    /**
     * 监测类型：STATIONARY(固定式声级计)/PERSONAL(个体剂量计)
     */
    private String sourceType;

    /**
     * 检测位置/区域
     */
    private String location;

    /**
     * 采集方式：IOT_AUTO/MANUAL
     */
    private String collectionMode;

    /**
     * 8小时等效声级 dB(A)
     */
    private BigDecimal lex8h;

    /**
     * 峰值声级 dB(C)
     */
    private BigDecimal lpeak;

    /**
     * 限值 dB(A)≤85
     */
    private BigDecimal limitLex8h;

    /**
     * 限值 dB(C)≤140
     */
    private BigDecimal limitLpeak;

    /**
     * 频谱分析(倍频程，JSON文本)
     */
    private String spectrum;

    /**
     * 关联检测标准编号
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
     * 检测照片URL(逗号分隔)
     */
    private String photoUrls;

    /**
     * 备注
     */
    private String remark;

}
