package cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * MES 安全环保检测-通用业务附件 DO
 *
 * 一条业务单据挂 N 个附件，以业务键 (biz_type,biz_no) 关联，与 mes_set_sign_record 同口径。
 * 文件本体在 infra 文件服务，本表只存访问地址——不搬字节。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_attachment")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetAttachmentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 业务关联类型：CHECK/LEDGER/MANIFEST/FACILITY/EMERGENCY 等
     */
    private String bizType;
    /**
     * 业务关联单号
     */
    private String bizNo;
    /**
     * 原始文件名（展示用）
     */
    private String fileName;
    /**
     * 文件访问地址（infra 文件服务返回）
     */
    private String fileUrl;
    /**
     * 备注
     */
    private String remark;

}
