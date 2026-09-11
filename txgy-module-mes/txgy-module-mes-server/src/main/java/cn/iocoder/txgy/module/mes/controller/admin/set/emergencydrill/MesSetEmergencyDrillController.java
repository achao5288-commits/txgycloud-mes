package cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillCoverageRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill.MesSetEmergencyDrillDO;
import cn.iocoder.txgy.module.mes.service.set.emergencydrill.MesSetEmergencyDrillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-应急演练")
@RestController
@RequestMapping("/mes/safety-env/emergency-drill")
@Validated
public class MesSetEmergencyDrillController {

    @Resource
    private MesSetEmergencyDrillService emergencydrillService;

    @PostMapping("/create")
    @Operation(summary = "创建应急演练")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:create')")
    public CommonResult<Long> createEmergencyDrill(@Valid @RequestBody MesSetEmergencyDrillSaveReqVO createReqVO) {
        return success(emergencydrillService.createEmergencyDrill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应急演练")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:update')")
    public CommonResult<Boolean> updateEmergencyDrill(@Valid @RequestBody MesSetEmergencyDrillSaveReqVO updateReqVO) {
        emergencydrillService.updateEmergencyDrill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应急演练")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:delete')")
    public CommonResult<Boolean> deleteEmergencyDrill(@RequestParam("id") Long id) {
        emergencydrillService.deleteEmergencyDrill(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急演练")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:query')")
    public CommonResult<MesSetEmergencyDrillRespVO> getEmergencyDrill(@RequestParam("id") Long id) {
        MesSetEmergencyDrillDO obj = emergencydrillService.getEmergencyDrill(id);
        return success(BeanUtils.toBean(obj, MesSetEmergencyDrillRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急演练分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:query')")
    public CommonResult<PageResult<MesSetEmergencyDrillRespVO>> getEmergencyDrillPage(
            @Valid MesSetEmergencyDrillPageReqVO pageReqVO) {
        PageResult<MesSetEmergencyDrillDO> pageResult = emergencydrillService.getEmergencyDrillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmergencyDrillRespVO.class));
    }

    // 演练的「完成」与「闭环」共用 close 权限——菜单里只放一个「记录·闭环」按钮

    @PostMapping("/finish")
    @Operation(summary = "演练完成（计划→已演练，须记录与评估齐全）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "evaluation", description = "评估结论，缺省沿用已有值")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:close')")
    public CommonResult<Boolean> finishEmergencyDrill(
            @RequestParam("id") Long id,
            @RequestParam(value = "evaluation", required = false) String evaluation) {
        emergencydrillService.finishEmergencyDrill(id, evaluation);
        return success(true);
    }

    @PostMapping("/close")
    @Operation(summary = "演练闭环（已演练→已闭环，有整改要求未完成则拒绝）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:close')")
    public CommonResult<Boolean> closeEmergencyDrill(@RequestParam("id") Long id) {
        emergencydrillService.closeEmergencyDrill(id);
        return success(true);
    }

    @GetMapping("/year-coverage")
    @Operation(summary = "获得年度演练覆盖情况（每年至少 1 次演练的判定）")
    @Parameter(name = "year", description = "年份，缺省今年", example = "2026")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-drill:query')")
    public CommonResult<MesSetEmergencyDrillCoverageRespVO> getYearCoverage(
            @RequestParam(value = "year", required = false) Integer year) {
        return success(emergencydrillService.getYearCoverage(year));
    }

}
