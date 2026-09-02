package cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-废水排放检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetWastewaterMapper extends BaseMapperX<MesSetWastewaterDO> {

    default PageResult<MesSetWastewaterDO> selectPage(MesSetWastewaterPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetWastewaterDO>()
                .eqIfPresent(MesSetWastewaterDO::getPollutantCode, reqVO.getPollutantCode())
                .eqIfPresent(MesSetWastewaterDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetWastewaterDO::getMonitorTime, reqVO.getMonitorTime())
                .orderByDesc(MesSetWastewaterDO::getId));
    }

    default MesSetWastewaterDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetWastewaterDO::getRecordNo, recordNo);
    }

}
