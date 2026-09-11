package cn.iocoder.txgy.module.mes.controller.admin.set.pollutionledger.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - MES 安全环保检测-污染/危废暂存台账 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesPollutionLedgerPageReqVO extends PageParam {

    @Schema(description = "批次号", example = "B20260903-001")
    private String batchNo;

    @Schema(description = "去向/库位", example = "危废暂存间B")
    private String location;

    @Schema(description = "台账状态：STORED/PROCESSING/REUSED/DISCHARGED/DISPOSED", example = "STORED")
    private String status;

    @Schema(description = "来源判定记录编号(模糊)", example = "PC-")
    private String sourceRecordNo;

    @Schema(description = "关联单号(模糊)")
    private String bizNo;

    @Schema(description = "物料/产品名称(模糊)")
    private String itemName;

    @Schema(description = "是否标记（被标记品清单：true=仅看已标记，false=仅看未标记，空=全部）", example = "true")
    private Boolean marked;

}
