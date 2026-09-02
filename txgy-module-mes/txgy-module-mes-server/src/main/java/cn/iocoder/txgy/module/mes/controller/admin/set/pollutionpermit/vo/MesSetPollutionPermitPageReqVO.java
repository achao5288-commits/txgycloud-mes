package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-排污许可管理 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPollutionPermitPageReqVO extends PageParam {

    @Schema(description = "排污许可证编号", example = "91450100-2025-001")
    private String permitNo;

    @Schema(description = "状态：VALID(有效)/EXPIRING(即将到期)/EXPIRED(已失效)/REVOKED(已注销)", example = "VALID")
    private String status;

}
