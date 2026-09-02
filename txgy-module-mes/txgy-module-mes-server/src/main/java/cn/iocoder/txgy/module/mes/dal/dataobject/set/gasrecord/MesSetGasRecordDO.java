package cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-作业环境气体检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_gas_record")
@KeySequence("mes_set_gas_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetGasRecordDO extends BaseDO {

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
     * 关联工单编号（事件触发型）
     */
    private Long woId;
    /**
     * 关联工序编号
     */
    private Long operationId;
    /**
     * 关联作业许可编号
     */
    private Long permitId;
    /**
     * 检测位置
     */
    private String location;
    /**
     * 气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2
     */
    private String gasType;
    /**
     * 检测浓度值
     */
    private BigDecimal concentration;
    /**
     * 单位：mg/m3 / % / %LEL
     */
    private String unit;
    /**
     * 限值
     */
    private BigDecimal limitValue;
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
     * 检测照片 URL（逗号分隔）
     */
    private String photoUrls;
    /**
     * 备注
     */
    private String remark;

}
