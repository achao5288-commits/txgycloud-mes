package cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-电气安全检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_electrical_record")
@KeySequence("mes_set_electrical_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetElectricalRecordDO extends BaseDO {

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
     * 关联设备编号
     */
    private Long deviceId;
    /**
     * 检测位置
     */
    private String location;
    /**
     * 检测项目（接地电阻/绝缘电阻/漏电保护/等电位等）
     */
    private String checkItem;
    /**
     * 测量值
     */
    private BigDecimal measuredValue;
    /**
     * 单位：Ω/MΩ/mA/V
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
     * 检测仪器编号
     */
    private String instrumentNo;
    /**
     * 仪器校准状态：0-未校准 1-已校准
     */
    private Integer instrumentCalibOk;
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
