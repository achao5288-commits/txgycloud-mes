package cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-危废转移联单 DO（国家固废管理信息系统五方流程）
 *
 * 五方角色：产生(generateUnit) / 运输(carrierUnit) / 接收(receiveUnit) / 贮存(storageUnit) / 处置(disposeUnit)。
 * 状态：DRAFT → DECLARED(已申报) → EFFECTIVE(已生效：运输+接收均确认) → TRANSFERRED(已出厂) → CLOSED(回执归档)。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_hazwaste_manifest")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetHazwasteManifestDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 国家固废系统电子转移联单号
     */
    private String manifestNo;
    /**
     * 危险废物类别代码(HW08/HW49等)
     */
    private String wasteCode;
    /**
     * 危险废物名称
     */
    private String wasteName;
    /**
     * 申报转移量
     */
    private BigDecimal quantity;
    /**
     * 数量单位
     */
    private String quantityUnit;
    /**
     * 产生单位（五方之一）
     */
    private String generateUnit;
    /**
     * 运输单位
     */
    private String carrierUnit;
    /**
     * 接收单位
     */
    private String receiveUnit;
    /**
     * 贮存单位
     */
    private String storageUnit;
    /**
     * 处置单位
     */
    private String disposeUnit;
    /**
     * 运输方确认时间
     */
    private LocalDateTime carrierConfirmTime;
    /**
     * 接收方确认时间
     */
    private LocalDateTime receiveConfirmTime;
    /**
     * 贮存方确认时间
     */
    private LocalDateTime storageConfirmTime;
    /**
     * 处置方确认时间
     */
    private LocalDateTime disposeConfirmTime;
    /**
     * 产生方申报时间
     */
    private LocalDateTime declaredTime;
    /**
     * 申报/确认时限（国家平台倒排提醒基准）
     */
    private LocalDateTime declareDeadline;
    /**
     * 运输车牌号（门卫扫牌核对）
     */
    private String vehicleNo;
    /**
     * 地磅净重(吨)
     */
    private BigDecimal netWeight;
    /**
     * 启运出厂时间
     */
    private LocalDateTime transferTime;
    /**
     * 门卫放行时间
     */
    private LocalDateTime gateReleaseTime;
    /**
     * 放行门卫
     */
    private String gateGuard;
    /**
     * 状态：DRAFT/DECLARED/EFFECTIVE/TRANSFERRED/CLOSED
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
