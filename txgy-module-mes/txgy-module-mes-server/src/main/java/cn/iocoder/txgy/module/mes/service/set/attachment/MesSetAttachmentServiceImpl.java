package cn.iocoder.txgy.module.mes.service.set.attachment;

import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.attachment.vo.MesSetAttachmentSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment.MesSetAttachmentDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.attachment.MesSetAttachmentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ATTACHMENT_NOT_EXISTS;

/**
 * MES 安全环保检测-业务附件 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetAttachmentServiceImpl implements MesSetAttachmentService {

    @Resource
    private MesSetAttachmentMapper attachmentMapper;

    @Override
    public Long createAttachment(MesSetAttachmentSaveReqVO saveReqVO) {
        MesSetAttachmentDO attachment = BeanUtils.toBean(saveReqVO, MesSetAttachmentDO.class);
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    @Override
    public List<MesSetAttachmentDO> getAttachmentList(String bizType, String bizNo) {
        return attachmentMapper.selectListByBiz(bizType, bizNo);
    }

    @Override
    public void deleteAttachment(Long id) {
        if (attachmentMapper.selectById(id) == null) {
            throw exception(SET_ATTACHMENT_NOT_EXISTS);
        }
        attachmentMapper.deleteById(id);
    }

}
