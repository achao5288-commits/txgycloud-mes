package cn.iocoder.txgy.module.mes.dal.mysql.set.hazardouswaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWastePageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazardouswaste.MesSetHazardousWasteDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-危废台账 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetHazardousWasteMapper extends BaseMapperX<MesSetHazardousWasteDO> {

    default PageResult<MesSetHazardousWasteDO> selectPage(MesSetHazardousWastePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetHazardousWasteDO>()
                .eqIfPresent(MesSetHazardousWasteDO::getWasteCode, reqVO.getWasteCode())
                .likeIfPresent(MesSetHazardousWasteDO::getWasteName, reqVO.getWasteName())
                .eqIfPresent(MesSetHazardousWasteDO::getStage, reqVO.getStage())
                .eqIfPresent(MesSetHazardousWasteDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetHazardousWasteDO::getId));
    }

    default MesSetHazardousWasteDO selectByManifestNo(String manifestNo) {
        return selectOne(MesSetHazardousWasteDO::getManifestNo, manifestNo);
    }

}
