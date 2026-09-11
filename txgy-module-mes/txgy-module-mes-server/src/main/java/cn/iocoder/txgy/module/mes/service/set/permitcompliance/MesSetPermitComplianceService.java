package cn.iocoder.txgy.module.mes.service.set.permitcompliance;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 排污许可合规判定（设计文档 §六「按证排污」）
 *
 * 排污许可证是全厂环保数据的**基准源**：限值取自证、监测对着证、总量卡在证。
 * 本服务只做两件事，都建立在「证是唯一权威」这个前提上：
 *   1）监测数据入库时按该排放口的许可限值自动判定（limit_value/result 不接受人工矛盾值）；
 *   2）自然年累计排放量对着许可年总量算 80% 黄警 / 100% 红线。
 *
 * @author OPENLAB BS
 */
public interface MesSetPermitComplianceService {

    String RESULT_PASS = "PASS";
    String RESULT_FAIL = "FAIL";
    String LEVEL_NORMAL = "NORMAL";
    String LEVEL_WARN = "WARN";
    String LEVEL_RED = "RED";

    /**
     * 按排放口许可限值判定一次监测。
     *
     * @return 命中的判定结果；**返回 null 表示无法判定**（无排放口 / 该口未配置此污染物限值），
     *         调用方应保持原值不动——不能把「没配限值」当成「达标」。
     */
    /**
     * 证载有效期预警提前量：届满前 60 天进入 EXPIRING。
     */
    long EXPIRE_WARN_DAYS = 60;
    /**
     * 证载水位：正常
     */
    String VALIDITY_NORMAL = "NORMAL";
    /**
     * 证载水位：60 天内届满，需办延续
     */
    String VALIDITY_EXPIRING = "EXPIRING";
    /**
     * 证载水位：已届满（未延续即锁定作业）
     */
    String VALIDITY_OVERDUE = "OVERDUE";

    LimitVerdict judgeByOutletLimit(Long outletId, String pollutantCode, BigDecimal concentration);

    /**
     * 证载有效期水位：距有效期止不足 60 天 → EXPIRING，已过期 → OVERDUE。
     *
     * @param permitId 许可证编号
     * @param onDate   判定基准日，缺省取今天
     * @return 水位；许可证不存在返回 null
     */
    PermitValidity checkPermitValidity(Long permitId, LocalDate onDate);

    /**
     * 自然年累计排放量 vs 许可年总量。
     *
     * @param onDate 判定所属日期（取其自然年，次年 1/1 自动归零）
     * @return 累计口径的合规水位；该口未配置该污染物的年总量时 annualLimit 为 null、level=NORMAL
     */
    AnnualQuota checkAnnualQuota(Long outletId, String pollutantCode, LocalDate onDate);

    /**
     * 校验排放口的许可限值 JSON 配置。
     * 配置期失败快——监测入库时才发现配错，等于整段时间的监测都没在比对。
     */
    void validateOutletLimits(String permitLimits);

    /**
     * 校验排污许可的年排放量 JSON 配置（驱动 80% 预警 / 100% 报警）。
     */
    void validateAnnualLimits(String annualLimits);

    /**
     * @param limitValue 命中的许可限值
     * @param result     PASS / FAIL
     */
    record LimitVerdict(BigDecimal limitValue, String result) {
    }

    /**
     * @param annualLimit 许可年总量(t)；未配置为 null
     * @param used        自然年累计排放量(t)
     * @param ratio       used / annualLimit；未配置为 null
     * @param level       NORMAL / WARN(≥80%) / RED(≥100%)
     */
    record AnnualQuota(BigDecimal annualLimit, BigDecimal used, BigDecimal ratio, String level) {
    }

    /**
     * @param permitNo 证号
     * @param endDate  有效期止
     * @param daysLeft 距有效期止的天数（负数＝已过期，0＝今天到期）
     * @param level    NORMAL / EXPIRING(≤60 天) / OVERDUE
     */
    record PermitValidity(String permitNo, LocalDate endDate, long daysLeft, String level) {
    }

}
