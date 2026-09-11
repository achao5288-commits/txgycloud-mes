package cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-粉尘检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_dust_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetDustRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 记录编号 DUST-YYYYMMDD-NNN
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
     * 关联设备编号(除尘/产尘设备)
     */
    private Long deviceId;

    /**
     * 检测位置/作业区域
     */
    private String location;

    /**
     * 检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2
     */
    private String dustType;

    /**
     * 检测浓度/含量
     */
    private BigDecimal concentration;

    /**
     * 游离SiO2含量%(总尘且需矽尘分级时)
     */
    private BigDecimal sio2Content;

    /**
     * 单位 mg/m3/%
     */
    private String unit;

    /**
     * 限值
     */
    private BigDecimal limitValue;

    /**
     * 引用国标
     */
    private String refStandard;

    /**
     * 结果：PASS/FAIL
     */
    private String result;

    /**
     * 采集方式：IOT_AUTO/MANUAL
     */
    private String collectionMode;

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
