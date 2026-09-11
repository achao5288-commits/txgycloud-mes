package cn.iocoder.txgy.module.mes.dal.mysql.set.noiserecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord.MesSetNoiseRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-噪声检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetNoiseRecordMapper extends BaseMapperX<MesSetNoiseRecordDO> {

    default MesSetNoiseRecordDO selectByRecordNo(String record_no) {
        return selectOne(MesSetNoiseRecordDO::getRecordNo, record_no);
    }

    default PageResult<MesSetNoiseRecordDO> selectPage(MesSetNoiseRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetNoiseRecordDO> query = new LambdaQueryWrapperX<MesSetNoiseRecordDO>()
                .likeIfPresent(MesSetNoiseRecordDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetNoiseRecordDO::getSourceType, reqVO.getSourceType())
                .likeIfPresent(MesSetNoiseRecordDO::getLocation, reqVO.getLocation())
                .eqIfPresent(MesSetNoiseRecordDO::getCollectionMode, reqVO.getCollectionMode())
                .eqIfPresent(MesSetNoiseRecordDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetNoiseRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
