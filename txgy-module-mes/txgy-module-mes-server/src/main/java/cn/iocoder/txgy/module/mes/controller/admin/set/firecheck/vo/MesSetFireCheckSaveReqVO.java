package cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-消防检查记录 新增/修改 Request VO")
@Data
public class MesSetFireCheckSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "记录编号")
    @NotEmpty(message = "记录编号不能为空")
    private String recordNo;

    @Schema(description = "关联检测计划编号")
    private Long planId;

    @Schema(description = "区域/位置")
    private String location;

    @Schema(description = "设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)")
    @NotEmpty(message = "设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)不能为空")
    private String facilityName;

    @Schema(description = "设施编号(资产编号)")
    private String facilityCode;

    @Schema(description = "检测时间")
    @NotNull(message = "检测时间不能为空")
    private LocalDateTime checkTime;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

    @Schema(description = "异常/不合格描述")
    private String problemDesc;

    @Schema(description = "检测人")
    private String inspector;

    @Schema(description = "检测照片URL(逗号分隔)")
    private String photoUrls;

    @Schema(description = "备注")
    private String remark;

}
