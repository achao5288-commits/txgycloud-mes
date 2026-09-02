package cn.iocoder.txgy.module.mes.dal.mysql.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-作业环境气体检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetGasRecordMapper extends BaseMapperX<MesSetGasRecordDO> {

    default PageResult<MesSetGasRecordDO> selectPage(MesSetGasRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetGasRecordDO>()
                .eqIfPresent(MesSetGasRecordDO::getGasType, reqVO.getGasType())
                .eqIfPresent(MesSetGasRecordDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetGasRecordDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetGasRecordDO::getId));
    }

    default MesSetGasRecordDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetGasRecordDO::getRecordNo, recordNo);
    }

}
