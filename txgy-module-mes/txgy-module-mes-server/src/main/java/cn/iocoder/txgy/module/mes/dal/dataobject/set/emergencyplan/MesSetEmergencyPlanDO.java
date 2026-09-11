package cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * MES 安全环保检测-应急预案 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emergency_plan")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmergencyPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 预案编号
     */
    private String planNo;

    /**
     * 预案名称
     */
    private String planName;

    /**
     * 预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)
     */
    private String planType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发布日期（备案时限与评估周期的起算点）
     */
    private LocalDate publishDate;

    /**
     * 备案截止日（发布 + 20 个工作日，派生值不进表单）
     */
    private LocalDate filingDeadline;

    /**
     * 备案号（报生态环境部门后登记）
     */
    private String filingNo;

    /**
     * 备案日期
     */
    private LocalDate filingDate;

    /**
     * 预案附件地址
     */
    private String attachUrl;

    /**
     * 上次评估修订日期
     */
    private LocalDate lastReviewDate;

    /**
     * 下次评估修订日期（发布或上次修订 + 3 年，派生值不进表单）
     */
    private LocalDate nextReviewDate;

    /**
     * 修订原因（工艺/物料/法规变化）
     */
    private String reviewReason;

    /**
     * 状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
