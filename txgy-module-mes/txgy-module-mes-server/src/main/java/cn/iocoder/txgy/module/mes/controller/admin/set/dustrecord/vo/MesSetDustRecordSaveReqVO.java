package cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-粉尘检测记录 新增/修改 Request VO")
@Data
public class MesSetDustRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号 DUST-YYYYMMDD-NNN")
    @NotEmpty(message = "记录编号 DUST-YYYYMMDD-NNN不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联工单编号")
    private Long woId;

    @Schema(description = "关联工序编号")
    private Long operationId;

    @Schema(description = "关联设备编号(除尘/产尘设备)")
    private Long deviceId;

    @Schema(description = "检测位置/作业区域")
    private String location;

    @Schema(description = "检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2")
    @NotEmpty(message = "检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2不能为空")
    private String dustType;

    @Schema(description = "检测浓度/含量")
    private BigDecimal concentration;

    @Schema(description = "游离SiO2含量%(总尘且需矽尘分级时)")
    private BigDecimal sio2Content;

    @Schema(description = "单位 mg/m3/%")
    private String unit;

    @Schema(description = "限值")
    private BigDecimal limitValue;

    @Schema(description = "引用国标")
    private String refStandard;

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
