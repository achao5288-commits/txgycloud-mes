package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-气体检测记录 Response VO")
@Data
public class MesSetGasRecordRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联工单编号（事件触发型）")
    private Long woId;

    @Schema(description = "关联工序编号")
    private Long operationId;

    @Schema(description = "关联作业许可编号")
    private Long permitId;

    @Schema(description = "检测位置")
    private String location;

    @Schema(description = "气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2")
    private String gasType;

    @Schema(description = "检测浓度值")
    private BigDecimal concentration;

    @Schema(description = "单位：mg/m3 / % / %LEL")
    private String unit;

    @Schema(description = "限值")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL")
    private String collectionMode;

    @Schema(description = "检测仪器编号")
    private String instrumentNo;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测时间")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片URL(逗号分隔)")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
