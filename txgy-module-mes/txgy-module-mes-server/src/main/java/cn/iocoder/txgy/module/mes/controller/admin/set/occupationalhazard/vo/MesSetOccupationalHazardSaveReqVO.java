package cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-职业危害检测 新增/修改 Request VO")
@Data
public class MesSetOccupationalHazardSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号 OH-YYYYMMDD-NNN")
    @NotEmpty(message = "记录编号 OH-YYYYMMDD-NNN不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联人员编号(个体暴露监测时；岗位检测可空)")
    private Long empId;

    @Schema(description = "因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL")
    @NotEmpty(message = "因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL不能为空")
    private String factorCategory;

    @Schema(description = "具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT")
    @NotEmpty(message = "具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT不能为空")
    private String factorCode;

    @Schema(description = "检测岗位/工作场所")
    @NotEmpty(message = "检测岗位/工作场所不能为空")
    private String workplace;

    @Schema(description = "数据来源：REUSE/ORIGINAL")
    private String sourceRefType;

    @Schema(description = "来源安全检测记录id(复用气体/噪声/粉尘)")
    private Long sourceRecordId;

    @Schema(description = "实测浓度/强度")
    private BigDecimal measuredValue;

    @Schema(description = "单位 mg/m3/dB(A)/mSv/C/m-s2等")
    private String unit;

    @Schema(description = "接触限值类型：MAC/PC-TWA/PC-STEL")
    private String limitType;

    @Schema(description = "职业接触限值")
    private BigDecimal oelValue;

    @Schema(description = "引用国标")
    private String refStandard;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测时间")
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

}
