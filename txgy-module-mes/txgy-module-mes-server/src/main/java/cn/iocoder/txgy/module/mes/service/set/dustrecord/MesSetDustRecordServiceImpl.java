package cn.iocoder.txgy.module.mes.service.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.dustrecord.MesSetDustRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_DUST_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_DUST_RECORD_NOT_EXISTS;

/**
 * MES 安全环保检测-粉尘浓度检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetDustRecordServiceImpl implements MesSetDustRecordService {

    @Resource
    private MesSetDustRecordMapper dustRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDustRecord(MesSetDustRecordSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetDustRecordDO dustRecord = BeanUtils.toBean(createReqVO, MesSetDustRecordDO.class);
        dustRecordMapper.insert(dustRecord);
        return dustRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDustRecord(MesSetDustRecordSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateDustRecordExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetDustRecordDO updateObj = BeanUtils.toBean(updateReqVO, MesSetDustRecordDO.class);
        dustRecordMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDustRecord(Long id) {
        // 1. 校验存在
        validateDustRecordExists(id);
        // 2. 删除
        dustRecordMapper.deleteById(id);
    }

    @Override
    public void validateDustRecordExists(Long id) {
        if (dustRecordMapper.selectById(id) == null) {
            throw exception(SET_DUST_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetDustRecordDO getDustRecord(Long id) {
        return dustRecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetDustRecordDO> getDustRecordPage(MesSetDustRecordPageReqVO pageReqVO) {
        return dustRecordMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetDustRecordDO exist = dustRecordMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_DUST_RECORD_NO_DUPLICATE);
        }
    }

}
