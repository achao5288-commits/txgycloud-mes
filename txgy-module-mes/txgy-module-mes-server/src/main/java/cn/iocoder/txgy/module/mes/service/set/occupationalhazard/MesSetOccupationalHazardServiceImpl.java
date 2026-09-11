package cn.iocoder.txgy.module.mes.service.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.occupationalhazard.MesSetOccupationalHazardMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_FACTOR_CATEGORY_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_RESULT_INVALID;

/**
 * MES 安全环保检测-职业危害检测 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetOccupationalHazardServiceImpl implements MesSetOccupationalHazardService {

    /**
     * 因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL
     */
    private static final Set<String> FACTOR_CATEGORYS = new HashSet<>(Arrays.asList("CHEMICAL", "PHYSICAL"));

    /**
     * 结果：PASS/FAIL
     */
    private static final Set<String> RESULTS = new HashSet<>(Arrays.asList("FAIL", "PASS"));

    @Resource
    private MesSetOccupationalHazardMapper occupationalhazardMapper;

    @Override
    public Long createOccupationalHazard(MesSetOccupationalHazardSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetOccupationalHazardDO obj = BeanUtils.toBean(createReqVO, MesSetOccupationalHazardDO.class);
        occupationalhazardMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateOccupationalHazard(MesSetOccupationalHazardSaveReqVO updateReqVO) {
        MesSetOccupationalHazardDO exist = validateOccupationalHazardExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        occupationalhazardMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetOccupationalHazardDO.class));
    }

    @Override
    public void deleteOccupationalHazard(Long id) {
        validateOccupationalHazardExists(id);
        occupationalhazardMapper.deleteById(id);
    }

    @Override
    public MesSetOccupationalHazardDO getOccupationalHazard(Long id) {
        return occupationalhazardMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetOccupationalHazardDO> getOccupationalHazardPage(MesSetOccupationalHazardPageReqVO pageReqVO) {
        return occupationalhazardMapper.selectPage(pageReqVO);
    }

    private MesSetOccupationalHazardDO validateOccupationalHazardExists(Long id) {
        MesSetOccupationalHazardDO obj = occupationalhazardMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_OCCUPATIONAL_HAZARD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL枚举、结果：PASS/FAIL枚举
     */
    private void validateBase(MesSetOccupationalHazardSaveReqVO reqVO, String origin) {
        MesSetOccupationalHazardDO exist = occupationalhazardMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_OCCUPATIONAL_HAZARD_NO_DUPLICATE);
        }
        if (reqVO.getFactorCategory() != null && !FACTOR_CATEGORYS.contains(reqVO.getFactorCategory())) {
            throw exception(SET_OCCUPATIONAL_HAZARD_FACTOR_CATEGORY_INVALID);
        }
        if (reqVO.getResult() != null && !RESULTS.contains(reqVO.getResult())) {
            throw exception(SET_OCCUPATIONAL_HAZARD_RESULT_INVALID);
        }
    }

}
