package cn.iocoder.txgy.module.mes.dal.mysql.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-危化品安全检查 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetChemicalSafetyMapper extends BaseMapperX<MesSetChemicalSafetyDO> {

    default MesSetChemicalSafetyDO selectByRecordNo(String record_no) {
        return selectOne(MesSetChemicalSafetyDO::getRecordNo, record_no);
    }

    default PageResult<MesSetChemicalSafetyDO> selectPage(MesSetChemicalSafetyPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetChemicalSafetyDO> query = new LambdaQueryWrapperX<MesSetChemicalSafetyDO>()
                .likeIfPresent(MesSetChemicalSafetyDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetChemicalSafetyDO::getChemicalCode, reqVO.getChemicalCode())
                .likeIfPresent(MesSetChemicalSafetyDO::getChemicalName, reqVO.getChemicalName())
                .eqIfPresent(MesSetChemicalSafetyDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetChemicalSafetyDO::getId);
        return selectPage(reqVO, query);
    }

}
