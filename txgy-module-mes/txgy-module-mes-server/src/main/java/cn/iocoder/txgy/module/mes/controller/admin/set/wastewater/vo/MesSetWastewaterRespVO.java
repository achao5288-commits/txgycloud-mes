package cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-废水排放检测记录 Response VO")
@Data
public class MesSetWastewaterRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "关联排放口编号", example = "1")
    private Long outletId;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "实验室样品编号(手工检测时)")
    private String sampleNo;

    @Schema(description = "污染物")
    private String pollutantCode;

    @Schema(description = "检测浓度 mg/L(pH无量纲)")
    private BigDecimal concentration;

    @Schema(description = "单位 mg/L(pH留空)")
    private String unit;

    @Schema(description = "排放流量 m3/h")
    private BigDecimal flowRate;

    @Schema(description = "排放量 kg/h")
    private BigDecimal emissionAmount;

    @Schema(description = "限值")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：ONLINE_AUTO/LAB_MANUAL")
    private String collectionMode;

    @Schema(description = "监测时间")
    private LocalDateTime monitorTime;

    @Schema(description = "在线监测仪/化验设备编号")
    private String instrumentNo;

    @Schema(description = "化验员(手工时)")
    private String inspector;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
