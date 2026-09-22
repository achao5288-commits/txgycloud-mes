package cn.iocoder.txgy.module.mes.service.pollution;

import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckControlLocationReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * MES 环保污染管控服务（P2「真落地管控」心脏）
 *
 * 污染判定的"写透"与"门禁/清理"统一在这里，供判定复核、仓储(领用/出库)与台账侧单向调用。
 * 只依赖 Mapper（判定/批次/台账），不依赖其它 Service，避免 bean 环。
 *
 * 语义约定（台账行 = 权威，批次戳 = 投影缓存）：
 *  - 台账行由"复核=有污染(POLLUTED)"按 source_check_id 1:1 登记，是某批污染管控的权威事实。
 *  - 批次污染戳 pollution_* 只是"该批是否仍有未闭环(STORED/PROCESSING)台账行"的投影：
 *    有未闭环行 → POLLUTED（去向/标记/来源取最早开放行）；全部闭环/从未登记 → 四列全空(NULL)。
 *  - "复核=无污染(CLEAN)" → 关闭该批历史未闭环台账行为 CLEARED(已解除) 后整体重投影清戳；
 *    台账人工处置到终态(已回用/已排放/已处置) → 该行闭环，按剩余开放行重投影（不会误清同批其它开放行）。
 *  - 门禁只认批次戳：仅 POLLUTED 拦领用；POLLUTED 或 marked 拦出库；NULL/无戳均放行。
 *
 * @author OPENLAB BS
 */
public interface MesPollutionControlService {

    /** 环节：采购入库 */
    String STAGE_PURCHASE_INBOUND = "PURCHASE_INBOUND";
    /** 环节：生产领用 */
    String STAGE_MATERIAL_ISSUE = "MATERIAL_ISSUE";
    /** 环节：中间废弃物 */
    String STAGE_WASTE_INTERMEDIATE = "WASTE_INTERMEDIATE";
    /** 环节：成品 */
    String STAGE_FINISHED_PRODUCT = "FINISHED_PRODUCT";

    /** 人工复核结果：无污染 */
    String REVIEW_CLEAN = "CLEAN";
    /** 人工复核结果：有污染 */
    String REVIEW_POLLUTED = "POLLUTED";

    /**
     * 复核落定后的写透效果（A1+A2，须在事务内调用，抛错即回滚复核）：
     *  1) 校验：判"有污染"必须填去向(去向/库位)；非中间废弃物环节还必须关联真实批次（缺失/查不到 → 抛错）；
     *  2) A2 台账登记：复核=有污染 → 写 mes_pollution_ledger(STORED)，同一 source_check_id 已登记则不重复；
     *  3) A1 批次戳投影：无污染 → 先关闭该批未闭环台账行为 CLEARED(已解除)，再按剩余开放行重刷批次戳。
     * 中间废弃物环节无批次主数据：只登记台账、不投影批次戳。
     *
     * @param reviewed 已复核的完整判定记录（含 reviewResult/location/marked/recordNo 等落库后回查的整行）
     */
    void applyReviewEffect(MesSetPollutionCheckDO reviewed);

    /**
     * 复核后单独调整受控库位（需求 5.4「受控库位调整」）：
     * 判定一旦复核即终态（reviewPollutionCheck 会拒改），但受控库位是**物理事实**，现场倒库/危废间扩容后必须能改。
     *
     * 与复核同用一把门禁尺子（有污染→必须受控库位；无污染→不得占用受控库位），并同步三处，缺一处就会出现"判定行说 A、台账和批次戳还在 B"：
     *  1) 判定行 location/location_id；
     *  2) 该判定已登记的台账行（**关键**：批次污染戳是从台账行投影的，见 refreshBatchStamp 取最早开放行的 location）。
     *     applyReviewEffect 的登记是"不存在才插"，重复复核也不会补这一步，所以只能在这里改；
     *  3) 批次戳重投影。
     *
     * 人为操作，故带手写签名（reqVO.signImg）：缺签名抛 SET_POLLUTION_CHECK_SIGN_REQUIRED，且早返回路径也要签字。
     *
     * @param reqVO 判定记录编号（必须已复核）+ 目标库位编号 + 手写签名
     */
    void changeControlLocation(MesSetPollutionCheckControlLocationReqVO reqVO);

    /**
     * A3 生产领用门禁：批次戳 POLLUTED（即该批仍有未闭环"有污染"台账行）→ 抛 MES_POLLUTION_ISSUE_BLOCKED 拒绝领用。
     *
     * @param batchNo 批次号（null/空/批次不存在 视为放行）
     */
    void assertIssueAllowed(String batchNo);

