package cn.iocoder.txgy.module.mes.service.set.signrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.signrecord.MesSetSignRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * MES 安全环保检测-签字记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetSignRecordServiceImpl implements MesSetSignRecordService {

    @Resource
    private MesSetSignRecordMapper signRecordMapper;

    @Override
    public Long createSignRecord(MesSetSignRecordDO record) {
        signRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public PageResult<MesSetSignRecordDO> getSignRecordPage(MesSetSignRecordPageReqVO pageReqVO) {
        return signRecordMapper.selectPage(pageReqVO);
    }

}
