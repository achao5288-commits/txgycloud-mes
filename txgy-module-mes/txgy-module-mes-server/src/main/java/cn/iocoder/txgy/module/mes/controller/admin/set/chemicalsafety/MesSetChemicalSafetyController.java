package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetySaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;
import cn.iocoder.txgy.module.mes.service.set.chemicalsafety.MesSetChemicalSafetyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-危化品安全巡查记录")
@RestController
@RequestMapping("/mes/safety-env/chemical-safety")
@Validated
public class MesSetChemicalSafetyController {

    @Resource
    private MesSetChemicalSafetyService chemicalSafetyService;

    @PostMapping("/create")
    @Operation(summary = "创建危化品安全巡查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical-safety:create')")
    public CommonResult<Long> createChemicalSafety(@Valid @RequestBody MesSetChemicalSafetySaveReqVO createReqVO) {
        return success(chemicalSafetyService.createChemicalSafety(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新危化品安全巡查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical-safety:update')")
    public CommonResult<Boolean> updateChemicalSafety(@Valid @RequestBody MesSetChemicalSafetySaveReqVO updateReqVO) {
        chemicalSafetyService.updateChemicalSafety(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除危化品安全巡查记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-chemical-safety:delete')")
    public CommonResult<Boolean> deleteChemicalSafety(@RequestParam("id") Long id) {
        chemicalSafetyService.deleteChemicalSafety(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得危化品安全巡查记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical-safety:query')")
    public CommonResult<MesSetChemicalSafetyRespVO> getChemicalSafety(@RequestParam("id") Long id) {
        MesSetChemicalSafetyDO chemicalSafety = chemicalSafetyService.getChemicalSafety(id);
        return success(BeanUtils.toBean(chemicalSafety, MesSetChemicalSafetyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得危化品安全巡查记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical-safety:query')")
    public CommonResult<PageResult<MesSetChemicalSafetyRespVO>> getChemicalSafetyPage(@Valid MesSetChemicalSafetyPageReqVO pageReqVO) {
        PageResult<MesSetChemicalSafetyDO> pageResult = chemicalSafetyService.getChemicalSafetyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetChemicalSafetyRespVO.class));
    }

}
