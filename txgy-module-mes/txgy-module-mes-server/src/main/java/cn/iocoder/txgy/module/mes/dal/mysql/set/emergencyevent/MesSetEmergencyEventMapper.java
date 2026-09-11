package cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 安全环保检测-应急事件 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmergencyEventMapper extends BaseMapperX<MesSetEmergencyEventDO> {

    default MesSetEmergencyEventDO selectByEventNo(String event_no) {
        return selectOne(MesSetEmergencyEventDO::getEventNo, event_no);
    }

    /**
     * 按发生时间取区间内的行，**闭起开止** `[start, end)`——与废气/废水监测行同口径，
     * 执行报告的统计期闭区间靠调用方传 `periodEnd.plusDays(1).atStartOfDay()` 实现（§15.1）。
     */
    default List<MesSetEmergencyEventDO> selectListByOccurRange(LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<MesSetEmergencyEventDO>()
                .ge(MesSetEmergencyEventDO::getOccurTime, start)
                .lt(MesSetEmergencyEventDO::getOccurTime, end)
                .orderByAsc(MesSetEmergencyEventDO::getOccurTime));
    }

    default PageResult<MesSetEmergencyEventDO> selectPage(MesSetEmergencyEventPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEmergencyEventDO> query = new LambdaQueryWrapperX<MesSetEmergencyEventDO>()
                .likeIfPresent(MesSetEmergencyEventDO::getEventNo, reqVO.getEventNo())
                .likeIfPresent(MesSetEmergencyEventDO::getEventType, reqVO.getEventType())
                .likeIfPresent(MesSetEmergencyEventDO::getLocation, reqVO.getLocation())
                .likeIfPresent(MesSetEmergencyEventDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEmergencyEventDO::getId);
        return selectPage(reqVO, query);
    }

}
