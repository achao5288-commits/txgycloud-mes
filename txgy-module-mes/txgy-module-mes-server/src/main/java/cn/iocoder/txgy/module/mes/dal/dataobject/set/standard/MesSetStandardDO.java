package cn.iocoder.txgy.module.mes.dal.dataobject.set.standard;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 安全环保检测-检测标准 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_standard")
@KeySequence("mes_set_standard_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetStandardDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 标准编号
     */
    private String standardNo;
    /**
     * 标准名称
     */
    private String standardName;
    /**
     * 检测域：SAFETY/ENV/HEALTH
     */
    private String domain;
    /**
     * 检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE 等
     */
    private String testType;
    /**
     * 引用国标编号（GBZ/GB/T）
     */
    private String refStandard;
    /**
     * 限值配置(JSON 文本)
     */
    private String limitsConfig;
    /**
     * 检测方法描述
     */
    private String method;
    /**
     * 周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT
     */
    private String periodType;
    /**
     * 事件触发配置(JSON 文本)
     */
    private String triggerConfig;
    /**
     * 适用区域/工序(JSON 文本)
     */
    private String applicableArea;
    /**
     * 状态：DRAFT/ACTIVE/OBSOLETE
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
