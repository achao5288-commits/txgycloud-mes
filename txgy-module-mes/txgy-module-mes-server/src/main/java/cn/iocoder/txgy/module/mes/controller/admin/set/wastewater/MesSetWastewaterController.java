package cn.iocoder.txgy.module.mes.controller.admin.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import cn.iocoder.txgy.module.mes.service.set.wastewater.MesSetWastewaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-废水排放检测记录")
@RestController
@RequestMapping("/mes/safety-env/wastewater")
@Validated
public class MesSetWastewaterController {

    @Resource
    private MesSetWastewaterService wastewaterService;

    @PostMapping("/create")
    @Operation(summary = "创建废水排放检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-wastewater:create')")
    public CommonResult<Long> createWastewater(@Valid @RequestBody MesSetWastewaterSaveReqVO createReqVO) {
        return success(wastewaterService.createWastewater(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新废水排放检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-wastewater:update')")
    public CommonResult<Boolean> updateWastewater(@Valid @RequestBody MesSetWastewaterSaveReqVO updateReqVO) {
        wastewaterService.updateWastewater(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除废水排放检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-wastewater:delete')")
    public CommonResult<Boolean> deleteWastewater(@RequestParam("id") Long id) {
        wastewaterService.deleteWastewater(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得废水排放检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-wastewater:query')")
    public CommonResult<MesSetWastewaterRespVO> getWastewater(@RequestParam("id") Long id) {
        MesSetWastewaterDO wastewater = wastewaterService.getWastewater(id);
        return success(BeanUtils.toBean(wastewater, MesSetWastewaterRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得废水排放检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-wastewater:query')")
    public CommonResult<PageResult<MesSetWastewaterRespVO>> getWastewaterPage(@Valid MesSetWastewaterPageReqVO pageReqVO) {
        PageResult<MesSetWastewaterDO> pageResult = wastewaterService.getWastewaterPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetWastewaterRespVO.class));
    }

}
