package cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import cn.iocoder.txgy.module.mes.service.set.ppecheck.MesSetPpeCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-PPE防护检查记录")
@RestController
@RequestMapping("/mes/safety-env/ppe-check")
@Validated
public class MesSetPpeCheckController {

    @Resource
    private MesSetPpeCheckService ppeCheckService;

    @PostMapping("/create")
    @Operation(summary = "创建 PPE防护检查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-ppe-check:create')")
    public CommonResult<Long> createPpeCheck(@Valid @RequestBody MesSetPpeCheckSaveReqVO createReqVO) {
        return success(ppeCheckService.createPpeCheck(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 PPE防护检查记录")
    @PreAuthorize("@ss.hasPermission('mes:set-ppe-check:update')")
    public CommonResult<Boolean> updatePpeCheck(@Valid @RequestBody MesSetPpeCheckSaveReqVO updateReqVO) {
        ppeCheckService.updatePpeCheck(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 PPE防护检查记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-ppe-check:delete')")
    public CommonResult<Boolean> deletePpeCheck(@RequestParam("id") Long id) {
        ppeCheckService.deletePpeCheck(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 PPE防护检查记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-ppe-check:query')")
    public CommonResult<MesSetPpeCheckRespVO> getPpeCheck(@RequestParam("id") Long id) {
        MesSetPpeCheckDO ppeCheck = ppeCheckService.getPpeCheck(id);
        return success(BeanUtils.toBean(ppeCheck, MesSetPpeCheckRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 PPE防护检查记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-ppe-check:query')")
    public CommonResult<PageResult<MesSetPpeCheckRespVO>> getPpeCheckPage(@Valid MesSetPpeCheckPageReqVO pageReqVO) {
        PageResult<MesSetPpeCheckDO> pageResult = ppeCheckService.getPpeCheckPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPpeCheckRespVO.class));
    }

}
