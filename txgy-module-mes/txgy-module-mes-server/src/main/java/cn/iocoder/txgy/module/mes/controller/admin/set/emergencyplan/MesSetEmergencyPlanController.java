package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan.MesSetEmergencyPlanDO;
import cn.iocoder.txgy.module.mes.service.set.emergencyplan.MesSetEmergencyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-应急预案")
@RestController
@RequestMapping("/mes/safety-env/emergency-plan")
@Validated
public class MesSetEmergencyPlanController {

    @Resource
    private MesSetEmergencyPlanService emergencyplanService;

    @PostMapping("/create")
    @Operation(summary = "创建应急预案")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:create')")
    public CommonResult<Long> createEmergencyPlan(@Valid @RequestBody MesSetEmergencyPlanSaveReqVO createReqVO) {
        return success(emergencyplanService.createEmergencyPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应急预案")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:update')")
    public CommonResult<Boolean> updateEmergencyPlan(@Valid @RequestBody MesSetEmergencyPlanSaveReqVO updateReqVO) {
        emergencyplanService.updateEmergencyPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应急预案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:delete')")
    public CommonResult<Boolean> deleteEmergencyPlan(@RequestParam("id") Long id) {
        emergencyplanService.deleteEmergencyPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急预案")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:query')")
    public CommonResult<MesSetEmergencyPlanRespVO> getEmergencyPlan(@RequestParam("id") Long id) {
        MesSetEmergencyPlanDO obj = emergencyplanService.getEmergencyPlan(id);
        return success(BeanUtils.toBean(obj, MesSetEmergencyPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急预案分页")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:query')")
    public CommonResult<PageResult<MesSetEmergencyPlanRespVO>> getEmergencyPlanPage(
            @Valid MesSetEmergencyPlanPageReqVO pageReqVO) {
        PageResult<MesSetEmergencyPlanDO> pageResult = emergencyplanService.getEmergencyPlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MesSetEmergencyPlanRespVO.class));
    }

    // 以下三个动作都写预案正文的"锚点字段"（发布日/备案号/评估日），统一走 file 权限——
    // 菜单里只放一个「发布·备案·修订」按钮，不按动作拆权限。

    @PostMapping("/publish")
    @Operation(summary = "发布预案（定发布日期，派生备案截止日与下次评估日）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "publishDate", description = "发布日期(yyyy-MM-dd)，缺省今天")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:file')")
    public CommonResult<Boolean> publishPlan(
            @RequestParam("id") Long id,
            @RequestParam(value = "publishDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishDate) {
        emergencyplanService.publishPlan(id, publishDate);
        return success(true);
    }

    @PostMapping("/file")
    @Operation(summary = "备案登记（写备案号与备案日期，转已备案）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "filingNo", description = "备案号", required = true)
    @Parameter(name = "filingDate", description = "备案日期(yyyy-MM-dd)，缺省今天")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:file')")
    public CommonResult<Boolean> filePlan(
            @RequestParam("id") Long id,
            @RequestParam("filingNo") String filingNo,
            @RequestParam(value = "filingDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filingDate) {
        emergencyplanService.filePlan(id, filingNo, filingDate);
        return success(true);
    }

    @PostMapping("/review")
    @Operation(summary = "评估修订（记修订日期/原因，下次评估顺延 3 年，需重新备案）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "reason", description = "修订原因（工艺/物料/法规变化）")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:file')")
    public CommonResult<Boolean> reviewPlan(@RequestParam("id") Long id,
                                            @RequestParam(value = "reason", required = false) String reason) {
        emergencyplanService.reviewPlan(id, reason);
        return success(true);
    }

    @GetMapping("/due-review")
    @Operation(summary = "获得待评估修订的预案（含已逾期）")
    @Parameter(name = "days", description = "提前天数，缺省 30")
    @PreAuthorize("@ss.hasPermission('mes:set-emergency-plan:query')")
    public CommonResult<List<MesSetEmergencyPlanRespVO>> getDueReview(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        return success(BeanUtils.toBean(emergencyplanService.listDueReview(days), MesSetEmergencyPlanRespVO.class));
    }

}
