package cn.iocoder.txgy.module.mes.service.set.emergencyevent;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDetailRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDisposeReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventSaveReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.MesSetHazardousWasteSaveReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemical.MesSetChemicalProfileMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent.MesSetEmergencyEventMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent.MesSetEmergencyWasteMapper;
import cn.iocoder.txgy.module.mes.service.set.hazwaste.MesSetHazwasteService;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import cn.iocoder.txgy.module.mes.service.set.tracechain.MesSetTraceChainService;
import cn.iocoder.txgy.module.mes.service.set.weighrecord.MesSetWeighRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_CHEMICAL_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_REPORT_CONTENT_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_STATUS_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_WASTE_ALREADY_BUILT;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_WASTE_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_EVENT_WASTE_WEIGHT_INVALID;

/**
 * MES 安全环保检测-应急事件 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmergencyEventServiceImpl implements MesSetEmergencyEventService {

    private static final Set<String> EVENT_TYPES =
            new HashSet<>(Arrays.asList("LEAK", "EXCEED", "FACILITY_FAULT", "OTHER"));

    private static final String STATUS_REPORTED = "REPORTED";
    private static final String STATUS_DISPOSING = "DISPOSING";
    private static final String STATUS_PENDING_REPORT = "PENDING_REPORT";
    private static final String STATUS_CLOSED = "CLOSED";

    /**
     * 泄漏场景 → 默认危废类别与名称（设计文档 §八 表格：按场景 HW49/HW06/HW08）。
     * 只在明细没给 `wasteCode` 时兜底，给了就听给的——现场判断优先于预设。
     */
    private static final Map<String, String[]> SCENARIO_WASTE = Map.of(
            "MDI_LEAK", new String[]{"HW49", "泄漏吸附物（异氰酸酯）"},
            "THINNER_LEAK", new String[]{"HW06", "废稀释剂及吸附物"},
            "WASTE_OIL_LEAK", new String[]{"HW08", "废矿物油及吸附物"});

    /**
     * 追溯链对象类型：应急事件（DO 注释里的 CHECK/LEDGER 之外的第三类）
     */
    private static final String TRACE_TYPE_EMERGENCY = "EMERGENCY";

    /**
     * 称重类型：产废
     */
    private static final String WEIGH_TYPE_PRODUCE = "PRODUCE";

    @Resource
    private MesSetEmergencyEventMapper emergencyeventMapper;

    @Resource
    private MesSetEmergencyWasteMapper emergencyWasteMapper;

    @Resource
    private MesSetWeighRecordService weighRecordService;

    @Resource
    private MesSetHazwasteService hazwasteService;

    @Resource
    private MesSetSignRecordService signRecordService;

    @Resource
    private MesSetTraceChainService traceChainService;

    @Resource
    private MesSetChemicalProfileMapper chemicalProfileMapper;

    @Override
    public Long createEmergencyEvent(MesSetEmergencyEventSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetEmergencyEventDO obj = BeanUtils.toBean(createReqVO, MesSetEmergencyEventDO.class);
        // 上报人/上报时间取登录态；派发、处置、签字、闭环的字段一律不认提交值
        obj.setStatus(STATUS_REPORTED);
        obj.setReportTime(LocalDateTime.now());
        obj.setReportUser(currentUserName());
        obj.setHandler(null);
        obj.setDispatchTime(null);
        obj.setDisposeNote(null);
        obj.setDisposePhotoUrl(null);
        obj.setWasteCount(null);
        obj.setWasteQuantity(null);
        obj.setReportContent(null);
        obj.setApprover(null);
        obj.setApproveTime(null);
        obj.setClosedTime(null);
        emergencyeventMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmergencyEvent(MesSetEmergencyEventSaveReqVO updateReqVO) {
        MesSetEmergencyEventDO exist = validateEmergencyEventExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getEventNo());
        MesSetEmergencyEventDO obj = BeanUtils.toBean(updateReqVO, MesSetEmergencyEventDO.class);
        // 状态与"数字类"字段服务端拥有；处置说明/报告正文是文字，允许改
        obj.setStatus(null);
        obj.setReportUser(null);
        obj.setReportTime(null);
        obj.setHandler(null);
        obj.setDispatchTime(null);
        obj.setWasteCount(null);
        obj.setWasteQuantity(null);
        obj.setApprover(null);
        obj.setApproveTime(null);
        obj.setClosedTime(null);
        emergencyeventMapper.updateById(obj);
    }

    @Override
    public void deleteEmergencyEvent(Long id) {
        validateEmergencyEventExists(id);
        emergencyeventMapper.deleteById(id);
    }

    @Override
    public MesSetEmergencyEventDO getEmergencyEvent(Long id) {
        return emergencyeventMapper.selectById(id);
    }

    @Override
    public MesSetEmergencyEventDetailRespVO getEmergencyEventDetail(Long id) {
        MesSetEmergencyEventDO event = validateEmergencyEventExists(id);
        MesSetEmergencyEventDetailRespVO detail = new MesSetEmergencyEventDetailRespVO();
        detail.setEvent(BeanUtils.toBean(event, MesSetEmergencyEventRespVO.class));
        detail.setWastes(emergencyWasteMapper.selectListByEventId(id));
        return detail;
    }

    @Override
    public PageResult<MesSetEmergencyEventDO> getEmergencyEventPage(MesSetEmergencyEventPageReqVO pageReqVO) {
        return emergencyeventMapper.selectPage(pageReqVO);
    }

    @Override
    public void dispatchEmergencyEvent(Long id, String handler) {
        MesSetEmergencyEventDO exist = validateEmergencyEventExists(id);
        if (!STATUS_REPORTED.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_EVENT_STATUS_INVALID);
        }
        MesSetEmergencyEventDO update = new MesSetEmergencyEventDO();
        update.setId(id);
        update.setStatus(STATUS_DISPOSING);
        update.setHandler(StrUtil.blankToDefault(handler, currentUserName()));
        update.setDispatchTime(LocalDateTime.now());
        emergencyeventMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disposeEmergencyEvent(MesSetEmergencyEventDisposeReqVO reqVO) {
        MesSetEmergencyEventDO exist = validateEmergencyEventExists(reqVO.getId());
        // 「已建过档」先于「状态不对」判：重复提交的人要看到的是"废物已登记，别再来一遍"，
        // 而不是笼统的"状态流转不合法"——后者听着像点错了按钮，前者才说清了为什么。
        if (exist.getWasteCount() != null) {
            // 已建过档就别再来一次：再写一遍等于凭空多出一批桶，危废台账会凭空膨胀
            throw exception(SET_EMERGENCY_EVENT_WASTE_ALREADY_BUILT);
        }
        if (!STATUS_REPORTED.equals(exist.getStatus()) && !STATUS_DISPOSING.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_EVENT_STATUS_INVALID);
        }

        List<MesSetEmergencyEventDisposeReqVO.Waste> wastes =
                reqVO.getWastes() == null ? List.of() : reqVO.getWastes();
        BigDecimal total = BigDecimal.ZERO;
        int seq = 0;
        for (MesSetEmergencyEventDisposeReqVO.Waste item : wastes) {
            seq++;
            // ① 过秤：先写称重记录拿证据。毛重 = 净重、皮重 0 —— 应急现场来不及去皮，
            //    但**必须有这条记录**，"没称"和"称了 0"才分得开。
            BigDecimal net = item.getNetWeight();
            if (net == null || net.signum() <= 0) {
                throw exception(SET_EMERGENCY_EVENT_WASTE_WEIGHT_INVALID);
            }
            if (StrUtil.isBlank(item.getContainerCode())) {
                throw exception(SET_EMERGENCY_EVENT_WASTE_REQUIRED);
            }
            String[] fallback = SCENARIO_WASTE.get(exist.getScenario());
            String wasteCode = StrUtil.isNotBlank(item.getWasteCode())
                    ? item.getWasteCode() : (fallback == null ? null : fallback[0]);
            String wasteName = StrUtil.isNotBlank(item.getWasteName())
                    ? item.getWasteName() : (fallback == null ? null : fallback[1]);
            if (StrUtil.isBlank(wasteCode) || StrUtil.isBlank(wasteName)) {
                throw exception(SET_EMERGENCY_EVENT_WASTE_REQUIRED);
            }

            MesSetWeighRecordSaveReqVO weigh = new MesSetWeighRecordSaveReqVO();
            weigh.setWeighType(WEIGH_TYPE_PRODUCE);
            weigh.setBizType(TRACE_TYPE_EMERGENCY);
            weigh.setBizNo(exist.getEventNo());
            weigh.setContainerCode(item.getContainerCode());
            weigh.setGrossWeight(net);
            weigh.setTareWeight(BigDecimal.ZERO);
            weigh.setPhoto(reqVO.getDisposePhotoUrl());
            weigh.setReason("应急事件 " + exist.getEventNo() + " 应急处置产废过秤");
            Long weighId = weighRecordService.createWeighRecord(weigh);

            // ② 贴签入账：复用危废台账，不另起一套（应急不绕过管控）
            MesSetHazardousWasteSaveReqVO waste = new MesSetHazardousWasteSaveReqVO();
            waste.setManifestNo(buildWasteManifestNo(exist.getEventNo(), seq));
            waste.setWasteCode(wasteCode);
            waste.setWasteName(wasteName);
            waste.setQuantity(net);
            waste.setQuantityUnit("kg");
            waste.setStorageLocation(item.getStorageLocation());
            waste.setContainerCode(item.getContainerCode());
            waste.setCounterparty("应急事件 " + exist.getEventNo());
            waste.setHandleTime(LocalDateTime.now());
            waste.setHandler(currentUserName());
            waste.setRemark(StrUtil.format("应急处置自动登记：{}（设计文档 §八.4 应急废物一律过秤贴签入台账）",
                    StrUtil.blankToDefault(exist.getLocation(), exist.getEventNo())));
            Long hazwasteId = hazwasteService.createHazardousWaste(waste);

            // ③ 明细行把三者钉在一起，供追溯反查"这只桶从哪来"
            MesSetEmergencyWasteDO detail = MesSetEmergencyWasteDO.builder()
                    .eventId(exist.getId())
                    .eventNo(exist.getEventNo())
                    .containerCode(item.getContainerCode())
                    .wasteCode(wasteCode)
                    .wasteName(wasteName)
                    .netWeight(net)
                    .storageLocation(item.getStorageLocation())
                    .weighRecordId(weighId)
                    .hazwasteId(hazwasteId)
                    .manifestNo(waste.getManifestNo())
                    .build();
            emergencyWasteMapper.insert(detail);
            total = total.add(net);
        }

        MesSetEmergencyEventDO update = new MesSetEmergencyEventDO();
        update.setId(exist.getId());
        update.setDisposeNote(reqVO.getDisposeNote());
        update.setDisposePhotoUrl(reqVO.getDisposePhotoUrl());
        // 桶数与合计净重**由明细算出来**，不是前端传的：传了也不认
        update.setWasteCount(seq);
        update.setWasteQuantity(total);
        update.setStatus(STATUS_PENDING_REPORT);
        emergencyeventMapper.updateById(update);

        writeTrace(exist, "应急处置完成", StrUtil.format("应急废物 {} 桶、合计 {} kg 已过秤贴签入危废台账",
                seq, total.stripTrailingZeros().toPlainString()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeEmergencyEvent(Long id, String reportContent, String approver, String signImg) {
        MesSetEmergencyEventDO exist = validateEmergencyEventExists(id);
        if (!STATUS_PENDING_REPORT.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_EVENT_STATUS_INVALID);
        }
        String content = StrUtil.blankToDefault(reportContent, exist.getReportContent());
        if (StrUtil.isBlank(content)) {
            throw exception(SET_EMERGENCY_EVENT_REPORT_CONTENT_REQUIRED);
        }
        LocalDateTime now = LocalDateTime.now();
        String who = StrUtil.blankToDefault(approver, currentUserName());

        MesSetEmergencyEventDO update = new MesSetEmergencyEventDO();
        update.setId(id);
        update.setReportContent(content);
        update.setApprover(who);
        update.setApproveTime(now);
        update.setClosedTime(now);
        update.setStatus(STATUS_CLOSED);
        emergencyeventMapper.updateById(update);

        // 负责人签字进签字表（只增不改删），与操作日志/称重多向印证
        signRecordService.createSignRecord(MesSetSignRecordDO.builder()
                .bizType(TRACE_TYPE_EMERGENCY)
                .bizNo(exist.getEventNo())
                .signRole("APPROVER")
                .signUser(who)
                .signUserId(getLoginUserId())
                .signTime(now)
                .opinion("应急事件报告签发闭环")
                .signImg(signImg)
                .build());

        writeTrace(exist, "应急事件闭环", StrUtil.format("负责人 {} 签发事件报告：{}", who, content));
    }

    /**
     * 危废台账业务号：`{类别}-EM-{事件号}-{桶序号}`。
     * 带序号是为了同一个事件产生多桶时各自有据可查（台账查重靠服务层，不靠唯一键）。
     */
    private String buildWasteManifestNo(String eventNo, int seq) {
        return StrUtil.format("EM-{}-{}", eventNo, seq);
    }

    private void writeTrace(MesSetEmergencyEventDO event, String action, String extra) {
        traceChainService.createTraceNode(MesSetTraceChainDO.builder()
                .traceCode(event.getEventNo())
                .traceType(TRACE_TYPE_EMERGENCY)
                .parentCode(event.getChemicalCode())
                .bizType(TRACE_TYPE_EMERGENCY)
                .bizNo(event.getEventNo())
                .nodeStage(TRACE_TYPE_EMERGENCY)
                .nodeAction(action)
                .batchStatus(event.getStatus())
                .operatorName(currentUserName())
                .nodeTime(LocalDateTime.now())
                .extra(StrUtil.maxLength(extra, 250))
                .build());
    }

    private MesSetEmergencyEventDO validateEmergencyEventExists(Long id) {
        MesSetEmergencyEventDO obj = emergencyeventMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EMERGENCY_EVENT_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、事件类型枚举、涉事化学品档案存在。
     * 化学品不填可以（不是每个事件都指向某一种料），填了就必须查得到——
     * 否则处置卡取不到 MSDS，等于给了个假关联。
     */
    private void validateBase(MesSetEmergencyEventSaveReqVO reqVO, String origin) {
        MesSetEmergencyEventDO exist = emergencyeventMapper.selectByEventNo(reqVO.getEventNo());
        if (exist != null && !exist.getEventNo().equals(origin)) {
            throw exception(SET_EMERGENCY_EVENT_NO_DUPLICATE);
        }
        if (reqVO.getEventType() != null && !EVENT_TYPES.contains(reqVO.getEventType())) {
            throw exception(SET_EMERGENCY_EVENT_TYPE_INVALID);
        }
        if (StrUtil.isNotBlank(reqVO.getChemicalCode())) {
            MesSetChemicalProfileDO profile = chemicalProfileMapper.selectByChemicalCode(reqVO.getChemicalCode());
            if (profile == null) {
                throw exception(SET_EMERGENCY_EVENT_CHEMICAL_NOT_EXISTS);
            }
        }
    }

    private String currentUserName() {
        String nickname = getLoginUserNickname();
        return StrUtil.isBlank(nickname) ? String.valueOf(getLoginUserId()) : nickname;
    }

}
