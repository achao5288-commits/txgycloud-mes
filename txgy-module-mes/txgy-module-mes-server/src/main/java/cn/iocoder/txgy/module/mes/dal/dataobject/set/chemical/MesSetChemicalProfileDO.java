package cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MES 安全环保检测-危化品档案 DO（设计文档 §5 主线A）
 *
 * 与 mes_set_chemical_safety 的分工：那张表是**一次巡检**的快照（标签/MSDS/储存/隔离四项检查结果），
 * 这张表是**一类物料**的常量属性（MSDS 挂载、禁配组、储量上限、专区要求）。两者用 chemicalCode 关联。
 *
 * 禁配判定以 compatGroup 为键：矩阵里锚定的组合才是硬拦截，未锚定的组合不猜（见 Service 常量）。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_chemical_profile")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetChemicalProfileDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 危化品档案编号
     */
    private String profileNo;
    /**
     * 危化品代码（与 mes_set_chemical_safety.chemical_code 对齐）
     */
    private String chemicalCode;
    /**
     * 关联物料 id（mes_md_item.id）
     *
     * **这张表与本表的唯一可靠关联**：chemicalCode/casNo 是 CAS 号，物料主数据用物料编码，
     * 两者永远对不上（设计文档 F10）。所以要判「某个在库批次的物料是不是危化品」，
     * 只能靠这条显式绑定，不能靠编码字符串猜。
     *
     * 为空 = 未绑定：在库侧（受控库位调整）**不判**禁配/专区——未绑定的物料不当危化品管，
     * 与「未锚定的禁配组合不猜」同一口径。
     */
    private Long itemId;
    /**
     * 危化品名称
     */
    private String chemicalName;
    /**
     * CAS 号
     */
    private String casNo;
    /**
     * 相容组（禁配判定用）：ISOCYANATE/POLYOL/THINNER/EPOXY/WATER/ALCOHOL/AMINE 等
     */
    private String compatGroup;
    /**
     * 危险类别（如 易燃液体/急性毒性/氧化性物质）
     */
    private String hazardClass;
    /**
     * 储存专区：GENERAL 一般区 / EXPLOSION_PROOF 防爆区 / ISOLATION 隔离区 / SPECIAL 专库
     */
    private String storageZone;
    /**
     * 具体库位描述
     */
    private String storageLocation;
    /**
     * 储量上限（同库位该物料总量，NULL=不限量）
     */
    private BigDecimal storageLimit;
    /**
     * 储量单位
     */
    private String storageUnit;
    /**
     * 当前存量（stock-in 累加，用于超量预警）
     */
    private BigDecimal stockQuantity;
    /**
     * 额外禁配相容组，逗号分隔（人工补充用，矩阵里已锚定的不必重复填）
     */
    private String incompatibleGroups;
    /**
     * 是否必须存放于防爆区
     */
    private Boolean explosionProof;
    /**
     * MSDS 文件 URL（前端直传，后端只存 URL）
     */
    private String msdsUrl;
    /**
     * MSDS 版本有效期（到期预警）
     */
    private LocalDate msdsExpireDate;
    /**
     * 是否纳入效期管理
     */
    private Boolean expireManage;
    /**
     * 保质期天数（实际到期冻结在 mes_wm_batch.expireDate 侧）
     */
    private Integer shelfLifeDays;
    /**
     * 应急措施（泄漏/接触处置）
     */
    private String emergencyMeasure;
    /**
     * 状态：ENABLED 启用 / DISABLED 停用
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
