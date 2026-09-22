package cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvBatchProfileRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvDashboardRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvInspectSummaryRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvRespVO;
import cn.iocoder.txgy.module.mes.service.set.inventoryenv.MesInventoryEnvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

/**
 * MES 在库环保视图
 *
 * 只读视图，没有写接口：行上的动作全部复用既有接口（环保判定走 pollution-check、
 * 冻解走 wm/material-stock、标记终审走 pollution-ledger）。
 * 权限沿用污染判定的 query，不新建权限点（新菜单/权限要动 system_menu，另说）。
 *
 * 待检清单没有独立接口：「检测状态=未检/超期/积压」分页查询就是待检清单，
 * 另起一个接口只会多一套会走样的口径。
 */
@Tag(name = "管理后台 - MES 在库环保视图")
@RestController
@RequestMapping("/mes/safety-env/inventory-env")
@Validated
public class MesInventoryEnvController {

    @Resource
    private MesInventoryEnvService inventoryEnvService;

    @GetMapping("/page")
    @Operation(summary = "获得在库环保视图分页（在库库存 × 批次污染投影 × 最近判定 × 四项判据）")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<PageResult<MesInventoryEnvRespVO>> getInventoryEnvPage(
            @Valid MesInventoryEnvPageReqVO pageReqVO) {
        return success(inventoryEnvService.getInventoryEnvPage(pageReqVO));
    }

    @GetMapping("/inspect")
    @Operation(summary = "全库环保体检（只读重算，返回各项判据命中数，不落体检批次）")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<MesInventoryEnvInspectSummaryRespVO> inspectAll() {
        return success(inventoryEnvService.inspectAll());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "合规看板（覆盖率 / 异常率 / 待复核积压 / 处置时效 / 按仓库与物料分类下钻）")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<MesInventoryEnvDashboardRespVO> getDashboard() {
        return success(inventoryEnvService.getDashboard());
    }

    @GetMapping("/batch-profile")
    @Operation(summary = "批次档案（前向/后向链 + 每个节点的环保档案注解）")
    @Parameter(name = "batchCode", description = "批次号", required = true, example = "B20260901001")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<MesInventoryEnvBatchProfileRespVO> getBatchProfile(@RequestParam("batchCode") String batchCode) {
        return success(inventoryEnvService.getBatchProfile(batchCode));
    }

}
