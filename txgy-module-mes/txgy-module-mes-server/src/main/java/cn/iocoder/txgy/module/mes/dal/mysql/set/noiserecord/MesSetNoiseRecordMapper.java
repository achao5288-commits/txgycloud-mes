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

    default PageResult<MesSetNoiseRecordDO> selectPage(MesSetNoiseRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetNoiseRecordDO>()
                .eqIfPresent(MesSetNoiseRecordDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(MesSetNoiseRecordDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetNoiseRecordDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetNoiseRecordDO::getId));
    }

    default MesSetNoiseRecordDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetNoiseRecordDO::getRecordNo, recordNo);
    }

}
