package cn.iocoder.txgy.module.mes.controller.admin.set.monitor;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.monitor.vo.MesSetMonitorBoardRespVO;
import cn.iocoder.txgy.module.mes.service.set.monitor.MesSetMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

/**
 * MES 安全环保检测-污染源在线监控看板（对接说明里的 board / live / dispatch / close 四个口）。
 *
 * <p>接口路径按前端 `index.html` 里写死的 `endpoints` 来定，改路径等于改前端的取数出口——
 * 那是页面里除 `normalizeBoard()` 之外唯一允许动的地方，能不动就不动。
 *
 * @author OPENLAB BS
 */
@Tag(name = "管理后台 - MES 安全环保检测-在线监控看板")
@RestController
@RequestMapping("/mes/safety-env/monitor")
@Validated
public class MesSetMonitorController {

    @Resource
    private MesSetMonitorService monitorService;

    @GetMapping("/board")
    @Operation(summary = "获得在线监控看板（许可余量口径同环保看板，本口为在线监测口径）")
    @Parameter(name = "range", description = "时间范围 24h/7d/30d，当前预留")
    @Parameter(name = "factor", description = "监控因子筛选，空则取读数最多的因子")
    @PreAuthorize("@ss.hasPermission('mes:set-monitor:query')")
    public CommonResult<MesSetMonitorBoardRespVO> getBoard(@RequestParam(value = "range", required = false) String range,
                                                           @RequestParam(value = "factor", required = false) String factor) {
        return success(monitorService.getBoard(range, factor));
    }

    /**
     * 实时大屏每 2 秒轮询一次。返回结构与 board 完全一致，只是页面只取 outletLive / hourTrend /
     * exceedRecords 与 KPI 四项——**同样返回全量**而不是裁一份精简包：大屏切换到看板时靠"缺失段落
     * 沿用上一屏"来省请求，后端裁字段会让这条回落逻辑失效。
     *
     * <p>仍然要权限：这是个无人值守的挂屏，但挂屏背后仍是一个登录会话，
     * 放开匿名等于把全厂排放数据摆到公网上。
     */
    @GetMapping("/read-only")
    @Operation(summary = "实时大屏轮询（结构同 board）")
    @Parameter(name = "range", description = "时间范围 24h/7d/30d，当前预留")
    @Parameter(name = "factor", description = "监控因子筛选，空则取读数最多的因子")
    @PreAuthorize("@ss.hasPermission('mes:set-monitor:query')")
    public CommonResult<MesSetMonitorBoardRespVO> getReadOnly(@RequestParam(value = "range", required = false) String range,
                                                              @RequestParam(value = "factor", required = false) String factor) {
        return success(monitorService.getBoard(range, factor));
    }

    @PostMapping("/exceed/{id}/dispatch")
    @Operation(summary = "超标派单（处置人按当前登录人回填）")
    @Parameter(name = "id", description = "超标事件编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-monitor:dispatch')")
    public CommonResult<Boolean> dispatch(@PathVariable("id") Long id) {
        monitorService.dispatchExceed(id);
        return success(true);
    }

    @PostMapping("/exceed/{id}/close")
    @Operation(summary = "超标闭环")
    @Parameter(name = "id", description = "超标事件编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-monitor:close')")
    public CommonResult<Boolean> close(@PathVariable("id") Long id) {
        monitorService.closeExceed(id);
        return success(true);
    }

}
