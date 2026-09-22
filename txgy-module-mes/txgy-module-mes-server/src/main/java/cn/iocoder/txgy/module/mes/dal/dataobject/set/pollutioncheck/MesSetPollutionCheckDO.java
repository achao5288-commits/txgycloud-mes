package cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-污染判定记录 DO
 *
 * 采购入库/生产领用/中间废弃物/成品 四环节的 AI 初筛 + 人工复核 判定记录。
 * 人工复核终态只允许：CLEAN(无污染)/POLLUTED(有污染)；AI 初筛为：
 * CLEAN/POLLUTED/UNCERTAIN(不确定)，"不确定"必须由人工复核收口。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_pollution_check")
@KeySequence("mes_set_pollution_check_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetPollutionCheckDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录编号 PC-YYYYMMDD-NNN
     */
    private String recordNo;
    /**
     * 被变更的原单号。空 = 首版；非空 = 本行是一张变更单（替代 originRecordNo 那一行）。
     * 单号本身走 generateRecordNo()，**不派生后缀** —— 本表仍带 uk_record_no(record_no) 唯一键。
     */
    private String originRecordNo;
    /**
     * 变更事由（变更单必填，说明为什么要把原单作废重开）
     */
    private String amendReason;
    /**
     * 替代本单的变更单号。收口前的行恒为空；一旦回填，本行即"已被替代"，内容彻底冻死。
     * 这是全表**唯一**允许写已收口行的列，只写一次、只有变更流程能写。
     */
    private String supersededBy;
    /**
     * 环节：PURCHASE_INBOUND(采购入库)/MATERIAL_ISSUE(生产领用)/WASTE_INTERMEDIATE(中间废弃物)/FINISHED_PRODUCT(成品)
     */
    private String stage;
    /**
     * 关联单号（入库单/领料单/报工单/成品入库单）
     */
    private String bizNo;
    /**
     * 批次编号（mes_wm_batch.id）——判定锚定的权威批次。
     * 空 = 历史手工录入的未关联记录（存量 32 条），非"无批次"；中间废弃物环节无批次主数据时也为空。
     */
    private Long batchId;
    /**
     * 批次号（显示快照，权威值见 batchId）
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
     * 现场观察到的污染特征（多选）：存 {@code PollutionLegalBasis.SIGN_CATALOG} 的 code，换行分隔。
     *
     * 供操作员按"眼睛看得见的现象"勾选，系统据此收窄复核时的法条候选。
     * ⚠️ 存的是 code 不是文案，增删条目时别改已有条目的 code。
     */
    private String fieldSigns;
    /**
     * 重量——单位见 {@link #unitName}，不是恒为 kg
     */
    private BigDecimal weight;
    /**
     * 重量单位快照：取自物料主单位；质量单位(kg/g/mg/t)已在入库前换算成 KG 并落 "KG"，
     * 其余(个/箱/米…)原样保留。为空表示来源行没带数量，重量列为空。
     */
    private String unitName;
    /**
     * AI 初筛结果：CLEAN(无污染)/POLLUTED(有污染)/UNCERTAIN(不确定)
     */
    private String aiResult;
    /**
     * AI 置信度(%) 0-100
     */
    private Integer aiConfidence;
    /**
     * AI 判定依据
     */
    private String aiReason;
    /**
     * AI 判定的法规依据（法规名 + 条款号/名录编号 + 要点），取自 {@code PollutionLegalBasis}
     *
     * 与 {@link #aiReason} 的分工：aiReason 是自然语言的理由，本字段是**可引用的法条出处**，
     * 对外出合规说明时引的是这个。
     */
    private String aiBasis;
    /**
     * AI 推荐存储方法
     */
    private String suggestedStorage;
    /**
     * 人工复核结果（最终）：CLEAN(无污染)/POLLUTED(有污染)，空=待复核
     */
    private String reviewResult;
    /**
     * 成品达标分支（仅环节 FINISHED_PRODUCT）：QUALIFIED(达标)/REWORK(局部缺陷返工)/SCRAPPED(整体报废)
     */
    private String finishedResult;
    /**
     * 人工复核的判定依据（复选的法规条款，换行分隔），取自与 AI 同一份 {@code PollutionLegalBasis.CATALOG} 白名单。
     *
     * 口径：**不强制**。填了就落库并对外可引，没填也放行（复核结论本身仍是终态权威）。
     * 分隔符是换行不是「；」——法条正文本身含「；」，用它做分隔回填时会被切碎。
     */
    private String reviewBasis;
    /**
     * 最终存储方法（复核确认）
     */
    private String storageMethod;
    /**
     * 处置方式：NORMAL_INBOUND/CONTROLLED_STORAGE/ISSUE_ALLOWED/REJECT_ISSUE/REUSE/DISCHARGE/ISOLATE_STORAGE/MARKED_STORAGE
     */
    private String disposition;
    /**
     * 去向/库位（库位名称快照）
     */
    private String location;
    /**
     * 受控库位编号（mes_wm_warehouse_area.id），污染管控门禁用；location 为其名称快照
     */
    private Long locationId;
    /**
     * 是否标记（有污染/不达标需管控的标记），0 否 1 是
     */
    private Boolean marked;
    /**
     * 复核人
     */
    private String reviewBy;
    /**
     * 复核时间
     */
    private LocalDateTime reviewTime;
    /**
     * 备注
     */
    private String remark;

}
