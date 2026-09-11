package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazwasteManifestDO;
import cn.iocoder.txgy.module.mes.service.set.hazwaste.MesSetHazwasteService;
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
 * MES 安全环保检测-危废链（台账 + 国家固废转移联单五方 + 门卫硬放行 + HJ1276 标签）。
 *
 * @author OPENLAB BS
 */
@Tag(name = "管理后台 - MES 安全环保检测-危废链")
@RestController
@RequestMapping("/mes/safety-env/hazwaste")
@Validated
public class MesSetHazwasteController {

    @Resource
    private MesSetHazwasteService hazwasteService;

    // ==================== 危废台账 ====================

    @PostMapping("/create")
    @Operation(summary = "创建危废台账记录")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:create')")
    public CommonResult<Long> createHazardousWaste(@Valid @RequestBody MesSetHazardousWasteSaveReqVO createReqVO) {
        return success(hazwasteService.createHazardousWaste(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新危废台账记录")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> updateHazardousWaste(@Valid @RequestBody MesSetHazardousWasteSaveReqVO updateReqVO) {
        hazwasteService.updateHazardousWaste(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除危废台账记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:delete')")
    public CommonResult<Boolean> deleteHazardousWaste(@RequestParam("id") Long id) {
        hazwasteService.deleteHazardousWaste(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得危废台账记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<MesSetHazardousWasteDO> getHazardousWaste(@RequestParam("id") Long id) {
        return success(hazwasteService.getHazardousWaste(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得危废台账分页")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<PageResult<MesSetHazardousWasteDO>> getHazardousWastePage(
            @Valid MesSetHazardousWastePageReqVO pageReqVO) {
        return success(hazwasteService.getHazardousWastePage(pageReqVO));
    }

    @PostMapping("/advance-stage")
    @Operation(summary = "推进危废台账环节（产生→贮存→转移→处置，只进不退）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @Parameter(name = "stage", description = "目标环节", required = true, example = "STORED")
    @Parameter(name = "handler", description = "经办人（留空取当前登录人）")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> advanceStage(@RequestParam("id") Long id,
                                             @RequestParam("stage") String stage,
                                             @RequestParam(value = "handler", required = false) String handler) {
        hazwasteService.advanceStage(id, stage, handler);
        return success(true);
    }

    // ==================== HJ1276 标签 ====================

    @GetMapping("/label")
    @Operation(summary = "获得危废 HJ1276 标签字段集（前端按国标模板渲染打印）")
    @Parameter(name = "id", description = "台账编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<MesSetHazwasteLabelRespVO> getLabel(@RequestParam("id") Long id) {
        return success(hazwasteService.getLabel(id));
    }

    @PostMapping("/label/archive")
    @Operation(summary = "把 HJ1276 标签落成文件存入文件服务，回写 label_url 并返回该 URL")
    @Parameter(name = "id", description = "台账编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<String> archiveLabel(@RequestParam("id") Long id) {
        return success(hazwasteService.archiveLabel(id));
    }

    // ==================== 危废转移联单（国家固废五方） ====================

    @PostMapping("/manifest/create")
    @Operation(summary = "创建危废转移联单")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:create')")
    public CommonResult<Long> createManifest(@Valid @RequestBody MesSetHazwasteManifestSaveReqVO createReqVO) {
        return success(hazwasteService.createManifest(createReqVO));
    }

    @PutMapping("/manifest/update")
    @Operation(summary = "更新危废转移联单")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> updateManifest(@Valid @RequestBody MesSetHazwasteManifestSaveReqVO updateReqVO) {
        hazwasteService.updateManifest(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/manifest/delete")
    @Operation(summary = "删除危废转移联单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:delete')")
    public CommonResult<Boolean> deleteManifest(@RequestParam("id") Long id) {
        hazwasteService.deleteManifest(id);
        return success(true);
    }

    @GetMapping("/manifest/get")
    @Operation(summary = "获得危废转移联单")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<MesSetHazwasteManifestDO> getManifest(@RequestParam("id") Long id) {
        return success(hazwasteService.getManifest(id));
    }

    @GetMapping("/manifest/get-by-no")
    @Operation(summary = "按联单号获得危废转移联单")
    @Parameter(name = "manifestNo", description = "联单号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<MesSetHazwasteManifestDO> getManifestByNo(@RequestParam("manifestNo") String manifestNo) {
        return success(hazwasteService.getManifestByNo(manifestNo));
    }

    @GetMapping("/manifest/page")
    @Operation(summary = "获得危废转移联单分页")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<PageResult<MesSetHazwasteManifestDO>> getManifestPage(
            @Valid MesSetHazwasteManifestPageReqVO pageReqVO) {
        return success(hazwasteService.getManifestPage(pageReqVO));
    }

    @PostMapping("/manifest/declare")
    @Operation(summary = "产生方申报联单（草稿→已申报）")
    @Parameter(name = "manifestNo", description = "联单号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> declareManifest(@RequestParam("manifestNo") String manifestNo) {
        hazwasteService.declareManifest(manifestNo);
        return success(true);
    }

    @PostMapping("/manifest/confirm")
    @Operation(summary = "五方之一确认（运输+接收都确认后联单自动生效）")
    @Parameter(name = "manifestNo", description = "联单号", required = true)
    @Parameter(name = "partyRole", description = "CARRIER/RECEIVER_PARTY/STORAGE/DISPOSER", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> confirmParty(@RequestParam("manifestNo") String manifestNo,
                                             @RequestParam("partyRole") String partyRole) {
        hazwasteService.confirmParty(manifestNo, partyRole);
        return success(true);
    }

    @PostMapping("/manifest/close")
    @Operation(summary = "回执归档（已出厂→已归档）")
    @Parameter(name = "manifestNo", description = "联单号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> closeManifest(@RequestParam("manifestNo") String manifestNo) {
        hazwasteService.closeManifest(manifestNo);
        return success(true);
    }

    @PostMapping("/manifest/sign")
    @Operation(summary = "四方会签（复用签字记录，只增不改删）")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:update')")
    public CommonResult<Boolean> signManifest(@Valid @RequestBody MesSetHazwasteSignReqVO signReqVO) {
        hazwasteService.signManifest(signReqVO.getManifestNo(), signReqVO.getSignRole(),
                signReqVO.getOpinion(), signReqVO.getSignImg());
        return success(true);
    }

    @GetMapping("/manifest/due-soon")
    @Operation(summary = "国家平台倒排提醒：已申报未生效且时限临近的联单（含已逾期）")
    @Parameter(name = "days", description = "提前天数，默认 3")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:query')")
    public CommonResult<List<MesSetHazwasteManifestDO>> listDueSoon(
            @RequestParam(value = "days", required = false, defaultValue = "3") Integer days) {
        return success(hazwasteService.listDueSoon(days));
    }

    // ==================== 门卫硬放行 ====================

    @PostMapping("/gate-check")
    @Operation(summary = "危废出厂门卫校验（只读，可反复试；四项缺一不放行）")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:gate')")
    public CommonResult<MesSetHazwasteGateCheckRespVO> checkGate(
            @Valid @RequestBody MesSetHazwasteGateCheckReqVO reqVO) {
        return success(hazwasteService.checkGate(reqVO));
    }

    @PostMapping("/gate-release")
    @Operation(summary = "门卫放行（硬）：任一校验不过按具体原因拦截，通过则补签门卫并推联单/台账至已转移")
    @PreAuthorize("@ss.hasPermission('mes:set-hazwaste:gate')")
    public CommonResult<MesSetHazwasteGateCheckRespVO> releaseGate(
            @Valid @RequestBody MesSetHazwasteGateCheckReqVO reqVO) {
        return success(hazwasteService.releaseGate(reqVO));
    }

}
