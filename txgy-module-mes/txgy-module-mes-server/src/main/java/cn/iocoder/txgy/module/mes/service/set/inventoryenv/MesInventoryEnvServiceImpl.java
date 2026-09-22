package cn.iocoder.txgy.module.mes.service.set.inventoryenv;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.md.item.MesMdItemTypeDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.materialstock.MesWmMaterialStockDO;
import cn.iocoder.txgy.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.inventoryenv.MesInventoryEnvMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesSetPollutionCheckMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.wm.materialstock.MesWmMaterialStockMapper;
import cn.iocoder.txgy.module.mes.service.md.item.MesMdItemTypeService;
import cn.iocoder.txgy.module.mes.service.wm.batch.MesWmBatchService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.txgy.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.WM_BATCH_NOT_EXISTS;

/**
 * MES 在库环保视图 Service 实现
 */
@Service
@Validated
public class MesInventoryEnvServiceImpl implements MesInventoryEnvService {

    /** 默认检测周期（天） */
    private static final int DEFAULT_CYCLE_DAYS = 90;
    /** 默认积压阈值（天） */
    private static final int DEFAULT_STOCKPILE_DAYS = 180;
    /** 污染投影：有污染（与 MesPollutionControlService.REVIEW_POLLUTED 同值，此处只做读判断） */
    private static final String POLLUTION_POLLUTED = "POLLUTED";

    @Resource
    private MesInventoryEnvMapper inventoryEnvMapper;

    @Resource
    private MesMdItemTypeService itemTypeService;

    @Resource
    private MesWmBatchService batchService;

    @Resource
    private MesMdItemMapper itemMapper;

    @Resource
    private MesSetPollutionCheckMapper pollutionCheckMapper;

    @Resource
    private MesWmMaterialStockMapper materialStockMapper;

