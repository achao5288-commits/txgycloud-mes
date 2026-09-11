package cn.iocoder.txgy.module.mes.service.set.pollutionledger;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo.MesPollutionLedgerMarkReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo.MesPollutionLedgerPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo.MesPollutionLedgerStatusReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerLogDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord.MesSetDischargeRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.service.set.dischargerecord.MesSetDischargeRecordService;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerLogMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerMapper;
import cn.iocoder.txgy.module.mes.service.pollution.MesPollutionControlService;
import cn.iocoder.txgy.module.mes.service.set.tracechain.MesSetTraceChainService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_LEDGER_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_LEDGER_STATUS_INVALID;

/**
 * MES 安全环保检测-污染/危废暂存台账 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesPollutionLedgerServiceImpl implements MesPollutionLedgerService {

    /**
     * 台账可流转目标状态（暂存 STORED 为自动登记态，不可人工流转回）
     */
    private static final Set<String> FLOW_TARGETS = new HashSet<>(Arrays.asList(
            MesPollutionLedgerDO.STATUS_PROCESSING,
            MesPollutionLedgerDO.STATUS_REUSED,
            MesPollutionLedgerDO.STATUS_DISCHARGED,
            MesPollutionLedgerDO.STATUS_DISPOSED));

    /**
     * 已闭环状态：人工处置终态(已回用/已排放/已处置) + 无污染自动解除(CLEARED)。闭环行禁止再流转(状态机不可逆)。
     */
    private static final Set<String> CLOSED_STATUSES = new HashSet<>(Arrays.asList(
            MesPollutionLedgerDO.STATUS_REUSED,
            MesPollutionLedgerDO.STATUS_DISCHARGED,
            MesPollutionLedgerDO.STATUS_DISPOSED,
            MesPollutionLedgerDO.STATUS_CLEARED));

    /**
     * 追溯对象类型：台账处置
     */
    public static final String TRACE_TYPE_LEDGER = "LEDGER";
    /**
     * 处置流转追溯节点环节
     */
    public static final String NODE_STAGE_DISPOSAL = "DISPOSAL";
    /**
     * 台账状态中文标签（追溯时间轴动作文案）
     */
    private static final Map<String, String> LEDGER_STATUS_LABELS = Map.of(
            "STORED", "暂存", "PROCESSING", "处置中", "REUSED", "已回用",
            "DISCHARGED", "已排放", "DISPOSED", "已处置", "CLEARED", "已解除");

    @Resource
    private MesPollutionLedgerMapper ledgerMapper;

    @Resource
    private MesPollutionLedgerLogMapper ledgerLogMapper;

    @Resource
    private MesPollutionControlService pollutionControlService;

    @Resource
    private MesSetTraceChainService traceChainService;

    @Resource
    private MesSetDischargeRecordService dischargeRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLedgerStatus(MesPollutionLedgerStatusReqVO reqVO) {
        MesPollutionLedgerDO ledger = validateLedgerExists(reqVO.getId());
        String status = reqVO.getStatus();
        if (!FLOW_TARGETS.contains(status)) {
            throw exception(SET_POLLUTION_LEDGER_STATUS_INVALID);
        }
        // 状态机：开放行(暂存/处置中)才能流转；已闭环(处置终态/已解除)禁止 reopen
        if (CLOSED_STATUSES.contains(ledger.getStatus())) {
            throw exception(SET_POLLUTION_LEDGER_STATUS_INVALID);
        }
        // 1. 流转
        String fromStatus = ledger.getStatus();
        ledger.setStatus(status);
        ledger.setStatusBy(getCurrentUserName());
        ledger.setStatusTime(LocalDateTime.now());
        if (StrUtil.isNotBlank(reqVO.getRemark())) {
            ledger.setRemark(reqVO.getRemark());
        }
        ledgerMapper.updateById(ledger);
        // 1.1 流转历史留痕（只增不改，完整处置链在 ledger_log）
        ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                .ledgerId(ledger.getId())
                .sourceRecordNo(ledger.getSourceRecordNo())
                .fromStatus(fromStatus)
                .toStatus(status)
                .operator(ledger.getStatusBy())
                .opTime(ledger.getStatusTime())
                .remark(reqVO.getRemark())
                .build());
        // 1.2 追溯留痕（一期B）：每步处置流转写一条 LEDGER 追溯节点（trace_code/biz_no=来源判定 recordNo），同事务
        traceChainService.createTraceNode(buildLedgerTraceNode(ledger, fromStatus, status, reqVO.getRemark()));
        // 1.3 排放合规流水：流转到 已排放(DISCHARGED) 时落一条流水（去向/标准取弹窗登记，去向留空回退台账去向），同事务
        if (MesPollutionLedgerDO.STATUS_DISCHARGED.equals(status)) {
            dischargeRecordService.createDischargeRecord(buildDischargeRecord(ledger, reqVO));
        }
        // 2. 终态(处置完成) → 该行闭环，重投影批次戳：同批若仍有其它未闭环台账行则保持拦截，全部闭环才清戳
        boolean terminal = MesPollutionLedgerDO.STATUS_REUSED.equals(status)
                || MesPollutionLedgerDO.STATUS_DISCHARGED.equals(status)
                || MesPollutionLedgerDO.STATUS_DISPOSED.equals(status);
        if (terminal) {
            pollutionControlService.refreshBatchStamp(ledger.getBatchNo());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLedgerMark(MesPollutionLedgerMarkReqVO reqVO) {
        MesPollutionLedgerDO ledger = validateLedgerExists(reqVO.getId());
        boolean marked = Boolean.TRUE.equals(reqVO.getMarked());
        if (Boolean.TRUE.equals(ledger.getMarked()) == marked) {
            return; // 结论未变，不重复留痕
        }
        String operator = getCurrentUserName();
        LocalDateTime now = LocalDateTime.now();
        // 1. 终审落地（marked=true 即禁止正常出库/销售）
        ledger.setMarked(marked);
        ledger.setUpdater(operator);
        ledgerMapper.updateById(ledger);
        String action = marked ? "标记（禁止正常出库）" : "解除标记";
        // 1.1 台账履历留痕（终审不改变状态，故 from=to）
        ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                .ledgerId(ledger.getId())
                .sourceRecordNo(ledger.getSourceRecordNo())
                .fromStatus(ledger.getStatus())
                .toStatus(ledger.getStatus())
                .operator(operator)
                .opTime(now)
                .remark("标记品终审：" + action + (StrUtil.isNotBlank(reqVO.getRemark()) ? "（" + reqVO.getRemark() + "）" : ""))
                .build());
        // 1.2 追溯留痕（一期B 同款）：与处置流转同一 bizNo 成链
        traceChainService.createTraceNode(MesSetTraceChainDO.builder()
                .traceCode(ledger.getSourceRecordNo())
                .traceType(TRACE_TYPE_LEDGER)
                .bizType(TRACE_TYPE_LEDGER)
                .bizNo(ledger.getSourceRecordNo())
                .nodeStage(NODE_STAGE_DISPOSAL)
                .nodeAction("标记品终审：" + action)
                .batchStatus(ledger.getStatus())
                .operatorName(operator)
                .nodeTime(now)
                .extra(reqVO.getRemark())
                .build());
        // 2. 批次戳重投影（marked 是拦截依据，必须跟着终审结论走）
        pollutionControlService.refreshBatchStamp(ledger.getBatchNo());
    }

    @Override
    public List<MesPollutionLedgerLogDO> getLedgerLogList(Long ledgerId) {
        return ledgerLogMapper.selectByLedgerId(ledgerId);
    }

    @Override
    public MesPollutionLedgerDO getLedger(Long id) {
        return ledgerMapper.selectById(id);
    }

    @Override
    public PageResult<MesPollutionLedgerDO> getLedgerPage(MesPollutionLedgerPageReqVO pageReqVO) {
        return ledgerMapper.selectPage(pageReqVO);
    }

    // ==================== 私有方法 ====================

    /**
     * 台账处置流转 → 追溯链 LEDGER 节点（audit 数据，随流转同事务写库）。
     * trace_code/biz_no 用来源判定 recordNo，与 CHECK 复核节点串成同一条业务链。
     */
    private MesSetTraceChainDO buildLedgerTraceNode(MesPollutionLedgerDO ledger, String fromStatus, String toStatus, String remark) {
        return MesSetTraceChainDO.builder()
                .traceCode(ledger.getSourceRecordNo())
                .traceType(TRACE_TYPE_LEDGER)
                .bizType(TRACE_TYPE_LEDGER)
                .bizNo(ledger.getSourceRecordNo())
                .nodeStage(NODE_STAGE_DISPOSAL)
                .nodeAction("台账状态：" + statusLabel(fromStatus) + " → " + statusLabel(toStatus))
                .batchStatus(toStatus)
                .operatorName(ledger.getStatusBy())
                .nodeTime(ledger.getStatusTime())
                .extra(remark)
                .build();
    }

    /**
     * 台账 已排放 流转 → 排放合规流水（audit 数据，随流转同事务写库）。
     * 去向：弹窗登记优先，留空回退台账去向/库位；登记人/时间取本次流转人/流转时间。
     */
    private MesSetDischargeRecordDO buildDischargeRecord(MesPollutionLedgerDO ledger, MesPollutionLedgerStatusReqVO reqVO) {
        MesSetDischargeRecordDO record = MesSetDischargeRecordDO.builder()
                .ledgerId(ledger.getId())
                .sourceRecordNo(ledger.getSourceRecordNo())
                .stage(ledger.getStage())
                .batchNo(ledger.getBatchNo())
                .itemCode(ledger.getItemCode())
                .itemName(ledger.getItemName())
                .itemSpec(ledger.getItemSpec())
                .destination(StrUtil.isNotBlank(reqVO.getDestination()) ? reqVO.getDestination() : ledger.getLocation())
                .standard(reqVO.getStandard())
                .dischargeTime(ledger.getStatusTime())
                .remark(reqVO.getRemark())
                .build();
        // 登记人落昵称（creator 属 BaseDO，@Builder 不含父类字段，且自动填充仅在为空时生效）
        record.setCreator(StrUtil.isNotBlank(ledger.getStatusBy()) ? ledger.getStatusBy() : getCurrentUserName());
        return record;
    }

    private String statusLabel(String status) {
        return status == null ? "" : LEDGER_STATUS_LABELS.getOrDefault(status, status);
    }

    private MesPollutionLedgerDO validateLedgerExists(Long id) {
        MesPollutionLedgerDO ledger = ledgerMapper.selectById(id);
        if (ledger == null) {
            throw exception(SET_POLLUTION_LEDGER_NOT_EXISTS);
        }
        return ledger;
    }

    /**
     * 当前登录人昵称，取不到则回退为登录用户 id
     */
    private String getCurrentUserName() {
        String nickname = getLoginUserNickname();
        if (nickname == null || nickname.isEmpty()) {
            return String.valueOf(getLoginUserId());
        }
        return nickname;
    }

}
