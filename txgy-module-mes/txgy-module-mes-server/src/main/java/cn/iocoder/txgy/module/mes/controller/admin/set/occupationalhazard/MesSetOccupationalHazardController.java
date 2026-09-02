package cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;
import cn.iocoder.txgy.module.mes.service.set.occupationalhazard.MesSetOccupationalHazardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-职业病危害因素检测记录")
@RestController
@RequestMapping("/mes/safety-env/occupational-hazard")
@Validated
public class MesSetOccupationalHazardController {

    @Resource
    private MesSetOccupationalHazardService occupationalHazardService;

    @PostMapping("/create")
    @Operation(summary = "创建职业病危害因素检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-occupational-hazard:create')")
    public CommonResult<Long> createOccupationalHazard(@Valid @RequestBody MesSetOccupationalHazardSaveReqVO createReqVO) {
        return success(occupationalHazardService.createOccupationalHazard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新职业病危害因素检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-occupational-hazard:update')")
    public CommonResult<Boolean> updateOccupationalHazard(@Valid @RequestBody MesSetOccupationalHazardSaveReqVO updateReqVO) {
        occupationalHazardService.updateOccupationalHazard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除职业病危害因素检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-occupational-hazard:delete')")
    public CommonResult<Boolean> deleteOccupationalHazard(@RequestParam("id") Long id) {
        occupationalHazardService.deleteOccupationalHazard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得职业病危害因素检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-occupational-hazard:query')")
    public CommonResult<MesSetOccupationalHazardRespVO> getOccupationalHazard(@RequestParam("id") Long id) {
        MesSetOccupationalHazardDO occupationalHazard = occupationalHazardService.getOccupationalHazard(id);
        return success(BeanUtils.toBean(occupationalHazard, MesSetOccupationalHazardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得职业病危害因素检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-occupational-hazard:query')")
    public CommonResult<PageResult<MesSetOccupationalHazardRespVO>> getOccupationalHazardPage(@Valid MesSetOccupationalHazardPageReqVO pageReqVO) {
        PageResult<MesSetOccupationalHazardDO> pageResult = occupationalHazardService.getOccupationalHazardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetOccupationalHazardRespVO.class));
    }

}
