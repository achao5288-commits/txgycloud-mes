package cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial.MesSetEmergencyMaterialDO;
import cn.iocoder.txgy.module.mes.service.set.emergencymaterial.MesSetEmergencyMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-应急物资")
@RestController
@RequestMapping("/mes/safety-env/emergency-material")
@Validated
public class MesSetEmergencyMaterialController {

    @Resource
    private MesSetEmergencyMaterialService emergencymaterialService;

    @PostMapping("/create")
    @Operation(summary = "创建应急物资")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:create')")
    public CommonResult<Long> createEmergencyMaterial(@Valid @RequestBody MesSetEmergencyMaterialSaveReqVO createReqVO) {
        return success(emergencymaterialService.createEmergencyMaterial(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应急物资")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:update')")
    public CommonResult<Boolean> updateEmergencyMaterial(@Valid @RequestBody MesSetEmergencyMaterialSaveReqVO updateReqVO) {
        emergencymaterialService.updateEmergencyMaterial(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应急物资")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:delete')")
    public CommonResult<Boolean> deleteEmergencyMaterial(@RequestParam("id") Long id) {
        emergencymaterialService.deleteEmergencyMaterial(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急物资")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:query')")
    public CommonResult<MesSetEmergencyMaterialRespVO> getEmergencyMaterial(@RequestParam("id") Long id) {
        MesSetEmergencyMaterialDO obj = emergencymaterialService.getEmergencyMaterial(id);
        return success(BeanUtils.toBean(obj, MesSetEmergencyMaterialRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急物资分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:query')")
    public CommonResult<PageResult<MesSetEmergencyMaterialRespVO>> getEmergencyMaterialPage(
            @Valid MesSetEmergencyMaterialPageReqVO pageReqVO) {
        PageResult<MesSetEmergencyMaterialDO> pageResult = emergencymaterialService.getEmergencyMaterialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmergencyMaterialRespVO.class));
    }

    @GetMapping("/alerts")
    @Operation(summary = "获得应急物资告警（缺货/过期/临期，按严重度排序）")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-material:query')")
    public CommonResult<List<MesSetEmergencyMaterialRespVO>> getAlerts() {
        return success(BeanUtils.toBean(emergencymaterialService.getAlerts(), MesSetEmergencyMaterialRespVO.class));
    }

}
