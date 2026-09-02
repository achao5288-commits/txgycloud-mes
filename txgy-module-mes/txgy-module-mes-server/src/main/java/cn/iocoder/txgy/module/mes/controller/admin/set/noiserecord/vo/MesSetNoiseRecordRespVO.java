package cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-噪声检测记录 Response VO")
@Data
public class MesSetNoiseRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "记录编号", example = "NOISE-20260902-001")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "关联设备编号", example = "1")
    private Long deviceId;

    @Schema(description = "关联作业人员编号", example = "1")
    private Long empId;

    @Schema(description = "噪声源类型", example = "EQUIPMENT")
    private String sourceType;

    @Schema(description = "检测位置", example = "3号车间")
    private String location;

    @Schema(description = "采集方式", example = "MANUAL")
    private String collectionMode;

    @Schema(description = "8小时等效声级 Lex8h(dB(A))", example = "85.5")
    private BigDecimal lex8h;

    @Schema(description = "峰值声级 Lpeak(dB(C))", example = "110.2")
    private BigDecimal lpeak;

    @Schema(description = "8小时等效声级限值(dB(A))", example = "85")
    private BigDecimal limitLex8h;

    @Schema(description = "峰值声级限值(dB(C))", example = "120")
    private BigDecimal limitLpeak;

    @Schema(description = "频谱分析结果")
    private String spectrum;

    @Schema(description = "关联执行标准编号", example = "1")
    private Long standardId;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "检测仪器编号", example = "INS-001")
    private String instrumentNo;

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
