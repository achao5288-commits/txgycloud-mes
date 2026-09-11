package cn.iocoder.txgy.module.mes.service.set.tracechain;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceReverseRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 危废全程追溯报告的文本渲染（设计文档 §10：「报告含时间轴+衡算表+签字+整改+应急」；
 * §12 三期「扫码出示全程，监管检查一键导出」）。
 *
 * <p>纯函数：只把已经查好的数据排成 Markdown，不碰数据库、不产生副作用。
 * 单独成类是因为排版代码比业务代码长得多，混进 ServiceImpl 会把两边都读糊。
 *
 * <p>输出 Markdown 而非 PDF：监管现场要的是能当场打开、能全文搜索、不依赖渲染器的件，
 * Markdown 满足且零依赖（PDF 渲染留到真有打印需求时再说）。
 *
 * @author OPENLAB BS
 */
final class MesSetTraceReportRenderer {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MesSetTraceReportRenderer() {
    }

    /**
     * @param trace      反向查来源的结果（时间轴/台账/签字/过秤证据都在里面）
     * @param events     来源应急事件，按 eventNo 索引
     * @param facilities 来源治理设施，按 facilityNo 索引
     */
    static String render(MesSetTraceReverseRespVO trace,
                         Map<String, MesSetEmergencyEventDO> events,
                         Map<String, MesSetTreatmentFacilityDO> facilities) {
        StringBuilder sb = new StringBuilder(8192);
        sb.append("# 危险废物全程追溯报告\n\n");
        sb.append("本报告按「一只桶」出具：现场扫码读出桶码后，用它向监管出示该桶")
                .append("从产生、过秤、入库到去向的全程凭据。\n\n");

        sb.append("| 项目 | 值 |\n| --- | --- |\n");
        sb.append("| 桶码 | ").append(cell(trace.getContainerCode())).append(" |\n");
        sb.append("| 联单号 | ").append(cell(trace.getManifestNo())).append(" |\n");
        sb.append("| 台账命中 | ").append(trace.getLedgers() == null ? 0 : trace.getLedgers().size())
                .append(" 行 |\n");
        sb.append("| 源头判定 | ")
                .append(Boolean.TRUE.equals(trace.getSourced()) ? "已溯源" : "**存在无源行**")
                .append(" |\n");
        sb.append("| 生成时间 | ").append(LocalDateTime.now().format(TS)).append(" |\n\n");

        if (!Boolean.TRUE.equals(trace.getFound())) {
            sb.append("## 结论：台账里查无此桶\n\n")
                    .append(StrUtil.blankToDefault(trace.getUnsourcedReason(), "危废台账中没有这只桶。"))
                    .append("\n");
            return sb.toString();
        }

        // ① 台账
        sb.append("## 一、危废台账\n\n");
        sb.append("| 联单号 | 废物 | 数量 | 环节 | 状态 | 库位 | 处理时间 | 经手人 |\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- | --- |\n");
        Map<String, BigDecimal> byUnit = new LinkedHashMap<>();
        for (MesSetTraceReverseRespVO.Ledger l : trace.getLedgers()) {
            sb.append("| ").append(cell(l.getManifestNo()))
                    .append(" | ").append(cell(l.getWasteName()))
                    .append(" | ").append(cell(amt(l.getQuantity())))
                    .append(" | ").append(cell(l.getStage()))
                    .append(" | ").append(cell(l.getStatus()))
                    .append(" | ").append(cell(l.getStorageLocation()))
                    .append(" | ").append(cell(dt(l.getHandleTime())))
                    .append(" | ").append(cell(l.getHandler()))
                    .append(" |\n");
            if (l.getQuantity() != null) {
                String unit = StrUtil.blankToDefault(l.getQuantityUnit(), "（未填单位）");
                byUnit.merge(unit, l.getQuantity(), BigDecimal::add);
            }
        }
        sb.append('\n');

        // ② 来源判定
        sb.append("## 二、来源判定\n\n");
        sb.append("设计要求：危废必须能反推源头，**反推不到就报无源**（§9 物料衡算）。\n\n");
        sb.append("| 联单号 | 来源类型 | 来源单据 | 说明 |\n| --- | --- | --- | --- |\n");
        for (MesSetTraceReverseRespVO.Ledger l : trace.getLedgers()) {
            String type = "UNSOURCED".equals(l.getSourceType()) ? "**无源**" : cell(l.getSourceType());
            sb.append("| ").append(cell(l.getManifestNo()))
                    .append(" | ").append(type)
                    .append(" | ").append(cell(l.getSourceDocNo()))
                    .append(" | ").append(cell(StrUtil.blankToDefault(l.getSourceName(), l.getSourceDetail())))
                    .append(" |\n");
        }
        sb.append('\n');
        if (!Boolean.TRUE.equals(trace.getSourced())) {
            sb.append("> **无源说明**：").append(cell(trace.getUnsourcedReason())).append("\n\n");
        }

        // ③ 时间轴
        sb.append("## 三、追溯时间轴\n\n");
        List<MesSetTraceChainDO> nodes = trace.getTraceNodes();
        if (nodes == null || nodes.isEmpty()) {
            sb.append("本桶在追溯链上没有节点：可能环节尚未流转到会写链的步骤（复核、处置流转）。\n\n");
        } else {
            sb.append("| 节点时间 | 阶段 | 动作 | 业务单号 | 操作人 | 状态 | 备注 |\n");
            sb.append("| --- | --- | --- | --- | --- | --- | --- |\n");
            for (MesSetTraceChainDO n : nodes) {
                sb.append("| ").append(cell(dt(n.getNodeTime())))
                        .append(" | ").append(cell(n.getNodeStage()))
                        .append(" | ").append(cell(n.getNodeAction()))
                        .append(" | ").append(cell(n.getBizNo()))
                        .append(" | ").append(cell(n.getOperatorName()))
                        .append(" | ").append(cell(n.getBatchStatus()))
                        .append(" | ").append(cell(n.getExtra()))
                        .append(" |\n");
            }
            sb.append('\n');
        }

        // ④ 衡算表
        appendBalance(sb, trace, byUnit);

        // ⑤ 签字
        sb.append("## 五、签字记录\n\n");
        List<MesSetSignRecordDO> signs = trace.getSignRecords();
        if (signs == null || signs.isEmpty()) {
            sb.append("本桶及其联单没有签字记录。\n\n");
        } else {
            sb.append("| 业务单号 | 角色 | 签字人 | 时间 | 地点 | 意见 |\n");
            sb.append("| --- | --- | --- | --- | --- | --- |\n");
            for (MesSetSignRecordDO s : signs) {
                sb.append("| ").append(cell(s.getBizNo()))
                        .append(" | ").append(cell(s.getSignRole()))
                        .append(" | ").append(cell(s.getSignUser()))
                        .append(" | ").append(cell(dt(s.getSignTime())))
                        .append(" | ").append(cell(s.getLocation()))
                        .append(" | ").append(cell(s.getOpinion()))
                        .append(" |\n");
            }
            sb.append('\n');
        }

        // ⑥ 应急 + ⑦ 整改
        appendEmergencyAndRectify(sb, trace, events, facilities);
        return sb.toString();
    }

