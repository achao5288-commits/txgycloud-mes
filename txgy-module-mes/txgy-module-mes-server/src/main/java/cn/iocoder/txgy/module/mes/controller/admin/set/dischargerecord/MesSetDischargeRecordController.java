package cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord.vo.MesSetDischargeRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord.MesSetDischargeRecordDO;
import cn.iocoder.txgy.module.mes.service.set.dischargerecord.MesSetDischargeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-排放合规流水")
@RestController
@RequestMapping("/mes/safety-env/pollution-discharge")
@Validated
public class MesSetDischargeRecordController {

    @Resource
    private MesSetDischargeRecordService dischargeRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得排放合规流水分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-discharge:query')")
    public CommonResult<PageResult<MesSetDischargeRecordDO>> getDischargeRecordPage(@Valid MesSetDischargeRecordPageReqVO pageReqVO) {
        return success(dischargeRecordService.getDischargeRecordPage(pageReqVO));
    }

}
