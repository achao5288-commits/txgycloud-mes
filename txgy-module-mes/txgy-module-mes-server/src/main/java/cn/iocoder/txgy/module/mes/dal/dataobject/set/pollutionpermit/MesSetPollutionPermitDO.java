package cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * MES 安全环保检测-排污许可管理 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_pollution_permit")
@KeySequence("mes_set_pollution_permit_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
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
     * 排污许可证编号
     */
    private String permitNo;
    /**
     * 企业名称
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
     * 有效期开始日期
     */
    private LocalDate startDate;
    /**
     * 有效期截止日期
     */
    private LocalDate endDate;
    /**
     * 排污口编码（多个以逗号分隔）
     */
    private String outletCodes;
    /**
     * 年度许可排放量（各污染物限值描述）
     */
    private String annualLimits;
    /**
     * 年度执行报告记录
     */
    private String annualReports;
    /**
     * 状态：VALID(有效)/EXPIRING(即将到期)/EXPIRED(已失效)/REVOKED(已注销)
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
