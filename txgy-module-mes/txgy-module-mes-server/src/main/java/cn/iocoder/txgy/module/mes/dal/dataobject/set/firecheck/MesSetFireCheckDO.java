package cn.iocoder.txgy.module.mes.dal.dataobject.set.firecheck;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-消防检查记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_fire_check")
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
     * 设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)
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
     * 检测照片URL(逗号分隔)
     */
    private String photoUrls;

    /**
     * 备注
     */
    private String remark;

}
