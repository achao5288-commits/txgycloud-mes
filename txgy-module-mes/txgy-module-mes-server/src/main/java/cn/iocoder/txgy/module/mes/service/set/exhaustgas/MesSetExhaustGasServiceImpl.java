package cn.iocoder.txgy.module.mes.service.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas.MesSetExhaustGasMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EXHAUST_GAS_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EXHAUST_GAS_NOT_EXISTS;

/**
 * MES 安全环保检测-废气排放检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetExhaustGasServiceImpl implements MesSetExhaustGasService {

    @Resource
    private MesSetExhaustGasMapper exhaustGasMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createExhaustGas(MesSetExhaustGasSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetExhaustGasDO exhaustGas = BeanUtils.toBean(createReqVO, MesSetExhaustGasDO.class);
        exhaustGasMapper.insert(exhaustGas);
        return exhaustGas.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExhaustGas(MesSetExhaustGasSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateExhaustGasExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetExhaustGasDO updateObj = BeanUtils.toBean(updateReqVO, MesSetExhaustGasDO.class);
        exhaustGasMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExhaustGas(Long id) {
        // 1. 校验存在
        validateExhaustGasExists(id);
        // 2. 删除
        exhaustGasMapper.deleteById(id);
    }

    @Override
    public void validateExhaustGasExists(Long id) {
        if (exhaustGasMapper.selectById(id) == null) {
            throw exception(SET_EXHAUST_GAS_NOT_EXISTS);
        }
    }

    @Override
    public MesSetExhaustGasDO getExhaustGas(Long id) {
        return exhaustGasMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetExhaustGasDO> getExhaustGasPage(MesSetExhaustGasPageReqVO pageReqVO) {
        return exhaustGasMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetExhaustGasDO exist = exhaustGasMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_EXHAUST_GAS_NO_DUPLICATE);
        }
    }

}
