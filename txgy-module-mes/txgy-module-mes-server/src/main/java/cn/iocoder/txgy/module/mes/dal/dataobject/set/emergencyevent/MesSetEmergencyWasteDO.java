package cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * MES 安全环保检测-应急废物明细 DO
 *
 * 事件子表：**没有独立 CRUD 接口**，只由 `MesSetEmergencyEventServiceImpl#disposeEvent` 逐桶写入。
 * 桶码 ↔ 称重记录 ↔ 危废台账行三者在这里对齐，供追溯反查"这只危废桶从哪来"。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_emergency_waste")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetEmergencyWasteDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 应急事件编号
     */
    private Long eventId;

    /**
     * 应急事件编号（冗余，便于按单号直查）
     */
    private String eventNo;

    /**
     * 危废桶码（贴 HJ1276 标签的那只桶）
     */
    private String containerCode;

    /**
     * 危废类别代码（泄漏吸附物按场景落 HW49/HW06/HW08）
     */
    private String wasteCode;

    /**
     * 危废名称
     */
    private String wasteName;

    /**
     * 净重 kg（过秤所得，不是手填）
     */
    private BigDecimal netWeight;

    /**
     * 入库暂存库位
     */
    private String storageLocation;

    /**
     * 称重记录编号（mes_set_weigh_record，过秤证据）
     */
    private Long weighRecordId;

    /**
     * 危废台账行编号（mes_set_hazardous_waste）
     */
    private Long hazwasteId;

    /**
     * 危废台账业务号（冗余，便于反查）
     */
    private String manifestNo;

}
