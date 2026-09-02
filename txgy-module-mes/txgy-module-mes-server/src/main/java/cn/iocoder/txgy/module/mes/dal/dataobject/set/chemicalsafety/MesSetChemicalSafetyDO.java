package cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-危化品安全巡查记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_chemical_safety")
@KeySequence("mes_set_chemical_safety_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
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
     * 危化品代码（CAS号等）
     */
    private String chemicalCode;
    /**
     * 危化品名称
     */
    private String chemicalName;
    /**
     * 储存地点
     */
    private String storageLocation;
    /**
     * 标识标签齐全：1-是 0-否
     */
    private Integer labelOk;
    /**
     * MSDS（安全技术说明书）齐全：1-是 0-否
     */
    private Integer msdsOk;
    /**
     * 储存条件符合要求：1-是 0-否
     */
    private Integer storageOk;
    /**
     * 分类存放/隔离存放合规：1-是 0-否
     */
    private Integer separationOk;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 异常/不合格描述
     */
    private String problemDesc;
    /**
     * 巡查人
     */
    private String inspector;
    /**
     * 巡查时间
     */
    private LocalDateTime inspectTime;
    /**
     * 备注
     */
    private String remark;

}
