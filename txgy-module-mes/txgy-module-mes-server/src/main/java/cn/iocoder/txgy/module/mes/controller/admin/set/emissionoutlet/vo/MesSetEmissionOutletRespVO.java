package cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - MES 安全环保检测-排放口 Response VO")
@Data
public class MesSetEmissionOutletRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "排放口编号")
    private String outletCode;

    @Schema(description = "排放口名称")
    private String outletName;

    @Schema(description = "排放类型：GAS/WASTEWATER/NOISE")
    private String outletType;

    @Schema(description = "主要污染物列表(JSON/CSV文本)")
    private String pollutantCodes;

    @Schema(description = "位置描述")
    private String location;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "排气筒高度 m")
    private BigDecimal stackHeight;

    @Schema(description = "在线监测方式：CEMS/MANUAL/NONE")
    private String monitorMethod;

    @Schema(description = "关联排污许可证号")
    private String permitNo;

    @Schema(description = "许可排放限值JSON文本")
    private String permitLimits;

    @Schema(description = "是否重点/国控排放口：1是/0否")
    private Integer isKeyOutlet;

    @Schema(description = "状态：ACTIVE/INACTIVE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
