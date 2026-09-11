package cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-排放口 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmissionOutletMapper extends BaseMapperX<MesSetEmissionOutletDO> {

    default MesSetEmissionOutletDO selectByOutletCode(String outlet_code) {
        return selectOne(MesSetEmissionOutletDO::getOutletCode, outlet_code);
    }

    default PageResult<MesSetEmissionOutletDO> selectPage(MesSetEmissionOutletPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEmissionOutletDO> query = new LambdaQueryWrapperX<MesSetEmissionOutletDO>()
                .likeIfPresent(MesSetEmissionOutletDO::getOutletCode, reqVO.getOutletCode())
                .likeIfPresent(MesSetEmissionOutletDO::getOutletName, reqVO.getOutletName())
                .eqIfPresent(MesSetEmissionOutletDO::getOutletType, reqVO.getOutletType())
                .eqIfPresent(MesSetEmissionOutletDO::getMonitorMethod, reqVO.getMonitorMethod())
                .likeIfPresent(MesSetEmissionOutletDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmissionOutletDO::getId);
        return selectPage(reqVO, query);
    }

}
