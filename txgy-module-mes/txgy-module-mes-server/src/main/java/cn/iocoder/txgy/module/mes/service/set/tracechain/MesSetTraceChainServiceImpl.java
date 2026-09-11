package cn.iocoder.txgy.module.mes.service.set.tracechain;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceChainPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceReverseRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent.MesSetEmergencyEventMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent.MesSetEmergencyWasteMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.facility.MesSetTreatmentFacilityMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste.MesSetHazardousWasteMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck.MesSetPollutionCheckMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.signrecord.MesSetSignRecordMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.tracechain.MesSetTraceChainMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.weighrecord.MesSetWeighRecordMapper;
import cn.iocoder.txgy.module.mes.service.set.facility.MesSetTreatmentFacilityService;
import cn.iocoder.txgy.module.mes.service.set.hazwaste.MesSetHazwasteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_TRACE_REVERSE_PARAM_REQUIRED;

/**
 * MES 安全环保检测-追溯链节点 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetTraceChainServiceImpl implements MesSetTraceChainService {

    /**
     * 换炭登记的联单号前缀（见 MesSetTreatmentFacilityServiceImpl#buildWasteManifestNo）：
     * `HW49-{设施编号}-{时间戳}`。反向查来源靠它认回设施——台账行上没有设施外键。
     */
    private static final String CARBON_MANIFEST_PREFIX = "HW49-";

    /**
     * 应急模块给追溯链/签字用的业务类型。定义在
     * {@code MesSetEmergencyEventServiceImpl.TRACE_TYPE_EMERGENCY}（private），此处照抄一份：
     * 反向查来源本来就是在缝合两个模块，与其为一个字符串造公共常量类，不如就地写明它从哪来。
     */
    private static final String EMERGENCY_TRACE_TYPE = "EMERGENCY";

    @Resource
    private MesSetTraceChainMapper traceChainMapper;

    @Resource
    private MesSetPollutionCheckMapper pollutionCheckMapper;

    @Resource
    private MesSetHazardousWasteMapper hazwasteMapper;

    @Resource
    private MesSetWeighRecordMapper weighRecordMapper;

    @Resource
    private MesSetEmergencyWasteMapper emergencyWasteMapper;

    @Resource
    private MesSetEmergencyEventMapper emergencyEventMapper;

    @Resource
    private MesSetTreatmentFacilityMapper facilityMapper;

    @Resource
    private MesSetSignRecordMapper signRecordMapper;

    @Override
    public Long createTraceNode(MesSetTraceChainDO node) {
        traceChainMapper.insert(node);
        return node.getId();
    }

    @Override
    public MesSetTraceChainDO getTraceNode(Long id) {
        return traceChainMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetTraceChainDO> getTraceNodePage(MesSetTraceChainPageReqVO pageReqVO) {
        // 批次全链追溯：给出批次号时，先取该批全部判定的 recordNo，再聚合各条链的节点（跨环节去向链）
        if (StrUtil.isNotBlank(pageReqVO.getBatchNo())) {
            List<String> recordNos = pollutionCheckMapper.selectRecordNoListByBatchNo(pageReqVO.getBatchNo());
            if (recordNos.isEmpty()) {
                return PageResult.empty();
            }
            return traceChainMapper.selectPageByBizNos(recordNos, pageReqVO);
        }
        return traceChainMapper.selectPage(pageReqVO);
    }

    // ==================== 反向查来源 ====================

    @Override
    public MesSetTraceReverseRespVO reverseTrace(String containerCode, String manifestNo) {
        if (StrUtil.isBlank(containerCode) && StrUtil.isBlank(manifestNo)) {
            throw exception(SET_TRACE_REVERSE_PARAM_REQUIRED);
        }

        MesSetTraceReverseRespVO resp = new MesSetTraceReverseRespVO();
        resp.setContainerCode(containerCode);
        resp.setManifestNo(manifestNo);

        // ① 先找到台账行。给了联单号就按联单号（唯一），否则按桶码（可能多行）
        List<MesSetHazardousWasteDO> ledgers = new ArrayList<>();
        if (StrUtil.isNotBlank(manifestNo)) {
            MesSetHazardousWasteDO one = hazwasteMapper.selectByManifestNo(manifestNo);
            if (one != null) {
                ledgers.add(one);
            }
        } else {
            ledgers.addAll(hazwasteMapper.selectListByContainerCode(containerCode));
        }
        // 两个都给时按桶码再收一次口，避免"联单号对得上但不是这只桶"
        if (StrUtil.isNotBlank(manifestNo) && StrUtil.isNotBlank(containerCode)) {
            ledgers.removeIf(l -> !Objects.equals(l.getContainerCode(), containerCode));
        }

        resp.setFound(!ledgers.isEmpty());
        if (ledgers.isEmpty()) {
            resp.setSourced(false);
            resp.setLedgers(List.of());
            resp.setWeighRecords(List.of());
            resp.setTraceNodes(List.of());
            resp.setSignRecords(List.of());
            resp.setUnsourcedReason("危废台账里没有这只桶/这张联单：可能从未登记、或桶码写错了。"
                    + "现场应先确认贴签上的桶码，再回台账查。");
            return resp;
        }

        // ② 逐行反查源头。一行查不到就算无源，不因为"别的行查到了"而放过
        List<MesSetTraceReverseRespVO.Ledger> rows = new ArrayList<>(ledgers.size());
        List<String> unsourced = new ArrayList<>();
        for (MesSetHazardousWasteDO l : ledgers) {
            MesSetTraceReverseRespVO.Ledger row = toLedger(l);
            if (!resolveSource(l, row)) {
                unsourced.add(l.getManifestNo());
            }
            rows.add(row);
        }
        resp.setLedgers(rows);
        resp.setSourced(unsourced.isEmpty());
        if (!unsourced.isEmpty()) {
            resp.setUnsourcedReason("以下台账行反查不到源头（属「无源危废」，须人工补录来源）："
                    + String.join("、", unsourced));
        }

        // ③ 过秤证据 / 时间轴 / 签字：都按「这只桶」取，多行台账共用同一份证据
        String code = ledgers.get(0).getContainerCode();
        resp.setWeighRecords(StrUtil.isBlank(code) ? List.of()
                : weighRecordMapper.selectListByContainerCode(code));

        // 时间轴与签字**不是**按桶码挂的：应急模块把链挂在**事件号**上、停运审批挂在**设施号**上，
        // 桶码/联单号只是台账侧的键。只按桶码查会得到一条空时间轴——链明明在，却看着像从没流转过。
        Set<String> bizNos = new LinkedHashSet<>();
        for (MesSetHazardousWasteDO l : ledgers) {
            bizNos.add(l.getManifestNo());
            if (StrUtil.isNotBlank(l.getContainerCode())) {
                bizNos.add(l.getContainerCode());
            }
        }
        for (MesSetTraceReverseRespVO.Ledger row : rows) {
            if (StrUtil.isNotBlank(row.getSourceDocNo())) {
                bizNos.add(row.getSourceDocNo());
            }
        }
        bizNos.remove(null);
        resp.setTraceNodes(traceChainMapper.selectListByBizNos(bizNos));

        // 签字同理：三类来源各按自己的 (bizType, bizNo) 挂签，混成一次查询会张冠李戴
        Map<String, MesSetSignRecordDO> signs = new LinkedHashMap<>();
        for (MesSetTraceReverseRespVO.Ledger row : rows) {
            collectSigns(signs, MesSetHazwasteService.SIGN_BIZ_TYPE, row.getManifestNo());
            if ("EMERGENCY".equals(row.getSourceType())) {
                collectSigns(signs, EMERGENCY_TRACE_TYPE, row.getSourceDocNo());
            } else if ("FACILITY_CARBON".equals(row.getSourceType())) {
                collectSigns(signs, MesSetTreatmentFacilityService.SHUTDOWN_SIGN_BIZ_TYPE,
                        row.getSourceDocNo());
            }
        }
        resp.setSignRecords(new ArrayList<>(signs.values()));
        return resp;
    }

    /**
     * 收一份签字，按「业务类型 + 主键」去重——同一张单可能被多行台账各提到一次。
     * 带 bizType 一起查而不是只按 bizNo：事件号与设施号在不同业务里都可能重复，只认号会串台。
     */
    private void collectSigns(Map<String, MesSetSignRecordDO> sink, String bizType, String bizNo) {
        if (StrUtil.isBlank(bizNo)) {
            return;
        }
        for (MesSetSignRecordDO s : signRecordMapper.selectListByBiz(bizType, bizNo)) {
            sink.putIfAbsent(bizType + "#" + s.getId(), s);
        }
    }

    @Override
    public String exportTraceReport(String containerCode, String manifestNo) {
        MesSetTraceReverseRespVO trace = reverseTrace(containerCode, manifestNo);

        // 报告要写来源单据的全貌（事件报告原文、设施换炭周期），查好的判定结果里只有单据号，
        // 这里按单据号补一次；同一来源可能被多行台账共用，故先登记再取，省掉重复查询。
        Map<String, MesSetEmergencyEventDO> events = new LinkedHashMap<>();
        Map<String, MesSetTreatmentFacilityDO> facilities = new LinkedHashMap<>();
        for (MesSetTraceReverseRespVO.Ledger l : trace.getLedgers()) {
            if (StrUtil.isBlank(l.getSourceDocNo())) {
                continue;
            }
            if ("EMERGENCY".equals(l.getSourceType()) && !events.containsKey(l.getSourceDocNo())) {
                events.put(l.getSourceDocNo(), emergencyEventMapper.selectByEventNo(l.getSourceDocNo()));
            } else if ("FACILITY_CARBON".equals(l.getSourceType())
                    && !facilities.containsKey(l.getSourceDocNo())) {
                facilities.put(l.getSourceDocNo(), facilityMapper.selectByFacilityNo(l.getSourceDocNo()));
            }
        }
        return MesSetTraceReportRenderer.render(trace, events, facilities);
    }

    private MesSetTraceReverseRespVO.Ledger toLedger(MesSetHazardousWasteDO l) {
        MesSetTraceReverseRespVO.Ledger row = new MesSetTraceReverseRespVO.Ledger();
        row.setId(l.getId());
        row.setManifestNo(l.getManifestNo());
        row.setWasteCode(l.getWasteCode());
        row.setWasteName(l.getWasteName());
        row.setQuantity(l.getQuantity());
        row.setQuantityUnit(l.getQuantityUnit());
        row.setStage(l.getStage());
        row.setStatus(l.getStatus());
        row.setStorageLocation(l.getStorageLocation());
        row.setContainerCode(l.getContainerCode());
        row.setCounterparty(l.getCounterparty());
        row.setWoId(l.getWoId());
        row.setHandleTime(l.getHandleTime());
        row.setHandler(l.getHandler());
        return row;
    }

    /**
     * 反查一行台账的来源，就地写进 row。查到返回 true。
     * <p>
     * 两条已知来源，都靠**既有字段**认，不加新表：
     * <ul>
     *   <li>应急处置产废：{@code mes_set_emergency_waste} 按桶码回指事件（桶码 + 联单号双条件确认）；</li>
     *   <li>治理设施换炭：联单号前缀 {@code HW49-{设施编号}-} 里带着设施编号。</li>
     * </ul>
     * 都认不出就如实标 UNSOURCED —— 这正是物料衡算要暴露的"无源危废"，不是查询失败。
     */
    private boolean resolveSource(MesSetHazardousWasteDO l, MesSetTraceReverseRespVO.Ledger row) {
        // ① 应急处置产废
        if (StrUtil.isNotBlank(l.getContainerCode())) {
            for (MesSetEmergencyWasteDO w : emergencyWasteMapper.selectListByContainerCode(l.getContainerCode())) {
                // 桶码可能重复（不同事件用了同一个桶），故必须联单号也对上才算同一批
                if (!Objects.equals(w.getManifestNo(), l.getManifestNo())) {
                    continue;
                }
                MesSetEmergencyEventDO event = emergencyEventMapper.selectByEventNo(w.getEventNo());
                row.setSourceType("EMERGENCY");
                row.setSourceDocNo(w.getEventNo());
                row.setSourceId(event == null ? null : event.getId());
                row.setSourceName(event == null
                        ? ("应急事件 " + w.getEventNo())
                        : StrUtil.format("{} · {} · {}", w.getEventNo(),
                        StrUtil.blankToDefault(event.getEventType(), "-"),
                        StrUtil.blankToDefault(event.getLocation(), "-")));
                row.setSourceDetail(StrUtil.format("应急处置时过秤登记（桶 {}，净重 {} kg），"
                                + "已同步写入危废台账，来源可追到事件与处置人 {}",
                        w.getContainerCode(),
                        w.getNetWeight() == null ? "-" : w.getNetWeight().stripTrailingZeros().toPlainString(),
                        StrUtil.blankToDefault(event == null ? null : event.getHandler(), "-")));
                return true;
            }
        }

        // ② 治理设施换炭
        String facilityNo = parseFacilityNo(l.getManifestNo());
        if (facilityNo != null) {
            MesSetTreatmentFacilityDO facility = facilityMapper.selectByFacilityNo(facilityNo);
            // 前缀对得上但设施查不到 = 设施被删了，这仍然是"查得到来源"，只是源头没了
            row.setSourceType("FACILITY_CARBON");
            row.setSourceDocNo(facilityNo);
            row.setSourceId(facility == null ? null : facility.getId());
            row.setSourceName(facility == null
                    ? ("治理设施 " + facilityNo + "（档案已不存在）")
                    : StrUtil.format("{}（{}）换炭",
                    facility.getFacilityName(), facility.getFacilityNo()));
            row.setSourceDetail(facility == null
                    ? StrUtil.format("联单号 {} 指向治理设施 {}，但该设施档案已不存在："
                            + "换炭记录还在，设施查不到了，需人工核对。", l.getManifestNo(), facilityNo)
                    : StrUtil.format("治理设施 {} 换炭自动登记：换下的{}转为危废 {}。"
                            + "换炭不走称重流程（登记的是更换量），故本行没有过秤记录。",
                    facility.getFacilityName(),
                    StrUtil.blankToDefault(facility.getConsumableName(), "耗材"),
                    StrUtil.blankToDefault(l.getWasteCode(), "-")));
            return true;
        }

        row.setSourceType("UNSOURCED");
        row.setSourceDetail(StrUtil.format("联单号 {} 与桶码 {} 都认不出源头：既非应急处置产废，"
                        + "也不带换炭前缀（{}）。若这条废物确由生产产生，说明源头登记缺失，"
                        + "请到产生环节补录（物料衡算要求危废必须能反推源头）。",
                l.getManifestNo(), l.getContainerCode(), CARBON_MANIFEST_PREFIX));
        return false;
    }

    /**
     * 从换炭联单号 `HW49-{设施编号}-{时间戳}` 里取设施编号；不是这个形状返回 null。
     * ponytail: 靠命名约定认来源，设施编号里若含 `-` 会截断——现网编号形如 TF-001 不含，够用。
     */
    private String parseFacilityNo(String manifestNo) {
        if (manifestNo == null || !manifestNo.startsWith(CARBON_MANIFEST_PREFIX)) {
            return null;
        }
        String rest = manifestNo.substring(CARBON_MANIFEST_PREFIX.length());
        int dash = rest.lastIndexOf('-');
        return dash <= 0 ? null : rest.substring(0, dash);
    }

}
