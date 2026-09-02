package cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-粉尘浓度检测记录 Response VO")
@Data
public class MesSetDustRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "记录编号", example = "DUST-20260902-001")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "关联设备编号", example = "1")
    private Long deviceId;

    @Schema(description = "检测位置", example = "2号车间")
    private String location;

    @Schema(description = "粉尘类型", example = "COAL")
    private String dustType;

    @Schema(description = "粉尘浓度值", example = "2.5")
    private BigDecimal concentration;

    @Schema(description = "游离二氧化硅含量(%)", example = "10.5")
    private BigDecimal sio2Content;

    @Schema(description = "单位", example = "mg/m3")
    private String unit;

    @Schema(description = "限值(mg/m3)", example = "4")
    private BigDecimal limitValue;

    @Schema(description = "执行标准", example = "GBZ 2.1-2019")
    private String refStandard;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "采集方式", example = "MANUAL")
    private String collectionMode;

    @Schema(description = "检测仪器编号", example = "INS-002")
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