    /**
     * 衡算表：同一只桶在**三处**留下的重量各自加起来，摆在一起看对不对得上。
     *
     * <p>不做合格判定——允差在设计文档里是个字典项、**目前没有配置页**，没有阈值就无从判定。
     * 硬写一个百分比看着像结论、实则是编的，比空着危险得多。故此处只列原始数字与差值。
     */
    private static void appendBalance(StringBuilder sb, MesSetTraceReverseRespVO trace,
                                      Map<String, BigDecimal> byUnit) {
        sb.append("## 四、衡算表\n\n");

        BigDecimal weighSum = sumNet(trace.getWeighRecords() == null ? null
                : trace.getWeighRecords().stream().map(MesSetWeighRecordDO::getNetWeight).toList());

        BigDecimal ledgerSum = BigDecimal.ZERO;
        boolean allKg = !byUnit.isEmpty();
        for (Map.Entry<String, BigDecimal> e : byUnit.entrySet()) {
            ledgerSum = ledgerSum.add(e.getValue());
            if (!isKilogram(e.getKey())) {
                allKg = false;
            }
        }

        sb.append("| 口径 | 数量 | 说明 |\n| --- | --- | --- |\n");
        if (byUnit.isEmpty()) {
            sb.append("| 台账登记 | — | 台账行未填数量 |\n");
        } else {
            for (Map.Entry<String, BigDecimal> e : byUnit.entrySet()) {
                sb.append("| 台账登记 | ").append(amt(e.getValue())).append(' ')
                        .append(cell(e.getKey())).append(" | 危废台账 quantity 合计 |\n");
            }
        }
        sb.append("| 过秤净重 | ").append(amt(weighSum)).append(" kg | 称重记录 net_weight 合计，共 ")
                .append(trace.getWeighRecords() == null ? 0 : trace.getWeighRecords().size())
                .append(" 条 |\n");

        BigDecimal diff = null;
        if (allKg && !byUnit.isEmpty()) {
            diff = weighSum.subtract(ledgerSum).abs();
            sb.append("| **差值** | **").append(amt(diff)).append(" kg** | |台账登记 − 过秤净重| |\n");
        }
        sb.append('\n');

        if (!byUnit.isEmpty() && !allKg) {
            sb.append("> 台账数量存在非千克单位，与过秤记录（kg）不同量纲，**故不出差值**——")
                    .append("换算要靠密度，凭空换出来的数字不如不换。\n\n");
        }
        sb.append("> **允差判定：未配置。** 设计文档 §9 要求「投入≈产物+危废+排放+损耗，允差预警」，")
                .append("允差阈值是字典项、目前没有配置页，因此本表只列原始数字与差值，不做合格判定。")
                .append("阈值配好后此处改为自动判定。\n\n");
        sb.append("> **投入侧数据缺失。** 物料衡算要闭合需原料投入/产物/损耗三侧数据，")
                .append("而工单侧目前没有环保联结字段，取不到；故本表只在危废侧做三处对照。\n\n");
    }

