package cn.iocoder.txgy.module.mes.service.set.firecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firecheck.vo.MesSetFireCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firecheck.MesSetFireCheckDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.firecheck.MesSetFireCheckMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_FIRE_CHECK_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_FIRE_CHECK_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_FIRE_CHECK_RESULT_INVALID;

/**
 * MES 安全环保检测-消防检查记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetFireCheckServiceImpl implements MesSetFireCheckService {

    /**
     * 结果：PASS/FAIL
     */
    private static final Set<String> RESULTS = new HashSet<>(Arrays.asList("FAIL", "PASS"));

    @Resource
    private MesSetFireCheckMapper firecheckMapper;

    @Override
    public Long createFireCheck(MesSetFireCheckSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetFireCheckDO obj = BeanUtils.toBean(createReqVO, MesSetFireCheckDO.class);
        firecheckMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateFireCheck(MesSetFireCheckSaveReqVO updateReqVO) {
        MesSetFireCheckDO exist = validateFireCheckExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        firecheckMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetFireCheckDO.class));
    }

    @Override
    public void deleteFireCheck(Long id) {
        validateFireCheckExists(id);
        firecheckMapper.deleteById(id);
    }

    @Override
    public MesSetFireCheckDO getFireCheck(Long id) {
        return firecheckMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetFireCheckDO> getFireCheckPage(MesSetFireCheckPageReqVO pageReqVO) {
        return firecheckMapper.selectPage(pageReqVO);
    }

    private MesSetFireCheckDO validateFireCheckExists(Long id) {
        MesSetFireCheckDO obj = firecheckMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_FIRE_CHECK_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、结果：PASS/FAIL枚举
     */
    private void validateBase(MesSetFireCheckSaveReqVO reqVO, String origin) {
        MesSetFireCheckDO exist = firecheckMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_FIRE_CHECK_NO_DUPLICATE);
        }
        if (reqVO.getResult() != null && !RESULTS.contains(reqVO.getResult())) {
            throw exception(SET_FIRE_CHECK_RESULT_INVALID);
        }
    }

}
