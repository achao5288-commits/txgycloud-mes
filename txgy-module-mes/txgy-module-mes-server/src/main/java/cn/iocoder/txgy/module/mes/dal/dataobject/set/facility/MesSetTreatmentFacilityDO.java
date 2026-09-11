package cn.iocoder.txgy.module.mes.dal.dataobject.set.facility;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-治污设施台账 DO
 *
 * 设计文档 §6.3：台账（活性炭吸附/催化燃烧/布袋除尘，关联排放口）
 * + 换炭即登记废活性炭 HW49 入桶 + 停运申报审批。
 *
 * 表名说明：设计稿总表写作 {@code mes_dg_facility}，本仓实际一律 {@code mes_set_*} 前缀，
 * 故落为 {@code mes_set_treatment_facility}（理由见 DDL 抬头）。
 *
 * 停运申报为什么就在本表：见 DDL 抬头。审批留痕走 {@code mes_set_sign_record}
 * （{@code biz_type = FACILITY_SHUTDOWN}），不另建申报表。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_treatment_facility")
@KeySequence("mes_set_treatment_facility_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetTreatmentFacilityDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 设施编号
     */
    private String facilityNo;

    /**
     * 设施名称
     */
    private String facilityName;

    /**
     * 设施类型：ACTIVATED_CARBON 活性炭吸附 / CATALYTIC_COMBUSTION 催化燃烧 / BAG_FILTER 布袋除尘
     */
    private String facilityType;

    /**
     * 关联排放口编号（mes_set_emission_outlet.outlet_code）
     */
    private String outletCode;

    /**
     * 所属产线/工序（设计文档 §3.3：喷涂 / 抛丸打磨）
     */
    private String lineCode;

    /**
     * 运行状态：RUNNING 运行 / STOPPED 停运
     */
    private String runStatus;

    /**
     * 设计风量 m³/h
     */
    private BigDecimal designAirVolume;

    /**
     * 耗材名称（如 活性炭）
     */
    private String consumableName;

    /**
     * 更换周期(天)
     */
    private Integer replaceCycleDays;

    /**
     * 上次更换日期
     */
    private LocalDate lastReplaceDate;

    /**
     * 下次更换日期（=上次+周期，用于到期预警）
     */
    private LocalDate nextReplaceDate;

    /**
     * 停运申报状态：NONE 无 / PENDING 待审批 / APPROVED 已批准 / REJECTED 已驳回
     */
    private String shutdownStatus;

    /**
     * 停运事由
     */
    private String shutdownReason;

    /**
     * 计划停运开始
     */
    private LocalDateTime shutdownPlanStart;

    /**
     * 计划停运结束
     */
    private LocalDateTime shutdownPlanEnd;

    /**
     * 申报时间
     */
    private LocalDateTime shutdownDeclaredAt;

    /**
     * 审批人
     */
    private String shutdownApprover;

    /**
     * 审批时间
     */
    private LocalDateTime shutdownApprovedAt;

    /**
     * 档案状态：ENABLED/DISABLED
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
