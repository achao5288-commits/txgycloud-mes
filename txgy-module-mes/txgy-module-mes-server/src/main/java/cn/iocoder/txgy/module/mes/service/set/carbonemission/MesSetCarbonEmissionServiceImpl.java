package cn.iocoder.txgy.module.mes.service.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.carbonemission.MesSetCarbonEmissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CARBON_EMISSION_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CARBON_EMISSION_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CARBON_EMISSION_ENERGY_TYPE_INVALID;

/**
 * MES 安全环保检测-碳排放核算 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetCarbonEmissionServiceImpl implements MesSetCarbonEmissionService {

    /**
     * 能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM
     */
    private static final Set<String> ENERGY_TYPES = new HashSet<>(Arrays.asList("COAL", "ELECTRICITY"));

    @Resource
    private MesSetCarbonEmissionMapper carbonemissionMapper;

    @Override
    public Long createCarbonEmission(MesSetCarbonEmissionSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetCarbonEmissionDO obj = BeanUtils.toBean(createReqVO, MesSetCarbonEmissionDO.class);
        carbonemissionMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateCarbonEmission(MesSetCarbonEmissionSaveReqVO updateReqVO) {
        MesSetCarbonEmissionDO exist = validateCarbonEmissionExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getCalcNo());
        carbonemissionMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetCarbonEmissionDO.class));
    }

    @Override
    public void deleteCarbonEmission(Long id) {
        validateCarbonEmissionExists(id);
        carbonemissionMapper.deleteById(id);
    }

    @Override
    public MesSetCarbonEmissionDO getCarbonEmission(Long id) {
        return carbonemissionMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetCarbonEmissionDO> getCarbonEmissionPage(MesSetCarbonEmissionPageReqVO pageReqVO) {
        return carbonemissionMapper.selectPage(pageReqVO);
    }

    private MesSetCarbonEmissionDO validateCarbonEmissionExists(Long id) {
        MesSetCarbonEmissionDO obj = carbonemissionMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_CARBON_EMISSION_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM枚举
     */
    private void validateBase(MesSetCarbonEmissionSaveReqVO reqVO, String origin) {
        MesSetCarbonEmissionDO exist = carbonemissionMapper.selectByCalcNo(reqVO.getCalcNo());
        if (exist != null && !exist.getCalcNo().equals(origin)) {
            throw exception(SET_CARBON_EMISSION_NO_DUPLICATE);
        }
        if (reqVO.getEnergyType() != null && !ENERGY_TYPES.contains(reqVO.getEnergyType())) {
            throw exception(SET_CARBON_EMISSION_ENERGY_TYPE_INVALID);
        }
    }

}
