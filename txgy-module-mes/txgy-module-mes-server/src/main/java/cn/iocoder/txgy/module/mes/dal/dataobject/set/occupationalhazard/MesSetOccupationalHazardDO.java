package cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-职业病危害因素检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_occupational_hazard")
@KeySequence("mes_set_occupational_hazard_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetOccupationalHazardDO extends BaseDO {

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
     * 关联员工编号
     */
    private Long empId;
    /**
     * 危害因素类别：CHEMICAL(化学)/DUST(粉尘)/PHYSICAL(物理)/BIOLOGICAL(生物)/OTHER(其他)
     */
    private String factorCategory;
    /**
     * 危害因素编码（如 GBZ 2.1-2019 对应代号）
     */
    private String factorCode;
    /**
     * 检测岗位/工作场所
     */
    private String workplace;
    /**
     * 来源关联类型：PLAN(检测计划)/TASK(检测任务)
     */
    private String sourceRefType;
    /**
     * 来源记录编号
     */
    private Long sourceRecordId;
    /**
     * 测量值
     */
    private BigDecimal measuredValue;
    /**
     * 单位（如 mg/m3 / dB(A)）
     */
    private String unit;
    /**
     * 限值类型：MAC(最高容许)/PC-TWA(时间加权平均)/PC-STEL(短时间接触)/NOISE(噪声限值)
     */
    private String limitType;
    /**
     * 职业接触限值(OEL)
     */
    private BigDecimal oelValue;
    /**
     * 参考标准（如 GBZ 2.1-2019）
     */
    private String refStandard;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 检测人
     */
    private String inspector;
    /**
     * 检测时间
     */
    private LocalDateTime inspectTime;
    /**
     * 备注
     */
    private String remark;

}
