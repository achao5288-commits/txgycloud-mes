package cn.iocoder.txgy.module.mes.dal.dataobject.set.plan;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * MES 安全环保检测-检测计划 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_plan")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 计划编号
     */
    private String planNo;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 触发类型：PERIODIC(周期)/EVENT(事件)
     */
    private String planType;

    /**
     * 周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY
     */
    private String periodType;

    /**
     * 生效开始日期
     */
    private LocalDate startDate;

    /**
     * 生效结束日期
     */
    private LocalDate endDate;

    /**
     * 关联设备编号(事件/设备型)
     */
    private Long machineryId;

    /**
     * 关联工序编号(事件/工单型)
     */
    private Long operationId;

    /**
     * 关联检测标准编号
     */
    private Long standardId;

    /**
     * 责任人/执行人编号
     */
    private Long assigneeId;

    /**
     * 状态：DRAFT/ACTIVE/STOPPED
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
