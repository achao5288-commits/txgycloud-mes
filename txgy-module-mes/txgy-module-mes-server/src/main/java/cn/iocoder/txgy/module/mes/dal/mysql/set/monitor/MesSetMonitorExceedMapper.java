package cn.iocoder.txgy.module.mes.dal.mysql.set.monitor;

import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.monitor.MesSetMonitorExceedDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MES 安全环保检测-污染物超标事件 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetMonitorExceedMapper extends BaseMapperX<MesSetMonitorExceedDO> {

    /**
     * 幂等 upsert 的探测口：看这一批 (排放口, 因子, 小时) 里哪些已经有事件了。
     *
     * <p>不按 (outletId, pollutantCode, occurTime) 逐条 selectOne——一屏下来几百次往返。
     * 一次把窗口内的行全取回来，内存里按三元组建索引。
     */
    default List<MesSetMonitorExceedDO> selectByOccurRange(LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<MesSetMonitorExceedDO>()
                .ge(MesSetMonitorExceedDO::getOccurTime, start)
                .lt(MesSetMonitorExceedDO::getOccurTime, end)
                .orderByDesc(MesSetMonitorExceedDO::getOccurTime)
                .orderByDesc(MesSetMonitorExceedDO::getId));
    }

    /**
     * 看板「超标明细」列表：最近 N 条，新的在前。
     */
    default List<MesSetMonitorExceedDO> selectLatest(int limit) {
        return selectList(new LambdaQueryWrapperX<MesSetMonitorExceedDO>()
                .orderByDesc(MesSetMonitorExceedDO::getOccurTime)
                .orderByDesc(MesSetMonitorExceedDO::getId)
                .last("LIMIT " + limit));
    }

}
