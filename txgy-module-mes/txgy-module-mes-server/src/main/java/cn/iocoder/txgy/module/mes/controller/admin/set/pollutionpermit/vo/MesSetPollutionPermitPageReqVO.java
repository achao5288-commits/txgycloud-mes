package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-排污许可证 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPollutionPermitPageReqVO extends PageParam {

    @Schema(description = "排污许可证编号(模糊)", example = "P-110108")
    private String permitNo;

    @Schema(description = "持证单位名称(模糊)", example = "华瀚")
    private String enterpriseName;

    @Schema(description = "状态：ACTIVE/EXPIRED/REVOKED", example = "ACTIVE")
    private String status;

}
