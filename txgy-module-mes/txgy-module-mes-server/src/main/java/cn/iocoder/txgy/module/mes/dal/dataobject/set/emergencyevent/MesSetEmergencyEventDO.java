package cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-应急事件 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emergency_event")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmergencyEventDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 事件编号
     */
    private String eventNo;

    /**
     * 事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)
     */
    private String eventType;

    /**
     * 泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK
     */
    private String scenario;

    /**
     * 发生时间
     */
    private LocalDateTime occurTime;

    /**
     * 发生地点
     */
    private String location;

    /**
     * 涉事化学品编码（关联危化品档案取 MSDS/禁配）
     */
    private String chemicalCode;

    /**
     * 泄漏量
     */
    private BigDecimal leakQuantity;

    /**
     * 影响范围
     */
    private String impactScope;

    /**
     * 上报人
     */
    private String reportUser;

    /**
     * 上报时间（PDA 一键上报）
     */
    private LocalDateTime reportTime;

    /**
     * 处置人（派发时指定）
     */
    private String handler;

    /**
     * 派发时间
     */
    private LocalDateTime dispatchTime;

    /**
     * 处置说明
     */
    private String disposeNote;

    /**
     * 处置照片
     */
    private String disposePhotoUrl;

    /**
     * 应急废物桶数（处置时按明细回填，非人工填写）
     */
    private Integer wasteCount;

    /**
     * 应急废物合计净重 kg（处置时按明细回填，非人工填写）
     */
    private BigDecimal wasteQuantity;

    /**
     * 事件报告（原因/数量/处置/整改）
     */
    private String reportContent;

    /**
     * 负责人
     */
    private String approver;

    /**
     * 签字时间
     */
    private LocalDateTime approveTime;

    /**
     * 闭环时间
     */
    private LocalDateTime closedTime;

    /**
     * 状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
