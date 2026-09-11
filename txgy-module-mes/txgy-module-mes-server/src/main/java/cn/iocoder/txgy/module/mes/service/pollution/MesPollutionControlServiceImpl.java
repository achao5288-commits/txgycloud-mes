package cn.iocoder.txgy.module.mes.service.pollution;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerLogDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesSetPollutionCheckMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerLogMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.batch.MesWmBatchMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.materialstock.MesWmMaterialStockMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseAreaMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.MES_POLLUTION_ISSUE_BLOCKED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.MES_POLLUTION_OUTBOUND_BLOCKED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_BATCH_NOT_FOUND;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_BATCH_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_CONTROLLED_FORBIDDEN;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_ID_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_NOT_CONTROLLED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_NOT_FOUND;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_REQUIRED;
import static cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckServiceImpl.FINISHED_SCRAPPED;

/**
 * MES 环保污染管控服务实现
 *
 * 台账行 = 该批污染管控的唯一权威；批次污染戳(pollution_*)只是"该批是否仍有未闭环台账行"的投影缓存。
 * "复核=有污染" → 登记台账(STORED) 并投影戳批次；"复核=无污染" → 关闭该批历史未闭环台账行(CLEARED) 后清戳。
 * 台账人工处置到终态(已回用/已排放/已处置) → 该行闭环，按剩余开放行重投影（refreshBatchStamp）。
 *
 * @author OPENLAB BS
 */
@Service
public class MesPollutionControlServiceImpl implements MesPollutionControlService {

    @Resource
    private MesSetPollutionCheckMapper pollutionCheckMapper;

    @Resource
    private MesWmBatchMapper batchMapper;

    @Resource
    private MesWmWarehouseAreaMapper warehouseAreaMapper;

    @Resource
    private MesPollutionLedgerMapper ledgerMapper;

    @Resource
    private MesPollutionLedgerLogMapper ledgerLogMapper;

    @Resource
    private MesWmMaterialStockMapper materialStockMapper;

