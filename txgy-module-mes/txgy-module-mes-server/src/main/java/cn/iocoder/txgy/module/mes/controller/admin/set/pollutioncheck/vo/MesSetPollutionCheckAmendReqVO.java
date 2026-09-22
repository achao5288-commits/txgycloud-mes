package cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发起变更 Request VO。
 *
 * 收口（已复核）后的判定内容不可就地修改，改动一律走变更单：新开一行判定承接改动，原单被标记为
 * 「已被替代」并从此只读。字段与原单同构，故直接继承 SaveReqVO —— 变更单要改的就是这些内容字段。
 *
 * 新单的编号、origin_record_no、签字都不从请求里取：编号由服务端生成，origin 由 originId 反查，
 * 签名的 biz_no 必须等于服务端生成的新单号（信前端传的单号会出现签字挂到别人单子上的情况）。
 */
@Schema(description = "管理后台 - MES 安全环保检测-污染判定记录 发起变更 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MesSetPollutionCheckAmendReqVO extends MesSetPollutionCheckSaveReqVO {

    @Schema(description = "被变更的原单编号（必须是已复核、且尚未被别的变更单替代的判定）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "被变更的原单编号不能为空")
    private Long originId;

    @Schema(description = "变更事由（必填，写清为什么要改：原判定哪里错了、依据什么改）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "现场复采后检出含铅，原判无污染有误")
    @NotEmpty(message = "变更事由不能为空")
    private String amendReason;

}
