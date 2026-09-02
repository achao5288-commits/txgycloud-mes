package cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-PPE防护检查记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_ppe_check")
@KeySequence("mes_set_ppe_check_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetPpeCheckDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录编号
     */
    private String recordNo;
    /**
     * 关联人员编号(佩戴校验对象)
     */
    private Long empId;
    /**
     * 关联工单编号
     */
    private Long woId;
    /**
     * 关联工序编号
     */
    private Long operationId;
    /**
     * PPE类别：HELMET/GOGGLES/RESPIRATOR/ANTISTATIC_CLOTHING/EARPLUGS/GLOVES/SAFETY_SHOES等
     */
    private String ppeType;
    /**
     * 检查方式：AI_VISION/MANUAL
     */
    private String checkMode;
    /**
     * 佩戴完整性：1是/0否
     */
    private Integer wearingOk;
    /**
     * 防护等级匹配性：1是/0否
     */
    private Integer gradeMatchOk;
    /**
     * 有效期/损坏检查：1是/0否
     */
    private Integer validOk;
    /**
     * PPE到期日期
     */
    private LocalDate expiryDate;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 是否阻断开工(FAIL时=1)
     */
    private Integer blockFlag;
    /**
     * AI识别设备编号
     */
    private String deviceNo;
    /**
     * 检查人(人工检查时)
     */
    private String checkerName;
    /**
     * 检查时间
     */
    private LocalDateTime checkTime;
    /**
     * 检查照片/AI抓拍URL(逗号分隔)
     */
    private String photoUrls;
    /**
     * 备注
     */
    private String remark;

}
