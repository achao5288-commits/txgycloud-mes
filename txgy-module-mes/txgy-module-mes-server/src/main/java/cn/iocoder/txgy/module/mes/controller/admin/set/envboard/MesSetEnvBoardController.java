package cn.iocoder.txgy.module.mes.controller.admin.set.envboard;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.envboard.vo.MesSetEnvBoardRespVO;
import cn.iocoder.txgy.module.mes.service.set.envboard.MesSetEnvBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-环保看板")
@RestController
@RequestMapping("/mes/safety-env/env-board")
@Validated
public class MesSetEnvBoardController {

    @Resource
    private MesSetEnvBoardService envBoardService;

    @GetMapping("/summary")
    @Operation(summary = "获得环保看板汇总（许可余量/设施运行/暂存倒计时/联单状态/应急物资）")
    @PreAuthorize("@ss.hasPermission('mes:set-env-board:query')")
    public CommonResult<MesSetEnvBoardRespVO> getSummary() {
        return success(envBoardService.getBoard());
    }

}
