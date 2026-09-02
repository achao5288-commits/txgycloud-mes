package cn.iocoder.txgy.module.mes.dal.mysql.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-压力容器检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPressureVesselMapper extends BaseMapperX<MesSetPressureVesselDO> {

    default PageResult<MesSetPressureVesselDO> selectPage(MesSetPressureVesselPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetPressureVesselDO>()
                .eqIfPresent(MesSetPressureVesselDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetPressureVesselDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetPressureVesselDO::getId));
    }

    default MesSetPressureVesselDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetPressureVesselDO::getRecordNo, recordNo);
    }

}
