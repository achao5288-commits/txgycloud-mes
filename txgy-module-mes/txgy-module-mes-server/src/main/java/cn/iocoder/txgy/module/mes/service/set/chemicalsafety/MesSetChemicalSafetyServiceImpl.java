package cn.iocoder.txgy.module.mes.service.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetySaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.chemicalsafety.MesSetChemicalSafetyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_SAFETY_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_SAFETY_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_CHEMICAL_SAFETY_RESULT_INVALID;

/**
 * MES 安全环保检测-危化品安全检查 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetChemicalSafetyServiceImpl implements MesSetChemicalSafetyService {

    /**
     * 结果：PASS/FAIL
     */
    private static final Set<String> RESULTS = new HashSet<>(Arrays.asList("FAIL", "PASS"));

    @Resource
    private MesSetChemicalSafetyMapper chemicalsafetyMapper;

    @Override
    public Long createChemicalSafety(MesSetChemicalSafetySaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetChemicalSafetyDO obj = BeanUtils.toBean(createReqVO, MesSetChemicalSafetyDO.class);
        chemicalsafetyMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateChemicalSafety(MesSetChemicalSafetySaveReqVO updateReqVO) {
        MesSetChemicalSafetyDO exist = validateChemicalSafetyExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        chemicalsafetyMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetChemicalSafetyDO.class));
    }

    @Override
    public void deleteChemicalSafety(Long id) {
        validateChemicalSafetyExists(id);
        chemicalsafetyMapper.deleteById(id);
    }

    @Override
    public MesSetChemicalSafetyDO getChemicalSafety(Long id) {
        return chemicalsafetyMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetChemicalSafetyDO> getChemicalSafetyPage(MesSetChemicalSafetyPageReqVO pageReqVO) {
        return chemicalsafetyMapper.selectPage(pageReqVO);
    }

    private MesSetChemicalSafetyDO validateChemicalSafetyExists(Long id) {
        MesSetChemicalSafetyDO obj = chemicalsafetyMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_CHEMICAL_SAFETY_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、结果：PASS/FAIL枚举
     */
    private void validateBase(MesSetChemicalSafetySaveReqVO reqVO, String origin) {
        MesSetChemicalSafetyDO exist = chemicalsafetyMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_CHEMICAL_SAFETY_NO_DUPLICATE);
        }
        if (reqVO.getResult() != null && !RESULTS.contains(reqVO.getResult())) {
            throw exception(SET_CHEMICAL_SAFETY_RESULT_INVALID);
        }
    }

}
