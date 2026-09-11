package cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-职业危害检测 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_occupational_hazard")
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
     * 记录编号 OH-YYYYMMDD-NNN
     */
    private String recordNo;

    /**
     * 关联检测计划编号
     */
    private Long planId;

    /**
     * 关联人员编号(个体暴露监测时；岗位检测可空)
     */
    private Long empId;

    /**
     * 因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL
     */
    private String factorCategory;

    /**
     * 具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT
     */
    private String factorCode;

    /**
     * 检测岗位/工作场所
     */
    private String workplace;

    /**
     * 数据来源：REUSE/ORIGINAL
     */
    private String sourceRefType;

    /**
     * 来源安全检测记录id(复用气体/噪声/粉尘)
     */
    private Long sourceRecordId;

    /**
     * 实测浓度/强度
     */
    private BigDecimal measuredValue;

    /**
     * 单位 mg/m3/dB(A)/mSv/C/m-s2等
     */
    private String unit;

    /**
     * 接触限值类型：MAC/PC-TWA/PC-STEL
     */
    private String limitType;

    /**
     * 职业接触限值
     */
    private BigDecimal oelValue;

    /**
     * 引用国标
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
