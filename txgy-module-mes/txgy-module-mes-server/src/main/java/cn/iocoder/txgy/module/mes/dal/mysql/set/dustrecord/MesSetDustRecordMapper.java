package cn.iocoder.txgy.module.mes.dal.mysql.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-粉尘浓度检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetDustRecordMapper extends BaseMapperX<MesSetDustRecordDO> {

    default PageResult<MesSetDustRecordDO> selectPage(MesSetDustRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetDustRecordDO>()
                .eqIfPresent(MesSetDustRecordDO::getDustType, reqVO.getDustType())
                .eqIfPresent(MesSetDustRecordDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetDustRecordDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetDustRecordDO::getId));
    }

    default MesSetDustRecordDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetDustRecordDO::getRecordNo, recordNo);
    }

}