    @Override
    public void applyReviewEffect(MesSetPollutionCheckDO reviewed) {
        if (reviewed == null || reviewed.getReviewResult() == null) {
            return;
        }
        boolean polluted = REVIEW_POLLUTED.equals(reviewed.getReviewResult());
        boolean waste = STAGE_WASTE_INTERMEDIATE.equals(reviewed.getStage());
        String batchNo = reviewed.getBatchNo();
        // 0) 受控存储门禁（需求 5.4 普通仓与受控仓隔离防混放）：先收口库位
        //    有污染 → 必须落"污染管控"库位；无污染 → 不得占用受控库位。库位名以所选库位为准回写快照。
        MesWmWarehouseAreaDO area = null;
        if (reviewed.getLocationId() != null) {
            area = warehouseAreaMapper.selectById(reviewed.getLocationId());
            if (area == null) {
                throw exception(SET_POLLUTION_CHECK_LOCATION_NOT_FOUND, reviewed.getLocationId());
            }
            reviewed.setLocation(area.getName());
            // 库位名回写快照：复核主流程的 updateById 在前(只落了 locationId)，此处补写，保证判定行/追溯节点与台账同名
            pollutionCheckMapper.update(null, new LambdaUpdateWrapper<MesSetPollutionCheckDO>()
                    .eq(MesSetPollutionCheckDO::getId, reviewed.getId())
                    .set(MesSetPollutionCheckDO::getLocation, area.getName()));
        }
        boolean controlled = area != null && Boolean.TRUE.equals(area.getPollutionControl());
        if (polluted) {
            if (area == null) {
                throw exception(SET_POLLUTION_CHECK_LOCATION_ID_REQUIRED);
            }
            if (!controlled) {
                throw exception(SET_POLLUTION_CHECK_LOCATION_NOT_CONTROLLED, area.getName());
            }
        } else if (controlled) {
            throw exception(SET_POLLUTION_CHECK_LOCATION_CONTROLLED_FORBIDDEN, area.getName());
        }
        // 1) 校验：中间废弃物无批次主数据，允许空批次；其余环节"有污染"必须落真实批次，否则抛错（同事务 → 复核整体回滚）
        MesWmBatchDO batch = StrUtil.isBlank(batchNo) ? null : batchMapper.selectByCode(batchNo);
        if (polluted) {
            if (StrUtil.isBlank(reviewed.getLocation())) {
                throw exception(SET_POLLUTION_CHECK_LOCATION_REQUIRED);
            }
            if (!waste && batch == null) {
                if (StrUtil.isBlank(batchNo)) {
                    throw exception(SET_POLLUTION_CHECK_BATCH_REQUIRED);
                }
                throw exception(SET_POLLUTION_CHECK_BATCH_NOT_FOUND, batchNo);
            }
        }
        // 2) A2 台账登记：仅"有污染"登记，同一判定记录(source_check_id)不重复
        if (polluted) {
            MesPollutionLedgerDO exist = ledgerMapper.selectBySourceCheckId(reviewed.getId());
            if (exist == null) {
                MesPollutionLedgerDO ledger = MesPollutionLedgerDO.builder()
                        .sourceCheckId(reviewed.getId())
                        .sourceRecordNo(reviewed.getRecordNo())
                        .stage(reviewed.getStage())
                        .bizNo(reviewed.getBizNo())
                        .batchNo(batchNo)
                        .itemCode(reviewed.getItemCode())
                        .itemName(reviewed.getItemName())
                        .itemSpec(reviewed.getItemSpec())
                        .weight(reviewed.getWeight())
                        .disposition(reviewed.getDisposition())
                        .storageMethod(reviewed.getStorageMethod())
                        .location(reviewed.getLocation())
                        .locationId(reviewed.getLocationId())
                        .marked(Boolean.TRUE.equals(reviewed.getMarked()))
                        .status(MesPollutionLedgerDO.STATUS_STORED)
                        .statusBy(reviewed.getReviewBy())
                        .statusTime(reviewed.getReviewTime())
                        .remark(reviewed.getRemark())
                        .build();
                ledgerMapper.insert(ledger);
                // 台账流转历史留痕：登记 STORED
                ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                        .ledgerId(ledger.getId())
                        .sourceRecordNo(ledger.getSourceRecordNo())
                        .toStatus(MesPollutionLedgerDO.STATUS_STORED)
                        .operator(ledger.getStatusBy())
                        .opTime(ledger.getStatusTime())
                        .remark("复核判为有污染，自动登记暂存台账")
                        .build());
            }
        } else if (batch != null) {
            // 3) "无污染"复核 = 解除该批此前误判的未闭环台账行（历史人工处置闭环的保留留痕），随后整体重投影清戳
            closeOpenLedgerByBatch(batchNo, reviewed);
        }
        // 4) A1 批次戳 = 台账投影重刷（仅能落地真实批次的环节；中间废弃物无主数据 → 只登记台账不投影）
        if (!waste && batch != null) {
            refreshBatchStamp(batchNo);
        }
        // 5) B1 在库行冻结投影：必须排在 refreshBatchStamp 之后——它读的批次污染戳是上一步刚重刷的，
        //    否则会拿旧戳推导（"判 CLEAN 才解冻"就会慢一拍解不开）。
        if (batch != null) {
            syncStockFrozenByBatch(batchNo);
        }
    }

    @Override
    public void assertIssueAllowed(String batchNo) {
        if (StrUtil.isBlank(batchNo)) {
            return;
        }
        MesWmBatchDO batch = batchMapper.selectByCode(batchNo);
        // 领用拦截与出库同口径：有污染 或 被标记（含成品整体报废的整批锁定）都不得领用
        if (batch != null && (REVIEW_POLLUTED.equals(batch.getPollutionStatus())
                || Boolean.TRUE.equals(batch.getPollutionMarked()))) {
            throw exception(MES_POLLUTION_ISSUE_BLOCKED, batchNo, safeLocation(batch.getPollutionLocation()));
        }
    }

    @Override
    public void assertOutboundAllowed(String batchNo) {
        if (StrUtil.isBlank(batchNo)) {
            return;
        }
        MesWmBatchDO batch = batchMapper.selectByCode(batchNo);
        if (batch != null) {
            boolean blocked = REVIEW_POLLUTED.equals(batch.getPollutionStatus())
                    || Boolean.TRUE.equals(batch.getPollutionMarked());
            if (blocked) {
                throw exception(MES_POLLUTION_OUTBOUND_BLOCKED, batchNo, safeLocation(batch.getPollutionLocation()));
            }
        }
    }

    @Override
    public void refreshBatchStamp(String batchNo) {
        if (StrUtil.isBlank(batchNo)) {
            return;
        }
        MesWmBatchDO batch = batchMapper.selectByCode(batchNo);
        if (batch == null) {
            return;
        }
        // 报废锁定（需求文档 §11 用例5「不达标成品整批锁定不出库」）：该批存在"整体报废"判定即锁死。
        // 与台账无关——无害报废没有污染台账行，也必须挡住出库/领用，且不因后续无污染复核被解除。
        boolean scrapLocked = pollutionCheckMapper.selectCount(new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                .eq(MesSetPollutionCheckDO::getBatchNo, batchNo)
                .eq(MesSetPollutionCheckDO::getFinishedResult, FINISHED_SCRAPPED)) > 0;
        List<MesPollutionLedgerDO> open = ledgerMapper.selectOpenByBatchNo(batchNo);
        if (open.isEmpty()) {
            // 该批已无未闭环"有污染"台账行 → 清污染戳；报废锁定若成立则保留 marked
            patchBatchStamp(batch.getId(), null, null, scrapLocked ? Boolean.TRUE : null, null);
        } else {
            // 仍开放 → 投影最早一条开放行的去向/标记/来源判定
            MesPollutionLedgerDO first = open.get(0);
            patchBatchStamp(batch.getId(),
                    REVIEW_POLLUTED,
                    first.getLocation(),
                    scrapLocked || Boolean.TRUE.equals(first.getMarked()),
                    first.getSourceRecordNo());
        }
    }

    @Override
    public int syncStockFrozenByBatch(String batchNo) {
        if (StrUtil.isBlank(batchNo)) {
            return 0;
        }
        MesWmBatchDO batch = batchMapper.selectByCode(batchNo);
        if (batch == null) {
            return 0;
        }
        boolean frozen = isBatchFrozen(batch);
        // 幂等重算：无论调用方是建单、复核还是定时任务，写进去的都是同一个推导结果
        return materialStockMapper.update(null, new LambdaUpdateWrapper<MesWmMaterialStockDO>()
                .eq(MesWmMaterialStockDO::getBatchCode, batchNo)
                .set(MesWmMaterialStockDO::getFrozen, frozen));
    }

    @Override
    public int freezeExpiredBatchStock() {
        List<MesWmBatchDO> expired = batchMapper.selectList(new LambdaQueryWrapperX<MesWmBatchDO>()
                .isNotNull(MesWmBatchDO::getExpireDate)
                .lt(MesWmBatchDO::getExpireDate, LocalDateTime.now()));
        int rows = 0;
        for (MesWmBatchDO batch : expired) {
            rows += syncStockFrozenByBatch(batch.getCode());
        }
        return rows;
    }

    @Override
    public int purgePendingByBizNo(String stage, String bizNo) {
        if (StrUtil.isBlank(bizNo)) {
            return 0;
        }
        // 链式 eq/isNull 收窄成基础类型 LambdaQueryWrapper，不能声明为 LambdaQueryWrapperX
        LambdaQueryWrapper<MesSetPollutionCheckDO> query = new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                .eq(MesSetPollutionCheckDO::getStage, stage)
                .eq(MesSetPollutionCheckDO::getBizNo, bizNo)
                .isNull(MesSetPollutionCheckDO::getReviewResult);
        // 待检冻结的行被删掉后，其冻结理由也随之消失 → 删前先记住涉及哪些批次，删后逐个重算解冻。
        // 漏掉这步会让"单据删了、库存永远冻着"——冻结态必须有对应的理由行存在才成立。
        List<String> batchNos = distinctBatchNos(query);
        int deleted = pollutionCheckMapper.delete(query);
        batchNos.forEach(this::syncStockFrozenByBatch);
        return deleted;
    }

    @Override
    public int purgePendingByBizNoItem(String stage, String bizNo, String itemCode, String batchNo) {
        if (StrUtil.isBlank(bizNo)) {
            return 0;
        }
        // 逐行 eq：链式 eq() 返回基础类型，随后无法再调 LambdaQueryWrapperX.eqIfPresent
        LambdaQueryWrapperX<MesSetPollutionCheckDO> query = new LambdaQueryWrapperX<>();
        query.eq(MesSetPollutionCheckDO::getStage, stage);
        query.eq(MesSetPollutionCheckDO::getBizNo, bizNo);
        query.isNull(MesSetPollutionCheckDO::getReviewResult);
        if (StrUtil.isNotBlank(itemCode)) {
            query.eq(MesSetPollutionCheckDO::getItemCode, itemCode);
        }
        if (StrUtil.isNotBlank(batchNo)) {
            query.eq(MesSetPollutionCheckDO::getBatchNo, batchNo);
        }
        List<String> batchNos = distinctBatchNos(query);
        int deleted = pollutionCheckMapper.delete(query);
        batchNos.forEach(this::syncStockFrozenByBatch);
        return deleted;
    }

    // ==================== 私有方法 ====================

    /**
     * 待删判定行涉及的去重批次号（删前取，删后就没得取了）。非空且去重。
     */
    private List<String> distinctBatchNos(LambdaQueryWrapper<MesSetPollutionCheckDO> query) {
        return pollutionCheckMapper.selectList(query).stream()
                .map(MesSetPollutionCheckDO::getBatchNo)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * "无污染"复核把该批所有未闭环(暂存/处置中)台账行置为 CLEARED(已解除)，留痕解除人/时间并追加备注；
     * 历史已人工处置到终态的行保留不动（处置记录本身不可逆）。
     */
    private void closeOpenLedgerByBatch(String batchNo, MesSetPollutionCheckDO reviewed) {
        List<MesPollutionLedgerDO> open = ledgerMapper.selectOpenByBatchNo(batchNo);
        if (open.isEmpty()) {
            return;
        }
        String reason = StrUtil.format("批次复核无污染，自动解除(源判定: {})", reviewed.getRecordNo());
        for (MesPollutionLedgerDO row : open) {
            String remark = StrUtil.blankToDefault(row.getRemark(), "");
            remark = remark.isEmpty() ? reason : remark + "；" + reason;
            ledgerMapper.update(null, new LambdaUpdateWrapper<MesPollutionLedgerDO>()
                    .eq(MesPollutionLedgerDO::getId, row.getId())
                    .set(MesPollutionLedgerDO::getStatus, MesPollutionLedgerDO.STATUS_CLEARED)
                    .set(MesPollutionLedgerDO::getStatusBy, reviewed.getReviewBy())
                    .set(MesPollutionLedgerDO::getStatusTime, reviewed.getReviewTime())
                    .set(MesPollutionLedgerDO::getRemark, remark));
            // 台账流转历史留痕：无污染复核自动解除 → CLEARED
            ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                    .ledgerId(row.getId())
                    .sourceRecordNo(row.getSourceRecordNo())
                    .fromStatus(row.getStatus())
                    .toStatus(MesPollutionLedgerDO.STATUS_CLEARED)
                    .operator(reviewed.getReviewBy())
                    .opTime(reviewed.getReviewTime())
                    .remark(reason)
                    .build());
        }
    }

    /**
     * 一批货"该不该冻"的唯一判据（在库行冻结态与批次污染戳的两个来源：批次自身 + 该批判定行）。
     * 三条取或，任一成立即冻——注意第 3 条与污染无关，是有效期维度（FEFO）。
     */
    private boolean isBatchFrozen(MesWmBatchDO batch) {
        // 1) 有污染 或 被标记（含成品整体报废的整批锁定）
        if (REVIEW_POLLUTED.equals(batch.getPollutionStatus())
                || Boolean.TRUE.equals(batch.getPollutionMarked())) {
            return true;
        }
        // 2) 仍有"待复核"判定行 → 待检期冻结；判 CLEAN 后本行归零，自然解冻
        Long pending = pollutionCheckMapper.selectCount(new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                .eq(MesSetPollutionCheckDO::getBatchNo, batch.getCode())
                .isNull(MesSetPollutionCheckDO::getReviewResult));
        if (pending != null && pending > 0) {
            return true;
        }
        // 3) 已过期 → 冻结。与污染解耦：过期货不因"判了无污染"就放行
        return batch.getExpireDate() != null && batch.getExpireDate().isBefore(LocalDateTime.now());
    }

    private String safeLocation(String location) {
        return StrUtil.blankToDefault(location, "-");
    }

    /**
     * updateById 默认忽略 null 字段，清戳/写 null 需用 update wrapper 显式 set NULL。
     */
    private void patchBatchStamp(Long batchId, String status, String location, Boolean marked, String srcRecord) {
        batchMapper.update(null, new LambdaUpdateWrapper<MesWmBatchDO>()
                .eq(MesWmBatchDO::getId, batchId)
                .set(MesWmBatchDO::getPollutionStatus, status)
                .set(MesWmBatchDO::getPollutionLocation, location)
                .set(MesWmBatchDO::getPollutionMarked, marked)
                .set(MesWmBatchDO::getPollutionSrcRecord, srcRecord));
    }

}
