package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - MES 安全环保检测-危化品档案 创建/修改 Request VO")
@Data
public class MesSetChemicalProfileSaveReqVO {

    @Schema(description = "编号（修改时必填）")
    private Long id;

    @Schema(description = "档案编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "档案编号不能为空")
    private String profileNo;

    @Schema(description = "危化品代码")
    private String chemicalCode;

    @Schema(description = "危化品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "危化品名称不能为空")
    private String chemicalName;

    @Schema(description = "CAS 号")
    private String casNo;

    @Schema(description = "关联物料 id。绑定后才在在库侧（受控库位调整）参与禁配/专区校验；"
            + "不绑定的物料不当危化品管——CAS 号与物料编码对不上，只能靠这条显式绑定")
    private Long itemId;

    @Schema(description = "相容组（禁配判定用）")
    private String compatGroup;

    @Schema(description = "禁配物清单（人读的说明文本，如「水、醇类、胺类」）——"
            + "泄漏处置卡要原文带出现场，故这里必须能填进来")
    private String incompatibleGroups;

    @Schema(description = "危险类别")
    private String hazardClass;

    @Schema(description = "储存专区：GENERAL/EXPLOSION_PROOF/ISOLATION/SPECIAL")
    private String storageZone;

    @Schema(description = "具体库位描述")
    private String storageLocation;

    @Schema(description = "储量上限")
    private BigDecimal storageLimit;

    @Schema(description = "储量单位")
    private String storageUnit;

    @Schema(description = "是否必须存放于防爆区")
    private Boolean explosionProof;

    @Schema(description = "MSDS 文件 URL")
    private String msdsUrl;

    @Schema(description = "MSDS 版本有效期")
    private LocalDate msdsExpireDate;

    @Schema(description = "是否纳入效期管理")
    private Boolean expireManage;

    @Schema(description = "保质期天数")
    private Integer shelfLifeDays;

    @Schema(description = "应急措施")
    private String emergencyMeasure;

    @Schema(description = "状态：ENABLED/DISABLED")
    private String status;

    @Schema(description = "备注")
    private String remark;

}
