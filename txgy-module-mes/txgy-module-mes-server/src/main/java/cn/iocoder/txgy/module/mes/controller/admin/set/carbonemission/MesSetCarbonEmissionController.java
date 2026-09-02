package cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import cn.iocoder.txgy.module.mes.service.set.carbonemission.MesSetCarbonEmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-碳排放核算记录")
@RestController
@RequestMapping("/mes/safety-env/carbon-emission")
@Validated
public class MesSetCarbonEmissionController {

    @Resource
    private MesSetCarbonEmissionService carbonEmissionService;

    @PostMapping("/create")
    @Operation(summary = "创建碳排放核算记录")
    @PreAuthorize("@ss.hasPermission('mes:set-carbon-emission:create')")
    public CommonResult<Long> createCarbonEmission(@Valid @RequestBody MesSetCarbonEmissionSaveReqVO createReqVO) {
        return success(carbonEmissionService.createCarbonEmission(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新碳排放核算记录")
    @PreAuthorize("@ss.hasPermission('mes:set-carbon-emission:update')")
    public CommonResult<Boolean> updateCarbonEmission(@Valid @RequestBody MesSetCarbonEmissionSaveReqVO updateReqVO) {
        carbonEmissionService.updateCarbonEmission(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除碳排放核算记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-carbon-emission:delete')")
    public CommonResult<Boolean> deleteCarbonEmission(@RequestParam("id") Long id) {
        carbonEmissionService.deleteCarbonEmission(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得碳排放核算记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-carbon-emission:query')")
    public CommonResult<MesSetCarbonEmissionRespVO> getCarbonEmission(@RequestParam("id") Long id) {
        MesSetCarbonEmissionDO carbonEmission = carbonEmissionService.getCarbonEmission(id);
        return success(BeanUtils.toBean(carbonEmission, MesSetCarbonEmissionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得碳排放核算记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-carbon-emission:query')")
    public CommonResult<PageResult<MesSetCarbonEmissionRespVO>> getCarbonEmissionPage(@Valid MesSetCarbonEmissionPageReqVO pageReqVO) {
        PageResult<MesSetCarbonEmissionDO> pageResult = carbonEmissionService.getCarbonEmissionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetCarbonEmissionRespVO.class));
    }

}
