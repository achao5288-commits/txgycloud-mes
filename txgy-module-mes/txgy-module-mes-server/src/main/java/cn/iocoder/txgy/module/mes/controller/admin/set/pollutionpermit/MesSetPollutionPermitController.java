package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.service.set.pollutionpermit.MesSetPollutionPermitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-排污许可管理")
@RestController
@RequestMapping("/mes/safety-env/pollution-permit")
@Validated
public class MesSetPollutionPermitController {

    @Resource
    private MesSetPollutionPermitService pollutionPermitService;

    @PostMapping("/create")
    @Operation(summary = "创建排污许可")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:create')")
    public CommonResult<Long> createPollutionPermit(@Valid @RequestBody MesSetPollutionPermitSaveReqVO createReqVO) {
        return success(pollutionPermitService.createPollutionPermit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排污许可")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:update')")
    public CommonResult<Boolean> updatePollutionPermit(@Valid @RequestBody MesSetPollutionPermitSaveReqVO updateReqVO) {
        pollutionPermitService.updatePollutionPermit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排污许可")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:delete')")
    public CommonResult<Boolean> deletePollutionPermit(@RequestParam("id") Long id) {
        pollutionPermitService.deletePollutionPermit(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排污许可")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:query')")
    public CommonResult<MesSetPollutionPermitRespVO> getPollutionPermit(@RequestParam("id") Long id) {
        MesSetPollutionPermitDO pollutionPermit = pollutionPermitService.getPollutionPermit(id);
        return success(BeanUtils.toBean(pollutionPermit, MesSetPollutionPermitRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排污许可分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-permit:query')")
    public CommonResult<PageResult<MesSetPollutionPermitRespVO>> getPollutionPermitPage(@Valid MesSetPollutionPermitPageReqVO pageReqVO) {
        PageResult<MesSetPollutionPermitDO> pageResult = pollutionPermitService.getPollutionPermitPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPollutionPermitRespVO.class));
    }

}
