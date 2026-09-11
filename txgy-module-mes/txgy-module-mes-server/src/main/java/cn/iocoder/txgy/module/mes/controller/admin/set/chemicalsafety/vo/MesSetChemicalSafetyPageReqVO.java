package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-危化品安全检查 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetChemicalSafetyPageReqVO extends PageParam {

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "危化品编码")
    private String chemicalCode;

    @Schema(description = "危化品名称")
    private String chemicalName;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
