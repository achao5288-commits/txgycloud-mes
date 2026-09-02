package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 作业环境气体检测记录 Response VO")
@Data
public class MesSetGasRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "记录编号", example = "GAS-20260901-001")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "关联作业许可编号", example = "1")
    private Long permitId;

    @Schema(description = "检测位置", example = "3号受限空间")
    private String location;

    @Schema(description = "气体类型", example = "CO")
    private String gasType;

    @Schema(description = "检测浓度值", example = "12.5")
    private BigDecimal concentration;

    @Schema(description = "单位", example = "mg/m3")
    private String unit;

    @Schema(description = "限值", example = "20")
    private BigDecimal limitValue;

    @Schema(description = "结果", example = "PASS")
    private String result;

    @Schema(description = "采集方式", example = "MANUAL")
    private String collectionMode;

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
