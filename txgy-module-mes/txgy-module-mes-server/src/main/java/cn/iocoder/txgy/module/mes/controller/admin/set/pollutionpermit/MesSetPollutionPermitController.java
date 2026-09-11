package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import cn.iocoder.txgy.module.mes.service.set.pollutionpermit.MesSetPollutionPermitService;
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

@Tag(name = "管理后台 - MES 安全环保检测-排污许可证")
@RestController
@RequestMapping("/mes/safety-env/pollution-permit")
@Validated
public class MesSetPollutionPermitController {

    @Resource
    private MesSetPollutionPermitService pollutionPermitService;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @PostMapping("/create")
    @Operation(summary = "创建排污许可证")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:create')")
    public CommonResult<Long> createPollutionPermit(@Valid @RequestBody MesSetPollutionPermitSaveReqVO createReqVO) {
        return success(pollutionPermitService.createPollutionPermit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排污许可证")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:update')")
    public CommonResult<Boolean> updatePollutionPermit(@Valid @RequestBody MesSetPollutionPermitSaveReqVO updateReqVO) {
        pollutionPermitService.updatePollutionPermit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排污许可证")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:delete')")
    public CommonResult<Boolean> deletePollutionPermit(@RequestParam("id") Long id) {
        pollutionPermitService.deletePollutionPermit(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排污许可证")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:query')")
    public CommonResult<MesSetPollutionPermitRespVO> getPollutionPermit(@RequestParam("id") Long id) {
        MesSetPollutionPermitDO permit = pollutionPermitService.getPollutionPermit(id);
        return success(BeanUtils.toBean(permit, MesSetPollutionPermitRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排污许可证分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:query')")
    public CommonResult<PageResult<MesSetPollutionPermitRespVO>> getPollutionPermitPage(
            @Valid MesSetPollutionPermitPageReqVO pageReqVO) {
        PageResult<MesSetPollutionPermitDO> pageResult = pollutionPermitService.getPollutionPermitPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPollutionPermitRespVO.class));
    }

    @GetMapping("/validity")
    @Operation(summary = "获得许可证有效期水位（距届满 ≤60 天 EXPIRING，已过期 OVERDUE）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "onDate", description = "判定基准日(yyyy-MM-dd)，缺省取今天")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:query')")
    public CommonResult<MesSetPermitComplianceService.PermitValidity> getValidity(
            @RequestParam("id") Long id,
            @RequestParam(value = "onDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate onDate) {
        return success(permitComplianceService.checkPermitValidity(id, onDate));
    }

}
