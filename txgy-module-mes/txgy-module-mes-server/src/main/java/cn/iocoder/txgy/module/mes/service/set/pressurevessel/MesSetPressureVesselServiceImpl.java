package cn.iocoder.txgy.module.mes.service.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pressurevessel.MesSetPressureVesselMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PRESSURE_VESSEL_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PRESSURE_VESSEL_NO_DUPLICATE;

/**
 * MES 安全环保检测-压力容器检查 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPressureVesselServiceImpl implements MesSetPressureVesselService {

    @Resource
    private MesSetPressureVesselMapper pressurevesselMapper;

    @Override
    public Long createPressureVessel(MesSetPressureVesselSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetPressureVesselDO obj = BeanUtils.toBean(createReqVO, MesSetPressureVesselDO.class);
        pressurevesselMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePressureVessel(MesSetPressureVesselSaveReqVO updateReqVO) {
        MesSetPressureVesselDO exist = validatePressureVesselExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        pressurevesselMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetPressureVesselDO.class));
    }

    @Override
    public void deletePressureVessel(Long id) {
        validatePressureVesselExists(id);
        pressurevesselMapper.deleteById(id);
    }

    @Override
    public MesSetPressureVesselDO getPressureVessel(Long id) {
        return pressurevesselMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPressureVesselDO> getPressureVesselPage(MesSetPressureVesselPageReqVO pageReqVO) {
        return pressurevesselMapper.selectPage(pageReqVO);
    }

    private MesSetPressureVesselDO validatePressureVesselExists(Long id) {
        MesSetPressureVesselDO obj = pressurevesselMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_PRESSURE_VESSEL_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)
     */
    private void validateBase(MesSetPressureVesselSaveReqVO reqVO, String origin) {
        MesSetPressureVesselDO exist = pressurevesselMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_PRESSURE_VESSEL_NO_DUPLICATE);
        }
    }

}
