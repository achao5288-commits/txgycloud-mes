package cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MES 安全环保检测-应急演练年度覆盖 RespVO
 *
 * 设计文档 §八.2「每年至少 1 次演练」的判定结果。判定放服务端：
 * 看板上要显示、执行报告要引用，同一句话别在两处各算一遍。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 应急演练年度覆盖 Response VO")
@Data
public class MesSetEmergencyDrillCoverageRespVO {

    @Schema(description = "年份", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026")
    private Integer year;

    @Schema(description = "该年已闭环的演练次数（已计划/已演练但未闭环的不算数）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer closedCount;

    @Schema(description = "该年演练总条数（含未闭环）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer totalCount;

    @Schema(description = "是否满足「每年至少 1 次」", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean satisfied;

    @Schema(description = "该年演练明细")
    private List<MesSetEmergencyDrillRespVO> drills;

}
