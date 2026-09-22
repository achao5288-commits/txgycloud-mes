package cn.iocoder.txgy.module.mes.service.set.pollutioncheck;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MES 污染判定的「可引用法规依据」白名单
 *
 * 这是全系统法条的唯一来源，三处共用同一份 {@link #CATALOG}：
 *   1. 拼装 {@link #PROMPT_BLOCK} 注入大模型提示词（模型只能从清单里引，禁止自创）；
 *   2. 不调模型时的词典降级兜底（{@link #FALLBACK_POLLUTED} 等）；
 *   3. 人工复核的「判定依据」多选项（经接口下发，见 MesSetPollutionCheckService#getLegalBasisList）。
 * 法规修订时只改这一个文件。
 *
 * ⚠️ 条款号已逐条核对《中华人民共和国固体废物污染环境防治法》2020 年修订版（2020-09-01 施行）公布文本。
 *    网传「第七十五条＝危废贮存要求」是 2016 旧版编号；2020 修订版第七十五条已改为「制定国家危险废物名录」，
 *    贮存/分类要求实际在第七十九条、第八十一条。增删条目务必核对官方文本后再改。
 *
 * @author OPENLAB BS
 */
public final class PollutionLegalBasis {

    private PollutionLegalBasis() {
    }

    /**
     * 固体废物污染环境防治法
     */
    public static final String LAW_SOLID_WASTE = "《中华人民共和国固体废物污染环境防治法》（2020年修订）";

    /**
     * 国家危险废物名录（生态环境部等五部门令第36号，2024-11-26 公布，2025-01-01 施行）
     */
    public static final String CATALOG_HAZARDOUS_WASTE = "《国家危险废物名录（2025年版）》";

    /**
     * 固体废物污染环境防治法（下拉展示用简称）
     */
    private static final String LAW_SOLID_WASTE_SHORT = "固废法（2020修订）";

    /**
     * 国家危险废物名录（下拉展示用简称）
     */
    private static final String CATALOG_HAZARDOUS_WASTE_SHORT = "危废名录（2025年版）";

    /**
     * 一条可引用的法规依据
     *
     * @param code      稳定标识（如 SW_79 / HW_CATALOG_2025），仅用于代码内引用，不落库
     * @param lawName   法规全称（对外引用格式）
     * @param lawShort  法规简称（下拉展示用）
     * @param articleNo 条款号（如"第七十九条"）；名录类无条款号，为 null
     * @param summary   要点摘要（下拉展示用短句）
     * @param text      要点全文；名录类此字段即名录的用途说明
     */
    public record Article(String code, String lawName, String lawShort,
                          String articleNo, String summary, String text) {

        /**
         * 落库文本：法规全称 + 条款号 + 要点全文
         *
         * 注意：多条之间由调用方以**换行**连接，不用「；」——法条正文本身含「；」，用它做分隔会被切碎。
         */
        public String toValue() {
            return articleNo == null
                    ? lawName + "：" + text
                    : lawName + articleNo + "：" + text;
        }

        /**
         * 下拉标签：法规简称 + 条款号 + 要点摘要
         */
        public String toLabel() {
            return articleNo == null
                    ? lawShort + " · " + summary
                    : lawShort + " " + articleNo + " · " + summary;
        }
    }

    /**
     * 法条清单（顺序即提示词与下拉的展示顺序：固废法条款在前，名录在后）
     */
    public static final List<Article> CATALOG = List.of(
            new Article("SW_20", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第二十条",
                    "防扬散、防流失、防渗漏，不得擅自倾倒堆放",
                    "产生、收集、贮存、运输、利用、处置固体废物的单位，应当采取防扬散、防流失、防渗漏"
                            + "或者其他防止污染环境的措施，不得擅自倾倒、堆放、丢弃、遗撒固体废物。"),
            new Article("SW_36", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第三十六条",
                    "全过程责任制度与工业固废管理台账",
                    "产生工业固体废物的单位应当建立健全工业固体废物产生、收集、贮存、运输、利用、处置"
                            + "全过程的污染环境防治责任制度，建立工业固体废物管理台账并可追溯、可查询。"),
            new Article("SW_75", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第七十五条",
                    "国家危险废物名录与鉴别标准由国务院主管部门制定",
                    "国务院生态环境主管部门应当会同国务院有关部门制定国家危险废物名录，"
                            + "规定统一的危险废物鉴别标准、鉴别方法、识别标志和鉴别单位管理要求。"),
            new Article("SW_77", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第七十七条",
                    "危废容器、设施、场所设危险废物识别标志",
                    "对危险废物的容器和包装物以及收集、贮存、运输、利用、处置危险废物的设施、场所，"
                            + "应当按照规定设置危险废物识别标志。"),
            new Article("SW_78", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第七十八条",
                    "制定危废管理计划，建立台账并申报流向",
                    "产生危险废物的单位应当按照国家有关规定制定危险废物管理计划；建立危险废物管理台账，"
                            + "并申报危险废物的种类、产生量、流向、贮存、处置等有关资料。"),
            new Article("SW_79", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第七十九条",
                    "按环保标准贮存利用处置危废，不得擅自倾倒堆放",
                    "产生危险废物的单位应当按照国家有关规定和环境保护标准要求贮存、利用、处置危险废物，"
                            + "不得擅自倾倒、堆放。"),
            new Article("SW_81", LAW_SOLID_WASTE, LAW_SOLID_WASTE_SHORT, "第八十一条",
                    "按危废特性分类贮存，禁止性质不相容废物混放",
                    "收集、贮存危险废物应当按照危险废物特性分类进行；禁止混合收集、贮存、运输、处置"
                            + "性质不相容而未经安全性处置的危险废物。"),
            new Article("HW_CATALOG_2025", CATALOG_HAZARDOUS_WASTE, CATALOG_HAZARDOUS_WASTE_SHORT, null,
                    "判断是否属危险废物及其 HW 类别",
                    "判断是否属危险废物及其废物类别（HW 类别）。")
    );

    /**
     * 一条「现场污染特征」——车间操作员按眼见的事实勾选，系统据此收窄复核时的法条候选。
     *
     * @param code         稳定标识（如 SIGN_LEAK）；落库存这个，改文案不影响已存数据
     * @param label        展示文案（用现场语言，不是法条语言）
     * @param articleCodes 命中的法条 code（引用 {@link #CATALOG}）；无对应法条的为空列表
     */
    public record FieldSign(String code, String label, List<String> articleCodes) {
    }

    /**
     * 现场污染特征清单（顺序即下拉展示顺序）
     *
     * 只收「现场用眼睛能看出来」的物料状态——含重金属、成分这类要检测才知道的不列在这里，
     * 它们属于 AI 与专员按物料属性判断的范围，不该让现场的人勾。
     * 「其他」有意不映射法条：没把握时留空交人工判断，比硬凑一条更诚实。
     * 注意：只勾「其他」时命中为空，复核的法条候选须回落到全量，否则会无处可选。
     * ⚠️ field_signs 落库存的是这里的 code，增删条目时别改已有条目的 code。
     */
    public static final List<FieldSign> SIGN_CATALOG = List.of(
            new FieldSign("SIGN_LEAK", "包装破损 / 容器渗漏", List.of("SW_79")),
            new FieldSign("SIGN_OIL", "表面油污 / 液体残留", List.of("SW_79")),
            new FieldSign("SIGN_DAMP", "受潮 / 结块 / 锈蚀", List.of("SW_79")),
            new FieldSign("SIGN_MIX", "与非相容物料混放", List.of("SW_81")),
            new FieldSign("SIGN_MIX_WASTE", "危废与一般固废混堆", List.of("SW_81")),
            new FieldSign("SIGN_HW_OIL", "含油 / 含溶剂特征", List.of("HW_CATALOG_2025", "SW_79")),
            new FieldSign("SIGN_HW_REACT", "疑似具有反应性 / 腐蚀性", List.of("HW_CATALOG_2025")),
            new FieldSign("SIGN_OTHER", "其他（请在说明中描述）", List.of())
    );

    /**
     * 按现场特征 code 求命中的法条，供人工复核收窄候选（打不到任何法条时返回空列表）。
     *
     * 顺序以 {@link #CATALOG} 为准去重，不用 HashSet 的迭代序——同一组特征必须每次算出同样的顺序，
     * 否则审计比对时会出现无意义的 diff。
     */
    public static List<Article> articlesOfSigns(List<String> signCodes) {
        if (signCodes == null || signCodes.isEmpty()) {
            return List.of();
        }
        Set<String> hitCodes = SIGN_CATALOG.stream()
                .filter(sign -> signCodes.contains(sign.code()))
                .flatMap(sign -> sign.articleCodes().stream())
                .collect(Collectors.toSet());
        return CATALOG.stream().filter(article -> hitCodes.contains(article.code())).toList();
    }

    /**
     * 注入大模型提示词的可引用法条清单（由 {@link #CATALOG} 拼装，勿手工维护）
     */
    public static final String PROMPT_BLOCK = buildPromptBlock();

    /**
     * 降级兜底：判定为有污染
     */
    public static final String FALLBACK_POLLUTED = LAW_SOLID_WASTE + "第七十九条（按环境保护标准要求贮存、处置，不得擅自倾倒堆放）、"
            + "第八十一条（按危险废物特性分类贮存，禁止与性质不相容废物混放）；"
            + "疑似属" + CATALOG_HAZARDOUS_WASTE + "管控，具体废物类别需按名录比对核实";

    /**
     * 降级兜底：判定为无污染（按一般工业固体废物管理）
     */
    public static final String FALLBACK_CLEAN = LAW_SOLID_WASTE + "第二十条（采取防扬散、防流失、防渗漏措施）、"
            + "第三十六条（建立工业固体废物全过程责任制度与管理台账）";

    /**
     * 降级兜底：信息不足、无法判定
     */
    public static final String FALLBACK_UNCERTAIN = LAW_SOLID_WASTE + "第七十五条（危险废物鉴别标准与鉴别方法由国务院生态环境主管部门规定）；"
            + "现有信息不足以定性，建议人工复核或送检鉴别后确定";

    /**
     * 按法规分组拼装提示词：法规名单独成行，条款号缩进两条空格；
     * 无条款号的（名录）直接接在法规名之后。
     */
    private static String buildPromptBlock() {
        StringBuilder builder = new StringBuilder();
        builder.append("【可引用法律依据清单】\n");
        builder.append("只能引用以下条目，禁止引用清单以外的任何法规、标准或条款号，禁止编造条款号。\n");
        String currentLaw = null;
        for (Article article : CATALOG) {
            if (!article.lawName().equals(currentLaw)) {
                builder.append(article.lawName()).append("：");
                currentLaw = article.lawName();
                if (article.articleNo() != null) {
                    builder.append("\n");
                }
            }
            if (article.articleNo() == null) {
                builder.append(article.text()).append("\n");
            } else {
                builder.append("  ").append(article.articleNo()).append("：").append(article.text()).append("\n");
            }
        }
        return builder.toString();
    }

}
