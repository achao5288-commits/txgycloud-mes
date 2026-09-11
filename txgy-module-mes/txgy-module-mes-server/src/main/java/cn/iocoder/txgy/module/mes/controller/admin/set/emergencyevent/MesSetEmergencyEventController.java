package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDetailRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDisposeReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import cn.iocoder.txgy.module.mes.service.set.emergencyevent.MesSetEmergencyEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-应急事件")
@RestController
@RequestMapping("/mes/safety-env/emergency-event")
@Validated
public class MesSetEmergencyEventController {

    @Resource
    private MesSetEmergencyEventService emergencyeventService;

    @PostMapping("/create")
    @Operation(summary = "创建应急事件")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:create')")
    public CommonResult<Long> createEmergencyEvent(@Valid @RequestBody MesSetEmergencyEventSaveReqVO createReqVO) {
        return success(emergencyeventService.createEmergencyEvent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应急事件")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:update')")
    public CommonResult<Boolean> updateEmergencyEvent(@Valid @RequestBody MesSetEmergencyEventSaveReqVO updateReqVO) {
        emergencyeventService.updateEmergencyEvent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应急事件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:delete')")
    public CommonResult<Boolean> deleteEmergencyEvent(@RequestParam("id") Long id) {
        emergencyeventService.deleteEmergencyEvent(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急事件")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:query')")
    public CommonResult<MesSetEmergencyEventRespVO> getEmergencyEvent(@RequestParam("id") Long id) {
        MesSetEmergencyEventDO obj = emergencyeventService.getEmergencyEvent(id);
        return success(BeanUtils.toBean(obj, MesSetEmergencyEventRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急事件分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:query')")
    public CommonResult<PageResult<MesSetEmergencyEventRespVO>> getEmergencyEventPage(
            @Valid MesSetEmergencyEventPageReqVO pageReqVO) {
        PageResult<MesSetEmergencyEventDO> pageResult = emergencyeventService.getEmergencyEventPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmergencyEventRespVO.class));
    }

    // 以下四个动作走的是 §291 状态机「上报→处置中(废物建档)→待报告→闭环」，共用 close 权限，
    // 菜单里只放两个按钮：「派发」「处置·报告」——状态不对时服务端会拒，前端不必自己判。

    @GetMapping("/detail")
    @Operation(summary = "获得应急事件详情（含应急废物逐桶明细）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:query')")
    public CommonResult<MesSetEmergencyEventDetailRespVO> getEmergencyEventDetail(@RequestParam("id") Long id) {
        return success(emergencyeventService.getEmergencyEventDetail(id));
    }

    @PostMapping("/dispatch")
    @Operation(summary = "应急任务派发（已上报→处置中）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "handler", description = "处置人，缺省取当前登录人")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:close')")
    public CommonResult<Boolean> dispatchEmergencyEvent(
            @RequestParam("id") Long id,
            @RequestParam(value = "handler", required = false) String handler) {
        emergencyeventService.dispatchEmergencyEvent(id, handler);
        return success(true);
    }

    @PostMapping("/dispose")
    @Operation(summary = "应急处置（逐桶过秤贴签入危废台账 → 待报告）")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:close')")
    public CommonResult<Boolean> disposeEmergencyEvent(@Valid @RequestBody MesSetEmergencyEventDisposeReqVO reqVO) {
        emergencyeventService.disposeEmergencyEvent(reqVO);
        return success(true);
    }

    @PostMapping("/close")
    @Operation(summary = "事件报告签发闭环（待报告→已闭环）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "reportContent", description = "事件报告正文（原因/数量/处置/整改），缺省沿用已有值")
    @Parameter(name = "approver", description = "签发负责人，缺省取当前登录人")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-event:close')")
    public CommonResult<Boolean> closeEmergencyEvent(
            @RequestParam("id") Long id,
            @RequestParam(value = "reportContent", required = false) String reportContent,
            @RequestParam(value = "approver", required = false) String approver,
            @RequestParam(value = "signImg", required = false) String signImg) {
        emergencyeventService.closeEmergencyEvent(id, reportContent, approver, signImg);
        return success(true);
    }

}
