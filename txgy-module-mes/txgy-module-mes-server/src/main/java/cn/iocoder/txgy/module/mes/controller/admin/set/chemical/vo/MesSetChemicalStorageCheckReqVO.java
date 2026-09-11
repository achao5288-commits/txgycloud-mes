package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 危化品库位/入库校验 Request VO")
@Data
public class MesSetChemicalStorageCheckReqVO {

    @Schema(description = "危化品档案编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "危化品档案编号不能为空")
    private String profileNo;

    @Schema(description = "拟存放库位（不传则用档案上登记的库位）")
    private String storageLocation;

    @Schema(description = "本次入库量（不传则只校验 MSDS/专区/禁配，不判储量上限）")
    private BigDecimal quantity;

}
