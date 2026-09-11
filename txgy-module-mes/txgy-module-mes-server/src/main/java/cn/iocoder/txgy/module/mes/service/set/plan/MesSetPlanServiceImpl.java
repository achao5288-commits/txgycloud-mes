package cn.iocoder.txgy.module.mes.service.set.plan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.plan.MesSetPlanDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.plan.MesSetPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_PLAN_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_PERIOD_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_STATUS_INVALID;

/**
 * MES 安全环保检测-检测计划 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPlanServiceImpl implements MesSetPlanService {

    /**
     * 触发类型：PERIODIC(周期)/EVENT(事件)
     */
    private static final Set<String> PLAN_TYPES = new HashSet<>(Arrays.asList("EVENT", "PERIODIC", "YEAR"));

    /**
     * 周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY
     */
    private static final Set<String> PERIOD_TYPES = new HashSet<>(Arrays.asList("MONTHLY", "YEAR"));

    /**
     * 状态：DRAFT/ACTIVE/STOPPED
     */
    private static final Set<String> STATUSS = new HashSet<>(Arrays.asList("ACTIVE", "DRAFT"));

    @Resource
    private MesSetPlanMapper planMapper;

    @Override
    public Long createPlan(MesSetPlanSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetPlanDO obj = BeanUtils.toBean(createReqVO, MesSetPlanDO.class);
        planMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePlan(MesSetPlanSaveReqVO updateReqVO) {
        MesSetPlanDO exist = validatePlanExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getPlanNo());
        planMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetPlanDO.class));
    }

    @Override
    public void deletePlan(Long id) {
        validatePlanExists(id);
        planMapper.deleteById(id);
    }

    @Override
    public MesSetPlanDO getPlan(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPlanDO> getPlanPage(MesSetPlanPageReqVO pageReqVO) {
        return planMapper.selectPage(pageReqVO);
    }

    private MesSetPlanDO validatePlanExists(Long id) {
        MesSetPlanDO obj = planMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_PLAN_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、触发类型：PERIODIC(周期)/EVENT(事件)枚举、周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY枚举、状态：DRAFT/ACTIVE/STOPPED枚举
     */
    private void validateBase(MesSetPlanSaveReqVO reqVO, String origin) {
        MesSetPlanDO exist = planMapper.selectByPlanNo(reqVO.getPlanNo());
        if (exist != null && !exist.getPlanNo().equals(origin)) {
            throw exception(SET_PLAN_NO_DUPLICATE);
        }
        if (reqVO.getPlanType() != null && !PLAN_TYPES.contains(reqVO.getPlanType())) {
            throw exception(SET_PLAN_PLAN_TYPE_INVALID);
        }
        if (reqVO.getPeriodType() != null && !PERIOD_TYPES.contains(reqVO.getPeriodType())) {
            throw exception(SET_PLAN_PERIOD_TYPE_INVALID);
        }
        if (reqVO.getStatus() != null && !STATUSS.contains(reqVO.getStatus())) {
            throw exception(SET_PLAN_STATUS_INVALID);
        }
    }

}
