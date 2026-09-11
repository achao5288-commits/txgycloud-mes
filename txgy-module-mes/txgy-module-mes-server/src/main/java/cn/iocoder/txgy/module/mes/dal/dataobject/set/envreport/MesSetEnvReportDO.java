package cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
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
     * 报告编号 EP-YYYYMMDD-NNN
     */
    private String reportNo;

    /**
     * 报告名称
     */
    private String reportName;

    /**
     * 报告来源：THIRD_PARTY/INTERNAL
     */
    private String reportType;

    /**
     * 类别：EXHAUST_GAS/WASTEWATER/NOISE/SOLID_WASTE/AMBIENT/COMPREHENSIVE等
     */
    private String reportCategory;

    /**
     * 复用质检报告模板编号
     */
    private Long templateId;

    /**
     * 报告统计期起
     */
    private LocalDate periodStart;

    /**
     * 报告统计期止
     */
    private LocalDate periodEnd;

    /**
     * 报告日期
     */
    private LocalDate reportDate;

    /**
     * 检测结果摘要JSON文本
     */
    private String dataSummary;

    /**
     * 报告文件URL(第三方导入PDF)
     */
    private String fileUrl;

    /**
     * 电子签名文件URL
     */
    private String signUrl;

    /**
     * 自定义表单配置id
     */
    private Long formId;

    /**
     * 状态：DRAFT/APPROVED/REJECTED/ARCHIVED
     */
    private String status;

    /**
     * 审核人(终审)
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
