package cn.iocoder.txgy.module.mes.service.pollution;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckControlLocationReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger.MesPollutionLedgerLogDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemical.MesSetChemicalProfileMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesSetPollutionCheckMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerLogMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionledger.MesPollutionLedgerMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.batch.MesWmBatchMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.materialstock.MesWmMaterialStockMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseAreaMapper;
import cn.iocoder.txgy.module.mes.service.set.chemical.ChemicalCompatRules;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.MES_POLLUTION_ISSUE_BLOCKED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_ITEM_INCOMPATIBLE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_ITEM_ZONE_MISMATCH;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.MES_POLLUTION_OUTBOUND_BLOCKED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_BATCH_NOT_FOUND;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_BATCH_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_CONTROLLED_FORBIDDEN;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_ID_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_NOT_CONTROLLED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_NOT_FOUND;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LOCATION_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_LEDGER_CLOSED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_NOT_REVIEWED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_SUPERSEDED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_SIGN_REQUIRED;
import static cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckServiceImpl.BIZ_TYPE_CHECK;
import static cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckServiceImpl.FINISHED_SCRAPPED;
import static cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckServiceImpl.SIGN_ROLE_OPERATOR;

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

    /**
     * 危化品档案：只注 Mapper 不注 Service。
     *
     * 本类「只依赖 Mapper，不依赖其它 Service，避免 bean 环」的约束依然成立——
     * 禁配/专区规则是纯函数（{@link ChemicalCompatRules}），不需要把化学品的 Service 拖进来。
     */
    @Resource
    private MesSetChemicalProfileMapper chemicalProfileMapper;

    /**
     * 签字落档。这里破了本类「只依赖 Mapper」的规矩，但不会成环：签字 Service 只依赖自己的
     * Mapper 与 system 的 AdminUserApi（反查昵称），不认识 mes 里任何 Service——所以
     * changeControlLocation 这个人为改库位的入口要签字，只能从这儿拿。
     */
    @Resource
    private MesSetSignRecordService signRecordService;

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
        MesWmWarehouseAreaDO area = validateControlledLocation(polluted, reviewed.getLocationId());
        // 危化品禁配/专区也在这里判一次：复核是**库位第一次落地**的地方，
        // 只在「受控库位调整」拦的话，改库位时被拒的人会绕过调整、改用复核把货放进去——门就形同虚设。
        assertChemicalStorageAllowed(reviewed, area);
        if (area != null) {
            reviewed.setLocation(area.getName());
            // 库位名回写快照：复核主流程的 updateById 在前(只落了 locationId)，此处补写，保证判定行/追溯节点与台账同名
            pollutionCheckMapper.update(null, new LambdaUpdateWrapper<MesSetPollutionCheckDO>()
                    .eq(MesSetPollutionCheckDO::getId, reviewed.getId())
                    .set(MesSetPollutionCheckDO::getLocation, area.getName()));
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
                        .sourceType(MesPollutionLedgerDO.SOURCE_POLLUTION_CHECK)
                        .stage(reviewed.getStage())
                        .bizNo(reviewed.getBizNo())
                        .batchNo(batchNo)
                        .itemCode(reviewed.getItemCode())
                        .itemName(reviewed.getItemName())
                        .itemSpec(reviewed.getItemSpec())
                        .weight(reviewed.getWeight())
                        // 单位必须跟着重量一起搬：台账只搬 weight 的话又会退回裸数字
                        .unitName(reviewed.getUnitName())
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
    @Transactional(rollbackFor = Exception.class)
    public Long registerWasteLedger(WasteRegister waste) {
        String operator = currentOperator();
        MesPollutionLedgerDO ledger = MesPollutionLedgerDO.builder()
                // 产废登记没有判定记录，source_check_id 留空，靠 source_record_no（作业票号）反查。
                // uk_ledger_source_check 是唯一键，但 MySQL 唯一索引允许多个 NULL，多条产废行可并存。
                .sourceCheckId(null)
                .sourceRecordNo(waste.sourceRecordNo())
                .sourceType(MesPollutionLedgerDO.SOURCE_WASTE_REGISTER)
                .stage(STAGE_WASTE_INTERMEDIATE)
                .itemCode(waste.itemCode())
                .itemName(waste.itemName())
                .itemSpec(waste.itemSpec())
                // 重量/单位与判定侧同口径：台账只搬 weight 的话又会退回裸数字
                .weight(waste.weight())
                .unitName(waste.unitName())
                .disposition("MARKED_STORAGE")
                .location(waste.location())
                .marked(Boolean.TRUE)
                .status(MesPollutionLedgerDO.STATUS_STORED)
                .statusBy(operator)
                .statusTime(LocalDateTime.now())
                .remark(waste.remark())
                .build();
        ledgerMapper.insert(ledger);
        ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                .ledgerId(ledger.getId())
                .sourceRecordNo(ledger.getSourceRecordNo())
                .toStatus(MesPollutionLedgerDO.STATUS_STORED)
                .operator(operator)
                .opTime(ledger.getStatusTime())
                .remark("产废称重登记入暂存台账")
                .build());
        return ledger.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeControlLocation(MesSetPollutionCheckControlLocationReqVO reqVO) {
        Long checkId = reqVO.getId();
        Long locationId = reqVO.getLocationId();
        if (locationId == null) {
            throw exception(SET_POLLUTION_CHECK_LOCATION_ID_REQUIRED);
        }
        MesSetPollutionCheckDO check = pollutionCheckMapper.selectById(checkId);
        if (check == null) {
            throw exception(SET_POLLUTION_CHECK_NOT_EXISTS);
        }
        // 未复核的记录不让改：复核那一刻才登记台账、才投影批次戳，此时改库位没有任何下游读它
        if (check.getReviewResult() == null) {
            throw exception(SET_POLLUTION_CHECK_NOT_REVIEWED);
        }
        // 已被变更单替代的原单是一份作废的历史文件，它名下的台账也不该再被当成"当前存放事实"来搬
        if (StrUtil.isNotBlank(check.getSupersededBy())) {
            throw exception(SET_POLLUTION_CHECK_SUPERSEDED, check.getSupersededBy());
        }
        // —— 签字即冻结在本方法上的落法：**只让台账动，判定落款不动** ——
        // 判定行是签了字的结论文件，它的 location/locationId 是复核那一刻的存放去向快照，属"内容"，
        // 收口后不再就地改写（要改去向结论就开变更单）。而"桶今天挪到哪个暂存间"是台账的**当前状态**，
        // 属流程流转，得能改——否则受控库位调整这个功能整体失效，桶就挪不动了。
        // 批次戳不受影响：refreshBatchStamp 取的是**台账**最早开放行的 location，跟着台账走，
        // 门禁提示语与实际存放地点依然对得上。代价是判定行上的去向会落后于台账，那是刻意留的历史快照。
        // 校验一律排在写之前：下面有判定行/台账行/流转历史三处写，任何一处抛错都必须整体回滚，
        // 否则就会留下"判定行说 A、台账还指着 B"的裂缝——正是本方法要消灭的那种不一致。
        MesWmWarehouseAreaDO area = validateControlledLocation(REVIEW_POLLUTED.equals(check.getReviewResult()), locationId);
        // 危化品相容组禁配 / 储存专区：与受控库位门禁是**两把独立的尺子**——
        // 那一把问"有污染的东西有没有放进受控库位"，这一把问"这个化学品能不能和库位里那些东西放一起"。
        // 两者都可能拦，所以都要在写之前跑。
        assertChemicalStorageAllowed(check, area);
        // 签字卡口：库位调整是环保专员手改的物理事实，缺签名拒掉。位置必须排在下面那条"原样提交"
        // 的早返回之前——用户点了确认就是一次人为操作，值没变也得留痕，否则"确认过"三个字就没证据了。
        // 排在所有写之前、且与它们同事务：后面任一处抛错一起回滚，不留"签了字但没改成"的假痕迹。
        signRecordService.requireSigned(new MesSetSignRecordService.SignPayload(
                BIZ_TYPE_CHECK, check.getRecordNo(), SIGN_ROLE_OPERATOR,
                reqVO.getSignImg(), reqVO.getOpinion()), SET_POLLUTION_CHECK_SIGN_REQUIRED);
        // 台账行必须一起搬：批次戳(pollution_location)是从**台账行**投影的（refreshBatchStamp 取最早开放行的 location），
        // 只改判定行会让批次戳一直指向旧库位，门禁提示语与实际存放地点对不上。
        MesPollutionLedgerDO ledger = ledgerMapper.selectBySourceCheckId(checkId);
        if (ledger != null) {
            // 已闭环(已回用/已排放/已处置/已解除)的台账行是历史事实：东西早就不在那儿了，
            // 改它的库位等于篡改记录，而且批次戳只认未闭环行、根本不会跟着变。只允许搬"还在库里"的那些。
            if (!isLedgerOpen(ledger.getStatus())) {
                throw exception(SET_POLLUTION_CHECK_LEDGER_CLOSED, ledger.getStatus());
            }
            // 原样提交（前端预填了当前库位，用户直接点确定）不必重写台账，也不该往流转历史里塞噪声。
            // 判定行的去向按上面的口径一个字都不写，签名已经在闸门那步留档，这里直接收工。
            if (area.getName().equals(ledger.getLocation()) && locationId.equals(ledger.getLocationId())) {
                return;
            }
        }
        if (ledger != null) {
            String from = ledger.getLocation();
            ledgerMapper.update(null, new LambdaUpdateWrapper<MesPollutionLedgerDO>()
                    .eq(MesPollutionLedgerDO::getId, ledger.getId())
                    .set(MesPollutionLedgerDO::getLocation, area.getName())
                    .set(MesPollutionLedgerDO::getLocationId, locationId));
            // 危废贮存地点变更是要留痕的事实（HJ 1276-2022）：状态没变，只记库位前后，不算流转
            ledgerLogMapper.insert(MesPollutionLedgerLogDO.builder()
                    .ledgerId(ledger.getId())
                    .sourceRecordNo(ledger.getSourceRecordNo())
                    .fromStatus(ledger.getStatus())
                    .toStatus(ledger.getStatus())
                    .operator(currentOperator())
                    .opTime(LocalDateTime.now())
                    .remark(StrUtil.format("受控库位调整为「{}」{}", area.getName(),
                            StrUtil.isBlank(from) ? "" : "（原：" + from + "）"))
                    .build());
        }
        refreshBatchStamp(check.getBatchNo());
    }

    /**
     * 受控存储门禁（需求 5.4 普通仓与受控仓隔离防混放），复核与「受控库位调整」共用一把尺子，
     * 免得两条路各写一份规则后走偏（例如只在一处放宽了受控要求）。
     *
     * @param polluted   复核结论是否"有污染"
     * @param locationId 目标库位编号，可空（无污染复核允许不落库位）
     * @return 解析出的库位；locationId 为空时返回 null
     */
    private MesWmWarehouseAreaDO validateControlledLocation(boolean polluted, Long locationId) {
        MesWmWarehouseAreaDO area = null;
        if (locationId != null) {
            area = warehouseAreaMapper.selectById(locationId);
            if (area == null) {
                throw exception(SET_POLLUTION_CHECK_LOCATION_NOT_FOUND, locationId);
            }
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
        return area;
    }

    /**
     * 危化品相容组禁配 / 储存专区校验（设计文档 §4.3 推迟项，此处补上）。
     *
     * **为什么当初做不了**：危化品档案用 CAS 号，物料主数据用物料编码，两者 0/10 命中（F10），
     * 所以系统根本不知道"这个在库批次的物料是不是危化品"。现在靠 `mes_set_chemical_profile.item_id`
     * 这条**显式绑定**解决——不靠编码字符串猜（猜错了就是拿合规当儿戏）。
     *
     * **未绑定一律放行**：没绑档案的物料不当危化品管。与「未锚定的禁配组合不猜」同一口径——
     * 宁可漏判让人工确认，也不能凭"看起来像"就拦货。
     *
     * 规则本身住在 {@link ChemicalCompatRules}，与档案侧 {@code checkStorage} 共用同一份矩阵，
     * 不在这里重写一遍（规则分两份是这类校验最典型的腐化方式）。
     *
     * @param check 判定行（取其批次反查物料）
     * @param area  目标库位
     */
    private void assertChemicalStorageAllowed(MesSetPollutionCheckDO check, MesWmWarehouseAreaDO area) {
        if (area == null) {
            return; // 无库位无从谈专区
        }
        // 批次锚点优先 batchId；**只有 batchNo 的行也要认**——batchId 是请求方传的（见
        // MesSetPollutionCheckServiceImpl.applyBatchAnchor），不是服务端反查的，
        // 所以「传 batchNo 不传 batchId」是一条能绕过这道门的后门，而 applyReviewEffect
        // 的批次戳投影恰恰认这种行。两处口径必须一致。
        MesWmBatchDO batch;
        if (check.getBatchId() != null) {
            batch = batchMapper.selectById(check.getBatchId());
        } else if (StrUtil.isNotBlank(check.getBatchNo())) {
            // batchNo 为空绝不能查：LambdaQueryWrapperX.eq 遇 null 会**跳过**该条件，
            // 那就不是"查不到"，而是捞出任意一条批次——比不判危险得多。
            batch = batchMapper.selectByCode(check.getBatchNo());
        } else {
            return; // 既无 batchId 又无 batchNo，认不出物料（纯手填的中间废弃物）
        }
        if (batch == null || batch.getItemId() == null) {
            return;
        }
        MesSetChemicalProfileDO profile = chemicalProfileMapper.selectEnabledByItemId(batch.getItemId());
        if (profile == null) {
            return; // 未绑定危化品档案：不当危化品管
        }
        String itemLabel = StrUtil.blankToDefault(check.getItemName(), String.valueOf(batch.getItemId()));
        String groupName = ChemicalCompatRules.groupName(profile.getCompatGroup());

        // ① 储存专区：稀释剂这类须防爆存放，库位专区对不上就不让进
        if (!ChemicalCompatRules.zoneSatisfies(profile.getCompatGroup(), profile.getExplosionProof(),
                area.getStorageZone())) {
            String need = Boolean.TRUE.equals(profile.getExplosionProof())
                    ? "防爆区" : ChemicalCompatRules.zoneName("EXPLOSION_PROOF");
            throw exception(SET_CHEMICAL_ITEM_ZONE_MISMATCH, itemLabel, groupName,
                    need, area.getName(), ChemicalCompatRules.zoneName(area.getStorageZone()));
        }

        // ② 同库位禁配：比对集取**该库位此刻在库的物料**（不是在库位主数据上挂一串名字）。
        //    这才是现场真正要问的问题——"我把这桶搬过去，会不会和已经放在那儿的打架"。
        //
        //    ⚠ 比的是 material_stock.**area_id**（→ mes_wm_warehouse_area.id，即"库位"），
        //    不是 location_id——后者指 mes_wm_warehouse_location（"库区"），是上一级。
        //    这两列名字只差一个词、都没注释，写错了比对集永远是空集，禁配就成了永不放行的摆设。
        List<MesWmMaterialStockDO> atLocation = materialStockMapper.selectList(
                new LambdaQueryWrapperX<MesWmMaterialStockDO>()
                        .eq(MesWmMaterialStockDO::getAreaId, area.getId()));
        Set<Long> otherItemIds = atLocation.stream()
                .map(MesWmMaterialStockDO::getItemId)
                .filter(Objects::nonNull)
                .filter(id -> !id.equals(batch.getItemId())) // 排除本物料自己，否则自己和自己比
                .collect(Collectors.toSet());
        for (MesSetChemicalProfileDO other : chemicalProfileMapper.selectEnabledByItemIds(otherItemIds)) {
            if (ChemicalCompatRules.isIncompatible(profile, other)) {
                throw exception(SET_CHEMICAL_ITEM_INCOMPATIBLE, itemLabel, groupName,
                        area.getName(), other.getChemicalName(),
                        ChemicalCompatRules.groupName(other.getCompatGroup()));
            }
        }
    }

    /** 台账行是否仍未闭环（口径与 MesPollutionLedgerMapper.selectOpenByBatchNo 一致：暂存/处置中 = 还在库里） */
    private boolean isLedgerOpen(String status) {
        return MesPollutionLedgerDO.STATUS_STORED.equals(status)
                || MesPollutionLedgerDO.STATUS_PROCESSING.equals(status);
    }

    /** 当前登录人昵称，取不到则回退为登录用户 id（与台账侧 getCurrentUserName 同口径） */
    private String currentOperator() {
        String nickname = getLoginUserNickname();
        return StrUtil.isBlank(nickname) ? String.valueOf(getLoginUserId()) : nickname;
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
    public List<MesSetPollutionCheckDO> listByBizNos(String stage, Collection<String> bizNos) {
        if (StrUtil.isBlank(stage) || bizNos == null || bizNos.isEmpty()) {
            return List.of();
        }
        // 逐行 eq：链式 eq() 收窄成基础 LambdaQueryWrapper，后面就挂不上 LambdaQueryWrapperX 的方法了
        LambdaQueryWrapperX<MesSetPollutionCheckDO> query = new LambdaQueryWrapperX<>();
        query.eq(MesSetPollutionCheckDO::getStage, stage);
        query.in(MesSetPollutionCheckDO::getBizNo, bizNos);
        return pollutionCheckMapper.selectList(query);
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
