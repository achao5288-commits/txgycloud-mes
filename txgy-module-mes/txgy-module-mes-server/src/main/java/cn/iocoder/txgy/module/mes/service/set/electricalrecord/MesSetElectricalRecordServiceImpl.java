package cn.iocoder.txgy.module.mes.service.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.electricalrecord.MesSetElectricalRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ELECTRICAL_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ELECTRICAL_RECORD_NOT_EXISTS;

/**
 * MES 安全环保检测-电气安全检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetElectricalRecordServiceImpl implements MesSetElectricalRecordService {

    @Resource
    private MesSetElectricalRecordMapper electricalRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createElectricalRecord(MesSetElectricalRecordSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetElectricalRecordDO electricalRecord = BeanUtils.toBean(createReqVO, MesSetElectricalRecordDO.class);
        electricalRecordMapper.insert(electricalRecord);
        return electricalRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateElectricalRecord(MesSetElectricalRecordSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateElectricalRecordExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetElectricalRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesSetElectricalRecordDO.class);
        electricalRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteElectricalRecord(Long id) {
        // 1. 校验存在
        validateElectricalRecordExists(id);
        // 2. 删除
        electricalRecordMapper.deleteById(id);
    }

    @Override
    public void validateElectricalRecordExists(Long id) {
        if (electricalRecordMapper.selectById(id) == null) {
            throw exception(SET_ELECTRICAL_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetElectricalRecordDO getElectricalRecord(Long id) {
        return electricalRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetElectricalRecordDO> getElectricalRecordPage(MesSetElectricalRecordPageReqVO pageReqVO) {
        return electricalRecordMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetElectricalRecordDO exist = electricalRecordMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_ELECTRICAL_RECORD_NO_DUPLICATE);
        }
    }

}
