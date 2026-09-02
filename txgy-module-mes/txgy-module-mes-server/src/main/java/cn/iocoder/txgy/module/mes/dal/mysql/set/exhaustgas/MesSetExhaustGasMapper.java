package cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-废气排放检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetExhaustGasMapper extends BaseMapperX<MesSetExhaustGasDO> {

    default PageResult<MesSetExhaustGasDO> selectPage(MesSetExhaustGasPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetExhaustGasDO>()
                .eqIfPresent(MesSetExhaustGasDO::getPollutantCode, reqVO.getPollutantCode())
                .eqIfPresent(MesSetExhaustGasDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetExhaustGasDO::getMonitorTime, reqVO.getMonitorTime())
                .orderByDesc(MesSetExhaustGasDO::getId));
    }

    default MesSetExhaustGasDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetExhaustGasDO::getRecordNo, recordNo);
    }

}
