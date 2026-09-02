package cn.iocoder.txgy.module.mes.service.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.gasrecord.MesSetGasRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_GAS_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_GAS_RECORD_NOT_EXISTS;

/**
 * MES 安全环保检测-作业环境气体检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetGasRecordServiceImpl implements MesSetGasRecordService {

    @Resource
    private MesSetGasRecordMapper gasRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGasRecord(MesSetGasRecordSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetGasRecordDO gasRecord = BeanUtils.toBean(createReqVO, MesSetGasRecordDO.class);
        gasRecordMapper.insert(gasRecord);
        return gasRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGasRecord(MesSetGasRecordSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateGasRecordExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetGasRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesSetGasRecordDO.class);
        gasRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGasRecord(Long id) {
        // 1. 校验存在
        validateGasRecordExists(id);
        // 2. 删除
        gasRecordMapper.deleteById(id);
    }

    @Override
    public void validateGasRecordExists(Long id) {
        if (gasRecordMapper.selectById(id) == null) {
            throw exception(SET_GAS_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetGasRecordDO getGasRecord(Long id) {
        return gasRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetGasRecordDO> getGasRecordPage(MesSetGasRecordPageReqVO pageReqVO) {
        return gasRecordMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetGasRecordDO exist = gasRecordMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_GAS_RECORD_NO_DUPLICATE);
        }
    }

}
