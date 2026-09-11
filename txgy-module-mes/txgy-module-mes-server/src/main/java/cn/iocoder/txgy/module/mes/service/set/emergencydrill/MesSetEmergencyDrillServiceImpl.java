package cn.iocoder.txgy.module.mes.service.set.emergencydrill;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillCoverageRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill.MesSetEmergencyDrillDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan.MesSetEmergencyPlanDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencydrill.MesSetEmergencyDrillMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyplan.MesSetEmergencyPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_DATE_BEFORE_PUBLISH;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_PLAN_NOT_FILED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_RECORD_INCOMPLETE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_RECTIFY_NOT_DONE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_STATUS_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_DRILL_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_NOT_EXISTS;

/**
 * MES 安全环保检测-应急演练 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmergencyDrillServiceImpl implements MesSetEmergencyDrillService {

    private static final Set<String> DRILL_TYPES =
            new HashSet<>(Arrays.asList("COMPREHENSIVE", "SPECIAL", "ONSITE"));

    private static final String STATUS_PLANNED = "PLANNED";
    private static final String STATUS_DONE = "DONE";
    private static final String STATUS_CLOSED = "CLOSED";

    private static final String RECTIFY_NONE = "NONE";
    private static final String RECTIFY_PENDING = "PENDING";
    private static final String RECTIFY_DONE = "DONE";

    @Resource
    private MesSetEmergencyDrillMapper emergencydrillMapper;

    @Resource
    private MesSetEmergencyPlanMapper emergencyplanMapper;

    @Override
    public Long createEmergencyDrill(MesSetEmergencyDrillSaveReqVO createReqVO) {
        MesSetEmergencyPlanDO plan = validateBase(createReqVO, null);
        MesSetEmergencyDrillDO obj = BeanUtils.toBean(createReqVO, MesSetEmergencyDrillDO.class);
        obj.setPlanVersion(plan.getVersion());
        obj.setStatus(STATUS_PLANNED);
        obj.setRectifyStatus(StrUtil.isBlank(obj.getRectifyRequirement()) ? RECTIFY_NONE : RECTIFY_PENDING);
        obj.setRectifyDoneDate(null);
        obj.setClosedDate(null);
        emergencydrillMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmergencyDrill(MesSetEmergencyDrillSaveReqVO updateReqVO) {
        MesSetEmergencyDrillDO exist = validateEmergencyDrillExists(updateReqVO.getId());
        MesSetEmergencyPlanDO plan = validateBase(updateReqVO, exist.getDrillNo());
        MesSetEmergencyDrillDO obj = BeanUtils.toBean(updateReqVO, MesSetEmergencyDrillDO.class);
        // 状态、闭环日期、整改完成日期都只能由动作写；置 null = 不参与 update
        obj.setStatus(null);
        obj.setClosedDate(null);
        obj.setRectifyDoneDate(null);
        obj.setPlanVersion(plan.getVersion());
        // 整改要求被清空 = 撤销整改；被填上或改内容 = 重新待整改（已完成的整改不因改文案而丢）
        if (StrUtil.isBlank(obj.getRectifyRequirement())) {
            obj.setRectifyStatus(RECTIFY_NONE);
        } else if (!RECTIFY_DONE.equals(exist.getRectifyStatus())) {
            obj.setRectifyStatus(RECTIFY_PENDING);
        } else {
            obj.setRectifyStatus(null);
        }
        emergencydrillMapper.updateById(obj);
    }

    @Override
    public void deleteEmergencyDrill(Long id) {
        validateEmergencyDrillExists(id);
        emergencydrillMapper.deleteById(id);
    }

    @Override
    public MesSetEmergencyDrillDO getEmergencyDrill(Long id) {
        return emergencydrillMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEmergencyDrillDO> getEmergencyDrillPage(MesSetEmergencyDrillPageReqVO pageReqVO) {
        return emergencydrillMapper.selectPage(pageReqVO);
    }

    @Override
    public void finishEmergencyDrill(Long id, String evaluation) {
        MesSetEmergencyDrillDO exist = validateEmergencyDrillExists(id);
        if (!STATUS_PLANNED.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_DRILL_STATUS_INVALID);
        }
        String eval = StrUtil.blankToDefault(evaluation, exist.getEvaluation());
        boolean hasRecord = StrUtil.isNotBlank(exist.getSignSheetUrl())
                || StrUtil.isNotBlank(exist.getPhotoUrl())
                || StrUtil.isNotBlank(exist.getVideoUrl());
        if (!hasRecord || StrUtil.isBlank(eval)) {
            throw exception(SET_EMERGENCY_DRILL_RECORD_INCOMPLETE);
        }
        MesSetEmergencyDrillDO update = new MesSetEmergencyDrillDO();
        update.setId(id);
        update.setStatus(STATUS_DONE);
        update.setEvaluation(eval);
        emergencydrillMapper.updateById(update);
    }

    @Override
    public void closeEmergencyDrill(Long id) {
        MesSetEmergencyDrillDO exist = validateEmergencyDrillExists(id);
        if (!STATUS_DONE.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_DRILL_STATUS_INVALID);
        }
        if (RECTIFY_PENDING.equals(exist.getRectifyStatus())) {
            throw exception(SET_EMERGENCY_DRILL_RECTIFY_NOT_DONE);
        }
        MesSetEmergencyDrillDO update = new MesSetEmergencyDrillDO();
        update.setId(id);
        update.setStatus(STATUS_CLOSED);
        update.setClosedDate(LocalDate.now());
        // 有整改要求且走到这里 = 已整改完；无要求则保持 NONE
        if (StrUtil.isNotBlank(exist.getRectifyRequirement())) {
            update.setRectifyStatus(RECTIFY_DONE);
            update.setRectifyDoneDate(LocalDate.now());
        }
        emergencydrillMapper.updateById(update);
    }

    @Override
    public MesSetEmergencyDrillCoverageRespVO getYearCoverage(Integer year) {
        int y = year == null ? LocalDate.now().getYear() : year;
        List<MesSetEmergencyDrillDO> drills =
                emergencydrillMapper.selectListByDateRange(LocalDate.of(y, 1, 1), LocalDate.of(y, 12, 31));
        long closed = drills.stream().filter(d -> STATUS_CLOSED.equals(d.getStatus())).count();

        MesSetEmergencyDrillCoverageRespVO resp = new MesSetEmergencyDrillCoverageRespVO();
        resp.setYear(y);
        resp.setTotalCount(drills.size());
        resp.setClosedCount((int) closed);
        // 「每年至少 1 次」按**已闭环**计数：没走完评估与整改的演练不算完成过一次演练
        resp.setSatisfied(closed >= 1);
        resp.setDrills(BeanUtils.toBean(drills, MesSetEmergencyDrillRespVO.class));
        return resp;
    }

    private MesSetEmergencyDrillDO validateEmergencyDrillExists(Long id) {
        MesSetEmergencyDrillDO obj = emergencydrillMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EMERGENCY_DRILL_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、演练类型枚举，以及**必须挂一份已发布/已备案的预案**。
     * 演练是对预案的检验，挂草稿等于检验一份还没生效的东西；演练日期也不能早于预案发布日。
     *
     * @return 关联的预案
     */
    private MesSetEmergencyPlanDO validateBase(MesSetEmergencyDrillSaveReqVO reqVO, String origin) {
        MesSetEmergencyDrillDO exist = emergencydrillMapper.selectByDrillNo(reqVO.getDrillNo());
        if (exist != null && !exist.getDrillNo().equals(origin)) {
            throw exception(SET_EMERGENCY_DRILL_NO_DUPLICATE);
        }
        if (reqVO.getDrillType() != null && !DRILL_TYPES.contains(reqVO.getDrillType())) {
            throw exception(SET_EMERGENCY_DRILL_TYPE_INVALID);
        }
        if (reqVO.getPlanId() == null) {
            throw exception(SET_EMERGENCY_DRILL_PLAN_NOT_FILED);
        }
        MesSetEmergencyPlanDO plan = emergencyplanMapper.selectById(reqVO.getPlanId());
        if (plan == null) {
            throw exception(SET_EMERGENCY_PLAN_NOT_EXISTS);
        }
        if (Objects.equals(plan.getStatus(), "DRAFT")) {
            throw exception(SET_EMERGENCY_DRILL_PLAN_NOT_FILED);
        }
        if (reqVO.getDrillDate() != null && plan.getPublishDate() != null
                && reqVO.getDrillDate().isBefore(plan.getPublishDate())) {
            throw exception(SET_EMERGENCY_DRILL_DATE_BEFORE_PUBLISH);
        }
        return plan;
    }

}
