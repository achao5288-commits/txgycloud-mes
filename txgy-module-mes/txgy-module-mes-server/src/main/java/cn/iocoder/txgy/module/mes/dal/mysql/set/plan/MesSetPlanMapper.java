package cn.iocoder.txgy.module.mes.dal.mysql.set.plan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.plan.MesSetPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-检测计划 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPlanMapper extends BaseMapperX<MesSetPlanDO> {

    default PageResult<MesSetPlanDO> selectPage(MesSetPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetPlanDO>()
                .likeIfPresent(MesSetPlanDO::getPlanName, reqVO.getPlanName())
                .eqIfPresent(MesSetPlanDO::getPlanType, reqVO.getPlanType())
                .eqIfPresent(MesSetPlanDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetPlanDO::getId));
    }

    default MesSetPlanDO selectByPlanNo(String planNo) {
        return selectOne(MesSetPlanDO::getPlanNo, planNo);
    }

}
