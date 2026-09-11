package cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-危化品安全检查 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_chemical_safety")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetChemicalSafetyDO extends BaseDO {

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
     * 危化品编码
     */
    private String chemicalCode;

    /**
     * 危化品名称
     */
    private String chemicalName;

    /**
     * 存储地点
     */
    private String storageLocation;

    /**
     * 标识完整性：1是/0否
     */
    private Boolean labelOk;

    /**
     * MSDS有效性：1是/0否
     */
    private Boolean msdsOk;

    /**
     * 储存条件(温湿度/通风)合格：1是/0否
     */
    private Boolean storageOk;

    /**
     * 禁忌物分离合格：1是/0否
     */
    private Boolean separationOk;

    /**
     * 结果：PASS/FAIL
     */
    private String result;

    /**
     * 异常/不合格描述
     */
    private String problemDesc;

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
