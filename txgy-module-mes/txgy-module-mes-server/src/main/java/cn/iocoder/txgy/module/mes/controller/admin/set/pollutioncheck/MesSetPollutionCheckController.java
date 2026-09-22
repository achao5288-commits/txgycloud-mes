package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck;

import cn.iocoder.txgy.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageParam;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.framework.excel.core.util.ExcelUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesFieldSignRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesPollutionLegalBasisRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAiRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAmendReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckControlLocationReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckReviewReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesPollutionCheckLogDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.service.pollution.MesPollutionControlService;
import cn.iocoder.txgy.module.mes.service.set.pollutioncheck.MesSetPollutionCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.txgy.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-污染判定记录")
@RestController
@RequestMapping("/mes/safety-env/pollution-check")
@Validated
public class MesSetPollutionCheckController {

    @Resource
    private MesSetPollutionCheckService pollutionCheckService;

    @Resource
    private MesPollutionControlService pollutionControlService;

    @PostMapping("/create")
    @Operation(summary = "创建污染判定记录(AI 初筛自动回填，落为待复核)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:create')")
    public CommonResult<Long> createPollutionCheck(@Valid @RequestBody MesSetPollutionCheckSaveReqVO createReqVO) {
        return success(pollutionCheckService.createPollutionCheck(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新污染判定记录(仅待复核)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:update')")
    public CommonResult<Boolean> updatePollutionCheck(@Valid @RequestBody MesSetPollutionCheckSaveReqVO updateReqVO) {
        pollutionCheckService.updatePollutionCheck(updateReqVO);
        return success(true);
    }

    @PostMapping("/review")
    @Operation(summary = "人工复核(终态收口：无污染/有污染)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:review')")
    public CommonResult<Boolean> reviewPollutionCheck(@Valid @RequestBody MesSetPollutionCheckReviewReqVO reviewReqVO) {
        pollutionCheckService.reviewPollutionCheck(reviewReqVO);
        return success(true);
    }

    @PostMapping("/amend")
    @Operation(summary = "发起变更(已收口的判定不可就地改，新开一张变更单承接改动；原单标记已被替代)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:create')")
    public CommonResult<Long> amendPollutionCheck(@Valid @RequestBody MesSetPollutionCheckAmendReqVO amendReqVO) {
        return success(pollutionCheckService.amendPollutionCheck(amendReqVO));
    }

    @PutMapping("/control-location")
    @Operation(summary = "受控库位调整(仅已复核；同步台账行与批次污染戳)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:review')")
    public CommonResult<Boolean> changeControlLocation(@Valid @RequestBody MesSetPollutionCheckControlLocationReqVO reqVO) {
        pollutionControlService.changeControlLocation(reqVO);
        return success(true);
    }

    @PostMapping("/prescreen")
    @Operation(summary = "AI 初筛预览(不落库)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<MesSetPollutionCheckAiRespVO> prescreenPollutionCheck(
            @Valid @RequestBody MesSetPollutionCheckSaveReqVO prescreenReqVO) {
        return success(pollutionCheckService.prescreenPollutionCheck(prescreenReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除污染判定记录(仅待复核)")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:delete')")
    public CommonResult<Boolean> deletePollutionCheck(@RequestParam("id") Long id) {
        pollutionCheckService.deletePollutionCheck(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得污染判定记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<MesSetPollutionCheckRespVO> getPollutionCheck(@RequestParam("id") Long id) {
        MesSetPollutionCheckDO pollutionCheck = pollutionCheckService.getPollutionCheck(id);
        return success(BeanUtils.toBean(pollutionCheck, MesSetPollutionCheckRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得污染判定记录分页")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<PageResult<MesSetPollutionCheckRespVO>> getPollutionCheckPage(
            @Valid MesSetPollutionCheckPageReqVO pageReqVO) {
        PageResult<MesSetPollutionCheckDO> pageResult = pollutionCheckService.getPollutionCheckPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetPollutionCheckRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出污染判定记录 Excel(供环保检查/追溯)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPollutionCheckExcel(@Valid MesSetPollutionCheckPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MesSetPollutionCheckDO> list = pollutionCheckService.getPollutionCheckPage(pageReqVO).getList();
        ExcelUtils.write(response, "污染判定记录.xls", "数据", MesSetPollutionCheckRespVO.class,
                BeanUtils.toBean(list, MesSetPollutionCheckRespVO.class));
    }

    @GetMapping("/history")
    @Operation(summary = "获得污染判定履历(创建→改单→复核，AI/人工快照只增不改)")
    @Parameter(name = "checkId", description = "判定记录编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<List<MesPollutionCheckLogDO>> getPollutionCheckHistory(@RequestParam("checkId") Long checkId) {
        return success(pollutionCheckService.getPollutionCheckLogList(checkId));
    }

    @GetMapping("/legal-basis")
    @Operation(summary = "获得可引用的法规依据白名单(AI 初筛与人工复核共用)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<List<MesPollutionLegalBasisRespVO>> getLegalBasisList() {
        return success(pollutionCheckService.getLegalBasisList());
    }

    @GetMapping("/field-signs")
    @Operation(summary = "获得现场污染特征清单(每项带命中的法条候选，供复核收窄)")
    @PreAuthorize("@ss.hasPermission('mes:set-pollution-check:query')")
    public CommonResult<List<MesFieldSignRespVO>> getFieldSignList() {
        return success(pollutionCheckService.getFieldSignList());
    }

}
