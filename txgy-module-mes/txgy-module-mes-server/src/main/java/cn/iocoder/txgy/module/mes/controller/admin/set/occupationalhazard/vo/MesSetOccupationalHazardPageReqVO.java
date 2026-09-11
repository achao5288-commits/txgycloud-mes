package cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-职业危害检测 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetOccupationalHazardPageReqVO extends PageParam {

    @Schema(description = "记录编号 OH-YYYYMMDD-NNN")
    private String recordNo;

    @Schema(description = "因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL")
    private String factorCategory;

    @Schema(description = "具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT")
    private String factorCode;

    @Schema(description = "检测岗位/工作场所")
    private String workplace;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
