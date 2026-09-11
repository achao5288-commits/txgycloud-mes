package cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-追溯链节点 DO
 *
 * 追溯链 = 系统写入的审计数据（前端只读）。污染判定复核收口、台账每步处置流转各写一节点，
 * 一条业务对象(trace_code=判定 recordNo)串成从"判定"到"处置闭环"的可追溯时间轴。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_trace_chain")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetTraceChainDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 追溯对象码（现=判定 recordNo(PC-...)；后续=批次码/容器码）
     */
    private String traceCode;
    /**
     * 对象类型：CHECK(污染判定)/LEDGER(台账处置)；预留 BATCH/CONTAINER
     */
    private String traceType;
    /**
     * 上游关联码（来源单号/批次码）
     */
    private String parentCode;
    /**
     * 业务关联类型（与 sign/weigh 共用：CHECK/LEDGER）
     */
    private String bizType;
    /**
     * 业务关联单号（判定 recordNo / 台账来源判定 recordNo）
     */
    private String bizNo;
    /**
     * 环节（复用四环节枚举；处置流转记 DISPOSAL）
     */
    private String nodeStage;
    /**
     * 节点中文动作（时间轴主展示）
     */
    private String nodeAction;
    /**
     * 节点时刻对象状态（判定→复核终值 CLEAN/POLLUTED/UNCERTAIN；处置→台账状态码）
     */
    private String batchStatus;
    /**
     * 操作人（复核人/流转人）
     */
    private String operatorName;
    /**
     * 节点时间
     */
    private LocalDateTime nodeTime;
    /**
     * JSON 备注（去向/处置方式等）
     */
    private String extra;

}
