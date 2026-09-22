package cn.iocoder.txgy.module.mes.controller.admin.set.tracechain;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceChainPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceReverseRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.service.set.tracechain.MesSetTraceChainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-污染追溯")
@RestController
@RequestMapping("/mes/safety-env/pollution-trace")
@Validated
public class MesSetTraceChainController {

    @Resource
    private MesSetTraceChainService traceChainService;

    @GetMapping("/get")
    @Operation(summary = "获得追溯链节点")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<MesSetTraceChainDO> getTraceNode(@RequestParam("id") Long id) {
        return success(traceChainService.getTraceNode(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得追溯链节点分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<PageResult<MesSetTraceChainDO>> getTraceNodePage(@Valid MesSetTraceChainPageReqVO pageReqVO) {
        return success(traceChainService.getTraceNodePage(pageReqVO));
    }

    @GetMapping("/reverse")
    @Operation(summary = "危废反向查来源（桶码/联单号 → 源头工单与设施）")
    @Parameter(name = "containerCode", description = "危废桶码（HJ1276 贴签上那个）")
    @Parameter(name = "manifestNo", description = "联单号；与桶码至少给一个")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<MesSetTraceReverseRespVO> reverseTrace(
            @RequestParam(value = "containerCode", required = false) String containerCode,
            @RequestParam(value = "manifestNo", required = false) String manifestNo) {
        return success(traceChainService.reverseTrace(containerCode, manifestNo));
    }

    @GetMapping("/reverse-list")
    @Operation(summary = "反向查来源·全量：点开即列全部危废台账行及源头判定，无需先输入桶码")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public CommonResult<List<MesSetTraceReverseRespVO.Ledger>> listAllSources() {
        return success(traceChainService.listAllSources());
    }

    @GetMapping(value = "/report", produces = "text/markdown;charset=UTF-8")
    @Operation(summary = "导出一只危废桶的全程追溯报告（Markdown 下载）")
    @Parameter(name = "containerCode", description = "危废桶码")
    @Parameter(name = "manifestNo", description = "联单号；与桶码至少给一个")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-trace:query')")
    public String exportReport(@RequestParam(value = "containerCode", required = false) String containerCode,
                               @RequestParam(value = "manifestNo", required = false) String manifestNo,
                               HttpServletResponse response) {
        String name = safeFileName(StrUtil.blankToDefault(manifestNo, containerCode));
        // 原样回内容而不是存文件服务再给 URL：监管现场点一下就要拿到件，
        // 多一跳文件服务就多一个"链接打不开"的现场故障。
        response.setHeader("Content-Disposition",
                "attachment; filename=\"trace-report-" + name + ".md\"");
        return traceChainService.exportTraceReport(containerCode, manifestNo);
    }

    /**
     * 下载文件名只留文件名安全字符：入参直接进响应头，带换行或引号就能把响应头拆开。
     */
    private static String safeFileName(String raw) {
        String s = StrUtil.nullToEmpty(raw).replaceAll("[^A-Za-z0-9_.-]", "");
        return s.isEmpty() ? "unknown" : s;
    }

}
