package cn.iocoder.txgy.module.mes.dal.mysql.set.firerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firerecord.MesSetFireCheckDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-消防设施检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetFireCheckMapper extends BaseMapperX<MesSetFireCheckDO> {

    default PageResult<MesSetFireCheckDO> selectPage(MesSetFireCheckPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetFireCheckDO>()
                .likeIfPresent(MesSetFireCheckDO::getLocation, reqVO.getLocation())
                .likeIfPresent(MesSetFireCheckDO::getFacilityName, reqVO.getFacilityName())
                .eqIfPresent(MesSetFireCheckDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetFireCheckDO::getId));
    }

    default MesSetFireCheckDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetFireCheckDO::getRecordNo, recordNo);
    }

}
