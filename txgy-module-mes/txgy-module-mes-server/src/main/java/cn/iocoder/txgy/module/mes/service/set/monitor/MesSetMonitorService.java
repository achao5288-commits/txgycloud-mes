package cn.iocoder.txgy.module.mes.service.set.monitor;

import cn.iocoder.txgy.module.mes.controller.admin.set.monitor.vo.MesSetMonitorBoardRespVO;

/**
 * MES 安全环保检测-污染源在线监控看板 Service 接口
 *
 * <p>看板与实时大屏**同源**（{@link #getBoard}），处置动作为派单与闭环两态流转。
 *
 * @author OPENLAB BS
 */
public interface MesSetMonitorService {

    /**
     * 获得在线监控看板（实时大屏轮询同一份）。
     *
     * <p>读数聚合是**读时计算**的：本项目里 XXL-Job 没跑起来，没有定时任务去把读数汇总成事件。
     * 于是每次读看板时顺带把近 {@code MATERIALIZE_DAYS} 天的读数按「小时均值」固化进
     * {@code mes_set_monitor_exceed}（幂等 upsert，已派单/已闭环的不会被覆盖回待处置）。
     * 量级见实现类注释，够用；接入定时任务后把这个动作挪走即可。
     *
     * @param range  时间范围 24h/7d/30d，当前仅作入参预留（前端筛选尚未细分口径）
     * @param factor 监控因子筛选，空则取窗口内读数最多的因子
     * @return 看板
     */
    MesSetMonitorBoardRespVO getBoard(String range, String factor);

    /**
     * 派单：待处置 → 处置中。处置人按**当前登录人**回填，不收前端传的人名。
     *
     * @param id 超标事件编号
     */
    void dispatchExceed(Long id);

    /**
     * 闭环：待处置/处置中 → 已闭环。已闭环不可再闭环。
     *
     * @param id 超标事件编号
     */
    void closeExceed(Long id);

}
