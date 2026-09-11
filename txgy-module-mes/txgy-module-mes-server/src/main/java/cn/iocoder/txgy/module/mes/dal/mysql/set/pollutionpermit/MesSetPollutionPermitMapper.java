package cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-排污许可证 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPollutionPermitMapper extends BaseMapperX<MesSetPollutionPermitDO> {

    default MesSetPollutionPermitDO selectByPermitNo(String permitNo) {
        return selectOne(MesSetPollutionPermitDO::getPermitNo, permitNo);
    }

    default PageResult<MesSetPollutionPermitDO> selectPage(MesSetPollutionPermitPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetPollutionPermitDO> query = new LambdaQueryWrapperX<MesSetPollutionPermitDO>()
                .likeIfPresent(MesSetPollutionPermitDO::getPermitNo, reqVO.getPermitNo())
                .likeIfPresent(MesSetPollutionPermitDO::getEnterpriseName, reqVO.getEnterpriseName())
                .eqIfPresent(MesSetPollutionPermitDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetPollutionPermitDO::getId);
        return selectPage(reqVO, query);
    }

}
