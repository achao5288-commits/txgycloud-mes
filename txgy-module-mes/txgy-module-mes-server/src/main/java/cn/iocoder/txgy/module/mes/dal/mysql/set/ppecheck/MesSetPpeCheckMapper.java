package cn.iocoder.txgy.module.mes.dal.mysql.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-PPE防护检查记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPpeCheckMapper extends BaseMapperX<MesSetPpeCheckDO> {

    default PageResult<MesSetPpeCheckDO> selectPage(MesSetPpeCheckPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetPpeCheckDO>()
                .eqIfPresent(MesSetPpeCheckDO::getPpeType, reqVO.getPpeType())
                .eqIfPresent(MesSetPpeCheckDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetPpeCheckDO::getCheckTime, reqVO.getCheckTime())
                .orderByDesc(MesSetPpeCheckDO::getId));
    }

    default MesSetPpeCheckDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetPpeCheckDO::getRecordNo, recordNo);
    }

}
