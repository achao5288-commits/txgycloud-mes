package cn.iocoder.txgy.module.mes.dal.mysql.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-碳排放核算记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetCarbonEmissionMapper extends BaseMapperX<MesSetCarbonEmissionDO> {

    default PageResult<MesSetCarbonEmissionDO> selectPage(MesSetCarbonEmissionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetCarbonEmissionDO>()
                .eqIfPresent(MesSetCarbonEmissionDO::getPeriodType, reqVO.getPeriodType())
                .eqIfPresent(MesSetCarbonEmissionDO::getEnergyType, reqVO.getEnergyType())
                .orderByDesc(MesSetCarbonEmissionDO::getId));
    }

    default MesSetCarbonEmissionDO selectByCalcNo(String calcNo) {
        return selectOne(MesSetCarbonEmissionDO::getCalcNo, calcNo);
    }

}
