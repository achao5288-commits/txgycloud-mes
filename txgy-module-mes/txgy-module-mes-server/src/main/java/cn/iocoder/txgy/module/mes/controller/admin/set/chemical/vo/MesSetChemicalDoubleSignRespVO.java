package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 危化品五双双人签字状态 Response VO")
@Data
public class MesSetChemicalDoubleSignRespVO {

    @Schema(description = "作业单号")
    private String bizNo;

    @Schema(description = "作业类型")
    private String signAction;

    @Schema(description = "已签账号（去重）")
    private List<String> signers;

    @Schema(description = "已签次数")
    private Integer signCount;

    @Schema(description = "是否已满足双人制（两个不同账号各签一次）")
    private Boolean complete;

    @Schema(description = "最近一次签字时间")
    private LocalDateTime lastSignTime;

}
