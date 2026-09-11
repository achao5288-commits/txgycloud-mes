package cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-称重记录 DO
 *
 * 危废产废/出厂称重留痕。本期**只落表只读**：无电子秤直采设备、无写入口，
 * data_source AUTO/MANUAL 与写入口留待后续迭代接入。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_weigh_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetWeighRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 称重类型：PRODUCE(产废)/FACTORY(出厂)；预留
     */
    private String weighType;
    /**
     * 业务关联类型：CHECK/LEDGER
     */
    private String bizType;
    /**
     * 业务关联单号
     */
    private String bizNo;
    /**
     * 桶/容器编码
     */
    private String containerCode;
    /**
     * 批次码
     */
    private String batchCode;
    /**
     * 电子秤设备编码
     */
    private String deviceCode;
    /**
     * 毛重(kg)
     */
    private BigDecimal grossWeight;
    /**
     * 皮重(kg)
     */
    private BigDecimal tareWeight;
    /**
     * 净重(kg)
     */
    private BigDecimal netWeight;
    /**
     * 数据来源：AUTO(电子秤直采)/MANUAL(人工录入)
     */
    private String dataSource;
    /**
     * 车牌号
     */
    private String plateNo;
    /**
     * 现场照片
     */
    private String photo;
    /**
     * 称重事由/MANUAL 授权理由
     */
    private String reason;
    /**
     * 操作人
     */
    private String operatorName;
    /**
     * 称重时间
     */
    private LocalDateTime weighTime;

}
