package cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import cn.iocoder.txgy.module.mes.service.set.pressurevessel.MesSetPressureVesselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-压力容器检查")
@RestController
@RequestMapping("/mes/safety-env/pressure-vessel")
@Validated
public class MesSetPressureVesselController {

    @Resource
    private MesSetPressureVesselService pressurevesselService;

    @PostMapping("/create")
    @Operation(summary = "创建压力容器检查")
    @PreAuthorize("@ss.hasPermission('mes:set-pressure-vessel:create')")
    public CommonResult<Long> createPressureVessel(@Valid @RequestBody MesSetPressureVesselSaveReqVO createReqVO) {
        return success(pressurevesselService.createPressureVessel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新压力容器检查")
    @PreAuthorize("@ss.hasPermission('mes:set-pressure-vessel:update')")
    public CommonResult<Boolean> updatePressureVessel(@Valid @RequestBody MesSetPressureVesselSaveReqVO updateReqVO) {
        pressurevesselService.updatePressureVessel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除压力容器检查")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-pressure-vessel:delete')")
    public CommonResult<Boolean> deletePressureVessel(@RequestParam("id") Long id) {
        pressurevesselService.deletePressureVessel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得压力容器检查")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-pressure-vessel:query')")
    public CommonResult<MesSetPressureVesselRespVO> getPressureVessel(@RequestParam("id") Long id) {
        MesSetPressureVesselDO obj = pressurevesselService.getPressureVessel(id);
        return success(BeanUtils.toBean(obj, MesSetPressureVesselRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得压力容器检查分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pressure-vessel:query')")
    public CommonResult<PageResult<MesSetPressureVesselRespVO>> getPressureVesselPage(
            @Valid MesSetPressureVesselPageReqVO pageReqVO) {
        PageResult<MesSetPressureVesselDO> pageResult = pressurevesselService.getPressureVesselPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPressureVesselRespVO.class));
    }

}
