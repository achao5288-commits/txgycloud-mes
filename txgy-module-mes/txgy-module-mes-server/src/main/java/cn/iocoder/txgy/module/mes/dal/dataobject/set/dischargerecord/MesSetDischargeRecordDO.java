package cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-排放合规流水 DO
 *
 * 无污染中间物/废水"排放"出产线的合规留档（只增不改删，前端只读）。
 * 台账流转到 已排放(DISCHARGED) 时同事务自动落档，登记人取当前登录人；
 * 与台账以 ledger_id 关联、与追溯链以 source_record_no 关联。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_pollution_discharge")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetDischargeRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 来源台账编号
     */
    private Long ledgerId;
    /**
     * 来源判定记录号(与追溯链同键)
     */
    private String sourceRecordNo;
    /**
     * 环节（中间废弃物等）
     */
    private String stage;
    /**
     * 批次号（可为空，中间废弃物无批次）
     */
    private String batchNo;
    /**
     * 物料/产品编码
     */
    private String itemCode;
    /**
     * 物料/产品名称
     */
    private String itemName;
    /**
     * 规格
     */
    private String itemSpec;
    /**
     * 排放去向（管线/处理站/回用渠等）
     */
    private String destination;
    /**
     * 执行排放标准/达标口径
     */
    private String standard;
    /**
     * 排放(登记)时间
     */
    private LocalDateTime dischargeTime;
    /**
     * 备注
     */
    private String remark;

}
