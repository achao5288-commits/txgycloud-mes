package cn.iocoder.txgy.module.mes.controller.admin.set.envboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MES 安全环保检测-环保看板 Response VO（设计文档 §12 三期：许可余量/设施运行/暂存倒计时/联单状态/应急物资）。
 *
 * <p>看板是**只读聚合**：不产生任何业务单据、不推进任何状态。它存在的意义是把
 * 「今天该有人去处理哪件事」摊在一屏上，所以每一块都给出**待办条数**（warnCount / overdueCount /
 * alertCount …）而不只是总量——总量是给汇报看的，待办数才是给干活的人看的。
 *
 * <p>判定口径全部复用既有服务（{@code checkAnnualQuota} / {@code checkPermitValidity} /
 * {@code getAlerts}），本看板**不自带一套阈值**，免得看板说绿灯、单据那边说红灯。
 *
 * @author OPENLAB BS
 */
@Schema(description = "管理后台 - 环保看板 Response VO")
@Data
public class MesSetEnvBoardRespVO {

    @Schema(description = "汇总生成时间")
    private LocalDateTime generatedAt;

    @Schema(description = "许可余量")
    private QuotaVO quota;

    @Schema(description = "治污设施运行")
    private FacilityVO facility;

    @Schema(description = "危废暂存倒计时")
    private StorageVO storage;

    @Schema(description = "危废联单状态")
    private ManifestVO manifest;

    @Schema(description = "应急物资")
    private MaterialVO material;

    // ==================== 许可余量 ====================

    @Schema(description = "许可余量：年总量水位 + 证载有效期")
    @Data
    public static class QuotaVO {

        @Schema(description = "证载有效期水位（届满前 60 天 EXPIRING、已过期 OVERDUE）")
        private List<PermitItem> permits;

        @Schema(description = "各排放口 × 污染物的自然年累计水位")
        private List<QuotaItem> items;

        @Schema(description = "待办：黄警（≥80%）条数")
        private Integer warnCount;

        @Schema(description = "待办：红线（≥100%）条数")
        private Integer redCount;
    }

    @Schema(description = "证载有效期")
    @Data
    public static class PermitItem {
        private String permitNo;
        private String enterpriseName;
        private LocalDate endDate;
        private Long daysLeft;
        /** NORMAL / EXPIRING / OVERDUE */
        private String level;
    }

    @Schema(description = "年总量水位")
    @Data
    public static class QuotaItem {
        private Long outletId;
        private String outletCode;
        private String pollutantCode;
        /** 许可年总量(t)；该口未配置时为 null——**没配不等于没超**，前端据此显示「未配置」而非绿灯 */
        private BigDecimal annualLimit;
        private BigDecimal used;
        private BigDecimal ratio;
        /** NORMAL / WARN / RED */
        private String level;
    }

    // ==================== 设施运行 ====================

    @Schema(description = "治污设施运行")
    @Data
    public static class FacilityVO {
        private Integer total;
        private Integer running;
        private Integer stopped;
        @Schema(description = "已申报停运（含已批准与待批复）")
        private Integer shutdownDeclared;
        @Schema(description = "换炭到期/逾期清单")
        private List<FacilityItem> dueReplace;
        @Schema(description = "未批先停清单——停运未走申报即违规")
        private List<FacilityItem> stoppedWithoutApproval;
    }

    @Schema(description = "设施条目")
    @Data
    public static class FacilityItem {
        private Long id;
        private String facilityNo;
        private String facilityName;
        private String runStatus;
        private String shutdownStatus;
        private LocalDate nextReplaceDate;
        /** 距应换日的天数，负数为已逾期 */
        private Long daysToReplace;
    }

    // ==================== 暂存倒计时 ====================

    @Schema(description = "危废暂存倒计时")
    @Data
    public static class StorageVO {

        @Schema(description = "本看板采用的贮存期限（天）。GB18597 贮存一般不超过一年；"
                + "它是**本看板的默认口径**，不是从配置读的——阈值配置页落地后应改为字典项。")
        private Integer limitDays;

        @Schema(description = "待办：已超过贮存期限")
        private Integer overdueCount;

        @Schema(description = "待办：30 天内到期")
        private Integer soonCount;

        @Schema(description = "在库暂存行，按剩余天数升序（最急的排最前）")
        private List<StorageItem> items;
    }

    @Schema(description = "暂存行")
    @Data
    public static class StorageItem {
        private Long id;
        private String sourceRecordNo;
        private String itemName;
        private BigDecimal weight;
        private String location;
        /** 进入暂存的时间（台账 status_time，即落 STORED 那一刻） */
        private LocalDateTime storedAt;
        private Long daysStored;
        /** 负数＝已超期 */
        private Long daysLeft;
        /** OVERDUE / SOON / NORMAL */
        private String level;
    }

    // ==================== 联单状态 ====================

    @Schema(description = "危废联单状态")
    @Data
    public static class ManifestVO {
        private Integer total;
        @Schema(description = "各状态条数：DRAFT/DECLARED/EFFECTIVE/TRANSFERRED/CLOSED")
        private Map<String, Integer> countByStatus;
        @Schema(description = "待办：已申报未生效且申报时限临近（含已逾期），按时限升序")
        private List<ManifestItem> dueSoon;
    }

    @Schema(description = "联单条目")
    @Data
    public static class ManifestItem {
        private Long id;
        private String manifestNo;
        private String wasteName;
        private BigDecimal quantity;
        private String status;
        private LocalDateTime declareDeadline;
        /** 距申报时限的小时数，负数为已逾期 */
        private Long hoursLeft;
    }

    // ==================== 应急物资 ====================

    @Schema(description = "应急物资")
    @Data
    public static class MaterialVO {
        @Schema(description = "待办：非正常状态的物资条数（缺货/过期/待检）")
        private Integer alertCount;
        @Schema(description = "待办清单，已按紧急度排序（缺货 > 过期 > 待检）")
        private List<MaterialItem> alerts;
    }

    @Schema(description = "物资条目")
    @Data
    public static class MaterialItem {
        private Long id;
        private String materialNo;
        private String materialName;
        private BigDecimal quantity;
        private String unit;
        private LocalDate expireDate;
        private String status;
    }

}
