package cn.iocoder.txgy.module.mes.service.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.carbonemission.MesSetCarbonEmissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CARBON_EMISSION_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CARBON_EMISSION_NOT_EXISTS;

/**
 * MES 安全环保检测-碳排放核算记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetCarbonEmissionServiceImpl implements MesSetCarbonEmissionService {

    @Resource
    private MesSetCarbonEmissionMapper carbonEmissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCarbonEmission(MesSetCarbonEmissionSaveReqVO createReqVO) {
        // 1. 校验核算批次号唯一
        validateCalcNoUnique(null, createReqVO.getCalcNo());
        // 2. 插入记录
        MesSetCarbonEmissionDO carbonEmission = BeanUtils.toBean(createReqVO, MesSetCarbonEmissionDO.class);
        carbonEmissionMapper.insert(carbonEmission);
        return carbonEmission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCarbonEmission(MesSetCarbonEmissionSaveReqVO updateReqVO) {
        // 1. 校验存在 + 核算批次号唯一
        validateCarbonEmissionExists(updateReqVO.getId());
        validateCalcNoUnique(updateReqVO.getId(), updateReqVO.getCalcNo());
        // 2. 更新
        MesSetCarbonEmissionDO updateObj = BeanUtils.toBean(updateReqVO, MesSetCarbonEmissionDO.class);
        carbonEmissionMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCarbonEmission(Long id) {
        // 1. 校验存在
        validateCarbonEmissionExists(id);
        // 2. 删除
        carbonEmissionMapper.deleteById(id);
    }

    @Override
    public void validateCarbonEmissionExists(Long id) {
        if (carbonEmissionMapper.selectById(id) == null) {
            throw exception(SET_CARBON_EMISSION_NOT_EXISTS);
        }
    }

    @Override
    public MesSetCarbonEmissionDO getCarbonEmission(Long id) {
        return carbonEmissionMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetCarbonEmissionDO> getCarbonEmissionPage(MesSetCarbonEmissionPageReqVO pageReqVO) {
        return carbonEmissionMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验核算批次号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param calcNo 核算批次号
     */
    private void validateCalcNoUnique(Long id, String calcNo) {
        MesSetCarbonEmissionDO exist = carbonEmissionMapper.selectByCalcNo(calcNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_CARBON_EMISSION_NO_DUPLICATE);
        }
    }

}
