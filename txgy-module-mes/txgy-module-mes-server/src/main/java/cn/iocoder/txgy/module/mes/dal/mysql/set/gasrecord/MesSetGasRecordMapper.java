package cn.iocoder.txgy.module.mes.dal.mysql.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-气体检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetGasRecordMapper extends BaseMapperX<MesSetGasRecordDO> {

    default MesSetGasRecordDO selectByRecordNo(String record_no) {
        return selectOne(MesSetGasRecordDO::getRecordNo, record_no);
    }

    default PageResult<MesSetGasRecordDO> selectPage(MesSetGasRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetGasRecordDO> query = new LambdaQueryWrapperX<MesSetGasRecordDO>()
                .likeIfPresent(MesSetGasRecordDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetGasRecordDO::getLocation, reqVO.getLocation())
                .eqIfPresent(MesSetGasRecordDO::getGasType, reqVO.getGasType())
                .likeIfPresent(MesSetGasRecordDO::getResult, reqVO.getResult())
                .likeIfPresent(MesSetGasRecordDO::getCollectionMode, reqVO.getCollectionMode())
                .orderByDesc(MesSetGasRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
