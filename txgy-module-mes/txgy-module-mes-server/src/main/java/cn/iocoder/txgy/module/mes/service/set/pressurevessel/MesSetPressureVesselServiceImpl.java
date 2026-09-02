package cn.iocoder.txgy.module.mes.service.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pressurevessel.MesSetPressureVesselMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PRESSURE_VESSEL_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PRESSURE_VESSEL_NOT_EXISTS;

/**
 * MES 安全环保检测-压力容器检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPressureVesselServiceImpl implements MesSetPressureVesselService {

    @Resource
    private MesSetPressureVesselMapper pressureVesselMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPressureVessel(MesSetPressureVesselSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetPressureVesselDO pressureVessel = BeanUtils.toBean(createReqVO, MesSetPressureVesselDO.class);
        pressureVesselMapper.insert(pressureVessel);
        return pressureVessel.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePressureVessel(MesSetPressureVesselSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validatePressureVesselExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetPressureVesselDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPressureVesselDO.class);
        pressureVesselMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePressureVessel(Long id) {
        // 1. 校验存在
        validatePressureVesselExists(id);
        // 2. 删除
        pressureVesselMapper.deleteById(id);
    }

    @Override
    public void validatePressureVesselExists(Long id) {
        if (pressureVesselMapper.selectById(id) == null) {
            throw exception(SET_PRESSURE_VESSEL_NOT_EXISTS);
        }
    }

    @Override
    public MesSetPressureVesselDO getPressureVessel(Long id) {
        return pressureVesselMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPressureVesselDO> getPressureVesselPage(MesSetPressureVesselPageReqVO pageReqVO) {
        return pressureVesselMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetPressureVesselDO exist = pressureVesselMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_PRESSURE_VESSEL_NO_DUPLICATE);
        }
    }

}
