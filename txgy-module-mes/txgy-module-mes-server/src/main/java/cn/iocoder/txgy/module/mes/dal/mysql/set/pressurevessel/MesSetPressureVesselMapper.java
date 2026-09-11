package cn.iocoder.txgy.module.mes.dal.mysql.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-压力容器检查 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPressureVesselMapper extends BaseMapperX<MesSetPressureVesselDO> {

    default MesSetPressureVesselDO selectByRecordNo(String record_no) {
        return selectOne(MesSetPressureVesselDO::getRecordNo, record_no);
    }

    default PageResult<MesSetPressureVesselDO> selectPage(MesSetPressureVesselPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetPressureVesselDO> query = new LambdaQueryWrapperX<MesSetPressureVesselDO>()
                .likeIfPresent(MesSetPressureVesselDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetPressureVesselDO::getVesselRegNo, reqVO.getVesselRegNo())
                .likeIfPresent(MesSetPressureVesselDO::getNdtMethods, reqVO.getNdtMethods())
                .likeIfPresent(MesSetPressureVesselDO::getResult, reqVO.getResult())
                .likeIfPresent(MesSetPressureVesselDO::getInspectOrg, reqVO.getInspectOrg())
                .orderByDesc(MesSetPressureVesselDO::getId);
        return selectPage(reqVO, query);
    }

}
