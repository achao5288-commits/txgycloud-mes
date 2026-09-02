package cn.iocoder.txgy.module.mes.dal.dataobject.set.firerecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-消防设施检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_fire_check")
@KeySequence("mes_set_fire_check_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetFireCheckDO extends BaseDO {

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
     * 关联检测计划编号
     */
    private Long planId;
    /**
     * 区域/位置
     */
    private String location;
    /**
     * 设施名称（灭火器/消火栓/烟感/温感/应急照明/疏散指示等）
     */
    private String facilityName;
    /**
     * 设施编号(资产编号)
     */
    private String facilityCode;
    /**
     * 检测时间
     */
    private LocalDateTime checkTime;
    /**
     * 结果：PASS/FAIL
     */
    private String result;
    /**
     * 异常/不合格描述
     */
    private String problemDesc;
    /**
     * 检测人
     */
    private String inspector;
    /**
     * 检测照片 URL（逗号分隔）
     */
    private String photoUrls;
    /**
     * 备注
     */
    private String remark;

}
