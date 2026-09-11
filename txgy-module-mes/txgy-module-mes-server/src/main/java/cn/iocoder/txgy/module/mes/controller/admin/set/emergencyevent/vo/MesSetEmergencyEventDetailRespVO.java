package cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo;

import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyWasteDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MES 安全环保检测-应急事件详情 RespVO
 *
 * 事件 + 其应急废物逐桶明细（桶码 ↔ 称重记录 ↔ 危废台账行）。用组合而不是继承：
 * 事件本体就是列表页那个 RespVO，前端不必学第二套字段名。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 应急事件详情 Response VO")
@Data
public class MesSetEmergencyEventDetailRespVO {

    @Schema(description = "事件本体", requiredMode = Schema.RequiredMode.REQUIRED)
    private MesSetEmergencyEventRespVO event;

    @Schema(description = "应急废物逐桶明细（未处置时为空数组）")
    private List<MesSetEmergencyWasteDO> wastes;

}
