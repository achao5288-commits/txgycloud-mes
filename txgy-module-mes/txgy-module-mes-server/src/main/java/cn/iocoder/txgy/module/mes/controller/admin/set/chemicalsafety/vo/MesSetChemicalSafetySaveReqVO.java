package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-危化品安全检查 新增/修改 Request VO")
@Data
public class MesSetChemicalSafetySaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    @NotEmpty(message = "记录编号不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "危化品编码")
    private String chemicalCode;

    @Schema(description = "危化品名称")
    @NotEmpty(message = "危化品名称不能为空")
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
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

}
