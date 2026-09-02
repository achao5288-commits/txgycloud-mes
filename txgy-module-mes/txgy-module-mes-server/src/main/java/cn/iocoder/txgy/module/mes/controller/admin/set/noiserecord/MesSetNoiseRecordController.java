package cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord.MesSetNoiseRecordDO;
import cn.iocoder.txgy.module.mes.service.set.noiserecord.MesSetNoiseRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-噪声检测记录")
@RestController
@RequestMapping("/mes/safety-env/noise-record")
@Validated
public class MesSetNoiseRecordController {

    @Resource
    private MesSetNoiseRecordService noiseRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建噪声检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-noise-record:create')")
    public CommonResult<Long> createNoiseRecord(@Valid @RequestBody MesSetNoiseRecordSaveReqVO createReqVO) {
        return success(noiseRecordService.createNoiseRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新噪声检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-noise-record:update')")
    public CommonResult<Boolean> updateNoiseRecord(@Valid @RequestBody MesSetNoiseRecordSaveReqVO updateReqVO) {
        noiseRecordService.updateNoiseRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除噪声检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-noise-record:delete')")
    public CommonResult<Boolean> deleteNoiseRecord(@RequestParam("id") Long id) {
        noiseRecordService.deleteNoiseRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得噪声检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-noise-record:query')")
    public CommonResult<MesSetNoiseRecordRespVO> getNoiseRecord(@RequestParam("id") Long id) {
        MesSetNoiseRecordDO noiseRecord = noiseRecordService.getNoiseRecord(id);
        return success(BeanUtils.toBean(noiseRecord, MesSetNoiseRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得噪声检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-noise-record:query')")
    public CommonResult<PageResult<MesSetNoiseRecordRespVO>> getNoiseRecordPage(@Valid MesSetNoiseRecordPageReqVO pageReqVO) {
        PageResult<MesSetNoiseRecordDO> pageResult = noiseRecordService.getNoiseRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetNoiseRecordRespVO.class));
    }

}
