package cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;
import cn.iocoder.txgy.module.mes.service.set.dustrecord.MesSetDustRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-粉尘浓度检测记录")
@RestController
@RequestMapping("/mes/safety-env/dust-record")
@Validated
public class MesSetDustRecordController {

    @Resource
    private MesSetDustRecordService dustRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建粉尘浓度检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-dust-record:create')")
    public CommonResult<Long> createDustRecord(@Valid @RequestBody MesSetDustRecordSaveReqVO createReqVO) {
        return success(dustRecordService.createDustRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新粉尘浓度检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-dust-record:update')")
    public CommonResult<Boolean> updateDustRecord(@Valid @RequestBody MesSetDustRecordSaveReqVO updateReqVO) {
        dustRecordService.updateDustRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除粉尘浓度检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-dust-record:delete')")
    public CommonResult<Boolean> deleteDustRecord(@RequestParam("id") Long id) {
        dustRecordService.deleteDustRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得粉尘浓度检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-dust-record:query')")
    public CommonResult<MesSetDustRecordRespVO> getDustRecord(@RequestParam("id") Long id) {
        MesSetDustRecordDO dustRecord = dustRecordService.getDustRecord(id);
        return success(BeanUtils.toBean(dustRecord, MesSetDustRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得粉尘浓度检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-dust-record:query')")
    public CommonResult<PageResult<MesSetDustRecordRespVO>> getDustRecordPage(@Valid MesSetDustRecordPageReqVO pageReqVO) {
        PageResult<MesSetDustRecordDO> pageResult = dustRecordService.getDustRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetDustRecordRespVO.class));
    }

}
