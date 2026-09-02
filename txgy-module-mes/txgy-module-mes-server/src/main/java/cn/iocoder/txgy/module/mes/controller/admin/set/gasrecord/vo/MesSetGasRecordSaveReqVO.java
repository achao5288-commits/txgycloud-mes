package cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 作业环境气体检测记录 新增/修改 Request VO")
@Data
public class MesSetGasRecordSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "GAS-20260901-001")
    @NotEmpty(message = "记录编号不能为空")
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

    @Schema(description = "气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2", requiredMode = Schema.RequiredMode.REQUIRED, example = "CO")
    @NotEmpty(message = "气体类型不能为空")
    private String gasType;

    @Schema(description = "检测浓度值", example = "12.5")
    private BigDecimal concentration;

    @Schema(description = "单位：mg/m3 / % / %LEL", example = "mg/m3")
    private String unit;

    @Schema(description = "限值", example = "20")
    private BigDecimal limitValue;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "采集方式：IOT_AUTO/MANUAL", example = "MANUAL")
    private String collectionMode;

    @Schema(description = "检测仪器编号", example = "INS-001")
    private String instrumentNo;

    @Schema(description = "检测人", example = "张三")
    private String inspector;

    @Schema(description = "检测时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "检测照片 URL（逗号分隔）", example = "/prod-api/1.png,/prod-api/2.png")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
