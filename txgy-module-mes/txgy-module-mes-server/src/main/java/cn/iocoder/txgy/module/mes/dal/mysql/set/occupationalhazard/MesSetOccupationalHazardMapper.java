package cn.iocoder.txgy.module.mes.dal.mysql.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-职业危害检测 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetOccupationalHazardMapper extends BaseMapperX<MesSetOccupationalHazardDO> {

    default MesSetOccupationalHazardDO selectByRecordNo(String record_no) {
        return selectOne(MesSetOccupationalHazardDO::getRecordNo, record_no);
    }

    default PageResult<MesSetOccupationalHazardDO> selectPage(MesSetOccupationalHazardPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetOccupationalHazardDO> query = new LambdaQueryWrapperX<MesSetOccupationalHazardDO>()
                .likeIfPresent(MesSetOccupationalHazardDO::getRecordNo, reqVO.getRecordNo())
                .eqIfPresent(MesSetOccupationalHazardDO::getFactorCategory, reqVO.getFactorCategory())
                .likeIfPresent(MesSetOccupationalHazardDO::getFactorCode, reqVO.getFactorCode())
                .likeIfPresent(MesSetOccupationalHazardDO::getWorkplace, reqVO.getWorkplace())
                .eqIfPresent(MesSetOccupationalHazardDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetOccupationalHazardDO::getId);
        return selectPage(reqVO, query);
    }

}
