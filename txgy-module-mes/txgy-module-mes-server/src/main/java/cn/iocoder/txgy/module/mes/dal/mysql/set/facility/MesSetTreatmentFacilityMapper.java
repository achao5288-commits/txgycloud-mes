package cn.iocoder.txgy.module.mes.dal.mysql.set.facility;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo.MesSetTreatmentFacilityPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * MES 安全环保检测-治污设施 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetTreatmentFacilityMapper extends BaseMapperX<MesSetTreatmentFacilityDO> {

    default MesSetTreatmentFacilityDO selectByFacilityNo(String facilityNo) {
        return selectOne(MesSetTreatmentFacilityDO::getFacilityNo, facilityNo);
    }

    default PageResult<MesSetTreatmentFacilityDO> selectPage(MesSetTreatmentFacilityPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetTreatmentFacilityDO>()
                .likeIfPresent(MesSetTreatmentFacilityDO::getFacilityNo, reqVO.getFacilityNo())
                .likeIfPresent(MesSetTreatmentFacilityDO::getFacilityName, reqVO.getFacilityName())
                .eqIfPresent(MesSetTreatmentFacilityDO::getFacilityType, reqVO.getFacilityType())
                .eqIfPresent(MesSetTreatmentFacilityDO::getOutletCode, reqVO.getOutletCode())
                .eqIfPresent(MesSetTreatmentFacilityDO::getRunStatus, reqVO.getRunStatus())
                .eqIfPresent(MesSetTreatmentFacilityDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetTreatmentFacilityDO::getId));
    }

    /**
     * 换炭到期预警：下次更换日期不晚于 deadline 的启用设施（含已逾期）。
     *
     * 只取 next_replace_date 非空的 —— 没设周期就没法算到期，不拿建档日凑一个。
     */
    default List<MesSetTreatmentFacilityDO> selectDueReplace(LocalDate deadline) {
        return selectList(new LambdaQueryWrapperX<MesSetTreatmentFacilityDO>()
                .eq(MesSetTreatmentFacilityDO::getStatus, "ENABLED")
                .isNotNull(MesSetTreatmentFacilityDO::getNextReplaceDate)
                .le(MesSetTreatmentFacilityDO::getNextReplaceDate, deadline)
                .orderByAsc(MesSetTreatmentFacilityDO::getNextReplaceDate));
    }

    /**
     * 未批先停：已停运但停运申报不是 APPROVED 的设施。
     */
    default List<MesSetTreatmentFacilityDO> selectStoppedWithoutApproval() {
        return selectList(new LambdaQueryWrapperX<MesSetTreatmentFacilityDO>()
                .eq(MesSetTreatmentFacilityDO::getStatus, "ENABLED")
                .eq(MesSetTreatmentFacilityDO::getRunStatus, "STOPPED")
                .ne(MesSetTreatmentFacilityDO::getShutdownStatus, "APPROVED"));
    }

    /**
     * 按产线取设施（同开同停比对用）。
     */
    default List<MesSetTreatmentFacilityDO> selectByLineCode(String lineCode) {
        return selectList(new LambdaQueryWrapperX<MesSetTreatmentFacilityDO>()
                .eq(MesSetTreatmentFacilityDO::getLineCode, lineCode)
                .eq(MesSetTreatmentFacilityDO::getStatus, "ENABLED"));
    }

}
