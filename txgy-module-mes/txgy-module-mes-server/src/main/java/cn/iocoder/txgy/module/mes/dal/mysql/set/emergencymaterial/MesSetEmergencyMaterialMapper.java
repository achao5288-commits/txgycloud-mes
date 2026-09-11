package cn.iocoder.txgy.module.mes.dal.mysql.set.emergencymaterial;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial.MesSetEmergencyMaterialDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-应急物资 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmergencyMaterialMapper extends BaseMapperX<MesSetEmergencyMaterialDO> {

    default MesSetEmergencyMaterialDO selectByMaterialNo(String material_no) {
        return selectOne(MesSetEmergencyMaterialDO::getMaterialNo, material_no);
    }

    default PageResult<MesSetEmergencyMaterialDO> selectPage(MesSetEmergencyMaterialPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEmergencyMaterialDO> query = new LambdaQueryWrapperX<MesSetEmergencyMaterialDO>()
                .likeIfPresent(MesSetEmergencyMaterialDO::getMaterialNo, reqVO.getMaterialNo())
                .likeIfPresent(MesSetEmergencyMaterialDO::getMaterialName, reqVO.getMaterialName())
                .likeIfPresent(MesSetEmergencyMaterialDO::getMaterialType, reqVO.getMaterialType())
                .likeIfPresent(MesSetEmergencyMaterialDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmergencyMaterialDO::getId);
        return selectPage(reqVO, query);
    }

}
