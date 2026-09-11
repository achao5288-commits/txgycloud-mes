package cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * MES 安全环保检测-废水监测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetWastewaterMapper extends BaseMapperX<MesSetWastewaterDO> {

    default MesSetWastewaterDO selectByRecordNo(String record_no) {
        return selectOne(MesSetWastewaterDO::getRecordNo, record_no);
    }

    /**
     * 按监测时间取区间内的行，**闭起开止** `[start, end)`。口径同废气侧。
     */
    default List<MesSetWastewaterDO> selectByMonitorRange(LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<MesSetWastewaterDO>()
                .ge(MesSetWastewaterDO::getMonitorTime, start)
                .lt(MesSetWastewaterDO::getMonitorTime, end));
    }

    /**
     * 区间内某排放口某污染物的排放量合计(kg)；无行返回 0。
     */
    default BigDecimal sumEmissionAmount(Long outletId, String pollutantCode,
                                         LocalDateTime start, LocalDateTime end) {
        return selectByMonitorRange(start, end).stream()
                .filter(r -> Objects.equals(r.getOutletId(), outletId))
                .filter(r -> Objects.equals(r.getPollutantCode(), pollutantCode))
                .map(MesSetWastewaterDO::getEmissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default PageResult<MesSetWastewaterDO> selectPage(MesSetWastewaterPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetWastewaterDO> query = new LambdaQueryWrapperX<MesSetWastewaterDO>()
                .likeIfPresent(MesSetWastewaterDO::getRecordNo, reqVO.getRecordNo())
                .eqIfPresent(MesSetWastewaterDO::getOutletId, reqVO.getOutletId())
                .likeIfPresent(MesSetWastewaterDO::getPollutantCode, reqVO.getPollutantCode())
                .likeIfPresent(MesSetWastewaterDO::getResult, reqVO.getResult())
                .eqIfPresent(MesSetWastewaterDO::getCollectionMode, reqVO.getCollectionMode())
                .orderByDesc(MesSetWastewaterDO::getId);
        return selectPage(reqVO, query);
    }

}
