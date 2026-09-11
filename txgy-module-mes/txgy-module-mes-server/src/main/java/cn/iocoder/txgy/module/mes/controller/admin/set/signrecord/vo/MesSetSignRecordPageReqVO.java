package cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-签字记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetSignRecordPageReqVO extends PageParam {

    @Schema(description = "业务关联类型：CHECK/LEDGER", example = "CHECK")
    private String bizType;

    @Schema(description = "业务关联单号", example = "PC-20260907101530001")
    private String bizNo;

    @Schema(description = "签字人(模糊)", example = "admin")
    private String signUser;

}
