package cn.iocoder.txgy.module.mes.controller.admin.set.standard;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;
import cn.iocoder.txgy.module.mes.service.set.standard.MesSetStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-检测标准")
@RestController
@RequestMapping("/mes/safety-env/standard")
@Validated
public class MesSetStandardController {

    @Resource
    private MesSetStandardService standardService;

    @PostMapping("/create")
    @Operation(summary = "创建检测标准")
    @PreAuthorize("@ss.hasPermission('mes:set-standard:create')")
    public CommonResult<Long> createStandard(@Valid @RequestBody MesSetStandardSaveReqVO createReqVO) {
        return success(standardService.createStandard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检测标准")
    @PreAuthorize("@ss.hasPermission('mes:set-standard:update')")
    public CommonResult<Boolean> updateStandard(@Valid @RequestBody MesSetStandardSaveReqVO updateReqVO) {
        standardService.updateStandard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检测标准")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-standard:delete')")
    public CommonResult<Boolean> deleteStandard(@RequestParam("id") Long id) {
        standardService.deleteStandard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检测标准")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-standard:query')")
    public CommonResult<MesSetStandardRespVO> getStandard(@RequestParam("id") Long id) {
        MesSetStandardDO standard = standardService.getStandard(id);
        return success(BeanUtils.toBean(standard, MesSetStandardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得检测标准分页")
    @PreAuthorize("@ss.hasPermission('mes:set-standard:query')")
    public CommonResult<PageResult<MesSetStandardRespVO>> getStandardPage(@Valid MesSetStandardPageReqVO pageReqVO) {
        PageResult<MesSetStandardDO> pageResult = standardService.getStandardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetStandardRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得启用状态的检测标准列表（下拉）")
    @PreAuthorize("@ss.hasPermission('mes:set-standard:query')")
    public CommonResult<List<MesSetStandardRespVO>> getStandardList() {
        List<MesSetStandardDO> list = standardService.getStandardList();
        return success(BeanUtils.toBean(list, MesSetStandardRespVO.class));
    }

}
