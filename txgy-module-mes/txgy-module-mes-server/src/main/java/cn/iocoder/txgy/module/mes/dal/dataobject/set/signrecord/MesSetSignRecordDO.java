package cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MES 安全环保检测-签字记录 DO
 *
 * 流程节点签字留档（只增不改删），与追溯链共用业务键 (biz_type,biz_no)。
 * 复核签字由复核收口自动落档；PDA 手写板(sign_img)待 infra 文件服务接入。
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_sign_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetSignRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 业务关联类型：CHECK/LEDGER
     */
    private String bizType;
    /**
     * 业务关联单号（判定 recordNo 等）
     */
    private String bizNo;
    /**
     * 签字角色：REVIEWER(复核)/OPERATOR(录入)/APPROVER(审批)；预留
     */
    private String signRole;
    /**
     * 签字人（显示用昵称）
     */
    private String signUser;
    /**
     * 签字账号ID。
     *
     * 设计文档 §295「账号即签名」：signUser 是给人看的昵称，重名/改名都会让"是不是同一个人"
     * 判错；五双双人制要求"双账号各签一次"，判重必须以账号为准，故单列一列。
     * 历史行为 NULL（那时没有双人判重需求），判重时只认非空值。
     */
    private Long signUserId;
    /**
     * 签字时间
     */
    private LocalDateTime signTime;
    /**
     * 签字地点/去向
     */
    private String location;
    /**
     * 签署意见
     */
    private String opinion;
    /**
     * 手写签字图片（未接文件服务前为空，预留）
     */
    private String signImg;

}
