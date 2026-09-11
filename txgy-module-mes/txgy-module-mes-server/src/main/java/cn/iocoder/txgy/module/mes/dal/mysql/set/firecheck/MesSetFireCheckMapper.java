package cn.iocoder.txgy.module.mes.dal.mysql.set.firecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firecheck.MesSetFireCheckDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-消防检查记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetFireCheckMapper extends BaseMapperX<MesSetFireCheckDO> {

    default MesSetFireCheckDO selectByRecordNo(String record_no) {
        return selectOne(MesSetFireCheckDO::getRecordNo, record_no);
    }

    default PageResult<MesSetFireCheckDO> selectPage(MesSetFireCheckPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetFireCheckDO> query = new LambdaQueryWrapperX<MesSetFireCheckDO>()
                .likeIfPresent(MesSetFireCheckDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetFireCheckDO::getLocation, reqVO.getLocation())
                .likeIfPresent(MesSetFireCheckDO::getFacilityName, reqVO.getFacilityName())
                .eqIfPresent(MesSetFireCheckDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetFireCheckDO::getId);
        return selectPage(reqVO, query);
    }

}
