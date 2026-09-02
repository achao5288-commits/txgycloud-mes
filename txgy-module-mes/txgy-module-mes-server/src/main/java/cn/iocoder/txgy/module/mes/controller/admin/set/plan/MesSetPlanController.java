package cn.iocoder.txgy.module.mes.controller.admin.set.plan;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.plan.vo.MesSetPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.plan.MesSetPlanDO;
import cn.iocoder.txgy.module.mes.service.set.plan.MesSetPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-检测计划")
@RestController
@RequestMapping("/mes/safety-env/plan")
@Validated
public class MesSetPlanController {

    @Resource
    private MesSetPlanService planService;

    @PostMapping("/create")
    @Operation(summary = "创建检测计划")
    @PreAuthorize("@ss.hasPermission('mes:set-plan:create')")
    public CommonResult<Long> createPlan(@Valid @RequestBody MesSetPlanSaveReqVO createReqVO) {
        return success(planService.createPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检测计划")
    @PreAuthorize("@ss.hasPermission('mes:set-plan:update')")
    public CommonResult<Boolean> updatePlan(@Valid @RequestBody MesSetPlanSaveReqVO updateReqVO) {
        planService.updatePlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检测计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-plan:delete')")
    public CommonResult<Boolean> deletePlan(@RequestParam("id") Long id) {
        planService.deletePlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检测计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-plan:query')")
    public CommonResult<MesSetPlanRespVO> getPlan(@RequestParam("id") Long id) {
        MesSetPlanDO plan = planService.getPlan(id);
        return success(BeanUtils.toBean(plan, MesSetPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得检测计划分页")
    @PreAuthorize("@ss.hasPermission('mes:set-plan:query')")
    public CommonResult<PageResult<MesSetPlanRespVO>> getPlanPage(@Valid MesSetPlanPageReqVO pageReqVO) {
        PageResult<MesSetPlanDO> pageResult = planService.getPlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPlanRespVO.class));
    }

}
