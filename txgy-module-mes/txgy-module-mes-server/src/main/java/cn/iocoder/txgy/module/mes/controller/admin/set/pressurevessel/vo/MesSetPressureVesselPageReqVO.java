package cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-压力容器检查 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetPressureVesselPageReqVO extends PageParam {

    @Schema(description = "检验记录/报告编号 PV-YYYY-NNN")
    private String recordNo;

    @Schema(description = "压力容器使用登记证号(特种设备注册代码)")
    private String vesselRegNo;

    @Schema(description = "无损检测方法：UT/RT/MT/PT(多选逗号分隔)")
    private String ndtMethods;

    @Schema(description = "综合结论 PASS/FAIL")
    private String result;

    @Schema(description = "检验机构(需资质)")
    private String inspectOrg;

}
