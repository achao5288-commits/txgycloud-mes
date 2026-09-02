package cn.iocoder.txgy.module.mes.service.set.plan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.plan.MesSetPlanDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.plan.MesSetPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PLAN_NOT_EXISTS;

/**
 * MES 安全环保检测-检测计划 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPlanServiceImpl implements MesSetPlanService {

    @Resource
    private MesSetPlanMapper planMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(MesSetPlanSaveReqVO createReqVO) {
        // 1. 校验计划编号唯一
        validatePlanNoUnique(null, createReqVO.getPlanNo());
        // 2. 插入记录（状态为空时由 DB 默认 DRAFT）
        MesSetPlanDO plan = BeanUtils.toBean(createReqVO, MesSetPlanDO.class);
        planMapper.insert(plan);
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(MesSetPlanSaveReqVO updateReqVO) {
        // 1. 校验存在 + 计划编号唯一
        validatePlanExists(updateReqVO.getId());
        validatePlanNoUnique(updateReqVO.getId(), updateReqVO.getPlanNo());
        // 2. 更新
        MesSetPlanDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPlanDO.class);
        planMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        // 1. 校验存在
        validatePlanExists(id);
        // 2. 删除
        planMapper.deleteById(id);
    }

    @Override
    public void validatePlanExists(Long id) {
        if (planMapper.selectById(id) == null) {
            throw exception(SET_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public MesSetPlanDO getPlan(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPlanDO> getPlanPage(MesSetPlanPageReqVO pageReqVO) {
        return planMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验计划编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param planNo 计划编号
     */
    private void validatePlanNoUnique(Long id, String planNo) {
        MesSetPlanDO exist = planMapper.selectByPlanNo(planNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_PLAN_NO_DUPLICATE);
        }
    }

}
