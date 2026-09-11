package cn.iocoder.txgy.module.mes.dal.mysql.set.emergencydrill;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill.MesSetEmergencyDrillDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * MES 安全环保检测-应急演练 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmergencyDrillMapper extends BaseMapperX<MesSetEmergencyDrillDO> {

    default MesSetEmergencyDrillDO selectByDrillNo(String drill_no) {
        return selectOne(MesSetEmergencyDrillDO::getDrillNo, drill_no);
    }

    /**
     * 某日期区间（闭区间，含首尾）内的演练——年度覆盖统计用。
     */
    default List<MesSetEmergencyDrillDO> selectListByDateRange(LocalDate start, LocalDate end) {
        return selectList(new LambdaQueryWrapperX<MesSetEmergencyDrillDO>()
                .ge(MesSetEmergencyDrillDO::getDrillDate, start)
                .le(MesSetEmergencyDrillDO::getDrillDate, end)
                .orderByAsc(MesSetEmergencyDrillDO::getDrillDate));
    }

    default PageResult<MesSetEmergencyDrillDO> selectPage(MesSetEmergencyDrillPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEmergencyDrillDO> query = new LambdaQueryWrapperX<MesSetEmergencyDrillDO>()
                .likeIfPresent(MesSetEmergencyDrillDO::getDrillNo, reqVO.getDrillNo())
                .likeIfPresent(MesSetEmergencyDrillDO::getDrillName, reqVO.getDrillName())
                .likeIfPresent(MesSetEmergencyDrillDO::getDrillType, reqVO.getDrillType())
                .likeIfPresent(MesSetEmergencyDrillDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmergencyDrillDO::getId);
        return selectPage(reqVO, query);
    }

}
