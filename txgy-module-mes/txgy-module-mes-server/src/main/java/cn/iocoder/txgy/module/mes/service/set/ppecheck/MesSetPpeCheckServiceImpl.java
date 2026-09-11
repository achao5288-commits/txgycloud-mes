package cn.iocoder.txgy.module.mes.service.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.ppecheck.MesSetPpeCheckMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_CHECK_MODE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_PPE_CHECK_RESULT_INVALID;

/**
 * MES 安全环保检测-劳保用品检查 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPpeCheckServiceImpl implements MesSetPpeCheckService {

    /**
     * 检查方式：AI_VISION/MANUAL
     */
    private static final Set<String> CHECK_MODES = new HashSet<>(Arrays.asList("AI_VISION", "MANUAL"));

    /**
     * 结果：PASS/FAIL
     */
    private static final Set<String> RESULTS = new HashSet<>(Arrays.asList("FAIL", "PASS"));

    @Resource
    private MesSetPpeCheckMapper ppecheckMapper;

    @Override
    public Long createPpeCheck(MesSetPpeCheckSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetPpeCheckDO obj = BeanUtils.toBean(createReqVO, MesSetPpeCheckDO.class);
        ppecheckMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updatePpeCheck(MesSetPpeCheckSaveReqVO updateReqVO) {
        MesSetPpeCheckDO exist = validatePpeCheckExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        ppecheckMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetPpeCheckDO.class));
    }

    @Override
    public void deletePpeCheck(Long id) {
        validatePpeCheckExists(id);
        ppecheckMapper.deleteById(id);
    }

    @Override
    public MesSetPpeCheckDO getPpeCheck(Long id) {
        return ppecheckMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPpeCheckDO> getPpeCheckPage(MesSetPpeCheckPageReqVO pageReqVO) {
        return ppecheckMapper.selectPage(pageReqVO);
    }

    private MesSetPpeCheckDO validatePpeCheckExists(Long id) {
        MesSetPpeCheckDO obj = ppecheckMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_PPE_CHECK_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、检查方式：AI_VISION/MANUAL枚举、结果：PASS/FAIL枚举
     */
    private void validateBase(MesSetPpeCheckSaveReqVO reqVO, String origin) {
        MesSetPpeCheckDO exist = ppecheckMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_PPE_CHECK_NO_DUPLICATE);
        }
        if (reqVO.getCheckMode() != null && !CHECK_MODES.contains(reqVO.getCheckMode())) {
            throw exception(SET_PPE_CHECK_CHECK_MODE_INVALID);
        }
        if (reqVO.getResult() != null && !RESULTS.contains(reqVO.getResult())) {
            throw exception(SET_PPE_CHECK_RESULT_INVALID);
        }
    }

}
