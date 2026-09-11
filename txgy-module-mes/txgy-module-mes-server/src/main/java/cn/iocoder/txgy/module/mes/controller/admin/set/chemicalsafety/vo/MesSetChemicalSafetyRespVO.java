package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危化品安全检查 Response VO")
@Data
public class MesSetChemicalSafetyRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "危化品编码")
    private String chemicalCode;

    @Schema(description = "危化品名称")
    private String chemicalName;

    @Schema(description = "存储地点")
    private String storageLocation;

    @Schema(description = "标识完整性：1是/0否")
    private Boolean labelOk;

    @Schema(description = "MSDS有效性：1是/0否")
    private Boolean msdsOk;

    @Schema(description = "储存条件(温湿度/通风)合格：1是/0否")
    private Boolean storageOk;

    @Schema(description = "禁忌物分离合格：1是/0否")
    private Boolean separationOk;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "异常/不合格描述")
    private String problemDesc;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测时间")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
