package cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.service.set.emissionoutlet.MesSetEmissionOutletService;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-排放口")
@RestController
@RequestMapping("/mes/safety-env/emission-outlet")
@Validated
public class MesSetEmissionOutletController {

    @Resource
    private MesSetEmissionOutletService emissionoutletService;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @PostMapping("/create")
    @Operation(summary = "创建排放口")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:create')")
    public CommonResult<Long> createEmissionOutlet(@Valid @RequestBody MesSetEmissionOutletSaveReqVO createReqVO) {
        return success(emissionoutletService.createEmissionOutlet(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排放口")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:update')")
    public CommonResult<Boolean> updateEmissionOutlet(@Valid @RequestBody MesSetEmissionOutletSaveReqVO updateReqVO) {
        emissionoutletService.updateEmissionOutlet(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排放口")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:delete')")
    public CommonResult<Boolean> deleteEmissionOutlet(@RequestParam("id") Long id) {
        emissionoutletService.deleteEmissionOutlet(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排放口")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:query')")
    public CommonResult<MesSetEmissionOutletRespVO> getEmissionOutlet(@RequestParam("id") Long id) {
        MesSetEmissionOutletDO obj = emissionoutletService.getEmissionOutlet(id);
        return success(BeanUtils.toBean(obj, MesSetEmissionOutletRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排放口分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:query')")
    public CommonResult<PageResult<MesSetEmissionOutletRespVO>> getEmissionOutletPage(
            @Valid MesSetEmissionOutletPageReqVO pageReqVO) {
        PageResult<MesSetEmissionOutletDO> pageResult = emissionoutletService.getEmissionOutletPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmissionOutletRespVO.class));
    }

    @GetMapping("/annual-quota")
    @Operation(summary = "获得排放口某污染物的自然年累计排放量水位（80% 黄警 / 100% 红线）")
    @Parameter(name = "outletId", description = "排放口编号", required = true, example = "1")
    @Parameter(name = "pollutantCode", description = "污染物编码", required = true, example = "SO2")
    @Parameter(name = "onDate", description = "判定基准日(yyyy-MM-dd)，取其自然年；缺省取今天")
    @PreAuthorize("@ss.hasPermission('mes:set-emission-outlet:query')")
    public CommonResult<MesSetPermitComplianceService.AnnualQuota> getAnnualQuota(
            @RequestParam("outletId") Long outletId,
            @RequestParam("pollutantCode") String pollutantCode,
            @RequestParam(value = "onDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate onDate) {
        return success(permitComplianceService.checkAnnualQuota(outletId, pollutantCode, onDate));
    }

}
