package cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.service.set.emissionoutlet.MesSetEmissionOutletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-排放口")
@RestController
@RequestMapping("/mes/safety-env/emission-outlet")
@Validated
public class MesSetEmissionOutletController {

    @Resource
    private MesSetEmissionOutletService emissionOutletService;

    @PostMapping("/create")
    @Operation(summary = "创建排放口")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:create')")
    public CommonResult<Long> createEmissionOutlet(@Valid @RequestBody MesSetEmissionOutletSaveReqVO createReqVO) {
        return success(emissionOutletService.createEmissionOutlet(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排放口")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:update')")
    public CommonResult<Boolean> updateEmissionOutlet(@Valid @RequestBody MesSetEmissionOutletSaveReqVO updateReqVO) {
        emissionOutletService.updateEmissionOutlet(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排放口")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:delete')")
    public CommonResult<Boolean> deleteEmissionOutlet(@RequestParam("id") Long id) {
        emissionOutletService.deleteEmissionOutlet(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排放口")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:query')")
    public CommonResult<MesSetEmissionOutletRespVO> getEmissionOutlet(@RequestParam("id") Long id) {
        MesSetEmissionOutletDO emissionOutlet = emissionOutletService.getEmissionOutlet(id);
        return success(BeanUtils.toBean(emissionOutlet, MesSetEmissionOutletRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排放口分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:query')")
    public CommonResult<PageResult<MesSetEmissionOutletRespVO>> getEmissionOutletPage(@Valid MesSetEmissionOutletPageReqVO pageReqVO) {
        PageResult<MesSetEmissionOutletDO> pageResult = emissionOutletService.getEmissionOutletPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmissionOutletRespVO.class));
    }

}
