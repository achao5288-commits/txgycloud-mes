package cn.iocoder.txgy.module.mes.controller.admin.set.facility;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import cn.iocoder.txgy.module.mes.service.set.facility.MesSetTreatmentFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

/**
 * MES 安全环保检测-治污设施运行管控（台账 / 换炭→HW49 / 停运申报审批 / 同开同停）。
 *
 * @author OPENLAB BS
 */
@Tag(name = "管理后台 - MES 安全环保检测-治污设施")
@RestController
@RequestMapping("/mes/safety-env/facility")
@Validated
public class MesSetTreatmentFacilityController {

    @Resource
    private MesSetTreatmentFacilityService facilityService;

    // ==================== 台账 ====================

    @PostMapping("/create")
    @Operation(summary = "创建治污设施")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:create')")
    public CommonResult<Long> createFacility(@Valid @RequestBody MesSetTreatmentFacilitySaveReqVO createReqVO) {
        return success(facilityService.createFacility(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新治污设施（运行状态与停运申报不可经此修改，须走申报审批）")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:update')")
    public CommonResult<Boolean> updateFacility(@Valid @RequestBody MesSetTreatmentFacilitySaveReqVO updateReqVO) {
        facilityService.updateFacility(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除治污设施")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-facility:delete')")
    public CommonResult<Boolean> deleteFacility(@RequestParam("id") Long id) {
        facilityService.deleteFacility(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得治污设施")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:query')")
    public CommonResult<MesSetTreatmentFacilityDO> getFacility(@RequestParam("id") Long id) {
        return success(facilityService.getFacility(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得治污设施分页")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:query')")
    public CommonResult<PageResult<MesSetTreatmentFacilityDO>> getFacilityPage(
            @Valid MesSetTreatmentFacilityPageReqVO pageReqVO) {
        return success(facilityService.getFacilityPage(pageReqVO));
    }

    // ==================== 换炭 → HW49 ====================

    @PostMapping("/replace-consumable")
    @Operation(summary = "耗材更换（换炭）：同一事务内登记废活性炭 HW49 入桶并顺延下次更换日期")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:operate')")
    public CommonResult<MesSetFacilityReplaceRespVO> replaceConsumable(
            @Valid @RequestBody MesSetFacilityReplaceReqVO reqVO) {
        return success(facilityService.replaceConsumable(reqVO));
    }

    @GetMapping("/due-replace")
    @Operation(summary = "换炭到期预警：下次更换日期不晚于 now+days（含已逾期）")
    @Parameter(name = "days", description = "预警天数，默认 30")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:query')")
    public CommonResult<List<MesSetTreatmentFacilityDO>> listDueReplace(
            @RequestParam(value = "days", required = false) Integer days) {
        return success(facilityService.listDueReplace(days));
    }

    // ==================== 停运申报 + 审批 ====================

    @PostMapping("/shutdown-declare")
    @Operation(summary = "停运申报（申报不等于停运，批准后才转停运）")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:operate')")
    public CommonResult<MesSetTreatmentFacilityDO> declareShutdown(
            @Valid @RequestBody MesSetFacilityShutdownDeclareReqVO reqVO) {
        return success(facilityService.declareShutdown(reqVO));
    }

    @PostMapping("/shutdown-approve")
    @Operation(summary = "停运审批（批准则转停运，驳回则保持运行）")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:operate')")
    public CommonResult<MesSetTreatmentFacilityDO> approveShutdown(
            @Valid @RequestBody MesSetFacilityShutdownApproveReqVO reqVO) {
        return success(facilityService.approveShutdown(reqVO));
    }

    @PostMapping("/resume")
    @Operation(summary = "设施复运（停运→运行，并归档本次停运申报）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-facility:operate')")
    public CommonResult<MesSetTreatmentFacilityDO> resumeFacility(@RequestParam("id") Long id) {
        return success(facilityService.resumeFacility(id));
    }

    @GetMapping("/stopped-without-approval")
    @Operation(summary = "未批先停清单：已停运但停运申报不是已批准的设施")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:query')")
    public CommonResult<List<MesSetTreatmentFacilityDO>> listStoppedWithoutApproval() {
        return success(facilityService.listStoppedWithoutApproval());
    }

    // ==================== 同开同停 ====================

    @PostMapping("/co-run-check")
    @Operation(summary = "同开同停校验（只读）：产线运行中而配套治污设施停运 → 违规")
    @PreAuthorize("@ss.hasPermission('mes:set-facility:query')")
    public CommonResult<MesSetFacilityCoRunRespVO> checkCoRun(
            @Valid @RequestBody MesSetFacilityCoRunReqVO reqVO) {
        return success(facilityService.checkCoRun(reqVO));
    }

}
