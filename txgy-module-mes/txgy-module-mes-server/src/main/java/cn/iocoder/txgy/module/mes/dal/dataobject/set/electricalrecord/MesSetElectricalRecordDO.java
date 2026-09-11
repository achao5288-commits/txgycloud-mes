package cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-电气安全检查 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_electrical_record")
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
     * 记录编号 ELEC-YYYYMMDD-NNN
     */
    private String recordNo;

    /**
     * 关联检测计划编号
     */
    private Long planId;

    /**
     * 关联设备/配电设施编号
     */
    private Long deviceId;

    /**
     * 检测位置(配电柜/线路区域)
     */
    private String location;

    /**
     * 检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE
     */
    private String checkItem;

    /**
     * 实测值
     */
    private BigDecimal measuredValue;

    /**
     * 单位 MΩ/Ω/mA/ms/V
     */
    private String unit;

    /**
     * 标准限值
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
     * 仪器校准状态快照：1已校准/0未校准
     */
    private Boolean instrumentCalibOk;

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
