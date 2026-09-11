package cn.iocoder.txgy.module.mes.service.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.electricalrecord.MesSetElectricalRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ELECTRICAL_RECORD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ELECTRICAL_RECORD_NO_DUPLICATE;

/**
 * MES 安全环保检测-电气安全检查 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetElectricalRecordServiceImpl implements MesSetElectricalRecordService {

    @Resource
    private MesSetElectricalRecordMapper electricalrecordMapper;

    @Override
    public Long createElectricalRecord(MesSetElectricalRecordSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetElectricalRecordDO obj = BeanUtils.toBean(createReqVO, MesSetElectricalRecordDO.class);
        electricalrecordMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateElectricalRecord(MesSetElectricalRecordSaveReqVO updateReqVO) {
        MesSetElectricalRecordDO exist = validateElectricalRecordExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        electricalrecordMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetElectricalRecordDO.class));
    }

    @Override
    public void deleteElectricalRecord(Long id) {
        validateElectricalRecordExists(id);
        electricalrecordMapper.deleteById(id);
    }

    @Override
    public MesSetElectricalRecordDO getElectricalRecord(Long id) {
        return electricalrecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetElectricalRecordDO> getElectricalRecordPage(MesSetElectricalRecordPageReqVO pageReqVO) {
        return electricalrecordMapper.selectPage(pageReqVO);
    }

    private MesSetElectricalRecordDO validateElectricalRecordExists(Long id) {
        MesSetElectricalRecordDO obj = electricalrecordMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_ELECTRICAL_RECORD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)
     */
    private void validateBase(MesSetElectricalRecordSaveReqVO reqVO, String origin) {
        MesSetElectricalRecordDO exist = electricalrecordMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_ELECTRICAL_RECORD_NO_DUPLICATE);
        }
    }

}
