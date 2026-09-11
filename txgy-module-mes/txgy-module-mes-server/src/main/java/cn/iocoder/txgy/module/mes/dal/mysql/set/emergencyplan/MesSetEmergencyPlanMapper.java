package cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyplan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan.MesSetEmergencyPlanDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * MES 安全环保检测-应急预案 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmergencyPlanMapper extends BaseMapperX<MesSetEmergencyPlanDO> {

    default MesSetEmergencyPlanDO selectByPlanNo(String plan_no) {
        return selectOne(MesSetEmergencyPlanDO::getPlanNo, plan_no);
    }

    /**
     * 待评估修订（含已逾期）：下次评估日 &lt;= 基准日。
     * 草稿没有 nextReviewDate，自然不入列——**没发布的不算"该修订了"**。
     */
    default List<MesSetEmergencyPlanDO> selectDueReview(LocalDate deadline) {
        return selectList(new LambdaQueryWrapperX<MesSetEmergencyPlanDO>()
                .isNotNull(MesSetEmergencyPlanDO::getNextReviewDate)
                .le(MesSetEmergencyPlanDO::getNextReviewDate, deadline)
                .orderByAsc(MesSetEmergencyPlanDO::getNextReviewDate));
    }

    default PageResult<MesSetEmergencyPlanDO> selectPage(MesSetEmergencyPlanPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEmergencyPlanDO> query = new LambdaQueryWrapperX<MesSetEmergencyPlanDO>()
                .likeIfPresent(MesSetEmergencyPlanDO::getPlanNo, reqVO.getPlanNo())
                .likeIfPresent(MesSetEmergencyPlanDO::getPlanName, reqVO.getPlanName())
                .likeIfPresent(MesSetEmergencyPlanDO::getPlanType, reqVO.getPlanType())
                .likeIfPresent(MesSetEmergencyPlanDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmergencyPlanDO::getId);
        return selectPage(reqVO, query);
    }

}
