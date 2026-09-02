package cn.iocoder.txgy.module.mes.service.set.firerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firerecord.MesSetFireCheckDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.firerecord.MesSetFireCheckMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_FIRE_CHECK_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_FIRE_CHECK_NOT_EXISTS;

/**
 * MES 安全环保检测-消防设施检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetFireCheckServiceImpl implements MesSetFireCheckService {

    @Resource
    private MesSetFireCheckMapper fireCheckMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFireCheck(MesSetFireCheckSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetFireCheckDO fireCheck = BeanUtils.toBean(createReqVO, MesSetFireCheckDO.class);
        fireCheckMapper.insert(fireCheck);
        return fireCheck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFireCheck(MesSetFireCheckSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateFireCheckExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetFireCheckDO updateObj = BeanUtils.toBean(updateReqVO, MesSetFireCheckDO.class);
        fireCheckMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFireCheck(Long id) {
        // 1. 校验存在
        validateFireCheckExists(id);
        // 2. 删除
        fireCheckMapper.deleteById(id);
    }

    @Override
    public void validateFireCheckExists(Long id) {
        if (fireCheckMapper.selectById(id) == null) {
            throw exception(SET_FIRE_CHECK_NOT_EXISTS);
        }
    }

    @Override
    public MesSetFireCheckDO getFireCheck(Long id) {
        return fireCheckMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetFireCheckDO> getFireCheckPage(MesSetFireCheckPageReqVO pageReqVO) {
        return fireCheckMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetFireCheckDO exist = fireCheckMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_FIRE_CHECK_NO_DUPLICATE);
        }
    }

}