    /**
     * 应急（来源事件全貌 + 报告原文）与整改（来源单据上登记过的整改/换炭待办）。
     * 来源是无源或非应急时如实写明，不硬凑内容。
     */
    private static void appendEmergencyAndRectify(StringBuilder sb, MesSetTraceReverseRespVO trace,
                                                  Map<String, MesSetEmergencyEventDO> events,
                                                  Map<String, MesSetTreatmentFacilityDO> facilities) {
        sb.append("## 六、应急\n\n");
        boolean any = false;
        for (MesSetTraceReverseRespVO.Ledger l : trace.getLedgers()) {
            if (!"EMERGENCY".equals(l.getSourceType())) {
                continue;
            }
            MesSetEmergencyEventDO e = events.get(l.getSourceDocNo());
            if (e == null) {
                continue;
            }
            any = true;
            sb.append("### 事件 ").append(cell(e.getEventNo())).append("\n\n");
            sb.append("| 项目 | 值 |\n| --- | --- |\n");
            row(sb, "类型", e.getEventType());
            row(sb, "场景", e.getScenario());
            row(sb, "发生时间", dt(e.getOccurTime()));
            row(sb, "地点", e.getLocation());
            row(sb, "化学品", e.getChemicalCode());
            row(sb, "泄漏量", amt(e.getLeakQuantity()));
            row(sb, "影响范围", e.getImpactScope());
            row(sb, "状态", e.getStatus());
            row(sb, "上报人", e.getReportUser());
            row(sb, "处置人", e.getHandler());
            row(sb, "审批人", e.getApprover());
            row(sb, "关闭时间", dt(e.getClosedTime()));
            row(sb, "产废", (e.getWasteCount() == null ? "-" : e.getWasteCount() + " 桶")
                    + " / " + amt(e.getWasteQuantity()) + " kg");
            sb.append('\n');
            row(sb, "处置说明", e.getDisposeNote());
            sb.append('\n');
            if (StrUtil.isNotBlank(e.getReportContent())) {
                sb.append("**事件报告原文**（原样带出，未作任何改写）：\n\n");
                sb.append("> ").append(cell(e.getReportContent())).append("\n\n");
            }
        }
        if (!any) {
            sb.append("本次追溯的来源不是应急事件，无应急内容。\n\n");
        }

        sb.append("## 七、整改\n\n");
        boolean anyRectify = false;
        for (MesSetTraceReverseRespVO.Ledger l : trace.getLedgers()) {
            if (!"FACILITY_CARBON".equals(l.getSourceType())) {
                continue;
            }
            MesSetTreatmentFacilityDO f = facilities.get(l.getSourceDocNo());
            if (f == null) {
                continue;
            }
            anyRectify = true;
            sb.append("- 设施 **").append(cell(f.getFacilityName())).append("**（")
                    .append(cell(f.getFacilityNo())).append("）：换炭周期 ")
                    .append(f.getReplaceCycleDays() == null ? "-" : f.getReplaceCycleDays() + " 天")
                    .append("，上次换炭 ").append(cell(date(f.getLastReplaceDate())))
                    .append("，下次应换 ")
                    .append(cell(date(f.getNextReplaceDate())));
            LocalDate next = f.getNextReplaceDate();
            if (next != null && next.isBefore(LocalDate.now())) {
                sb.append(" —— **已逾期**，属待整改项");
            }
            sb.append('\n');
            if ("SHUTDOWN".equals(f.getShutdownStatus())) {
                sb.append("  - 该设施已申报停运：")
                        .append(cell(StrUtil.blankToDefault(f.getShutdownReason(), "未填原因")))
                        .append("，申报人 ").append(cell(f.getShutdownApprover()))
                        .append("（申报/批复：").append(cell(dt(f.getShutdownDeclaredAt())))
                        .append(" / ").append(cell(dt(f.getShutdownApprovedAt()))).append("）\n");
            }
        }
        if (!anyRectify) {
            sb.append("本次追溯涉及的来源单据未登记整改要求。\n");
        }
    }

