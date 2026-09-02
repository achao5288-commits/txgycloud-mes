package cn.iocoder.txgy.module.mes.service.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_NOT_EXISTS;

/**
 * MES 安全环保检测-排放口 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmissionOutletServiceImpl implements MesSetEmissionOutletService {

    @Resource
    private MesSetEmissionOutletMapper emissionOutletMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmissionOutlet(MesSetEmissionOutletSaveReqVO createReqVO) {
        // 1. 校验排放口编号唯一
        validateOutletCodeUnique(null, createReqVO.getOutletCode());
        // 2. 插入排放口
        MesSetEmissionOutletDO emissionOutlet = BeanUtils.toBean(createReqVO, MesSetEmissionOutletDO.class);
        emissionOutletMapper.insert(emissionOutlet);
        return emissionOutlet.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmissionOutlet(MesSetEmissionOutletSaveReqVO updateReqVO) {
        // 1. 校验存在 + 排放口编号唯一
        validateEmissionOutletExists(updateReqVO.getId());
        validateOutletCodeUnique(updateReqVO.getId(), updateReqVO.getOutletCode());
        // 2. 更新
        MesSetEmissionOutletDO updateObj = BeanUtils.toBean(updateReqVO, MesSetEmissionOutletDO.class);
        emissionOutletMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmissionOutlet(Long id) {
        // 1. 校验存在
        validateEmissionOutletExists(id);
        // 2. 删除
        emissionOutletMapper.deleteById(id);
    }

    @Override
    public void validateEmissionOutletExists(Long id) {
        if (emissionOutletMapper.selectById(id) == null) {
            throw exception(SET_EMISSION_OUTLET_NOT_EXISTS);
        }
    }

    @Override
    public MesSetEmissionOutletDO getEmissionOutlet(Long id) {
        return emissionOutletMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEmissionOutletDO> getEmissionOutletPage(MesSetEmissionOutletPageReqVO pageReqVO) {
        return emissionOutletMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验排放口编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param outletCode 排放口编号
     */
    private void validateOutletCodeUnique(Long id, String outletCode) {
        MesSetEmissionOutletDO exist = emissionOutletMapper.selectByOutletCode(outletCode);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_EMISSION_OUTLET_NO_DUPLICATE);
        }
    }

}
