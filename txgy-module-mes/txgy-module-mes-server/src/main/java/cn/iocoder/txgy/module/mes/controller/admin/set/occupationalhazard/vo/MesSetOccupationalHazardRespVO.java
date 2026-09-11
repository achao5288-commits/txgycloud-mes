package cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-职业危害检测 Response VO")
@Data
public class MesSetOccupationalHazardRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "记录编号 OH-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "关联人员编号(个体暴露监测时；岗位检测可空)")
    private Long empId;

    @Schema(description = "因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL")
    private String factorCategory;

    @Schema(description = "具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT")
    private String factorCode;

    @Schema(description = "检测岗位/工作场所")
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
    private LocalDateTime inspectTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
