package cn.iocoder.txgy.module.mes.dal.mysql.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-危化品安全巡查记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetChemicalSafetyMapper extends BaseMapperX<MesSetChemicalSafetyDO> {

    default PageResult<MesSetChemicalSafetyDO> selectPage(MesSetChemicalSafetyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetChemicalSafetyDO>()
                .eqIfPresent(MesSetChemicalSafetyDO::getChemicalCode, reqVO.getChemicalCode())
                .likeIfPresent(MesSetChemicalSafetyDO::getChemicalName, reqVO.getChemicalName())
                .eqIfPresent(MesSetChemicalSafetyDO::getStorageLocation, reqVO.getStorageLocation())
                .eqIfPresent(MesSetChemicalSafetyDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetChemicalSafetyDO::getId));
    }

    default MesSetChemicalSafetyDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetChemicalSafetyDO::getRecordNo, recordNo);
    }

}
