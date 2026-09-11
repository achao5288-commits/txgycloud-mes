package cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;
import cn.iocoder.txgy.module.mes.service.set.weighrecord.MesSetWeighRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-污染追溯(称重记录)")
@RestController
@RequestMapping("/mes/safety-env/pollution-weigh")
@Validated
public class MesSetWeighRecordController {

    @Resource
    private MesSetWeighRecordService weighRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得称重记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<PageResult<MesSetWeighRecordDO>> getWeighRecordPage(@Valid MesSetWeighRecordPageReqVO pageReqVO) {
        return success(weighRecordService.getWeighRecordPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "手工登记称重(产废/出厂)；电子秤直采走 AUTO，待硬件接入")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:create')")
    public CommonResult<Long> createWeighRecord(@Valid @RequestBody MesSetWeighRecordSaveReqVO reqVO) {
        return success(weighRecordService.createWeighRecord(reqVO));
    }

}
