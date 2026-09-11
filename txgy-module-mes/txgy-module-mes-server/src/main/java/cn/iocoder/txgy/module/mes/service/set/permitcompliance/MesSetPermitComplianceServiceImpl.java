package cn.iocoder.txgy.module.mes.service.set.permitcompliance;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.util.json.JsonUtils;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas.MesSetExhaustGasMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionpermit.MesSetPollutionPermitMapper;
import cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater.MesSetWastewaterMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PERMIT_ANNUAL_LIMITS_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PERMIT_OUTLET_LIMITS_INVALID;

/**
 * 排污许可合规判定实现（设计文档 §六）
 *
 * 配置格式（与库中既有 annual_limits 同族命名，见 mes_set_pollution_permit id=4 的演示行）：
 * <pre>
 * emission_outlet.permit_limits  [{"pollutantCode":"VOCs","pollutantName":"非甲烷总烃","limitValue":60,"unit":"mg/m3"}]
 * pollution_permit.annual_limits [{"pollutantCode":"VOCs","pollutantName":"挥发性有机物","annualLimitT":12,"annualUsedT":0}]
 * </pre>
 *
 * @author OPENLAB BS
 */
@Slf4j
@Service
public class MesSetPermitComplianceServiceImpl implements MesSetPermitComplianceService {

    /**
     * 80% 黄警红线（设计文档 §六-2：年累计达许可总量 80% 黄警、100% 红线锁定新增作业）
     */
    private static final BigDecimal WARN_RATIO = new BigDecimal("0.8");

    /**
     * 单位换算：监测的 emission_amount 以 kg 计，许可年总量 annualLimitT 以 t 计。
     * ⚠️ 该换算基于「排放量按 kg 记」这一约定（库中 emission_amount 无独立单位列）。
     *    若现场口径是 g 或 t，改这里一处即可——判定与红线都走这个系数。
     */
    private static final BigDecimal KG_PER_TON = new BigDecimal("1000");

    @Resource
    private MesSetEmissionOutletMapper outletMapper;

    @Resource
    private MesSetPollutionPermitMapper permitMapper;

    @Resource
    private MesSetExhaustGasMapper exhaustGasMapper;

    @Resource
    private MesSetWastewaterMapper wastewaterMapper;

    @Override
    public LimitVerdict judgeByOutletLimit(Long outletId, String pollutantCode, BigDecimal concentration) {
        if (outletId == null || StrUtil.isBlank(pollutantCode) || concentration == null) {
            return null;
        }
        MesSetEmissionOutletDO outlet = outletMapper.selectById(outletId);
        if (outlet == null || StrUtil.isBlank(outlet.getPermitLimits())) {
            return null;
        }
        BigDecimal limit = limitOf(outlet.getPermitLimits(), pollutantCode);
        if (limit == null) {
            return null;
        }
        // 浓度 > 限值即超标。"等于限值"算达标——限值是排放上限，取到边界不算超。
        String result = concentration.compareTo(limit) > 0 ? RESULT_FAIL : RESULT_PASS;
        return new LimitVerdict(limit, result);
    }

    @Override
    public AnnualQuota checkAnnualQuota(Long outletId, String pollutantCode, LocalDate onDate) {
        BigDecimal used = annualUsed(outletId, pollutantCode, onDate);
        BigDecimal limit = annualLimitOf(outletId, pollutantCode);
        if (limit == null) {
            // 没配年总量就只报累计量，不虚报水位——"没配"不等于"红外"也不等于"安全"
            return new AnnualQuota(null, used, null, LEVEL_NORMAL);
        }
        BigDecimal ratio = used.divide(limit, 4, RoundingMode.HALF_UP);
        String level = ratio.compareTo(BigDecimal.ONE) >= 0 ? LEVEL_RED
                : ratio.compareTo(WARN_RATIO) >= 0 ? LEVEL_WARN : LEVEL_NORMAL;
        return new AnnualQuota(limit, used, ratio, level);
    }

    @Override
    public PermitValidity checkPermitValidity(Long permitId, LocalDate onDate) {
        if (permitId == null) {
            return null;
        }
        MesSetPollutionPermitDO permit = permitMapper.selectById(permitId);
        if (permit == null) {
            return null;
        }
        LocalDate today = onDate != null ? onDate : LocalDate.now();
        LocalDate end = permit.getEndDate();
        if (end == null) {
            // 没登有效期就当作已逾期：证载期限是"按证排污"的前提，缺了不能算安全
            return new PermitValidity(permit.getPermitNo(), null, 0, VALIDITY_OVERDUE);
        }
        long daysLeft = ChronoUnit.DAYS.between(today, end);
        // 到期当天仍算有效（daysLeft==0 → EXPIRING），过了才 OVERDUE
        String level = daysLeft < 0 ? VALIDITY_OVERDUE
                : daysLeft <= EXPIRE_WARN_DAYS ? VALIDITY_EXPIRING : VALIDITY_NORMAL;
        return new PermitValidity(permit.getPermitNo(), end, daysLeft, level);
    }

