package cn.iocoder.txgy.module.mes.dal.dataobject.set.hazardouswaste;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-危废台账 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_hazardous_waste")
@KeySequence("mes_set_hazardous_waste_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetHazardousWasteDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 转移联单编号
     */
    private String manifestNo;
    /**
     * 危废代码（HW代码，如 HW08）
     */
    private String wasteCode;
    /**
     * 危废名称
     */
    private String wasteName;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 数量单位
     */
    private String quantityUnit;
    /**
     * 状态阶段：GENERATED/STORED/TRANSFERRED/DISPOSED
     */
    private String stage;
    /**
     * 储存地点
     */
    private String storageLocation;
    /**
     * 接收方/处置方
     */
    private String counterparty;
    /**
     * 关联工单编号
     */
    private Long woId;
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    /**
     * 处理人
     */
    private String handler;
    /**
     * 状态：DRAFT/APPROVED/REJECTED
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
