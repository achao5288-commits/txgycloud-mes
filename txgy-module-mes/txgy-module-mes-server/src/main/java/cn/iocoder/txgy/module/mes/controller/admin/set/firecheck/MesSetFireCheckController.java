package cn.iocoder.txgy.module.mes.controller.admin.set.firecheck;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firecheck.MesSetFireCheckDO;
import cn.iocoder.txgy.module.mes.service.set.firecheck.MesSetFireCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-消防检查记录")
@RestController
@RequestMapping("/mes/safety-env/fire-check")
@Validated
public class MesSetFireCheckController {

    @Resource
    private MesSetFireCheckService firecheckService;

    @PostMapping("/create")
    @Operation(summary = "创建消防检查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:create')")
    public CommonResult<Long> createFireCheck(@Valid @RequestBody MesSetFireCheckSaveReqVO createReqVO) {
        return success(firecheckService.createFireCheck(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消防检查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:update')")
    public CommonResult<Boolean> updateFireCheck(@Valid @RequestBody MesSetFireCheckSaveReqVO updateReqVO) {
        firecheckService.updateFireCheck(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除消防检查记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:delete')")
    public CommonResult<Boolean> deleteFireCheck(@RequestParam("id") Long id) {
        firecheckService.deleteFireCheck(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得消防检查记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:query')")
    public CommonResult<MesSetFireCheckRespVO> getFireCheck(@RequestParam("id") Long id) {
        MesSetFireCheckDO obj = firecheckService.getFireCheck(id);
        return success(BeanUtils.toBean(obj, MesSetFireCheckRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得消防检查记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-fire-check:query')")
    public CommonResult<PageResult<MesSetFireCheckRespVO>> getFireCheckPage(
            @Valid MesSetFireCheckPageReqVO pageReqVO) {
        PageResult<MesSetFireCheckDO> pageResult = firecheckService.getFireCheckPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetFireCheckRespVO.class));
    }

}