    @Override
    public void validateOutletLimits(String permitLimits) {
        if (StrUtil.isBlank(permitLimits)) {
            return;
        }
        List<LimitItem> items = parse(permitLimits, LimitItem.class, SET_PERMIT_OUTLET_LIMITS_INVALID);
        for (LimitItem item : items) {
            if (StrUtil.isBlank(item.getPollutantCode()) || item.getLimitValue() == null
                    || item.getLimitValue().signum() <= 0) {
                throw exception(SET_PERMIT_OUTLET_LIMITS_INVALID);
            }
        }
    }

    @Override
    public void validateAnnualLimits(String annualLimits) {
        if (StrUtil.isBlank(annualLimits)) {
            return;
        }
        List<AnnualItem> items = parse(annualLimits, AnnualItem.class, SET_PERMIT_ANNUAL_LIMITS_INVALID);
        for (AnnualItem item : items) {
            if (StrUtil.isBlank(item.getPollutantCode()) || item.getAnnualLimitT() == null
                    || item.getAnnualLimitT().signum() <= 0) {
                throw exception(SET_PERMIT_ANNUAL_LIMITS_INVALID);
            }
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 取该排放口某污染物的许可浓度限值；未配置返回 null。
     * 解析失败按"未配置"处理并告警——配置期已由 validateOutletLimits 拦下格式错误，
     * 能走到这里说明是直接改库或历史脏数据，不该让监测入库整个 500。
     */
    private BigDecimal limitOf(String permitLimits, String pollutantCode) {
        List<LimitItem> items;
        try {
            items = JsonUtils.parseArray(permitLimits, LimitItem.class);
        } catch (Exception e) {
            log.error("[limitOf][排放口许可限值 JSON 解析失败，本次不做限值比对，config={}]", permitLimits, e);
            return null;
        }
        return items.stream()
                .filter(i -> pollutantCode.equals(i.getPollutantCode()))
                .map(LimitItem::getLimitValue)
                .findFirst().orElse(null);
    }

    /**
     * 该排放口所绑排污许可证上某污染物的年许可总量(t)；证或配置缺失返回 null。
     */
    private BigDecimal annualLimitOf(Long outletId, String pollutantCode) {
        MesSetEmissionOutletDO outlet = outletMapper.selectById(outletId);
        if (outlet == null || StrUtil.isBlank(outlet.getPermitNo())) {
            return null;
        }
        MesSetPollutionPermitDO permit = permitMapper.selectByPermitNo(outlet.getPermitNo());
        if (permit == null || StrUtil.isBlank(permit.getAnnualLimits())) {
            return null;
        }
        try {
            return JsonUtils.parseArray(permit.getAnnualLimits(), AnnualItem.class).stream()
                    .filter(i -> pollutantCode.equals(i.getPollutantCode()))
                    .map(AnnualItem::getAnnualLimitT)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            log.error("[annualLimitOf][许可年排放量 JSON 解析失败，本次不做红线判定，permitNo={}, config={}]",
                    permit.getPermitNo(), permit.getAnnualLimits(), e);
            return null;
        }
    }

    /**
     * 自然年累计排放量(t)：废气 + 废水两张监测表按 排放口+污染物+自然年 求和后换算。
     * 口径见设计文档 §六-2「自然年 Σ 监测浓度×标干风量×时长，次年 1/1 重置」——本实现对"量"取
     * 已落库的 emission_amount 求和（它正是那个乘积的结果列），次年归零由自然年边界天然满足。
     */
    private BigDecimal annualUsed(Long outletId, String pollutantCode, LocalDate onDate) {
        if (outletId == null || StrUtil.isBlank(pollutantCode)) {
            return BigDecimal.ZERO;
        }
        LocalDate day = onDate != null ? onDate : LocalDate.now();
        LocalDateTime start = day.withDayOfYear(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        // ponytail: 逐行取回内存求和。单口单污染物小时级 CEMS 约 8.7k 行/年，够用；
        //           量级再大（或改成分钟级）再换 SQL SUM 聚合。
        BigDecimal kg = exhaustGasMapper.sumEmissionAmount(outletId, pollutantCode, start, end)
                .add(wastewaterMapper.sumEmissionAmount(outletId, pollutantCode, start, end));
        return kg.divide(KG_PER_TON, 3, RoundingMode.HALF_UP);
    }

    /**
     * 解析配置 JSON，格式错误转成业务错误码（配置期失败快）。
     */
    private <T> List<T> parse(String text, Class<T> clazz, cn.iocoder.txgy.framework.common.exception.ErrorCode errorCode) {
        try {
            return JsonUtils.parseArray(text, clazz);
        } catch (Exception e) {
            log.error("[parse][许可配置 JSON 格式错误，config={}]", text, e);
            throw exception(errorCode);
        }
    }

    /**
     * 排放口许可浓度限值条目
     */
    @Data
    public static class LimitItem {
        private String pollutantCode;
        private String pollutantName;
        private BigDecimal limitValue;
        private String unit;
    }

    /**
     * 许可年排放总量条目
     */
    @Data
    public static class AnnualItem {
        private String pollutantCode;
        private String pollutantName;
        private BigDecimal annualLimitT;
        private BigDecimal annualUsedT;
    }

}
