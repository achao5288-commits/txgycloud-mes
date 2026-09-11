package cn.iocoder.txgy.module.mes.dal.mysql.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-劳保用品检查 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPpeCheckMapper extends BaseMapperX<MesSetPpeCheckDO> {

    default MesSetPpeCheckDO selectByRecordNo(String record_no) {
        return selectOne(MesSetPpeCheckDO::getRecordNo, record_no);
    }

    default PageResult<MesSetPpeCheckDO> selectPage(MesSetPpeCheckPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetPpeCheckDO> query = new LambdaQueryWrapperX<MesSetPpeCheckDO>()
                .likeIfPresent(MesSetPpeCheckDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetPpeCheckDO::getPpeType, reqVO.getPpeType())
                .eqIfPresent(MesSetPpeCheckDO::getCheckMode, reqVO.getCheckMode())
                .eqIfPresent(MesSetPpeCheckDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetPpeCheckDO::getId);
        return selectPage(reqVO, query);
    }

}
