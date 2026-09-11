package cn.iocoder.txgy.module.mes.service.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater.MesSetWastewaterMapper;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_WASTEWATER_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_WASTEWATER_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_WASTEWATER_COLLECTION_MODE_INVALID;

/**
 * MES 安全环保检测-废水监测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetWastewaterServiceImpl implements MesSetWastewaterService {

    /**
     * 采集方式：ONLINE_AUTO/LAB_MANUAL
     */
    private static final Set<String> COLLECTION_MODES = new HashSet<>(Arrays.asList("LAB_MANUAL", "MANUAL"));

    @Resource
    private MesSetWastewaterMapper wastewaterMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Override
    public Long createWastewater(MesSetWastewaterSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetWastewaterDO obj = BeanUtils.toBean(createReqVO, MesSetWastewaterDO.class);
        applyOutletLimit(obj);
        wastewaterMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateWastewater(MesSetWastewaterSaveReqVO updateReqVO) {
        MesSetWastewaterDO exist = validateWastewaterExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        MesSetWastewaterDO obj = BeanUtils.toBean(updateReqVO, MesSetWastewaterDO.class);
        applyOutletLimit(obj);
        wastewaterMapper.updateById(obj);
    }

    @Override
    public void deleteWastewater(Long id) {
        validateWastewaterExists(id);
        wastewaterMapper.deleteById(id);
    }

    @Override
    public MesSetWastewaterDO getWastewater(Long id) {
        return wastewaterMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetWastewaterDO> getWastewaterPage(MesSetWastewaterPageReqVO pageReqVO) {
        return wastewaterMapper.selectPage(pageReqVO);
    }

    private MesSetWastewaterDO validateWastewaterExists(Long id) {
        MesSetWastewaterDO obj = wastewaterMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_WASTEWATER_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、采集方式：ONLINE_AUTO/LAB_MANUAL枚举
     */
    private void validateBase(MesSetWastewaterSaveReqVO reqVO, String origin) {
        MesSetWastewaterDO exist = wastewaterMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_WASTEWATER_NO_DUPLICATE);
        }
        if (reqVO.getCollectionMode() != null && !COLLECTION_MODES.contains(reqVO.getCollectionMode())) {
            throw exception(SET_WASTEWATER_COLLECTION_MODE_INVALID);
        }
    }

    /**
     * 按该排放口的许可限值重算 limitValue/result，**不接受人工填的矛盾值**。语义同废气侧：
     * 判不出就原样保留人工值，「没配」不等于「达标」。
     */
    private void applyOutletLimit(MesSetWastewaterDO obj) {
        MesSetPermitComplianceService.LimitVerdict verdict = permitComplianceService.judgeByOutletLimit(
                obj.getOutletId(), obj.getPollutantCode(), obj.getConcentration());
        if (verdict == null) {
            return;
        }
        obj.setLimitValue(verdict.limitValue());
        obj.setResult(verdict.result());
    }

}
