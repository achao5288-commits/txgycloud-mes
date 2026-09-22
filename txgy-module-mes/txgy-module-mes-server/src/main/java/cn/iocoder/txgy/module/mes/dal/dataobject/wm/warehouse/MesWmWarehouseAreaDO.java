package cn.iocoder.txgy.module.mes.dal.dataobject.wm.warehouse;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 库位 DO
 */
@TableName("mes_wm_warehouse_area")
@KeySequence("mes_wm_warehouse_area_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWmWarehouseAreaDO extends BaseDO {

    /**
     * 虚拟线边库位编码（配合 {@link MesWmWarehouseDO#WIP_VIRTUAL_WAREHOUSE} 使用）
     */
    public static final String WIP_VIRTUAL_AREA = "WIP_VIRTUAL_AREA";

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 库位编码
     */
    private String code;
    /**
     * 库位名称
     */
    private String name;
    /**
     * 库区编号
     *
     * 关联 {@link MesWmWarehouseLocationDO#getId()}
     */
    private Long locationId;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 最大载重
     */
    private BigDecimal maxLoad;
    /**
     * 位置 X
     */
    private Integer positionX;
    /**
     * 位置 Y
     */
    private Integer positionY;
    /**
     * 位置 Z
     */
    private Integer positionZ;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.txgy.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;

    /**
     * 是否冻结
     */
    private Boolean frozen;
    /**
     * 是否允许物料混放
     */
    private Boolean allowItemMixing;
    /**
     * 是否允许批次混放
     */
    private Boolean allowBatchMixing;
    /**
     * 是否污染管控库位（1=受控/危废暂存，污染品只能入此处，普通品不得占用）
     */
    private Boolean pollutionControl;
    /**
     * 储存专区：GENERAL 一般区 / EXPLOSION_PROOF 防爆区 / ISOLATION 隔离区
     *
     * 与 pollutionControl **相互独立**：一个库位可以既是被污染管控的危废暂存间、又是防爆区
     * （受控库位与防爆分区是两回事，合在一列里会出现"既是又不是"的死结）。
     * 危化品相容组的专区要求（如稀释剂须防爆存放）判的就是这一列。
     */
    private String storageZone;
    /**
     * 备注
     */
    private String remark;

}
