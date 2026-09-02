package cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;
import cn.iocoder.txgy.module.mes.service.set.electricalrecord.MesSetElectricalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-电气安全检测记录")
@RestController
@RequestMapping("/mes/safety-env/electrical-record")
@Validated
public class MesSetElectricalRecordController {

    @Resource
    private MesSetElectricalRecordService electricalRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建电气安全检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-electrical-record:create')")
    public CommonResult<Long> createElectricalRecord(@Valid @RequestBody MesSetElectricalRecordSaveReqVO createReqVO) {
        return success(electricalRecordService.createElectricalRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新电气安全检测记录")
    @PreAuthorize("@ss.hasPermission('mes:set-electrical-record:update')")
    public CommonResult<Boolean> updateElectricalRecord(@Valid @RequestBody MesSetElectricalRecordSaveReqVO updateReqVO) {
        electricalRecordService.updateElectricalRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除电气安全检测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-electrical-record:delete')")
    public CommonResult<Boolean> deleteElectricalRecord(@RequestParam("id") Long id) {
        electricalRecordService.deleteElectricalRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得电气安全检测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-electrical-record:query')")
    public CommonResult<MesSetElectricalRecordRespVO> getElectricalRecord(@RequestParam("id") Long id) {
        MesSetElectricalRecordDO electricalRecord = electricalRecordService.getElectricalRecord(id);
        return success(BeanUtils.toBean(electricalRecord, MesSetElectricalRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得电气安全检测记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-electrical-record:query')")
    public CommonResult<PageResult<MesSetElectricalRecordRespVO>> getElectricalRecordPage(@Valid MesSetElectricalRecordPageReqVO pageReqVO) {
        PageResult<MesSetElectricalRecordDO> pageResult = electricalRecordService.getElectricalRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetElectricalRecordRespVO.class));
    }

}
