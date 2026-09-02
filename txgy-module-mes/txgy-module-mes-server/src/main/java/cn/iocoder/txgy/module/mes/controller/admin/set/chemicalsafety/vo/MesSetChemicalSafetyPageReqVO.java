package cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 安全环保检测-危化品安全巡查记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetChemicalSafetyPageReqVO extends PageParam {

    @Schema(description = "危化品代码（CAS号等）", example = "CAS-71-43-2")
    private String chemicalCode;

    @Schema(description = "危化品名称", example = "苯")
    private String chemicalName;

    @Schema(description = "储存地点", example = "危化品库A")
    private String storageLocation;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "巡查时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inspectTime;

}
