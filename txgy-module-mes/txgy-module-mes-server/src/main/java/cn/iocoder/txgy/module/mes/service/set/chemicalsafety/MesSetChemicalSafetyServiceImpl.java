package cn.iocoder.txgy.module.mes.service.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetySaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemicalsafety.MesSetChemicalSafetyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_SAFETY_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_SAFETY_NOT_EXISTS;

/**
 * MES 安全环保检测-危化品安全巡查记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetChemicalSafetyServiceImpl implements MesSetChemicalSafetyService {

    @Resource
    private MesSetChemicalSafetyMapper chemicalSafetyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChemicalSafety(MesSetChemicalSafetySaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetChemicalSafetyDO chemicalSafety = BeanUtils.toBean(createReqVO, MesSetChemicalSafetyDO.class);
        chemicalSafetyMapper.insert(chemicalSafety);
        return chemicalSafety.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChemicalSafety(MesSetChemicalSafetySaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateChemicalSafetyExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetChemicalSafetyDO updateObj = BeanUtils.toBean(updateReqVO, MesSetChemicalSafetyDO.class);
        chemicalSafetyMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChemicalSafety(Long id) {
        // 1. 校验存在
        validateChemicalSafetyExists(id);
        // 2. 删除
        chemicalSafetyMapper.deleteById(id);
    }

    @Override
    public void validateChemicalSafetyExists(Long id) {
        if (chemicalSafetyMapper.selectById(id) == null) {
            throw exception(SET_CHEMICAL_SAFETY_NOT_EXISTS);
        }
    }

    @Override
    public MesSetChemicalSafetyDO getChemicalSafety(Long id) {
        return chemicalSafetyMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetChemicalSafetyDO> getChemicalSafetyPage(MesSetChemicalSafetyPageReqVO pageReqVO) {
        return chemicalSafetyMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetChemicalSafetyDO exist = chemicalSafetyMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_CHEMICAL_SAFETY_NO_DUPLICATE);
        }
    }

}
