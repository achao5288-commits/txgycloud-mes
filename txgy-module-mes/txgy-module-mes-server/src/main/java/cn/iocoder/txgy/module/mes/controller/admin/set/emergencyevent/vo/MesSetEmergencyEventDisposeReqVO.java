package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * MES 安全环保检测-应急事件处置 Request VO
 *
 * 设计文档 §八.4：处置拍照 → **应急废物一律过秤贴签入危废台账（应急不绕过管控）**。
 * 所以这里的 `wastes` 不是"填个数量"，而是逐桶登记：一桶一行，
 * 服务端据此各写一条称重记录 + 一条危废台账行，三者同事务。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 应急事件处置 Request VO")
@Data
public class MesSetEmergencyEventDisposeReqVO {

    @Schema(description = "事件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "事件编号不能为空")
    private Long id;

    @Schema(description = "处置说明", example = "干沙围堵后收集，未用水冲")
    private String disposeNote;

    @Schema(description = "处置照片地址")
    private String disposePhotoUrl;

    @Schema(description = "应急废物逐桶明细。**可以为空**（本次确未产生废物），"
            + "但只要有废物就必须逐桶给全，服务端不接受手填的合计数量")
    @Valid
    private List<Waste> wastes;

    @Schema(description = "应急废物单桶明细")
    @Data
    public static class Waste {

        @Schema(description = "危废桶码", requiredMode = Schema.RequiredMode.REQUIRED, example = "BUCKET-20260910-01")
        private String containerCode;

        @Schema(description = "危废类别代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "HW49")
        private String wasteCode;

        @Schema(description = "危废名称", example = "泄漏吸附物")
        private String wasteName;

        @Schema(description = "过秤净重 kg（必须大于 0）", requiredMode = Schema.RequiredMode.REQUIRED, example = "12.500")
        private BigDecimal netWeight;

        @Schema(description = "入库暂存库位", example = "危废暂存间 A")
        private String storageLocation;

    }

}
