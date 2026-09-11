package cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * MES 安全环保检测-废气监测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetExhaustGasMapper extends BaseMapperX<MesSetExhaustGasDO> {

    default MesSetExhaustGasDO selectByRecordNo(String record_no) {
        return selectOne(MesSetExhaustGasDO::getRecordNo, record_no);
    }

    /**
     * 按监测时间取区间内的行，**闭起开止** `[start, end)`。
     * 执行报告的统计期闭区间靠调用方传 `periodEnd.plusDays(1).atStartOfDay()` 实现（见 §15.1）。
     */
    default List<MesSetExhaustGasDO> selectByMonitorRange(LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<MesSetExhaustGasDO>()
                .ge(MesSetExhaustGasDO::getMonitorTime, start)
                .lt(MesSetExhaustGasDO::getMonitorTime, end));
    }

    /**
     * 区间内某排放口某污染物的排放量合计(kg)；无行返回 0。
     * ponytail: 取回内存求和，同 PermCompliance.annualUsed 的口径与量级判断。
     */
    default BigDecimal sumEmissionAmount(Long outletId, String pollutantCode,
                                         LocalDateTime start, LocalDateTime end) {
        return selectByMonitorRange(start, end).stream()
                .filter(r -> Objects.equals(r.getOutletId(), outletId))
                .filter(r -> Objects.equals(r.getPollutantCode(), pollutantCode))
                .map(MesSetExhaustGasDO::getEmissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default PageResult<MesSetExhaustGasDO> selectPage(MesSetExhaustGasPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetExhaustGasDO> query = new LambdaQueryWrapperX<MesSetExhaustGasDO>()
                .likeIfPresent(MesSetExhaustGasDO::getRecordNo, reqVO.getRecordNo())
                .eqIfPresent(MesSetExhaustGasDO::getOutletId, reqVO.getOutletId())
                .likeIfPresent(MesSetExhaustGasDO::getPollutantCode, reqVO.getPollutantCode())
                .likeIfPresent(MesSetExhaustGasDO::getResult, reqVO.getResult())
                .eqIfPresent(MesSetExhaustGasDO::getCollectionMode, reqVO.getCollectionMode())
                .orderByDesc(MesSetExhaustGasDO::getId);
        return selectPage(reqVO, query);
    }

}
