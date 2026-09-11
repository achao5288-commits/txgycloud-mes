package cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-排放合规流水 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetDischargeRecordPageReqVO extends PageParam {

    @Schema(description = "来源判定记录号", example = "PC-20260907101620002")
    private String sourceRecordNo;

    @Schema(description = "环节", example = "WASTE_INTERMEDIATE")
    private String stage;

    @Schema(description = "批次号", example = "")
    private String batchNo;

    @Schema(description = "物料/产品名称(模糊)", example = "废水")
    private String itemName;

    @Schema(description = "排放去向(模糊)", example = "污水处理站")
    private String destination;

}
