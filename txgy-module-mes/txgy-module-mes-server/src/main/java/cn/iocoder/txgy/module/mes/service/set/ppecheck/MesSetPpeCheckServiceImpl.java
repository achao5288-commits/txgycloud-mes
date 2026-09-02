package cn.iocoder.txgy.module.mes.service.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.ppecheck.MesSetPpeCheckMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_NOT_EXISTS;

/**
 * MES 安全环保检测-PPE防护检查记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPpeCheckServiceImpl implements MesSetPpeCheckService {

    @Resource
    private MesSetPpeCheckMapper ppeCheckMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPpeCheck(MesSetPpeCheckSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetPpeCheckDO ppeCheck = BeanUtils.toBean(createReqVO, MesSetPpeCheckDO.class);
        ppeCheckMapper.insert(ppeCheck);
        return ppeCheck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePpeCheck(MesSetPpeCheckSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validatePpeCheckExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetPpeCheckDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPpeCheckDO.class);
        ppeCheckMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePpeCheck(Long id) {
        // 1. 校验存在
        validatePpeCheckExists(id);
        // 2. 删除
        ppeCheckMapper.deleteById(id);
    }

    @Override
    public void validatePpeCheckExists(Long id) {
        if (ppeCheckMapper.selectById(id) == null) {
            throw exception(SET_PPE_CHECK_NOT_EXISTS);
        }
    }

    @Override
    public MesSetPpeCheckDO getPpeCheck(Long id) {
        return ppeCheckMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPpeCheckDO> getPpeCheckPage(MesSetPpeCheckPageReqVO pageReqVO) {
        return ppeCheckMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetPpeCheckDO exist = ppeCheckMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_PPE_CHECK_NO_DUPLICATE);
        }
    }

}
