package cn.iocoder.txgy.module.mes.controller.admin.set.chemical;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import cn.iocoder.txgy.module.mes.service.set.chemical.MesSetChemicalProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

/**
 * MES 安全环保检测-危化品链（档案 / 分级储存禁配 / 储量上限 / 五双双人签字）。
 *
 * @author OPENLAB BS
 */
@Tag(name = "管理后台 - MES 安全环保检测-危化品链")
@RestController
@RequestMapping("/mes/safety-env/chemical")
@Validated
public class MesSetChemicalProfileController {

    @Resource
    private MesSetChemicalProfileService chemicalProfileService;

    // ==================== 危化品档案 ====================

    @PostMapping("/create")
    @Operation(summary = "创建危化品档案")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:create')")
    public CommonResult<Long> createProfile(@Valid @RequestBody MesSetChemicalProfileSaveReqVO createReqVO) {
        return success(chemicalProfileService.createProfile(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新危化品档案")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:update')")
    public CommonResult<Boolean> updateProfile(@Valid @RequestBody MesSetChemicalProfileSaveReqVO updateReqVO) {
        chemicalProfileService.updateProfile(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除危化品档案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:delete')")
    public CommonResult<Boolean> deleteProfile(@RequestParam("id") Long id) {
        chemicalProfileService.deleteProfile(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得危化品档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:query')")
    public CommonResult<MesSetChemicalProfileDO> getProfile(@RequestParam("id") Long id) {
        return success(chemicalProfileService.getProfile(id));
    }

    @GetMapping("/get-by-no")
    @Operation(summary = "按档案编号获得危化品档案")
    @Parameter(name = "profileNo", description = "档案编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:query')")
    public CommonResult<MesSetChemicalProfileDO> getProfileByNo(@RequestParam("profileNo") String profileNo) {
        return success(chemicalProfileService.getProfileByNo(profileNo));
    }

    @GetMapping("/page")
    @Operation(summary = "获得危化品档案分页")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:query')")
    public CommonResult<PageResult<MesSetChemicalProfileDO>> getProfilePage(
            @Valid MesSetChemicalProfilePageReqVO pageReqVO) {
        return success(chemicalProfileService.getProfilePage(pageReqVO));
    }

    // ==================== 分级储存四道校验 ====================

    @PostMapping("/check-storage")
    @Operation(summary = "危化品库位/入库校验（只判不写）：MSDS → 专区 → 禁配 → 储量上限")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:check')")
    public CommonResult<MesSetChemicalStorageCheckRespVO> checkStorage(
            @Valid @RequestBody MesSetChemicalStorageCheckReqVO reqVO) {
        return success(chemicalProfileService.checkStorage(reqVO));
    }

    @PostMapping("/stock-in")
    @Operation(summary = "危化品入库（过四道校验后累加存量）")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:check')")
    public CommonResult<MesSetChemicalStorageCheckRespVO> stockIn(
            @Valid @RequestBody MesSetChemicalStockInReqVO reqVO) {
        return success(chemicalProfileService.stockIn(reqVO));
    }

    // ==================== 五双双人签字 ====================

    @PostMapping("/double-sign")
    @Operation(summary = "五双双人签字（同一作业同一动作须两个不同账号各签一次）")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:sign')")
    public CommonResult<MesSetChemicalDoubleSignRespVO> doubleSign(
            @Valid @RequestBody MesSetChemicalDoubleSignReqVO reqVO) {
        return success(chemicalProfileService.doubleSign(reqVO));
    }

    @GetMapping("/double-sign-status")
    @Operation(summary = "获得五双双人签字状态")
    @Parameter(name = "bizNo", description = "作业单号", required = true)
    @Parameter(name = "signAction", description = "作业类型", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:query')")
    public CommonResult<MesSetChemicalDoubleSignRespVO> getDoubleSignStatus(
            @RequestParam("bizNo") String bizNo, @RequestParam("signAction") String signAction) {
        return success(chemicalProfileService.getDoubleSignStatus(bizNo, signAction));
    }

    // ==================== 预警 ====================

    @GetMapping("/alerts")
    @Operation(summary = "危化品预警汇总：超量 / MSDS 缺失 / MSDS 版本将到期")
    @Parameter(name = "days", description = "MSDS 到期预警天数，默认 30")
    @PreAuthorize("@ss.hasPermission('mes:set-chemical:query')")
    public CommonResult<MesSetChemicalAlertRespVO> getAlerts(
            @RequestParam(value = "days", required = false) Integer days) {
        return success(chemicalProfileService.getAlerts(days));
    }

}
