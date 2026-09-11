package cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * HJ 1276-2022《危险废物识别标志设置技术规范》标签字段集。
 *
 * 后端只出「合规字段 + 二维码内容」，标签样式由前端按国标模板渲染/打印；
 * 电子留档走 POST /hazwaste/label/archive 存文件服务。
 */
@Schema(description = "管理后台 - 危废标签(HJ1276) Response VO")
@Data
@Builder
public class MesSetHazwasteLabelRespVO {

    @Schema(description = "标签标题（固定）")
    private String title;

    @Schema(description = "危险废物名称")
    private String wasteName;

    @Schema(description = "废物类别（HW 码）")
    private String wasteCode;

    @Schema(description = "危险特性（如 毒性/易燃性/腐蚀性）")
    private String hazardTraits;

    @Schema(description = "产生单位")
    private String generateUnit;

    @Schema(description = "批次/容器码（一桶一码）")
    private String containerCode;

    @Schema(description = "重量")
    private BigDecimal quantity;

    @Schema(description = "重量单位")
    private String quantityUnit;

    @Schema(description = "产生/贴签日期")
    private String labelDate;

    @Schema(description = "二维码内容（扫此码即查该桶全程）")
    private String qrContent;

    @Schema(description = "已归档的标签文件 URL（未归档为空）")
    private String labelUrl;

}
