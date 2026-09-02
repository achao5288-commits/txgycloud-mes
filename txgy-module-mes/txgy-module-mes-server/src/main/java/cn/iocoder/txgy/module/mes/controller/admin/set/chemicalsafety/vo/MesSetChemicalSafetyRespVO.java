package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危化品安全巡查记录 Response VO")
@Data
public class MesSetChemicalSafetyRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "关联检测计划编号", example = "1")
    private Long planId;

    @Schema(description = "危化品代码（CAS号等）")
    private String chemicalCode;

    @Schema(description = "危化品名称")
    private String chemicalName;

    @Schema(description = "储存地点")
    private String storageLocation;

    @Schema(description = "标识标签齐全：1-是 0-否", example = "1")
    private Integer labelOk;

    @Schema(description = "MSDS（安全技术说明书）齐全：1-是 0-否", example = "1")
    private Integer msdsOk;

    @Schema(description = "储存条件符合要求：1-是 0-否", example = "1")
    private Integer storageOk;

    @Schema(description = "分类存放/隔离存放合规：1-是 0-否", example = "1")
    private Integer separationOk;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "异常/不合格描述")
    private String problemDesc;

    @Schema(description = "巡查人")
    private String inspector;

    @Schema(description = "巡查时间")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
