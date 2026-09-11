package cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.MesSetHazwasteManifestPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazwasteManifestDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 安全环保检测-危废转移联单 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetHazwasteManifestMapper extends BaseMapperX<MesSetHazwasteManifestDO> {

    default MesSetHazwasteManifestDO selectByManifestNo(String manifestNo) {
        return selectOne(MesSetHazwasteManifestDO::getManifestNo, manifestNo);
    }

    default PageResult<MesSetHazwasteManifestDO> selectPage(MesSetHazwasteManifestPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetHazwasteManifestDO> query = new LambdaQueryWrapperX<MesSetHazwasteManifestDO>()
                .likeIfPresent(MesSetHazwasteManifestDO::getManifestNo, reqVO.getManifestNo())
                .eqIfPresent(MesSetHazwasteManifestDO::getWasteCode, reqVO.getWasteCode())
                .likeIfPresent(MesSetHazwasteManifestDO::getWasteName, reqVO.getWasteName())
                .likeIfPresent(MesSetHazwasteManifestDO::getVehicleNo, reqVO.getVehicleNo())
                .eqIfPresent(MesSetHazwasteManifestDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetHazwasteManifestDO::getId);
        return selectPage(reqVO, query);
    }

    /**
     * 待办：已申报但未生效、且申报时限不晚于 until（国家平台倒排提醒，含已逾期）。
     * 本地无调度底座，故做成可查列表而非定时推送。
     */
    default List<MesSetHazwasteManifestDO> selectDueSoon(LocalDateTime until) {
        return selectList(new LambdaQueryWrapperX<MesSetHazwasteManifestDO>()
                .eq(MesSetHazwasteManifestDO::getStatus, "DECLARED")
                .isNotNull(MesSetHazwasteManifestDO::getDeclareDeadline)
                .le(MesSetHazwasteManifestDO::getDeclareDeadline, until)
                .orderByAsc(MesSetHazwasteManifestDO::getDeclareDeadline));
    }

}
