package cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-称重记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetWeighRecordPageReqVO extends PageParam {

    @Schema(description = "称重类型：PRODUCE/FACTORY", example = "FACTORY")
    private String weighType;

    @Schema(description = "业务关联类型：CHECK/LEDGER", example = "LEDGER")
    private String bizType;

    @Schema(description = "业务关联单号", example = "PC-20260907101530001")
    private String bizNo;

}