    @Override
    public PageResult<MesInventoryEnvRespVO> getInventoryEnvPage(MesInventoryEnvPageReqVO pageReqVO) {
        // 1. 物料分类含子分类，与库存台账同口径（否则选了父分类筛不出东西）
        Set<Long> itemTypeIds = null;
        if (pageReqVO.getItemTypeId() != null) {
            itemTypeIds = new HashSet<>();
            itemTypeIds.add(pageReqVO.getItemTypeId());
            itemTypeIds.addAll(convertSet(itemTypeService.getItemTypeChildrenList(pageReqVO.getItemTypeId()),
                    MesMdItemTypeDO::getId));
        }
        applyThresholdDefaults(pageReqVO);

        // 2. 分页查询
        Page<MesInventoryEnvRespVO> page = MyBatisUtils.buildPage(pageReqVO);
        // 判据列里带派生表与 EXISTS，MP 的计数 SQL 自动裁剪（optimizeJoin）会把它裁坏
        page.setOptimizeCountSql(false);
        IPage<MesInventoryEnvRespVO> result =
                inventoryEnvMapper.selectInventoryEnvPage(page, pageReqVO, itemTypeIds);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public MesInventoryEnvInspectSummaryRespVO inspectAll() {
        return inventoryEnvMapper.selectInspectSummary(thresholdOnlyReqVO());
    }

    @Override
    public MesInventoryEnvDashboardRespVO getDashboard() {
        MesInventoryEnvPageReqVO reqVO = thresholdOnlyReqVO();
        MesInventoryEnvInspectSummaryRespVO summary = inventoryEnvMapper.selectInspectSummary(reqVO);

        MesInventoryEnvDashboardRespVO resp = new MesInventoryEnvDashboardRespVO();
        resp.setSummary(summary);
        resp.setCoverRate(rate(summary.getCoveredBatchCount(), summary.getBatchCount()));
        // 异常率的分母也是「已复核批次」——没判过的批次不能算进异常率，否则未检越多异常率越低。
        // 分子分母都由同一条 SQL 按批次算，行数（一批多行）不会把比率顶到 1 以上。
        resp.setPollutedRate(rate(summary.getPollutedBatchCount(), summary.getCoveredBatchCount()));
        resp.setPendingReview(inventoryEnvMapper.selectPendingReview());
        resp.setLedgerDuration(inventoryEnvMapper.selectLedgerDuration());
        resp.setByWarehouse(inventoryEnvMapper.selectDashboardByWarehouse(reqVO));
        resp.setByItemType(inventoryEnvMapper.selectDashboardByItemType(reqVO));
        return resp;
    }

    @Override
    public MesInventoryEnvBatchProfileRespVO getBatchProfile(String batchCode) {
        MesWmBatchDO focus = batchService.getBatchByCode(batchCode);
        if (focus == null) {
            // 查档案查不到 ≠ 复核时批次对不上：后者文案是"请核对批次号后再复核"，用在这里会指错方向
            throw exception(WM_BATCH_NOT_EXISTS);
        }
        // 1. 两条链都不含起点自身（既有实现只返回上下游），起点的档案单独给
        List<MesWmBatchDO> forward = batchService.getForwardBatchList(batchCode);
        List<MesWmBatchDO> backward = batchService.getBackwardBatchList(batchCode);

        // 2. 一次性把链上所有批次的主数据补齐：逐个查会退化成 N+1（链深上限 10，但宽度不设限）
        Set<String> codes = new HashSet<>();
        codes.add(batchCode);
        forward.forEach(b -> codes.add(b.getCode()));
        backward.forEach(b -> codes.add(b.getCode()));
        Map<Long, String> itemNameMap = loadItemNames(focus, forward, backward);
        Map<String, MesSetPollutionCheckDO> lastCheckMap = loadLastChecks(codes);
        Set<String> inStockCodes = loadInStockCodes(codes);

        MesInventoryEnvBatchProfileRespVO resp = new MesInventoryEnvBatchProfileRespVO();
        resp.setFocus(toNode(focus, itemNameMap, lastCheckMap, inStockCodes, true));
        resp.setForward(toNodes(forward, itemNameMap, lastCheckMap, inStockCodes));
        resp.setBackward(toNodes(backward, itemNameMap, lastCheckMap, inStockCodes));
        // 3. 未判定 ≠ 无污染，污染 ≠ 未判定，两个数分开给，别让前端自己推
        Set<String> allCodes = new HashSet<>(codes);
        resp.setUnjudgedCount((int) allCodes.stream().filter(c -> !isJudged(lastCheckMap.get(c))).count());
        resp.setPollutedCount((int) allCodes.stream()
                .filter(c -> POLLUTION_POLLUTED.equals(pollutionStatusOf(c, focus, forward, backward))).count());
        return resp;
    }

    // ==================== 私有方法 ====================

    /**
     * 周期口径兜底：显式传 null 会让 DATE_SUB(..., INTERVAL NULL DAY) 变 NULL，判据静默失效。
     */
    private void applyThresholdDefaults(MesInventoryEnvPageReqVO reqVO) {
        if (reqVO.getCycleDays() == null) {
            reqVO.setCycleDays(DEFAULT_CYCLE_DAYS);
        }
        if (reqVO.getStockpileDays() == null) {
            reqVO.setStockpileDays(DEFAULT_STOCKPILE_DAYS);
        }
    }

    /** 体检/看板只认两个阈值，筛选字段一概不传：这两个口径是对全库的，带上筛选就成了「筛选结果的汇总」 */
    private MesInventoryEnvPageReqVO thresholdOnlyReqVO() {
        MesInventoryEnvPageReqVO reqVO = new MesInventoryEnvPageReqVO();
        reqVO.setCycleDays(DEFAULT_CYCLE_DAYS);
        reqVO.setStockpileDays(DEFAULT_STOCKPILE_DAYS);
        return reqVO;
    }

    private Double rate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0) {
            return 0D;
        }
        return round(numerator == null ? 0 : numerator, denominator);
    }

    private Double round(int numerator, int denominator) {
        return Math.round(numerator * 10000D / denominator) / 10000D;
    }

    private Map<Long, String> loadItemNames(MesWmBatchDO focus, List<MesWmBatchDO> forward,
                                              List<MesWmBatchDO> backward) {
        Set<Long> itemIds = new HashSet<>();
        List<MesWmBatchDO> all = new ArrayList<>(forward);
        all.addAll(backward);
        all.add(focus);
        all.forEach(b -> {
            if (b.getItemId() != null) {
                itemIds.add(b.getItemId());
            }
        });
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return convertMap(itemMapper.selectByIds(itemIds), MesMdItemDO::getId, MesMdItemDO::getName);
    }

    /** 每个批次号只留 id 最大的一条判定（与列表页「最近一次判定」的锚点算法一致） */
    private Map<String, MesSetPollutionCheckDO> loadLastChecks(Set<String> codes) {
        List<MesSetPollutionCheckDO> list = pollutionCheckMapper.selectList(
                new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                        .in(MesSetPollutionCheckDO::getBatchNo, codes)
                        .orderByDesc(MesSetPollutionCheckDO::getId));
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<String, MesSetPollutionCheckDO> map = new HashMap<>();
        for (MesSetPollutionCheckDO check : list) {
            // 已按 id 倒序，先到即最新
            map.putIfAbsent(check.getBatchNo(), check);
        }
        return map;
    }

    private Set<String> loadInStockCodes(Set<String> codes) {
        List<MesWmMaterialStockDO> stocks = materialStockMapper.selectList(
                new LambdaQueryWrapperX<MesWmMaterialStockDO>()
                        .in(MesWmMaterialStockDO::getBatchCode, codes)
                        .ne(MesWmMaterialStockDO::getQuantity, 0));
        Set<String> result = new HashSet<>();
        stocks.forEach(s -> {
            if (s.getBatchCode() != null) {
                result.add(s.getBatchCode());
            }
        });
        return result;
    }

    private boolean isJudged(MesSetPollutionCheckDO check) {
        return check != null && check.getReviewResult() != null;
    }

    private String pollutionStatusOf(String code, MesWmBatchDO focus, List<MesWmBatchDO> forward,
                                     List<MesWmBatchDO> backward) {
        if (code.equals(focus.getCode())) {
            return focus.getPollutionStatus();
        }
        return CollUtil.emptyIfNull(forward).stream().filter(b -> code.equals(b.getCode())).findFirst()
                .or(() -> CollUtil.emptyIfNull(backward).stream().filter(b -> code.equals(b.getCode())).findFirst())
                .map(MesWmBatchDO::getPollutionStatus).orElse(null);
    }

    private List<MesInventoryEnvBatchProfileNodeRespVO> toNodes(List<MesWmBatchDO> batches,
                                                                Map<Long, String> itemNameMap,
                                                                Map<String, MesSetPollutionCheckDO> lastCheckMap,
                                                                Set<String> inStockCodes) {
        List<MesInventoryEnvBatchProfileNodeRespVO> nodes = new ArrayList<>();
        for (MesWmBatchDO batch : CollUtil.emptyIfNull(batches)) {
            nodes.add(toNode(batch, itemNameMap, lastCheckMap, inStockCodes, false));
        }
        return nodes;
    }

    private MesInventoryEnvBatchProfileNodeRespVO toNode(MesWmBatchDO batch, Map<Long, String> itemNameMap,
                                                         Map<String, MesSetPollutionCheckDO> lastCheckMap,
                                                         Set<String> inStockCodes, boolean focus) {
        MesSetPollutionCheckDO check = lastCheckMap.get(batch.getCode());
        MesInventoryEnvBatchProfileNodeRespVO node = new MesInventoryEnvBatchProfileNodeRespVO();
        node.setBatchId(batch.getId());
        node.setBatchCode(batch.getCode());
        node.setItemId(batch.getItemId());
        node.setItemName(batch.getItemId() == null ? null : itemNameMap.get(batch.getItemId()));
        node.setProductionDate(batch.getProduceDate());
        node.setExpireDate(batch.getExpireDate());
        node.setQualityStatus(batch.getQualityStatus());
        node.setPollutionStatus(batch.getPollutionStatus());
        node.setPollutionLocation(batch.getPollutionLocation());
        node.setPollutionMarked(batch.getPollutionMarked());
        node.setPollutionSrcRecord(batch.getPollutionSrcRecord());
        node.setLastReviewResult(check == null ? null : check.getReviewResult());
        node.setLastCheckRecordNo(check == null ? null : check.getRecordNo());
        node.setLastCheckTime(check == null ? null : check.getReviewTime());
        node.setJudged(isJudged(check));
        node.setInStock(inStockCodes.contains(batch.getCode()));
        node.setFocus(focus);
        return node;
    }

}
