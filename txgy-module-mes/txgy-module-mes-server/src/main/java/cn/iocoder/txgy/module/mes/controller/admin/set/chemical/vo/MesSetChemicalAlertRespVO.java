package cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo;

import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 危化品预警汇总。
 *
 * 三条都是**能从档案表直接算出来的**：超量、MSDS 缺失、MSDS 版本将到期。
 * 刻意**不含**"物料即将过期"——批次到期冻结已经在库存投影侧做了（符合设计文档 §5.3 复用
 * mes_wm_batch.expireDate 的口径），这里再列一遍等于两处判同一件事，早晚不一致。
 *
 * 之所以直接带 DO 而不是再包一层 VO：这是一个只读汇总视图，字段名即前端所需，包一层是纯噪音。
 */
@Schema(description = "管理后台 - 危化品预警汇总 Response VO")
@Data
public class MesSetChemicalAlertRespVO {

    @Schema(description = "超储量上限（当前存量 > 储量上限）")
    private List<MesSetChemicalProfileDO> overQuota;

    @Schema(description = "未挂载 MSDS（已启用状态下仍缺 MSDS）")
    private List<MesSetChemicalProfileDO> msdsMissing;

    @Schema(description = "MSDS 版本将到期或已到期")
    private List<MesSetChemicalProfileDO> msdsExpiring;

}
