package cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-消防检查记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetFireCheckPageReqVO extends PageParam {

    @Schema(description = "记录编号")
    private String recordNo;

    @Schema(description = "区域/位置")
    private String location;

    @Schema(description = "设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)")
    private String facilityName;

    @Schema(description = "结果：PASS/FAIL")
    private String result;

}
