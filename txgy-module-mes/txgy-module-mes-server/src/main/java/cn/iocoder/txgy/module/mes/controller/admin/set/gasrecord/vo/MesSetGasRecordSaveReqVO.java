package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-气体检测记录 新增/修改 Request VO")
@Data
public class MesSetGasRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    @NotEmpty(message = "记录编号不能为空")
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
    @NotEmpty(message = "气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2不能为空")
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
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片URL(逗号分隔)")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
