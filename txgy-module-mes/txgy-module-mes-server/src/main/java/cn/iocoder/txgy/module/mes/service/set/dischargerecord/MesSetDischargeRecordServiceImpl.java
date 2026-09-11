package cn.iocoder.txgy.module.mes.service.set.dischargerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord.vo.MesSetDischargeRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord.MesSetDischargeRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.dischargerecord.MesSetDischargeRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * MES 安全环保检测-排放合规流水 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetDischargeRecordServiceImpl implements MesSetDischargeRecordService {

    @Resource
    private MesSetDischargeRecordMapper dischargeRecordMapper;

    @Override
    public Long createDischargeRecord(MesSetDischargeRecordDO record) {
        dischargeRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public PageResult<MesSetDischargeRecordDO> getDischargeRecordPage(MesSetDischargeRecordPageReqVO pageReqVO) {
        return dischargeRecordMapper.selectPage(pageReqVO);
    }

}
