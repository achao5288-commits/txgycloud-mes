package cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-环保检测报告 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_env_report")
@KeySequence("mes_set_env_report_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEnvReportDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 报告编号
     */
    private String reportNo;
    /**
     * 报告名称
     */
    private String reportName;
    /**
     * 报告类型：MONTHLY(月度)/QUARTERLY(季度)/ANNUAL(年度)/OTHER(其他)
     */
    private String reportType;
    /**
     * 报告类别：SELF_MONITOR(自行监测)/COMPLIANCE(合规性)/OTHER(其他)
     */
    private String reportCategory;
    /**
     * 关联报告模板编号
     */
    private Long templateId;
    /**
     * 报告周期开始日期
     */
    private LocalDate periodStart;
    /**
     * 报告周期结束日期
     */
    private LocalDate periodEnd;
    /**
     * 报告生成日期
     */
    private LocalDate reportDate;
    /**
     * 数据汇总摘要
     */
    private String dataSummary;
    /**
     * 报告文件 URL
     */
    private String fileUrl;
    /**
     * 签章文件 URL
     */
    private String signUrl;
    /**
     * 关联表单编号
     */
    private Long formId;
    /**
     * 状态：DRAFT(草稿)/SUBMITTED(已提交)/APPROVED(已审批)/REJECTED(已驳回)
     */
    private String status;
    /**
     * 审核人
     */
    private String auditBy;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 备注
     */
    private String remark;

}
