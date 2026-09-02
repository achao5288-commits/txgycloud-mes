package cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-压力容器检测记录 Response VO")
@Data
public class MesSetPressureVesselRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联设备编号", example = "1")
    private Long deviceId;

    @Schema(description = "压力容器使用登记证号")
    private String vesselRegNo;

    @Schema(description = "壁厚测定最小壁厚 mm")
    private BigDecimal wallThickness;

    @Schema(description = "无损检测方法：UT/RT/MT/PT")
    private String ndtMethods;

    @Schema(description = "无损检测结果 JSON 文本")
    private String ndtResults;

    @Schema(description = "安全阀校验合格：1是/0否")
    private Integer safetyValveOk;

    @Schema(description = "耐压试验压力 MPa")
    private BigDecimal pressureTestValue;

    @Schema(description = "耐压试验结果 PASS/FAIL")
    private String pressureTestResult;

    @Schema(description = "工艺允许压力 MPa")
    private BigDecimal processPressure;

    @Schema(description = "综合结论 PASS/FAIL")
    private String result;

    @Schema(description = "检验机构")
    private String inspectOrg;

    @Schema(description = "下次检验日期")
    private LocalDate nextInspectDate;

    @Schema(description = "检验报告文件 URL")
    private String reportFileUrl;

    @Schema(description = "检验人")
    private String inspector;

    @Schema(description = "检验时间")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
