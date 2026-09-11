package cn.iocoder.txgy.module.mes.service.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.exhaustgas.MesSetExhaustGasMapper;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EXHAUST_GAS_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EXHAUST_GAS_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EXHAUST_GAS_COLLECTION_MODE_INVALID;

/**
 * MES 安全环保检测-废气监测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetExhaustGasServiceImpl implements MesSetExhaustGasService {

    /**
     * 采集方式：CEMS_AUTO/MANUAL
     */
    private static final Set<String> COLLECTION_MODES = new HashSet<>(Arrays.asList("CEMS_AUTO", "MANUAL"));

    @Resource
    private MesSetExhaustGasMapper exhaustgasMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Override
    public Long createExhaustGas(MesSetExhaustGasSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetExhaustGasDO obj = BeanUtils.toBean(createReqVO, MesSetExhaustGasDO.class);
        applyOutletLimit(obj);
        exhaustgasMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateExhaustGas(MesSetExhaustGasSaveReqVO updateReqVO) {
        MesSetExhaustGasDO exist = validateExhaustGasExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        MesSetExhaustGasDO obj = BeanUtils.toBean(updateReqVO, MesSetExhaustGasDO.class);
        applyOutletLimit(obj);
        exhaustgasMapper.updateById(obj);
    }

    @Override
    public void deleteExhaustGas(Long id) {
        validateExhaustGasExists(id);
        exhaustgasMapper.deleteById(id);
    }

    @Override
    public MesSetExhaustGasDO getExhaustGas(Long id) {
        return exhaustgasMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetExhaustGasDO> getExhaustGasPage(MesSetExhaustGasPageReqVO pageReqVO) {
        return exhaustgasMapper.selectPage(pageReqVO);
    }

    private MesSetExhaustGasDO validateExhaustGasExists(Long id) {
        MesSetExhaustGasDO obj = exhaustgasMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EXHAUST_GAS_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、采集方式：CEMS_AUTO/MANUAL枚举
     */
    private void validateBase(MesSetExhaustGasSaveReqVO reqVO, String origin) {
        MesSetExhaustGasDO exist = exhaustgasMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_EXHAUST_GAS_NO_DUPLICATE);
        }
        if (reqVO.getCollectionMode() != null && !COLLECTION_MODES.contains(reqVO.getCollectionMode())) {
            throw exception(SET_EXHAUST_GAS_COLLECTION_MODE_INVALID);
        }
    }

    /**
     * 按该排放口的许可限值重算 limitValue/result，**不接受人工填的矛盾值**（设计文档 §六「按证排污」）。
     * 判不出（没挂排放口 / 该口未配此污染物限值）就原样保留人工值——「没配」不等于「达标」。
     */
    private void applyOutletLimit(MesSetExhaustGasDO obj) {
        MesSetPermitComplianceService.LimitVerdict verdict = permitComplianceService.judgeByOutletLimit(
                obj.getOutletId(), obj.getPollutantCode(), obj.getConcentration());
        if (verdict == null) {
            return;
        }
        obj.setLimitValue(verdict.limitValue());
        obj.setResult(verdict.result());
    }

}
