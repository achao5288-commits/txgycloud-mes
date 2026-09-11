package cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * MES 安全环保检测-排污许可证 DO
 *
 * 排污许可是全厂环保数据的"基准源"：排放口(排放限值/许可年总量)挂在本证下，
 * 在线监测数据按本证限值比对，执行报告按证载频次自动汇总（团队终版 §六）。
 * 本期先落成可维护的许可证主数据模块，串联比对/红线/报告为后续迭代。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_pollution_permit")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetPollutionPermitDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 排污许可证编号（全库唯一，跨租户须加 -H 后缀）
     */
    private String permitNo;
    /**
     * 持证单位名称（排放单位/企业）
     */
    private String enterpriseName;
    /**
     * 发证机关
     */
    private String issuingAuthority;
    /**
     * 发证日期
     */
    private LocalDate issueDate;
    /**
     * 许可有效期起始
     */
    private LocalDate startDate;
    /**
     * 许可有效期止
     */
    private LocalDate endDate;
    /**
     * 绑定的排放口编号列表（逗号分隔，如 DA001,DA002）
     */
    private String outletCodes;
    /**
     * 许可年排放总量 JSON 文本：[{"pollutantCode","pollutantName","annualLimitT","annualUsedT"}]
     */
    private String annualLimits;
    /**
     * 执行报告配置 JSON 文本：[{"reportType":"QUARTERLY/ANNUAL","periodStart","periodEnd"}]
     */
    private String annualReports;
    /**
     * 状态：ACTIVE(有效)/EXPIRED(已过期)/REVOKED(已注销)
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
