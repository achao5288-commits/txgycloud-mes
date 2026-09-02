package cn.iocoder.txgy.module.mes.controller.admin.set.firerecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firerecord.MesSetFireCheckDO;
import cn.iocoder.txgy.module.mes.service.set.firerecord.MesSetFireCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-消防设施检测记录")
@RestController
@RequestMapping("/mes/safety-env/fire-check")
@Validated
public class MesSetFireCheckController {

    @Resource
    private MesSetFireCheckService fireCheckService;

    @PostMapping("/create")
    @Operation(summary = "创建消防设施检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:create')")
    public CommonResult<Long> createFireCheck(@Valid @RequestBody MesSetFireCheckSaveReqVO createReqVO) {
        return success(fireCheckService.createFireCheck(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消防设施检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:update')")
    public CommonResult<Boolean> updateFireCheck(@Valid @RequestBody MesSetFireCheckSaveReqVO updateReqVO) {
        fireCheckService.updateFireCheck(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除消防设施检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:delete')")
    public CommonResult<Boolean> deleteFireCheck(@RequestParam("id") Long id) {
        fireCheckService.deleteFireCheck(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得消防设施检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:query')")
    public CommonResult<MesSetFireCheckRespVO> getFireCheck(@RequestParam("id") Long id) {
        MesSetFireCheckDO fireCheck = fireCheckService.getFireCheck(id);
        return success(BeanUtils.toBean(fireCheck, MesSetFireCheckRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得消防设施检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:query')")
    public CommonResult<PageResult<MesSetFireCheckRespVO>> getFireCheckPage(@Valid MesSetFireCheckPageReqVO pageReqVO) {
        PageResult<MesSetFireCheckDO> pageResult = fireCheckService.getFireCheckPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetFireCheckRespVO.class));
    }

}
