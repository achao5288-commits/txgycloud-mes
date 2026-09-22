package cn.iocoder.txgy.module.mes.service.set.pollutioncheck;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesFieldSignRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesPollutionLegalBasisRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAiRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAmendReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckReviewReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesPollutionCheckLogDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.txgy.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.md.unitmeasure.MesMdUnitMeasureMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesPollutionCheckLogMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesSetPollutionCheckMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.batch.MesWmBatchMapper;
import cn.iocoder.txgy.module.mes.service.pollution.MesPollutionControlService;
import cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckAiService.PrescreenResult;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import cn.iocoder.txgy.module.mes.service.set.tracechain.MesSetTraceChainService;
import cn.iocoder.txgy.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_ALREADY_REVIEWED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_AMEND_ORIGIN_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_AMEND_REASON_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_SUPERSEDED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_BATCH_ID_NOT_FOUND;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_ITEM_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_FINISHED_RESULT_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_FINISHED_RESULT_NOT_ALLOWED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_FINISHED_RESULT_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_SCRAP_LOCATION_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_REVIEW_RESULT_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_SIGN_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_CHECK_STAGE_INVALID;

/**
 * MES 安全环保检测-污染判定记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPollutionCheckServiceImpl implements MesSetPollutionCheckService {

    /**
     * 环节：采购入库
     */
    public static final String STAGE_PURCHASE_INBOUND = "PURCHASE_INBOUND";
    /**
     * 环节：生产领用
     */
    public static final String STAGE_MATERIAL_ISSUE = "MATERIAL_ISSUE";
    /**
     * 环节：中间废弃物
     */
    public static final String STAGE_WASTE_INTERMEDIATE = "WASTE_INTERMEDIATE";
    /**
     * 环节：成品
     */
    public static final String STAGE_FINISHED_PRODUCT = "FINISHED_PRODUCT";
    /**
     * 环节：在库（对已在库批次发起检测，锚点是 batch_id 而不是某张单据；
     * 与其它四个环节的区别是「没有发生业务动作」，只是对存量做体检）
     */
    public static final String STAGE_IN_STOCK = "IN_STOCK";

    /**
     * 人工复核结果：无污染
     */
    public static final String REVIEW_CLEAN = "CLEAN";
    /**
     * 人工复核结果：有污染
     */
    public static final String REVIEW_POLLUTED = "POLLUTED";

    /**
     * 追溯/签字业务关联类型：污染判定
     */
    public static final String BIZ_TYPE_CHECK = "CHECK";
    /**
     * 签字角色：复核
     */
    public static final String SIGN_ROLE_REVIEWER = "REVIEWER";
    /**
     * 签字角色：操作人（发起检测、受控库位调整等前端手点的动作）
     */
    public static final String SIGN_ROLE_OPERATOR = "OPERATOR";

    private static final Set<String> STAGES = new HashSet<>(Arrays.asList(
            STAGE_PURCHASE_INBOUND, STAGE_MATERIAL_ISSUE, STAGE_WASTE_INTERMEDIATE, STAGE_FINISHED_PRODUCT,
            STAGE_IN_STOCK));
    private static final Set<String> REVIEW_RESULTS = new HashSet<>(Arrays.asList(REVIEW_CLEAN, REVIEW_POLLUTED));
    /**
     * 成品达标分支：达标（出厂）/ 局部缺陷（返工为主）/ 整体报废（整批锁定，剥离层经鉴别才转危废）
     */
    public static final String FINISHED_QUALIFIED = "QUALIFIED";
    public static final String FINISHED_REWORK = "REWORK";
    public static final String FINISHED_SCRAPPED = "SCRAPPED";
    private static final Set<String> FINISHED_RESULTS = new HashSet<>(
            Arrays.asList(FINISHED_QUALIFIED, FINISHED_REWORK, FINISHED_SCRAPPED));

    private static final DateTimeFormatter RECORD_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private MesSetPollutionCheckMapper pollutionCheckMapper;

    @Resource
    private MesPollutionCheckLogMapper pollutionCheckLogMapper;

    @Resource
    private MesSetPollutionCheckAiService pollutionCheckAiService;

    @Resource
    private MesPollutionControlService pollutionControlService;

    @Resource
    private MesSetTraceChainService traceChainService;

    @Resource
    private MesSetSignRecordService signRecordService;

    @Resource
    private MesWmBatchMapper batchMapper;

    @Resource
    private MesMdItemMapper itemMapper;

    @Resource
    private MesMdUnitMeasureMapper unitMeasureMapper;

    /**
     * 内置质量单位 → KG 系数。
     *
     * 故意写死，**不读 mes_md_unit_measure.change_rate**：那张表现存数据自相矛盾
     *（g→KG 写 0.1、mg→KG 写 0.001，按"1 主单位 = x 本单位"读 g 该是 1000，
     * 按"1 本单位 = x 主单位"读 mg 该是 0.000001），拿它换算只会算出错的数。
     * kg/g/mg/t 是物理常量，写死的不会错。
     *
     * ponytail: 只认这四个码；要支持自定义单位(斤/磅)得先有一张可信的换算率表。
     */
    private static final Map<String, BigDecimal> MASS_TO_KG = Map.of(
            "kg", BigDecimal.ONE,
            "g", new BigDecimal("0.001"),
            "mg", new BigDecimal("0.000001"),
            "t", new BigDecimal("1000"));
    /**
     * 已换算质量的落库单位
     */
    private static final String UNIT_KG = "KG";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPollutionCheck(MesSetPollutionCheckSaveReqVO createReqVO) {
        validateStage(createReqVO.getStage());
        // 1. 组装记录，自动编号
        MesSetPollutionCheckDO pollutionCheck = BeanUtils.toBean(createReqVO, MesSetPollutionCheckDO.class);
        // 1.1 锚定批次：必须排在 AI 初筛之前——初筛读的是 itemName，得先换成物料主数据里的真名
        resolveBatchAnchor(pollutionCheck);
        // 1.2 重量带单位：同样要排在 insert 之前，weight 裸数字在台账里读不出量纲
        resolveWeightUnit(pollutionCheck);
        pollutionCheck.setRecordNo(generateRecordNo());
        // 1.3 签字卡口：发起检测是人为主张（要向系统声明"这批要查"），缺签名拒掉，签名键 = 新建的 recordNo。
        //     排在 insert 之前是为了让"没签名"这条路径一个字节都不落库；同事务，后面抛错一起回滚。
        //     批量发起检测由前端一次签名覆盖一批，逐单走这个接口，每单各留一条签字。
        signRecordService.requireSigned(new MesSetSignRecordService.SignPayload(
                BIZ_TYPE_CHECK, pollutionCheck.getRecordNo(), SIGN_ROLE_OPERATOR,
                createReqVO.getSignImg(), createReqVO.getOpinion()), SET_POLLUTION_CHECK_SIGN_REQUIRED);
        // 2. AI 初筛回填，落为待复核（reviewResult=null）
        applyAiPrescreen(pollutionCheck);
        // 3. 插入
        pollutionCheckMapper.insert(pollutionCheck);
        // 3.1 B1 入库待检冻结：判定单一落库（reviewResult=null 即待检），该批在库行立即冻结。
        //     判 CLEAN 后由复核侧同一投影解冻——"待检即冻、判净才解"的入口在这。
        pollutionControlService.syncStockFrozenByBatch(pollutionCheck.getBatchNo());
        // 4. 履历留痕：创建(含 AI 初筛快照)
        appendCheckLog(pollutionCheck, MesPollutionCheckLogDO.OP_CREATE, getCurrentUserName(), LocalDateTime.now());
        return pollutionCheck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePollutionCheck(MesSetPollutionCheckSaveReqVO updateReqVO) {
        // 1. 校验存在 + 未收口才可改
        MesSetPollutionCheckDO exist = validatePollutionCheckExists(updateReqVO.getId());
        assertContentMutable(exist);
        validateStage(updateReqVO.getStage());
        // 2. 更新基础信息并重新 AI 初筛
        MesSetPollutionCheckDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPollutionCheckDO.class);
        resolveBatchAnchor(updateObj);
        resolveWeightUnit(updateObj);
        applyAiPrescreen(updateObj);
        pollutionCheckMapper.updateById(updateObj);
        // 2.1 改单可能换批次：旧批该解冻就解冻、新批该冻结就冻结（各自重算，互不牵连）
        pollutionControlService.syncStockFrozenByBatch(exist.getBatchNo());
        pollutionControlService.syncStockFrozenByBatch(updateObj.getBatchNo());
        // 3. 履历留痕：改单后的真实状态快照(含 AI 重筛结果)，旧版不覆盖
        MesSetPollutionCheckDO fresh = pollutionCheckMapper.selectById(exist.getId());
        appendCheckLog(fresh, MesPollutionCheckLogDO.OP_UPDATE, getCurrentUserName(), LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePollutionCheck(Long id) {
        // 1. 校验存在 + 未收口才可删
        MesSetPollutionCheckDO exist = validatePollutionCheckExists(id);
        assertContentMutable(exist);
        // 2. 删除
        pollutionCheckMapper.deleteById(id);
        // 2.1 冻结理由行没了 → 重算该批冻结态（可能因此解冻）
        pollutionControlService.syncStockFrozenByBatch(exist.getBatchNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long amendPollutionCheck(MesSetPollutionCheckAmendReqVO amendReqVO) {
        // 1. 原单必须存在、已收口、且还没被别的变更单替代
        //    未复核的判定直接改就行（update 就够），不必开变更单——变更单是给"结论已出、要推翻"用的
        MesSetPollutionCheckDO origin = validatePollutionCheckExists(amendReqVO.getOriginId());
        if (origin.getReviewResult() == null) {
            throw exception(SET_POLLUTION_CHECK_AMEND_ORIGIN_INVALID);
        }
        if (StrUtil.isNotBlank(origin.getSupersededBy())) {
            throw exception(SET_POLLUTION_CHECK_SUPERSEDED, origin.getSupersededBy());
        }
        if (StrUtil.isBlank(amendReqVO.getAmendReason())) {
            throw exception(SET_POLLUTION_CHECK_AMEND_REASON_REQUIRED);
        }
        validateStage(amendReqVO.getStage());
        // 2. 新单：编号服务端生成、id 清掉走 insert、指向原单
        MesSetPollutionCheckDO amend = BeanUtils.toBean(amendReqVO, MesSetPollutionCheckDO.class);
        amend.setId(null);
        amend.setRecordNo(generateRecordNo());
        amend.setOriginRecordNo(origin.getRecordNo());
        amend.setSupersededBy(null);
        resolveBatchAnchor(amend);
        resolveWeightUnit(amend);
        // 2.1 签字卡口：签名键 = **服务端刚生成的新单号**。信前端传的单号会让签字挂到别的单上
        signRecordService.requireSigned(new MesSetSignRecordService.SignPayload(
                BIZ_TYPE_CHECK, amend.getRecordNo(), SIGN_ROLE_OPERATOR,
                amendReqVO.getSignImg(), amendReqVO.getOpinion()), SET_POLLUTION_CHECK_SIGN_REQUIRED);
        // 2.2 新单同样回到待复核：变更后的结论要重新签，不能继承原单的复核
        applyAiPrescreen(amend);
        pollutionCheckMapper.insert(amend);
        pollutionControlService.syncStockFrozenByBatch(amend.getBatchNo());
        appendCheckLog(amend, MesPollutionCheckLogDO.OP_AMEND, getCurrentUserName(), LocalDateTime.now());
        // 3. 原单只回填 supersededBy —— 这是全表唯一一处允许写已收口行的地方，且只此一列、只写一次。
        //    用 update wrapper 显式 set 而不是 updateById：内容列一个都不许被顺带写进去。
        pollutionCheckMapper.update(null, new LambdaUpdateWrapper<MesSetPollutionCheckDO>()
                .eq(MesSetPollutionCheckDO::getId, origin.getId())
                .set(MesSetPollutionCheckDO::getSupersededBy, amend.getRecordNo()));
        appendCheckLog(origin, MesPollutionCheckLogDO.OP_SUPERSEDE, getCurrentUserName(), LocalDateTime.now());
        // 4. 追溯两条节点。查询只按 (biz_type, biz_no) 等值、不走 parent_code，只写一条另一边就查不到
        traceChainService.createTraceNode(buildAmendTraceNode(origin.getRecordNo(), origin.getStage(),
                StrUtil.format("已被变更单 {} 替代：{}", amend.getRecordNo(), amendReqVO.getAmendReason())));
        traceChainService.createTraceNode(buildAmendTraceNode(amend.getRecordNo(), amend.getStage(),
                StrUtil.format("替代原单 {}：{}", origin.getRecordNo(), amendReqVO.getAmendReason())));
        return amend.getId();
    }

    /**
     * 内容冻结判据 = **收口线**：已复核（结论已出）或已被变更单替代（连内容带结论一起作废）。
     *
     * 刻意**不是**「有没有签字行」：判定在创建时就落了一条发起检测签字（那是「这批要查」的主张，
     * 不是结论），按签字行冻结的话，待复核的草稿从建单那一刻起就永久不可改不可删，纠错都做不到。
     */
    private void assertContentMutable(MesSetPollutionCheckDO exist) {
        if (StrUtil.isNotBlank(exist.getSupersededBy())) {
            throw exception(SET_POLLUTION_CHECK_SUPERSEDED, exist.getSupersededBy());
        }
        if (exist.getReviewResult() != null) {
            throw exception(SET_POLLUTION_CHECK_ALREADY_REVIEWED);
        }
    }

    /**
     * 变更单的追溯节点：原单与新单各一条，nodeAction 说明"谁替代了谁、为什么"。
     */
    private MesSetTraceChainDO buildAmendTraceNode(String recordNo, String stage, String action) {
        return MesSetTraceChainDO.builder()
                .traceCode(recordNo)
                .traceType(BIZ_TYPE_CHECK)
                .bizType(BIZ_TYPE_CHECK)
                .bizNo(recordNo)
                .nodeStage(stage)
                .nodeAction(action)
                .operatorName(getLoginUserNickname())
                .nodeTime(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewPollutionCheck(MesSetPollutionCheckReviewReqVO reviewReqVO) {
        // 1. 校验存在 + 未复核
        MesSetPollutionCheckDO exist = validatePollutionCheckExists(reviewReqVO.getId());
        if (exist.getReviewResult() != null) {
            throw exception(SET_POLLUTION_CHECK_ALREADY_REVIEWED);
        }
        String reviewResult = reviewReqVO.getReviewResult();
        if (!REVIEW_RESULTS.contains(reviewResult)) {
            throw exception(SET_POLLUTION_CHECK_REVIEW_RESULT_INVALID);
        }
        // 1.1 成品达标分支（需求文档 §11 用例5）：成品环节必填三分支，其他环节不得填。
        //     与 reviewResult 正交：reviewResult 管污染态(放行不放行)，finishedResult 管达标与否。
        String finishedResult = reviewReqVO.getFinishedResult();
        boolean finished = STAGE_FINISHED_PRODUCT.equals(exist.getStage());
        if (finished) {
            if (StrUtil.isBlank(finishedResult)) {
                throw exception(SET_POLLUTION_CHECK_FINISHED_RESULT_REQUIRED);
            }
            if (!FINISHED_RESULTS.contains(finishedResult)) {
                throw exception(SET_POLLUTION_CHECK_FINISHED_RESULT_INVALID);
            }
        } else {
            if (StrUtil.isNotBlank(finishedResult)) {
                throw exception(SET_POLLUTION_CHECK_FINISHED_RESULT_NOT_ALLOWED);
            }
            finishedResult = null;
        }
        boolean scrapped = FINISHED_SCRAPPED.equals(finishedResult);
        // 整体报废不管有害无害都要给出存放去向（有害→受控存储，无害→直接存储+标记）
        if (scrapped && reviewReqVO.getLocationId() == null) {
            throw exception(SET_POLLUTION_CHECK_SCRAP_LOCATION_REQUIRED);
        }
        // 2. 组装复核结果（终态二值收口）
        boolean polluted = REVIEW_POLLUTED.equals(reviewResult);
        MesSetPollutionCheckDO updateObj = MesSetPollutionCheckDO.builder()
                .id(exist.getId())
                .reviewResult(reviewResult)
                .finishedResult(finishedResult)
                // 判定依据：**不强制**——填了就落库并对外可引，没填也放行。
                // 不设非空校验是有意的：复核结论本身才是终态权威，逼着凑一条法条比留空更糟
                // （现场特征只勾了"其他"时候选本来就是空的，强制会把人卡死）。
                .reviewBasis(reviewReqVO.getReviewBasis())
                .storageMethod(defaultStorageMethod(reviewResult, exist.getSuggestedStorage()))
                .disposition(defaultDisposition(exist.getStage(), reviewResult))
                // 报废一律强制标记（整批锁定不出库）；其余沿用入参或"有污染即标记"
                .marked(scrapped || (reviewReqVO.getMarked() != null ? reviewReqVO.getMarked() : polluted))
                .reviewBy(getCurrentUserName())
                .reviewTime(LocalDateTime.now())
                .remark(reviewReqVO.getRemark())
                .build();
        // 可选的字段按人工复核补充
        if (reviewReqVO.getStorageMethod() != null && !reviewReqVO.getStorageMethod().isEmpty()) {
            updateObj.setStorageMethod(reviewReqVO.getStorageMethod());
        }
        if (reviewReqVO.getDisposition() != null && !reviewReqVO.getDisposition().isEmpty()) {
            updateObj.setDisposition(reviewReqVO.getDisposition());
        }
        updateObj.setLocation(reviewReqVO.getLocation());
        updateObj.setLocationId(reviewReqVO.getLocationId());
        pollutionCheckMapper.updateById(updateObj);
        // 复核写透（P2）：戳真实批次污染戳 + 有污染自动登记台账（A1+A2），同事务
        MesSetPollutionCheckDO reviewed = pollutionCheckMapper.selectById(exist.getId());
        pollutionControlService.applyReviewEffect(reviewed);
        // 履历留痕：人工复核终态快照（在写透成功后再记，复核被校验拒绝则整体回滚不落痕）
        appendCheckLog(reviewed, MesPollutionCheckLogDO.OP_REVIEW, reviewed.getReviewBy(), reviewed.getReviewTime());
        // 追溯留痕（一期B）：复核收口写一条 CHECK 追溯节点 + 复核签字，同事务
        traceChainService.createTraceNode(buildReviewTraceNode(reviewed));
        signRecordService.createSignRecord(buildReviewSignRecord(reviewed, reviewReqVO.getSignImg()));
    }

    @Override
    public MesSetPollutionCheckAiRespVO prescreenPollutionCheck(MesSetPollutionCheckSaveReqVO reqVO) {
        PrescreenResult result = pollutionCheckAiService.prescreen(reqVO.getItemName(), reqVO.getItemSpec(), reqVO.getStage());
        return BeanUtils.toBean(result, MesSetPollutionCheckAiRespVO.class);
    }

    @Override
    public MesSetPollutionCheckDO getPollutionCheck(Long id) {
        return pollutionCheckMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPollutionCheckDO> getPollutionCheckPage(MesSetPollutionCheckPageReqVO pageReqVO) {
        return pollutionCheckMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesPollutionCheckLogDO> getPollutionCheckLogList(Long checkId) {
        return pollutionCheckLogMapper.selectByCheckId(checkId);
    }

    @Override
    public List<MesPollutionLegalBasisRespVO> getLegalBasisList() {
        return PollutionLegalBasis.CATALOG.stream()
                .map(article -> new MesPollutionLegalBasisRespVO(article.toLabel(), article.toValue()))
                .toList();
    }

    @Override
    public List<MesFieldSignRespVO> getFieldSignList() {
        return PollutionLegalBasis.SIGN_CATALOG.stream()
                .map(sign -> new MesFieldSignRespVO(sign.label(), sign.code(),
                        PollutionLegalBasis.articlesOfSigns(List.of(sign.code())).stream()
                                .map(article -> new MesPollutionLegalBasisRespVO(article.toLabel(), article.toValue()))
                                .toList()))
                .toList();
    }

    // ==================== 私有方法 ====================

    /**
     * 追加一条判定履历：把"该次操作之后"的真实快照(物料名+AI初筛+人工复核)落成不可改的一行。
     * 供 CREATE(初筛)/UPDATE(重筛)/REVIEW(复核) 三处调用，须在同一事务内。
     */
    private void appendCheckLog(MesSetPollutionCheckDO source, String opType, String opBy, LocalDateTime opTime) {
        if (source == null || source.getId() == null) {
            return;
        }
        MesPollutionCheckLogDO log = MesPollutionCheckLogDO.builder()
                .checkId(source.getId())
                .recordNo(source.getRecordNo())
                .opType(opType)
                .itemName(source.getItemName())
                .fieldSigns(source.getFieldSigns())
                .aiResult(source.getAiResult())
                .aiConfidence(source.getAiConfidence())
                .aiReason(source.getAiReason())
                .aiBasis(source.getAiBasis())
                .suggestedStorage(source.getSuggestedStorage())
                .reviewResult(source.getReviewResult())
                .reviewBasis(source.getReviewBasis())
                .storageMethod(source.getStorageMethod())
                .disposition(source.getDisposition())
                .location(source.getLocation())
                .marked(source.getMarked())
                .remark(source.getRemark())
                .opBy(opBy)
                .opTime(opTime)
                .build();
        pollutionCheckLogMapper.insert(log);
    }

    /**
     * 复核收口 → 追溯链 CHECK 节点（audit 数据，随复核同事务写库；trace_code=判定 recordNo）
     */
    private MesSetTraceChainDO buildReviewTraceNode(MesSetPollutionCheckDO reviewed) {
        return MesSetTraceChainDO.builder()
                .traceCode(reviewed.getRecordNo())
                .traceType(BIZ_TYPE_CHECK)
                .bizType(BIZ_TYPE_CHECK)
                .bizNo(reviewed.getRecordNo())
                .nodeStage(reviewed.getStage())
                .nodeAction(REVIEW_CLEAN.equals(reviewed.getReviewResult())
                        ? "复核收口：无污染合格，正常流转"
                        : "复核收口：有污染受控，登记台账处置")
                .batchStatus(reviewed.getReviewResult())
                .operatorName(reviewed.getReviewBy())
                .nodeTime(reviewed.getReviewTime())
                .extra(reviewed.getLocation())
                .build();
    }

    /**
     * 复核收口 → 复核签字（REVIEWER，意见=复核结果）
     */
    private MesSetSignRecordDO buildReviewSignRecord(MesSetPollutionCheckDO reviewed, String signImg) {
        return MesSetSignRecordDO.builder()
                .bizType(BIZ_TYPE_CHECK)
                .bizNo(reviewed.getRecordNo())
                .signRole(SIGN_ROLE_REVIEWER)
                .signUser(reviewed.getReviewBy())
                .signTime(reviewed.getReviewTime())
                .location(reviewed.getLocation())
                .opinion(REVIEW_CLEAN.equals(reviewed.getReviewResult()) ? "复核通过：无污染" : "复核判定：有污染，须受控处置")
                .signImg(signImg)
                .build();
    }

    /**
     * AI 初筛回填（未复核状态只应持有 AI 预分类）
     */
    private void applyAiPrescreen(MesSetPollutionCheckDO pollutionCheck) {
        PrescreenResult result = pollutionCheckAiService.prescreen(
                pollutionCheck.getItemName(), pollutionCheck.getItemSpec(), pollutionCheck.getStage());
        pollutionCheck.setAiResult(result.getAiResult());
        pollutionCheck.setAiConfidence(result.getAiConfidence());
        pollutionCheck.setAiReason(result.getAiReason());
        pollutionCheck.setAiBasis(result.getAiBasis());
        pollutionCheck.setSuggestedStorage(result.getSuggestedStorage());
        pollutionCheck.setReviewResult(null);
    }

    /**
     * 判定锚定批次：传了 batchId 就以「批次 + 物料主数据」为准，**覆盖**请求里的批次号/物料编码/名称/规格。
     *
     * 为什么必须服务端覆盖而不是信任入参：入参原本是人工打字的自由文本，实测 69 条在用判定里
     * 只有 37 条(54%)的 batch_no 能 join 回 mes_wm_batch、单号只有 10 条(14%)能 join 回真实单据。
     * 覆盖之后 batch_no 变成批次主数据的快照，下游 refreshBatchStamp / isBatchFrozen / 台账
     * 那些按 batch_no 字符串匹配的既有逻辑就自动可靠了，一行都不用改。
     *
     * 不传 batchId 时保留原行为：中间废弃物环节没有批次主数据，以及历史/接口兼容。
     */
    private void resolveBatchAnchor(MesSetPollutionCheckDO check) {
        if (check.getBatchId() == null) {
            if (StrUtil.isBlank(check.getItemName())) {
                throw exception(SET_POLLUTION_CHECK_ITEM_REQUIRED);
            }
            return;
        }
        MesWmBatchDO batch = batchMapper.selectById(check.getBatchId());
        if (batch == null) {
            throw exception(SET_POLLUTION_CHECK_BATCH_ID_NOT_FOUND, check.getBatchId());
        }
        check.setBatchNo(batch.getCode());
        MesMdItemDO item = batch.getItemId() == null ? null : itemMapper.selectById(batch.getItemId());
        if (item != null) {
            check.setItemCode(item.getCode());
            check.setItemName(item.getName());
            check.setItemSpec(item.getSpecification());
        }
        // 批次存在但物料主数据缺失：仍要求有名字，否则 AI 初筛与后续追溯都无依据
        if (StrUtil.isBlank(check.getItemName())) {
            throw exception(SET_POLLUTION_CHECK_ITEM_REQUIRED);
        }
    }

    /**
     * 重量单位落库：把物料主单位名写进 unit_name，质量单位顺手归一到 KG。
     *
     * 请求里的 weight 是来源单据的数量原值(入库数量/领料数量/在库数量)，本身不带单位，
     * 只落个裸数字的话台账那列没法读——123 到底是 123kg 还是 123 个。
     *
     * 非质量单位(个/箱/米/瓶)没有可比质量，**不换算**，原样保留数量并把原单位写进去，
     * 台账显示成「123 个」。
     */
    private void resolveWeightUnit(MesSetPollutionCheckDO check) {
        if (check.getWeight() == null) {
            return; // 没带数量就没什么可标的
        }
        MesMdItemDO item = null;
        if (check.getBatchId() != null) {
            MesWmBatchDO batch = batchMapper.selectById(check.getBatchId());
            item = (batch == null || batch.getItemId() == null) ? null
                    : itemMapper.selectById(batch.getItemId());
        }
        // 无批次的行(物料未启用批次管理)拿不到 batch，只能按编码反查——在库体检必须锚批次，
        // 但采购入库/领料/成品这些单据行是可以没有批次的，这条分支就是给它们走的
        if (item == null && StrUtil.isNotBlank(check.getItemCode())) {
            item = itemMapper.selectByCode(check.getItemCode());
        }
        if (item == null || item.getUnitMeasureId() == null) {
            return;
        }
        // 单位要跨租户读：mes_md_unit_measure 是全局字典(KG/个/箱/米…)，不是租户私有数据。
        // 现网数据本身就这么引用——租户 2010 的「小包装盒」(id 1094) 的 unit_measure_id
        // 指向租户 1 的单位行 202(PCS)。带租户过滤查出来是 null，单位就永远落不下去、
        // 换算也无从谈起，而且**不报错**。
        final Long unitMeasureId = item.getUnitMeasureId(); // lambda 只认 effectively final
        MesMdUnitMeasureDO unit = TenantUtils.executeIgnore(
                () -> unitMeasureMapper.selectById(unitMeasureId));
        if (unit == null) {
            return;
        }
        BigDecimal toKg = MASS_TO_KG.get(StrUtil.trimToEmpty(unit.getCode()).toLowerCase());
        if (toKg != null) {
            check.setWeight(check.getWeight().multiply(toKg));
            check.setUnitName(UNIT_KG);
            return;
        }
        check.setUnitName(StrUtil.blankToDefault(unit.getName(), unit.getCode()));
    }

    /**
     * 记录编号：PC-yyyyMMddHHmmss+3随机数
     */
    private String generateRecordNo() {
        String time = RECORD_NO_FORMATTER.format(LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(0, 1000);
        return String.format("PC-%s%03d", time, random);
    }

    /**
     * 最终存储方法默认值：无污染普通仓；有污染沿用 AI 推荐（无则受控隔离）
     */
    private String defaultStorageMethod(String reviewResult, String suggestedStorage) {
        if (REVIEW_CLEAN.equals(reviewResult)) {
            return "普通仓储";
        }
        return (suggestedStorage != null && !suggestedStorage.isEmpty()) ? suggestedStorage : "污染受控隔离存储";
    }

    /**
     * 处置方式默认值：按 环节 × 人工复核结果
     */
    private String defaultDisposition(String stage, String reviewResult) {
        boolean polluted = REVIEW_POLLUTED.equals(reviewResult);
        if (polluted) {
            if (STAGE_MATERIAL_ISSUE.equals(stage)) {
                return "REJECT_ISSUE"; // 拒绝领用
            }
            if (STAGE_WASTE_INTERMEDIATE.equals(stage)) {
                return "ISOLATE_STORAGE"; // 隔离暂存/保存处理
            }
            return "CONTROLLED_STORAGE"; // 采购入库/成品 → 受控存储
        }
        if (STAGE_MATERIAL_ISSUE.equals(stage)) {
            return "ISSUE_ALLOWED"; // 允许领用
        }
        if (STAGE_WASTE_INTERMEDIATE.equals(stage)) {
            return "REUSE"; // 回用（人工也可改选排放 DISCHARGE）
        }
        return "NORMAL_INBOUND"; // 采购入库/成品 → 正常入库/直接存储
    }

    private void validateStage(String stage) {
        if (!STAGES.contains(stage)) {
            throw exception(SET_POLLUTION_CHECK_STAGE_INVALID);
        }
    }

    private MesSetPollutionCheckDO validatePollutionCheckExists(Long id) {
        MesSetPollutionCheckDO pollutionCheck = pollutionCheckMapper.selectById(id);
        if (pollutionCheck == null) {
            throw exception(SET_POLLUTION_CHECK_NOT_EXISTS);
        }
        return pollutionCheck;
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
