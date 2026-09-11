package cn.iocoder.txgy.module.mes.service.set.attachment;

import cn.iocoder.txgy.module.mes.controller.admin.set.attachment.vo.MesSetAttachmentSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment.MesSetAttachmentDO;

import java.util.List;

/**
 * MES 安全环保检测-业务附件 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetAttachmentService {

    /**
     * 登记一个附件（文件已由前端传到 infra 文件服务，这里只存地址）
     *
     * @param saveReqVO 附件信息
     * @return 编号
     */
    Long createAttachment(MesSetAttachmentSaveReqVO saveReqVO);

    /**
     * 按业务键取附件列表
     *
     * @param bizType 业务关联类型
     * @param bizNo   业务关联单号
     * @return 附件列表（上传顺序）
     */
    List<MesSetAttachmentDO> getAttachmentList(String bizType, String bizNo);

    /**
     * 删除附件（逻辑删，只解除与该单据的关联）
     *
     * @param id 编号
     */
    void deleteAttachment(Long id);

}
