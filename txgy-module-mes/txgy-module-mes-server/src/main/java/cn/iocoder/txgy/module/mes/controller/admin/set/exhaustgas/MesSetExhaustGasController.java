package cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import cn.iocoder.txgy.module.mes.service.set.exhaustgas.MesSetExhaustGasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-废气排放检测记录")
@RestController
@RequestMapping("/mes/safety-env/exhaust-gas")
@Validated
public class MesSetExhaustGasController {

    @Resource
    private MesSetExhaustGasService exhaustGasService;

    @PostMapping("/create")
    @Operation(summary = "创建废气排放检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-exhaust-gas:create')")
    public CommonResult<Long> createExhaustGas(@Valid @RequestBody MesSetExhaustGasSaveReqVO createReqVO) {
        return success(exhaustGasService.createExhaustGas(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新废气排放检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-exhaust-gas:update')")
    public CommonResult<Boolean> updateExhaustGas(@Valid @RequestBody MesSetExhaustGasSaveReqVO updateReqVO) {
        exhaustGasService.updateExhaustGas(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除废气排放检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-exhaust-gas:delete')")
    public CommonResult<Boolean> deleteExhaustGas(@RequestParam("id") Long id) {
        exhaustGasService.deleteExhaustGas(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得废气排放检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-exhaust-gas:query')")
    public CommonResult<MesSetExhaustGasRespVO> getExhaustGas(@RequestParam("id") Long id) {
        MesSetExhaustGasDO exhaustGas = exhaustGasService.getExhaustGas(id);
        return success(BeanUtils.toBean(exhaustGas, MesSetExhaustGasRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得废气排放检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-exhaust-gas:query')")
    public CommonResult<PageResult<MesSetExhaustGasRespVO>> getExhaustGasPage(@Valid MesSetExhaustGasPageReqVO pageReqVO) {
        PageResult<MesSetExhaustGasDO> pageResult = exhaustGasService.getExhaustGasPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetExhaustGasRespVO.class));
    }

}
