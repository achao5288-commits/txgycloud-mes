package cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-噪声检测记录 新增/修改 Request VO")
@Data
public class MesSetNoiseRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号 NOISE-YYYYMMDD-NNN")
    @NotEmpty(message = "记录编号 NOISE-YYYYMMDD-NNN不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联工单编号")
    private Long woId;

    @Schema(description = "关联工序编号")
    private Long operationId;

    @Schema(description = "关联设备编号")
    private Long deviceId;

    @Schema(description = "关联人员编号(个体剂量计佩戴人)")
    private Long empId;

    @Schema(description = "监测类型：STATIONARY(固定式声级计)/PERSONAL(个体剂量计)")
    @NotEmpty(message = "监测类型：STATIONARY(固定式声级计)/PERSONAL(个体剂量计)不能为空")
    private String sourceType;

    @Schema(description = "检测位置/区域")
    private String location;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL")
    private String collectionMode;

    @Schema(description = "8小时等效声级 dB(A)")
    private BigDecimal lex8h;

    @Schema(description = "峰值声级 dB(C)")
    private BigDecimal lpeak;

    @Schema(description = "限值 dB(A)≤85")
    private BigDecimal limitLex8h;

    @Schema(description = "限值 dB(C)≤140")
    private BigDecimal limitLpeak;

    @Schema(description = "频谱分析(倍频程，JSON文本)")
    private String spectrum;

    @Schema(description = "关联检测标准编号")
    private Long standardId;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "检测仪器编号")
    private String instrumentNo;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测时间")
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片URL(逗号分隔)")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