    /**
     * A3 成品(销售)出库门禁：批次戳 POLLUTED 或 marked → 抛 MES_POLLUTION_OUTBOUND_BLOCKED 禁止出库。
     *
     * @param batchNo 批次号（null/空/批次不存在 视为放行）
     */
    void assertOutboundAllowed(String batchNo);

    /**
     * 按"未闭环台账行"重投影批次戳（A1/A2 心脏）：
     * 该批仍有未闭环(STORED/PROCESSING)台账行 → 戳 POLLUTED（去向/标记/来源取最早开放行）；
     * 全部闭环/从未登记 → 四列清空(NULL)，批次恢复可正常流转。
     * 台账处置到终态、"无污染"复核后均调用本方法重刷，同批存在多条开放台账时不会被单行闭环误清。
     *
     * @param batchNo 批次号（空/无主数据 视为无需投影，安全跳过）
     */
    void refreshBatchStamp(String batchNo);

    /**
     * 库存冻结投影（B1「入库待检冻结」）：把某批次的在库行 `material_stock.frozen` 重算成**唯一权威**结论。
     *
     * 与批次污染戳同源同点——都从"批次 + 判定"推导，只是在库行多认一条**过期**：
     *  1) 批次戳 POLLUTED 或 marked（含成品整体报废锁批）→ 冻结；
     *  2) 该批仍存在"待复核"(reviewResult IS NULL)判定行 → 冻结（**待检期冻结，判 CLEAN 才解冻**）；
     *  3) 批次已过期(expireDate < now) → 冻结（FEFO/过期冻结，不因后续无污染复核被解冻）。
     * 三条件取或，**幂等**：重复调用结果一致，故建单/复核/定时任务三处都能无条件调。
     *
     * @param batchNo 批次号（空/无主数据 → 安全跳过）
     * @return 被更新的在库行数
     */
    int syncStockFrozenByBatch(String batchNo);

    /**
     * 过期冻结扫描（B2，由定时任务驱动）：把所有已过期批次的在库行按 {@link #syncStockFrozenByBatch} 重算冻结态。
     * 判定为过期是**只冻结、不污染**——过期不等于有害，不写批次污染戳、不登台账。
     *
     * @return 被更新的在库行数
     */
    int freezeExpiredBatchStock();

    /**
     * A4 级联清理：删除某单据(stage+bizNo)下仍"待复核"(reviewResult IS NULL)的判定记录。
     *
     * @return 清理条数
     */
    int purgePendingByBizNo(String stage, String bizNo);

    /**
     * 按单据号批量取判定行（只读，供单据列表做「本单判定到哪一步」投影）。
     *
     * 单据表与判定表没有外键，唯一句柄是 biz_no，逐单查就是 N+1，所以在这里开一个批量口子；
     * 不在这里聚合，是因为「几行算判完」要用单据自己的行数去比，判定侧不知道单据有几行。
     *
     * @param stage  环节（如 {@link #STAGE_PURCHASE_INBOUND}）
     * @param bizNos 单据号集合；空集合返回空表（**不返回全表**）
     */
    List<MesSetPollutionCheckDO> listByBizNos(String stage, Collection<String> bizNos);

    /**
     * A4 级联清理(行删除)：删除某单据某行(stage+bizNo+itemCode，可带 batchNo)下仍"待复核"的判定记录。
     *
     * @return 清理条数
     */
    int purgePendingByBizNoItem(String stage, String bizNo, String itemCode, String batchNo);

    /**
     * 产废登记：直接把一条产废记录登记为台账行（**不经过污染判定**）。
     *
     * 用于治理设施换炭、设备维保换油等「确定性危废」——这类废物的危废身份是法定的、不需要现场判断。
     * 绕开判定表既避免往判定表灌机器数据（那会让人误以为是人判的），
     * 也避免把 createPollutionCheck 内部的 AI 调用带进业务事务。
     * 登记后 status=STORED、source_type=WASTE_REGISTER，可继续走台账的处置流转。
     * 产废无批次主数据，故不做批次戳投影（与"中间废弃物只登记台账不投影"同口径）。
     *
     * @param waste 产废信息
     * @return 生成的台账行 id
     */
    Long registerWasteLedger(WasteRegister waste);

    /**
     * 产废登记入参
     *
     * @param itemCode       物料编码（用真实物料，如 TX-FW-CAR-005）
     * @param itemSpec       规格/废物类别代码（如 HW49 900-039-49）
     * @param weight         重量；单位见 unitName（与判定侧同口径，不另立数量列）
     * @param unitName       重量单位
     * @param location       暂存去向
     * @param sourceRecordNo 来源单号（如换炭作业票号），可供反查
     * @param remark         备注
     */
    record WasteRegister(String itemCode, String itemName, String itemSpec,
                         BigDecimal weight, String unitName,
                         String location, String sourceRecordNo,
                         String remark) {
    }

}
