package cn.iocoder.txgy.module.mes.service.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emissionoutlet.MesSetEmissionOutletMapper;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_OUTLET_TYPE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMISSION_OUTLET_MONITOR_METHOD_INVALID;

/**
 * MES 安全环保检测-排放口 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmissionOutletServiceImpl implements MesSetEmissionOutletService {

    /**
     * 排放类型：GAS/WASTEWATER/NOISE
     */
    private static final Set<String> OUTLET_TYPES = new HashSet<>(Arrays.asList("EXHAUST_GAS", "GAS", "WASTE_WATER"));

    /**
     * 在线监测方式：CEMS/MANUAL/NONE
     */
    private static final Set<String> MONITOR_METHODS = new HashSet<>(Arrays.asList("CEMS", "MANUAL"));

    @Resource
    private MesSetEmissionOutletMapper emissionoutletMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Override
    public Long createEmissionOutlet(MesSetEmissionOutletSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetEmissionOutletDO obj = BeanUtils.toBean(createReqVO, MesSetEmissionOutletDO.class);
        emissionoutletMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmissionOutlet(MesSetEmissionOutletSaveReqVO updateReqVO) {
        MesSetEmissionOutletDO exist = validateEmissionOutletExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getOutletCode());
        emissionoutletMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetEmissionOutletDO.class));
    }

    @Override
    public void deleteEmissionOutlet(Long id) {
        validateEmissionOutletExists(id);
        emissionoutletMapper.deleteById(id);
    }

    @Override
    public MesSetEmissionOutletDO getEmissionOutlet(Long id) {
        return emissionoutletMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEmissionOutletDO> getEmissionOutletPage(MesSetEmissionOutletPageReqVO pageReqVO) {
        return emissionoutletMapper.selectPage(pageReqVO);
    }

    private MesSetEmissionOutletDO validateEmissionOutletExists(Long id) {
        MesSetEmissionOutletDO obj = emissionoutletMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EMISSION_OUTLET_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、排放类型：GAS/WASTEWATER/NOISE枚举、在线监测方式：CEMS/MANUAL/NONE枚举
     */
    private void validateBase(MesSetEmissionOutletSaveReqVO reqVO, String origin) {
        MesSetEmissionOutletDO exist = emissionoutletMapper.selectByOutletCode(reqVO.getOutletCode());
        if (exist != null && !exist.getOutletCode().equals(origin)) {
            throw exception(SET_EMISSION_OUTLET_NO_DUPLICATE);
        }
        if (reqVO.getOutletType() != null && !OUTLET_TYPES.contains(reqVO.getOutletType())) {
            throw exception(SET_EMISSION_OUTLET_OUTLET_TYPE_INVALID);
        }
        if (reqVO.getMonitorMethod() != null && !MONITOR_METHODS.contains(reqVO.getMonitorMethod())) {
            throw exception(SET_EMISSION_OUTLET_MONITOR_METHOD_INVALID);
        }
        // 许可限值 JSON 在**配置保存时**就校验，不拖到监测入库（§14.1「配置期失败快」）。
        // 校验口径与合规判定同源：不光要"是个 JSON 数组"，每条还得有 pollutantCode 且 limitValue>0。
        // 否则一串合法但无意义的 JSON 能过校验，然后在限值比对里静默失效——那等于没有比对。
        permitComplianceService.validateOutletLimits(reqVO.getPermitLimits());
    }

}
