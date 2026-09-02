package cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWastePageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWasteRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWasteSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazardouswaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.service.set.hazardouswaste.MesSetHazardousWasteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-危废台账")
@RestController
@RequestMapping("/mes/safety-env/hazardous-waste")
@Validated
public class MesSetHazardousWasteController {

    @Resource
    private MesSetHazardousWasteService hazardousWasteService;

    @PostMapping("/create")
    @Operation(summary = "创建危废台账")
    @PreAuthorize("@ss.hasPermission('mes:set-hazardous-waste:create')")
    public CommonResult<Long> createHazardousWaste(@Valid @RequestBody MesSetHazardousWasteSaveReqVO createReqVO) {
        return success(hazardousWasteService.createHazardousWaste(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新危废台账")
    @PreAuthorize("@ss.hasPermission('mes:set-hazardous-waste:update')")
    public CommonResult<Boolean> updateHazardousWaste(@Valid @RequestBody MesSetHazardousWasteSaveReqVO updateReqVO) {
        hazardousWasteService.updateHazardousWaste(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除危废台账")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazardous-waste:delete')")
    public CommonResult<Boolean> deleteHazardousWaste(@RequestParam("id") Long id) {
        hazardousWasteService.deleteHazardousWaste(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得危废台账")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-hazardous-waste:query')")
    public CommonResult<MesSetHazardousWasteRespVO> getHazardousWaste(@RequestParam("id") Long id) {
        MesSetHazardousWasteDO hazardousWaste = hazardousWasteService.getHazardousWaste(id);
        return success(BeanUtils.toBean(hazardousWaste, MesSetHazardousWasteRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得危废台账分页")
    @PreAuthorize("@ss.hasPermission('mes:set-hazardous-waste:query')")
    public CommonResult<PageResult<MesSetHazardousWasteRespVO>> getHazardousWastePage(@Valid MesSetHazardousWastePageReqVO pageReqVO) {
        PageResult<MesSetHazardousWasteDO> pageResult = hazardousWasteService.getHazardousWastePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetHazardousWasteRespVO.class));
    }

}
