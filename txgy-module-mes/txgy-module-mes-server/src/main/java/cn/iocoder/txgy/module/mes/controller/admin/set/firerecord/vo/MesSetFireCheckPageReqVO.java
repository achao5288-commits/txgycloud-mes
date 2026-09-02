package cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 安全环保检测-消防设施检测记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesSetFireCheckPageReqVO extends PageParam {

    @Schema(description = "区域/位置", example = "车间A")
    private String location;

    @Schema(description = "设施名称", example = "灭火器")
    private String facilityName;

    @Schema(description = "结果：PASS/FAIL", example = "PASS")
    private String result;

    @Schema(description = "检测时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] checkTime;

}
