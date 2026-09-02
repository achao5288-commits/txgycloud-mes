package cn.iocoder.txgy.module.mes.controller.admin.set.envreport;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport.MesSetEnvReportDO;
import cn.iocoder.txgy.module.mes.service.set.envreport.MesSetEnvReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-环保检测报告")
@RestController
@RequestMapping("/mes/safety-env/env-report")
@Validated
public class MesSetEnvReportController {

    @Resource
    private MesSetEnvReportService envReportService;

    @PostMapping("/create")
    @Operation(summary = "创建环保检测报告")
    @PreAuthorize("@ss.hasPermission('mes:set-env-report:create')")
    public CommonResult<Long> createEnvReport(@Valid @RequestBody MesSetEnvReportSaveReqVO createReqVO) {
        return success(envReportService.createEnvReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新环保检测报告")
    @PreAuthorize("@ss.hasPermission('mes:set-env-report:update')")
    public CommonResult<Boolean> updateEnvReport(@Valid @RequestBody MesSetEnvReportSaveReqVO updateReqVO) {
        envReportService.updateEnvReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除环保检测报告")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-env-report:delete')")
    public CommonResult<Boolean> deleteEnvReport(@RequestParam("id") Long id) {
        envReportService.deleteEnvReport(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得环保检测报告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-env-report:query')")
    public CommonResult<MesSetEnvReportRespVO> getEnvReport(@RequestParam("id") Long id) {
        MesSetEnvReportDO envReport = envReportService.getEnvReport(id);
        return success(BeanUtils.toBean(envReport, MesSetEnvReportRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得环保检测报告分页")
    @PreAuthorize("@ss.hasPermission('mes:set-env-report:query')")
    public CommonResult<PageResult<MesSetEnvReportRespVO>> getEnvReportPage(@Valid MesSetEnvReportPageReqVO pageReqVO) {
        PageResult<MesSetEnvReportDO> pageResult = envReportService.getEnvReportPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEnvReportRespVO.class));
    }

}
