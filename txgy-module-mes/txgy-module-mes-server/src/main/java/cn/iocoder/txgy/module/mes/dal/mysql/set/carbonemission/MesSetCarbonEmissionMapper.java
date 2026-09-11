package cn.iocoder.txgy.module.mes.dal.mysql.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-碳排放核算 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetCarbonEmissionMapper extends BaseMapperX<MesSetCarbonEmissionDO> {

    default MesSetCarbonEmissionDO selectByCalcNo(String calc_no) {
        return selectOne(MesSetCarbonEmissionDO::getCalcNo, calc_no);
    }

    default PageResult<MesSetCarbonEmissionDO> selectPage(MesSetCarbonEmissionPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetCarbonEmissionDO> query = new LambdaQueryWrapperX<MesSetCarbonEmissionDO>()
                .likeIfPresent(MesSetCarbonEmissionDO::getCalcNo, reqVO.getCalcNo())
                .likeIfPresent(MesSetCarbonEmissionDO::getPeriodType, reqVO.getPeriodType())
                .eqIfPresent(MesSetCarbonEmissionDO::getPeriodStart, reqVO.getPeriodStart())
                .eqIfPresent(MesSetCarbonEmissionDO::getEnergyType, reqVO.getEnergyType())
                .orderByDesc(MesSetCarbonEmissionDO::getId);
        return selectPage(reqVO, query);
    }

}
