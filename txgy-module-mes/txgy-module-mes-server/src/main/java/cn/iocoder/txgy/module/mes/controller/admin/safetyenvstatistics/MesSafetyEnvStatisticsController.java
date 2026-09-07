package cn.iocoder.txgy.module.mes.controller.admin.safetyenvstatistics;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.module.mes.controller.admin.safetyenvstatistics.vo.MesSafetyEnvStatisticsRespVO;
import cn.iocoder.txgy.module.mes.service.safetyenvstatistics.MesSafetyEnvStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测统计")
@RestController
@RequestMapping("/mes/safety-env/statistics")
@Validated
public class MesSafetyEnvStatisticsController {

    @Resource
    private MesSafetyEnvStatisticsService safetyEnvStatisticsService;

    @GetMapping("/summary")
    @Operation(summary = "获得安全环保检测汇总统计")
    @PreAuthorize("@ss.hasPermission('mes:safety-env-statistics:query')")
    public CommonResult<MesSafetyEnvStatisticsRespVO> getMesSafetyEnvStatistics() {
        return success(safetyEnvStatisticsService.getMesSafetyEnvStatistics());
    }

}
