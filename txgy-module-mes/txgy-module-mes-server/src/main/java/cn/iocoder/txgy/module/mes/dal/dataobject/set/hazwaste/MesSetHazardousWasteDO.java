package cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-危废台账 DO
 *
 * 环节 stage 只进不退：GENERATED(产生) → STORED(贮存) → TRANSFERRED(转移) → DISPOSED(处置)。
 * 联单号 manifest_no 与国家固废系统联单（{@link MesSetHazwasteManifestDO}）同键，无生效联单不得出厂。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_hazardous_waste")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetHazardousWasteDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 危废联单号（与国家固废系统联单同键）
     */
    private String manifestNo;
    /**
     * 危废代码(如 HW08)
     */
    private String wasteCode;
    /**
     * 危废名称
     */
    private String wasteName;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 数量单位（吨等）
     */
    private String quantityUnit;
    /**
     * 台账环节：GENERATED(产生)/STORED(贮存)/TRANSFERRED(转移)/DISPOSED(处置)
     */
    private String stage;
    /**
     * 贮存地点
     */
    private String storageLocation;
    /**
     * 容器码（一桶一码，危废标签二维码内容）
     */
    private String containerCode;
    /**
     * HJ1276 标签归档文件 URL
     */
    private String labelUrl;
    /**
     * 交接方/接收单位
     */
    private String counterparty;
    /**
     * 来源工单编号
     */
    private Long woId;
    /**
     * 交接/处理时间
     */
    private LocalDateTime handleTime;
    /**
     * 经办人
     */
    private String handler;
    /**
     * 审批状态：DRAFT/APPROVED/REJECTED
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
