package cn.iocoder.txgy.module.mes.controller.admin.set.emergencysop;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencysop.vo.MesSetEmergencySopRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemical.MesSetChemicalProfileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_SOP_CHEMICAL_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_SOP_PARAM_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_SOP_SCENARIO_INVALID;

/**
 * MES 安全环保检测-泄漏应急处置卡 Controller（PDA 扫码即看）
 *
 * 设计文档 §八.3 的三条泄漏 SOP（黑料/稀释剂/废机油）是**内置常量**，不是表数据——
 * 建一张三行的表来存三段文字，不如放在这里一眼看得全。
 * 化学品档案里已有的 `emergency_measure`/`msds_url`/`incompatible_groups` 则就地复用，
 * 只在拿得到的时候才带上（"没配就别编"）。
 *
 * 这是只读的参考文本，不掺任何业务单据，故不加 `@PreAuthorize`：
 * §八.3 要的就是现场扫码零摩擦可看。
 */
@Tag(name = "管理后台 - MES 安全环保检测-应急处置卡")
@RestController
@RequestMapping("/mes/safety-env/emergency-sop")
@Validated
public class MesSetEmergencySopController {

    /**
     * 场景 → 处置卡。顺序即执行顺序；`forbidden` 是红线，单独列出来是因为
     * 它们恰恰是最容易做错的直觉动作（比如"用水冲一下"）。
     */
    private static final Map<String, String[]> SCENARIOS = Map.of(
            "MDI_LEAK", new String[]{
                    "黑料（MDI）泄漏",
                    "干沙或惰性吸附材料围堵收集|严禁用水冲洗——异氰酸酯遇水放热放气|收集后专用清洗剂擦洗地面|通风、禁明火、无关人员撤离",
                    "防毒面具|化学防护服|防化手套|护目镜",
                    "严禁用水冲|严禁扫帚干扫扬尘|严禁与醇类/胺类混收",
                    "HW49", "泄漏吸附物（异氰酸酯）"},
            "THINNER_LEAK", new String[]{
                    "稀释剂泄漏",
                    "切断一切火源、停止动火作业|防静电着装，禁用铁质器具敲击|沙土围堵拦截，防止流入下水道|吸油毡/沙土吸附收集",
                    "防毒面具|防静电服|防化手套|护目镜",
                    "严禁一切明火与火花|严禁使用非防爆工具|严禁倒入下水道",
                    "HW06", "废稀释剂及吸附物"},
            "WASTE_OIL_LEAK", new String[]{
                    "废机油泄漏",
                    "吸油毡吸附收集|沙袋围堰拦截扩散|清扫后废吸油毡密封装桶",
                    "防化手套|防滑劳保鞋|护目镜",
                    "严禁进入雨水沟|严禁用水冲入地面排水|严禁与废稀释剂混收",
                    "HW08", "废矿物油及吸附物"});

    @Resource
    private MesSetChemicalProfileMapper chemicalProfileMapper;

    @GetMapping("/card")
    @Operation(summary = "获得泄漏应急处置卡（按场景，或按化学品档案）")
    @Parameter(name = "scenario", description = "泄漏场景：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK")
    @Parameter(name = "chemicalCode", description = "化学品代码，按档案取 MSDS 与应急处置措施")
    public CommonResult<MesSetEmergencySopRespVO> getSopCard(
            @RequestParam(value = "scenario", required = false) String scenario,
            @RequestParam(value = "chemicalCode", required = false) String chemicalCode) {
        boolean hasScenario = StrUtil.isNotBlank(scenario);
        boolean hasChemical = StrUtil.isNotBlank(chemicalCode);
        if (!hasScenario && !hasChemical) {
            throw exception(SET_EMERGENCY_SOP_PARAM_REQUIRED);
        }

        MesSetEmergencySopRespVO vo = new MesSetEmergencySopRespVO();
        if (hasScenario) {
            String[] preset = SCENARIOS.get(scenario);
            if (preset == null) {
                throw exception(SET_EMERGENCY_SOP_SCENARIO_INVALID);
            }
            vo.setScenario(scenario);
            vo.setTitle(preset[0]);
            vo.setSteps(StrUtil.splitTrim(preset[1], '|'));
            vo.setPpe(StrUtil.splitTrim(preset[2], '|'));
            vo.setForbidden(StrUtil.splitTrim(preset[3], '|'));
            vo.setWasteCode(preset[4]);
            vo.setWasteName(preset[5]);
            vo.setSource("PRESET");
        }

        if (hasChemical) {
            MesSetChemicalProfileDO profile = chemicalProfileMapper.selectByChemicalCode(chemicalCode);
            if (profile == null) {
                throw exception(SET_EMERGENCY_SOP_CHEMICAL_NOT_EXISTS);
            }
            vo.setChemicalCode(profile.getChemicalCode());
            vo.setChemicalName(profile.getChemicalName());
            vo.setMsdsUrl(profile.getMsdsUrl());
            vo.setStorageZone(StrUtil.blankToDefault(profile.getStorageZone(), profile.getStorageLocation()));
            vo.setIncompatibleGroups(profile.getIncompatibleGroups());
            vo.setEmergencyMeasure(profile.getEmergencyMeasure());
            if (!hasScenario) {
                // 只有化学品、没有场景：不带预设步骤（没配就别编），
                // 现场照 emergencyMeasure 与 MSDS 走
                vo.setTitle(StrUtil.format("{} 泄漏", profile.getChemicalName()));
                vo.setSource("CHEMICAL_PROFILE");
            } else {
                vo.setSource("PRESET+CHEMICAL_PROFILE");
            }
        }
        return success(vo);
    }

    /**
     * 供前端下拉/按钮组使用的场景清单（顺序固定，Map.of 的遍历顺序是随机的，故另列）。
     */
    @GetMapping("/scenarios")
    @Operation(summary = "获得内置泄漏场景清单")
    public CommonResult<List<String[]>> getScenarios() {
        return success(List.of(
                new String[]{"MDI_LEAK", SCENARIOS.get("MDI_LEAK")[0]},
                new String[]{"THINNER_LEAK", SCENARIOS.get("THINNER_LEAK")[0]},
                new String[]{"WASTE_OIL_LEAK", SCENARIOS.get("WASTE_OIL_LEAK")[0]}));
    }

}
