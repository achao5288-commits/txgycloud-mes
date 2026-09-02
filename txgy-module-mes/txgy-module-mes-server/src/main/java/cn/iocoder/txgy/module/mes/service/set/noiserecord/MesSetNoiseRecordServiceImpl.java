package cn.iocoder.txgy.module.mes.service.set.noiserecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord.MesSetNoiseRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.noiserecord.MesSetNoiseRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_NOT_EXISTS;

/**
 * MES 安全环保检测-噪声检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetNoiseRecordServiceImpl implements MesSetNoiseRecordService {

    @Resource
    private MesSetNoiseRecordMapper noiseRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNoiseRecord(MesSetNoiseRecordSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetNoiseRecordDO noiseRecord = BeanUtils.toBean(createReqVO, MesSetNoiseRecordDO.class);
        noiseRecordMapper.insert(noiseRecord);
        return noiseRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoiseRecord(MesSetNoiseRecordSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateNoiseRecordExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetNoiseRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesSetNoiseRecordDO.class);
        noiseRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoiseRecord(Long id) {
        // 1. 校验存在
        validateNoiseRecordExists(id);
        // 2. 删除
        noiseRecordMapper.deleteById(id);
    }

    @Override
    public void validateNoiseRecordExists(Long id) {
        if (noiseRecordMapper.selectById(id) == null) {
            throw exception(SET_NOISE_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetNoiseRecordDO getNoiseRecord(Long id) {
        return noiseRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetNoiseRecordDO> getNoiseRecordPage(MesSetNoiseRecordPageReqVO pageReqVO) {
        return noiseRecordMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetNoiseRecordDO exist = noiseRecordMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_NOISE_RECORD_NO_DUPLICATE);
        }
    }

}
