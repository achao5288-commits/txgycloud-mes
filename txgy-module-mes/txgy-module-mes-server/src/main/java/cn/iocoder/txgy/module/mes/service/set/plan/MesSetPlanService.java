package cn.iocoder.txgy.module.mes.service.set.plan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.plan.MesSetPlanDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-检测计划 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPlanService {

    /**
     * 创建检测计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPlan(@Valid MesSetPlanSaveReqVO createReqVO);

    /**
     * 更新检测计划
     *
     * @param updateReqVO 更新信息
     */
    void updatePlan(@Valid MesSetPlanSaveReqVO updateReqVO);

    /**
     * 删除检测计划
     *
     * @param id 编号
     */
    void deletePlan(Long id);

    /**
     * 校验检测计划存在
     *
     * @param id 编号
     */
    void validatePlanExists(Long id);

    /**
     * 获得检测计划
     *
     * @param id 编号
     * @return 检测计划
     */
    MesSetPlanDO getPlan(Long id);

    /**
     * 获得检测计划分页
     *
     * @param pageReqVO 分页查询
     * @return 检测计划分页
     */
    PageResult<MesSetPlanDO> getPlanPage(MesSetPlanPageReqVO pageReqVO);

}
