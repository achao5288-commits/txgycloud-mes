package cn.iocoder.txgy.module.mes.dal.mysql.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-职业病危害因素检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetOccupationalHazardMapper extends BaseMapperX<MesSetOccupationalHazardDO> {

    default PageResult<MesSetOccupationalHazardDO> selectPage(MesSetOccupationalHazardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetOccupationalHazardDO>()
                .eqIfPresent(MesSetOccupationalHazardDO::getFactorCode, reqVO.getFactorCode())
                .eqIfPresent(MesSetOccupationalHazardDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetOccupationalHazardDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetOccupationalHazardDO::getId));
    }

    default MesSetOccupationalHazardDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetOccupationalHazardDO::getRecordNo, recordNo);
    }

}
