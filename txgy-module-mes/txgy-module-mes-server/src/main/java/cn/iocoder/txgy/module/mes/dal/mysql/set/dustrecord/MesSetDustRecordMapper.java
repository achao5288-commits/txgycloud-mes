package cn.iocoder.txgy.module.mes.dal.mysql.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-粉尘检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetDustRecordMapper extends BaseMapperX<MesSetDustRecordDO> {

    default MesSetDustRecordDO selectByRecordNo(String record_no) {
        return selectOne(MesSetDustRecordDO::getRecordNo, record_no);
    }

    default PageResult<MesSetDustRecordDO> selectPage(MesSetDustRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetDustRecordDO> query = new LambdaQueryWrapperX<MesSetDustRecordDO>()
                .likeIfPresent(MesSetDustRecordDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetDustRecordDO::getLocation, reqVO.getLocation())
                .likeIfPresent(MesSetDustRecordDO::getDustType, reqVO.getDustType())
                .likeIfPresent(MesSetDustRecordDO::getResult, reqVO.getResult())
                .likeIfPresent(MesSetDustRecordDO::getCollectionMode, reqVO.getCollectionMode())
                .orderByDesc(MesSetDustRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
