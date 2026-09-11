package cn.iocoder.txgy.module.mes.service.set.standard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.standard.MesSetStandardMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_DOMAIN_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_PERIOD_TYPE_INVALID;

/**
 * MES 安全环保检测-检测标准 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetStandardServiceImpl implements MesSetStandardService {

    /**
     * 检测域：SAFETY/ENV/HEALTH
     */
    private static final Set<String> DOMAINS = new HashSet<>(Arrays.asList("EXHAUST", "GAS", "HEALTH"));

    /**
     * 周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT
     */
    private static final Set<String> PERIOD_TYPES = new HashSet<>(Arrays.asList("YEAR", "YEARLY"));

    @Resource
    private MesSetStandardMapper standardMapper;

    @Override
    public Long createStandard(MesSetStandardSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetStandardDO obj = BeanUtils.toBean(createReqVO, MesSetStandardDO.class);
        standardMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateStandard(MesSetStandardSaveReqVO updateReqVO) {
        MesSetStandardDO exist = validateStandardExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getStandardNo());
        standardMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetStandardDO.class));
    }

    @Override
    public void deleteStandard(Long id) {
        validateStandardExists(id);
        standardMapper.deleteById(id);
    }

    @Override
    public MesSetStandardDO getStandard(Long id) {
        return standardMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetStandardDO> getStandardPage(MesSetStandardPageReqVO pageReqVO) {
        return standardMapper.selectPage(pageReqVO);
    }

    private MesSetStandardDO validateStandardExists(Long id) {
        MesSetStandardDO obj = standardMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_STANDARD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、检测域：SAFETY/ENV/HEALTH枚举、周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT枚举
     */
    private void validateBase(MesSetStandardSaveReqVO reqVO, String origin) {
        MesSetStandardDO exist = standardMapper.selectByStandardNo(reqVO.getStandardNo());
        if (exist != null && !exist.getStandardNo().equals(origin)) {
            throw exception(SET_STANDARD_NO_DUPLICATE);
        }
        if (reqVO.getDomain() != null && !DOMAINS.contains(reqVO.getDomain())) {
            throw exception(SET_STANDARD_DOMAIN_INVALID);
        }
        if (reqVO.getPeriodType() != null && !PERIOD_TYPES.contains(reqVO.getPeriodType())) {
            throw exception(SET_STANDARD_PERIOD_TYPE_INVALID);
        }
    }

}