    private static void row(StringBuilder sb, String k, Object v) {
        sb.append("| ").append(cell(k)).append(" | ").append(cell(v)).append(" |\n");
    }

    private static BigDecimal sumNet(List<BigDecimal> values) {
        BigDecimal sum = BigDecimal.ZERO;
        if (values != null) {
            for (BigDecimal v : values) {
                if (v != null) {
                    sum = sum.add(v);
                }
            }
        }
        return sum;
    }

    /**
     * 吨也是重量，但和 kg 不同量纲，不能直接相加——只有千克才参与差值。
     * ponytail: 只认这几个写法的千克，工厂填「KG」「kg」「千克」都覆盖到了；填别的单位就不出差值。
     */
    private static boolean isKilogram(String unit) {
        String u = StrUtil.trimToEmpty(unit).toLowerCase();
        return u.equals("kg") || u.equals("千克") || u.equals("公斤") || u.equals("kgs");
    }

    private static String amt(BigDecimal v) {
        return v == null ? "" : v.stripTrailingZeros().toPlainString();
    }

    private static String dt(LocalDateTime v) {
        return v == null ? "" : v.format(TS);
    }

    private static String date(LocalDate v) {
        return v == null ? "" : v.toString();
    }

    /**
     * 单元格转义：单据里的意见/报告是自由文本，可能带 `|` 或换行——
     * 一个竖线就能把整张表拆散，报告是给监管看的，不能被一条备注搅烂。
     */
    private static String cell(Object v) {
        if (v == null) {
            return "";
        }
        return String.valueOf(v)
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replaceAll("[\\r\\n]+", " ");
    }

}
