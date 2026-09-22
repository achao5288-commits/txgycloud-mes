package cn.iocoder.txgy.module.mes.service.set.chemical;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;

import java.util.Map;
import java.util.Set;

/**
 * 危化品相容组规则（设计文档 §5.2 锚定）。
 *
 * **为什么单独抽一个纯静态类**：禁配/专区这两条规则有两个入口——
 *   1) 档案侧 `MesSetChemicalProfileServiceImpl.checkStorage`：按「危化品档案 + 库位文本」判；
 *   2) 在库侧 `MesPollutionControlServiceImpl.changeControlLocation`：按「物料 → 绑定的档案 + 库位 id」判。
 * 两边的**数据来源不同**（一个拿 storageLocation 文本，一个拿 mes_wm_warehouse_area），
 * 但**判定规则必须完全一致**——规则分两份是这类合规校验最典型的腐化方式，所以规则留在这里，
 * 数据访问各自留在自己的 Service（在库侧也就只需要多注入一个 Mapper，不引 Service 依赖、不成环）。
 *
 * 禁配判定以 compatGroup 为键：**矩阵里锚定的组合才是硬拦截，未锚定的组合不猜**。
 * 禁配是有化学依据的事，宁可漏判让人工确认，也不能凭"看起来像"就拦货。
 *
 * @author OPENLAB BS
 */
public final class ChemicalCompatRules {

    private ChemicalCompatRules() {
    }

    /**
     * 相容组中文名。
     *
     * 只收设计文档 §5.2 / §130 点名的物料族（黑料/白料/稀释剂/环氧粉末/水/醇/胺）；
     * **没收录的按原值回显、不猜**——与 HW_HAZARD_TRAITS 同一口径，编错了比留空危险。
     */
    public static final Map<String, String> GROUP_NAMES = Map.of(
            "ISOCYANATE", "异氰酸酯(黑料)",
            "POLYOL", "组合聚醚(白料)",
            "THINNER", "稀释剂",
            "EPOXY", "环氧粉末",
            "WATER", "水",
            "ALCOHOL", "醇类",
            "AMINE", "胺类");

    /**
     * 储存专区中文名（库位主数据 `mes_wm_warehouse_area.storage_zone`）。
     */
    public static final Map<String, String> ZONE_NAMES = Map.of(
            "GENERAL", "一般区",
            "EXPLOSION_PROOF", "防爆区",
            "ISOLATION", "隔离区");

    /**
     * 禁配矩阵（设计文档 §5.2 锚定）：
     * 「异氰酸酯(黑料)…远离水醇胺（放热剧反应）」、「组合聚醚(白料)…与黑料剧烈反应 → 分间、双锁分开」。
     *
     * 只列文档点名的组合。其余组合**不判**。
     */
    public static final String[][] INCOMPATIBLE_PAIRS = {
            {"ISOCYANATE", "POLYOL"},
            {"ISOCYANATE", "WATER"},
            {"ISOCYANATE", "ALCOHOL"},
            {"ISOCYANATE", "AMINE"},
    };

    /**
     * 必须存放于防爆区的相容组（设计文档 §5.2「稀释剂：易燃液体专柜」）。
     */
    public static final Set<String> EXPLOSION_PROOF_GROUPS = Set.of("THINNER");

    /**
     * 该相容组（或该档案的防爆标记）是否必须存放于防爆区。
     */
    public static boolean needExplosionProof(String compatGroup, Boolean explosionProof) {
        return Boolean.TRUE.equals(explosionProof) || EXPLOSION_PROOF_GROUPS.contains(compatGroup);
    }

    /**
     * 给定库位专区，是否满足该相容组的专区要求。
     *
     * @param areaZone 库位专区；档案侧传档案上的 storageZone，在库侧传 mes_wm_warehouse_area.storage_zone
     */
    public static boolean zoneSatisfies(String compatGroup, Boolean explosionProof, String areaZone) {
        return !needExplosionProof(compatGroup, explosionProof) || "EXPLOSION_PROOF".equals(areaZone);
    }

    /**
     * 两档案是否禁配：命中矩阵（对无序），或任一方在 incompatibleGroups 里人工点名了对方。
     */
    public static boolean isIncompatible(MesSetChemicalProfileDO a, MesSetChemicalProfileDO b) {
        String ga = a.getCompatGroup();
        String gb = b.getCompatGroup();
        if (ga != null && gb != null) {
            for (String[] pair : INCOMPATIBLE_PAIRS) {
                if ((pair[0].equals(ga) && pair[1].equals(gb)) || (pair[1].equals(ga) && pair[0].equals(gb))) {
                    return true;
                }
            }
        }
        return inExtraList(a.getIncompatibleGroups(), gb) || inExtraList(b.getIncompatibleGroups(), ga);
    }

    /**
     * 取相容组中文名；没收录的按原值回显，不猜。
     */
    public static String groupName(String compatGroup) {
        if (compatGroup == null) {
            return null;
        }
        return GROUP_NAMES.getOrDefault(compatGroup, compatGroup);
    }

    /**
     * 取专区中文名；没收录的按原值回显。
     */
    public static String zoneName(String zone) {
        if (StrUtil.isBlank(zone)) {
            return "未设置";
        }
        return ZONE_NAMES.getOrDefault(zone, zone);
    }

    private static boolean inExtraList(String extra, String group) {
        if (StrUtil.isBlank(extra) || group == null) {
            return false;
        }
        for (String s : extra.split(",")) {
            if (group.equals(s.trim())) {
                return true;
            }
        }
        return false;
    }
}
