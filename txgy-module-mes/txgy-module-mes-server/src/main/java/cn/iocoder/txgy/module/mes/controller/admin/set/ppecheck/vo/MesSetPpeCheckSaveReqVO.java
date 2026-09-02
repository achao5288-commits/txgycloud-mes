package cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-PPE防护检查记录 新增/修改 Request VO")
@Data
public class MesSetPpeCheckSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "记录编号不能为空")
    private String recordNo;

    @Schema(description = "关联人员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联人员编号不能为空")
    private Long empId;

    @Schema(description = "关联工单编号", example = "1")
    private Long woId;

    @Schema(description = "关联工序编号", example = "1")
    private Long operationId;

    @Schema(description = "PPE类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "PPE类别不能为空")
    private String ppeType;

    @Schema(description = "检查方式：AI_VISION/MANUAL")
    private String checkMode;

    @Schema(description = "佩戴完整性：1是/0否")
    private Integer wearingOk;

    @Schema(description = "防护等级匹配性：1是/0否")
    private Integer gradeMatchOk;

    @Schema(description = "有效期/损坏检查：1是/0否")
    private Integer validOk;

    @Schema(description = "PPE到期日期")
    private LocalDate expiryDate;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "是否阻断开工：1是/0否")
    private Integer blockFlag;

    @Schema(description = "AI识别设备编号")
    private String deviceNo;

    @Schema(description = "检查人(人工检查时)")
    private String checkerName;

    @Schema(description = "检查时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "检查时间不能为空")
    private LocalDateTime checkTime;

    @Schema(description = "检查照片/AI抓拍URL（逗号分隔）")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
