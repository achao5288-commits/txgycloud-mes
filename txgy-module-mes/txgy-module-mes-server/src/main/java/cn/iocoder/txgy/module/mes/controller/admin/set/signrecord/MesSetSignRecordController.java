package cn.iocoder.txgy.module.mes.controller.admin.set.signrecord;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
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

@Tag(name = "管理后台 - MES 安全环保检测-污染追溯(签字记录)")
@RestController
@RequestMapping("/mes/safety-env/pollution-sign")
@Validated
public class MesSetSignRecordController {

    @Resource
    private MesSetSignRecordService signRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得签字记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<PageResult<MesSetSignRecordDO>> getSignRecordPage(@Valid MesSetSignRecordPageReqVO pageReqVO) {
        return success(signRecordService.getSignRecordPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "手工追加签字（追溯页：选签字人 + 手写签名）")
    // 权限刻意复用污染判定的 create：签字入口长在追溯抽屉里，没有独立页面可挂按钮，
    // 与同一抽屉里的「手工登记称重」取同一个权限，不为一个按钮单开菜单行。
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:create')")
    public CommonResult<Long> createSignRecord(@Valid @RequestBody MesSetSignRecordSaveReqVO reqVO) {
        return success(signRecordService.createManualSign(reqVO));
    }

}
