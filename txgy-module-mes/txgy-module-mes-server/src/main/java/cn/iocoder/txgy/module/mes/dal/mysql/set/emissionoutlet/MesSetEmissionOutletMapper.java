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

    default PageResult<MesSetEmissionOutletDO> selectPage(MesSetEmissionOutletPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetEmissionOutletDO>()
                .likeIfPresent(MesSetEmissionOutletDO::getOutletName, reqVO.getOutletName())
                .eqIfPresent(MesSetEmissionOutletDO::getOutletType, reqVO.getOutletType())
                .eqIfPresent(MesSetEmissionOutletDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmissionOutletDO::getId));
    }

    default MesSetEmissionOutletDO selectByOutletCode(String outletCode) {
        return selectOne(MesSetEmissionOutletDO::getOutletCode, outletCode);
    }

}
