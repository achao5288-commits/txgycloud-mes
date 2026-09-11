package cn.iocoder.txgy.module.mes.controller.admin.set.emergencysop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MES 安全环保检测-泄漏应急处置卡 RespVO（PDA 扫码即看）
 *
 * 设计文档 §八.3：处置卡随 MSDS 同步 PDA。字段刻意做成"念得出来"的清单，
 * 现场是戴着手套点屏幕，不能让人读小作文。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 应急处置卡 Response VO")
@Data
public class MesSetEmergencySopRespVO {

    @Schema(description = "泄漏场景代码", example = "MDI_LEAK")
    private String scenario;

    @Schema(description = "场景名称", example = "黑料（MDI）泄漏")
    private String title;

    @Schema(description = "数据来源：CHEMICAL_PROFILE(按化学品档案) / PRESET(内置三场景)",
            example = "PRESET")
    private String source;

    @Schema(description = "化学品代码", example = "CHEM-MDI")
    private String chemicalCode;

    @Schema(description = "化学品名称", example = "聚合 MDI")
    private String chemicalName;

    @Schema(description = "MSDS 地址")
    private String msdsUrl;

    @Schema(description = "贮存库位")
    private String storageZone;

    @Schema(description = "禁配物（不得混放/混收）")
    private String incompatibleGroups;

    @Schema(description = "个体防护，按穿戴顺序")
    private List<String> ppe;

    @Schema(description = "处置步骤，按执行顺序")
    private List<String> steps;

    @Schema(description = "必须避免的动作（红线）")
    private List<String> forbidden;

    @Schema(description = "产废类别代码", example = "HW49")
    private String wasteCode;

    @Schema(description = "产废名称", example = "泄漏吸附物（异氰酸酯）")
    private String wasteName;

    @Schema(description = "化学品档案自带的应急处置文本（source=CHEMICAL_PROFILE 时有值）")
    private String emergencyMeasure;

}
