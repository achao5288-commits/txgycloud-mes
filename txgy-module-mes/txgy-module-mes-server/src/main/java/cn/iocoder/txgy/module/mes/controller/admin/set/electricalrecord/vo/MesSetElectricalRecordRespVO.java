package cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-电气安全检测记录 Response VO")
@Data
public class MesSetElectricalRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "记录编号", example = "ELEC-20260902-001")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long deviceId;

    @Schema(description = "检测位置", example = "配电室A")
    private String location;

    @Schema(description = "检测项目", example = "GROUND_RESISTANCE")
    private String checkItem;

    @Schema(description = "测量值", example = "1.2")
    private BigDecimal measuredValue;

    @Schema(description = "单位", example = "Ω")
    private String unit;

    @Schema(description = "限值", example = "4")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "检测仪器编号", example = "INS-003")
    private String instrumentNo;

    @Schema(description = "仪器校准状态：0-未校准 1-已校准", example = "1")
    private Integer instrumentCalibOk;

    @Schema(description = "检测人", example = "张三")
    private String inspector;

    @Schema(description = "检测时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片 URL")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
