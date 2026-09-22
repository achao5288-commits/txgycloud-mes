package cn.iocoder.txgy.module.mes.service.set.attachment;

import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.attachment.vo.MesSetAttachmentSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment.MesSetAttachmentDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.attachment.MesSetAttachmentMapper;
import cn.iocoder.txgy.module.mes.service.set.signrecord.MesSetSignRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ATTACHMENT_BIZ_SIGNED;
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

    @Resource
    private MesSetSignRecordService signRecordService;

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
        MesSetAttachmentDO attachment = attachmentMapper.selectById(id);
        if (attachment == null) {
            throw exception(SET_ATTACHMENT_NOT_EXISTS);
        }
        // 签字即冻结：附件是签字结论的证据（复核弹窗里签的现场照片/检测报告就挂在这条 biz_no 上），
        // 业务单一旦有签字，附件就不能再删——删了之后签字行还在、它引用的证据却没了，追责时说不清。
        // bizType/bizNo 成对传：签字表按这两列检索，只传 bizNo 会认成别的业务类型的单子。
        if (signRecordService.isSigned(attachment.getBizType(), attachment.getBizNo())) {
            throw exception(SET_ATTACHMENT_BIZ_SIGNED);
        }
        attachmentMapper.deleteById(id);
    }

}
