package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;
import cn.iocoder.txgy.module.mes.service.set.gasrecord.MesSetGasRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-作业环境气体检测记录")
@RestController
@RequestMapping("/mes/safety-env/gas-record")
@Validated
public class MesSetGasRecordController {

    @Resource
    private MesSetGasRecordService gasRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建气体检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-gas-record:create')")
    public CommonResult<Long> createGasRecord(@Valid @RequestBody MesSetGasRecordSaveReqVO createReqVO) {
        return success(gasRecordService.createGasRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新气体检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-gas-record:update')")
    public CommonResult<Boolean> updateGasRecord(@Valid @RequestBody MesSetGasRecordSaveReqVO updateReqVO) {
        gasRecordService.updateGasRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除气体检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-gas-record:delete')")
    public CommonResult<Boolean> deleteGasRecord(@RequestParam("id") Long id) {
        gasRecordService.deleteGasRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得气体检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-gas-record:query')")
    public CommonResult<MesSetGasRecordRespVO> getGasRecord(@RequestParam("id") Long id) {
        MesSetGasRecordDO gasRecord = gasRecordService.getGasRecord(id);
        return success(BeanUtils.toBean(gasRecord, MesSetGasRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得气体检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-gas-record:query')")
    public CommonResult<PageResult<MesSetGasRecordRespVO>> getGasRecordPage(@Valid MesSetGasRecordPageReqVO pageReqVO) {
        PageResult<MesSetGasRecordDO> pageResult = gasRecordService.getGasRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetGasRecordRespVO.class));
    }

}
