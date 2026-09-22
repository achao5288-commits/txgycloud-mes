package cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionledger;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-污染/危废暂存台账 DO
 *
 * 人工复核判"有污染(POLLUTED)"的判定记录自动登记入台账（A2），
 * 状态：暂存(STORED)/处置中(PROCESSING)/已回用(REUSED)/已排放(DISCHARGED)/已处置(DISPOSED)/已解除(CLEARED)。
 * 终态(已回用/已排放/已处置) = 人工处置闭环；已解除(CLEARED) = 系统在"批次后续复核为无污染"时自动关闭的历史误判行。
 * 台账行是该批次的唯一管控权威；批次污染戳(pollution_*)只是"该批是否仍有未闭环台账行"的投影缓存。
 *
 * 与 mes_set_pollution_check 一对一（uk source_check_id）：一条判定记录至多一条台账。
 *
 * @author OPENLAB BS
 */
@TableName("mes_pollution_ledger")
@KeySequence("mes_pollution_ledger_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesPollutionLedgerDO extends BaseDO {

    /** 台账状态：暂存 */
    public static final String STATUS_STORED = "STORED";
    /** 台账状态：处置中 */
    public static final String STATUS_PROCESSING = "PROCESSING";
    /** 台账状态：已回用 */
    public static final String STATUS_REUSED = "REUSED";
    /** 台账状态：已排放 */
    public static final String STATUS_DISCHARGED = "DISCHARGED";
    /** 台账状态：已处置 */
    public static final String STATUS_DISPOSED = "DISPOSED";
    /** 台账状态：已解除（批次后续复核为无污染时，系统自动关闭历史"有污染"台账行） */
    public static final String STATUS_CLEARED = "CLEARED";

    /** 来源类型：判定复核登记（复核判"有污染"自动登记，有 source_check_id） */
    public static final String SOURCE_POLLUTION_CHECK = "POLLUTION_CHECK";
    /** 来源类型：产废登记（换炭/换油等称重入账，无判定记录，source_check_id 为空） */
    public static final String SOURCE_WASTE_REGISTER = "WASTE_REGISTER";

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 来源判定记录ID
     *
     * 关联 {@link MesSetPollutionCheckDO#getId()}；产废登记({@link #SOURCE_WASTE_REGISTER})无判定记录，此列为 NULL。
     */
    private Long sourceCheckId;
    /**
     * 来源判定记录编号（PC-...）
     */
    private String sourceRecordNo;
    /**
     * 来源类型：POLLUTION_CHECK(判定复核登记)/WASTE_REGISTER(产废登记)
     *
     * 历史行为空，按 {@link #SOURCE_POLLUTION_CHECK} 理解（迁移脚本已回填）。
     */
    private String sourceType;
    /**
     * 环节：PURCHASE_INBOUND(采购入库)/MATERIAL_ISSUE(生产领用)/WASTE_INTERMEDIATE(中间废弃物)/FINISHED_PRODUCT(成品)
     */
    private String stage;
    /**
     * 关联单号
     */
    private String bizNo;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 物料/产品编码
     */
    private String itemCode;
    /**
     * 物料/产品名称
     */
    private String itemName;
    /**
     * 规格
     */
    private String itemSpec;
    /**
     * 重量——由源判定行带入，同一批废物只有一个重量来源；单位见 {@link #unitName}
     */
    private BigDecimal weight;
    /**
     * 重量单位快照，随重量一起从源判定行带入
     */
    private String unitName;
    /**
     * 处置方式
     */
    private String disposition;
    /**
     * 最终存储方法
     */
    private String storageMethod;
    /**
     * 去向/库位
     */
    private String location;
    /**
     * 受控库位编号（mes_wm_warehouse_area.id），污染管控门禁用；location 为其名称快照
     */
    private Long locationId;
    /**
     * 是否标记
     */
    private Boolean marked;
    /**
     * 台账状态：STORED(暂存)/PROCESSING(处置中)/REUSED(已回用)/DISCHARGED(已排放)/DISPOSED(已处置)/CLEARED(已解除)
     */
    private String status;
    /**
     * 最近流转人
     */
    private String statusBy;
    /**
     * 最近流转时间
     */
    private LocalDateTime statusTime;
    /**
     * 备注
     */
    private String remark;

}
