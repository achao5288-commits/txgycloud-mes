package cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-劳保用品检查 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPpeCheckPageReqVO extends PageParam {

    @Schema(description = "记录编号 PPE-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "PPE类别：HELMET/GOGGLES/RESPIRATOR/ANTISTATIC_CLOTHING/EARPLUGS/GLOVES/SAFETY_SHOES等")
    private String ppeType;

    @Schema(description = "检查方式：AI_VISION/MANUAL")
    private String checkMode;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
