package cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * MES 安全环保检测-应急演练 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emergency_drill")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmergencyDrillDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 演练编号
     */
    private String drillNo;

    /**
     * 演练名称
     */
    private String drillName;

    /**
     * 关联预案编号（须为已发布/已备案的预案）
     */
    private Long planId;

    /**
     * 演练时的预案版本快照
     */
    private String planVersion;

    /**
     * 演练类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)
     */
    private String drillType;

    /**
     * 演练日期
     */
    private LocalDate drillDate;

    /**
     * 参加人数
     */
    private Integer participantCount;

    /**
     * 参加人员
     */
    private String participants;

    /**
     * 签到表附件
     */
    private String signSheetUrl;

    /**
     * 演练照片
     */
    private String photoUrl;

    /**
     * 演练视频
     */
    private String videoUrl;

    /**
     * 演练评估
     */
    private String evaluation;

    /**
     * 整改要求（填写后整改状态转 PENDING）
     */
    private String rectifyRequirement;

    /**
     * 整改状态：NONE(无需整改)/PENDING(待整改)/DONE(已整改)
     */
    private String rectifyStatus;

    /**
     * 整改完成日期
     */
    private LocalDate rectifyDoneDate;

    /**
     * 闭环日期
     */
    private LocalDate closedDate;

    /**
     * 状态：PLANNED(已计划)/DONE(已演练)/CLOSED(已闭环)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
