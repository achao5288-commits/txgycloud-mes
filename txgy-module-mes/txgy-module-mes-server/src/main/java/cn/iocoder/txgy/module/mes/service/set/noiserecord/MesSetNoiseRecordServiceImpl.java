package cn.iocoder.txgy.module.mes.service.set.noiserecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord.MesSetNoiseRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.noiserecord.MesSetNoiseRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_COLLECTION_MODE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_NOISE_RECORD_RESULT_INVALID;

/**
 * MES 安全环保检测-噪声检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetNoiseRecordServiceImpl implements MesSetNoiseRecordService {

    /**
     * 采集方式：IOT_AUTO/MANUAL
     */
    private static final Set<String> COLLECTION_MODES = new HashSet<>(Arrays.asList("AUTO", "MANUAL"));

    /**
     * 结果：PASS/FAIL
     */
    private static final Set<String> RESULTS = new HashSet<>(Arrays.asList("FAIL", "PASS"));

    @Resource
    private MesSetNoiseRecordMapper noiserecordMapper;

    @Override
    public Long createNoiseRecord(MesSetNoiseRecordSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetNoiseRecordDO obj = BeanUtils.toBean(createReqVO, MesSetNoiseRecordDO.class);
        noiserecordMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateNoiseRecord(MesSetNoiseRecordSaveReqVO updateReqVO) {
        MesSetNoiseRecordDO exist = validateNoiseRecordExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        noiserecordMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetNoiseRecordDO.class));
    }

    @Override
    public void deleteNoiseRecord(Long id) {
        validateNoiseRecordExists(id);
        noiserecordMapper.deleteById(id);
    }

    @Override
    public MesSetNoiseRecordDO getNoiseRecord(Long id) {
        return noiserecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetNoiseRecordDO> getNoiseRecordPage(MesSetNoiseRecordPageReqVO pageReqVO) {
        return noiserecordMapper.selectPage(pageReqVO);
    }

    private MesSetNoiseRecordDO validateNoiseRecordExists(Long id) {
        MesSetNoiseRecordDO obj = noiserecordMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_NOISE_RECORD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、采集方式：IOT_AUTO/MANUAL枚举、结果：PASS/FAIL枚举
     */
    private void validateBase(MesSetNoiseRecordSaveReqVO reqVO, String origin) {
        MesSetNoiseRecordDO exist = noiserecordMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_NOISE_RECORD_NO_DUPLICATE);
        }
        if (reqVO.getCollectionMode() != null && !COLLECTION_MODES.contains(reqVO.getCollectionMode())) {
            throw exception(SET_NOISE_RECORD_COLLECTION_MODE_INVALID);
        }
        if (reqVO.getResult() != null && !RESULTS.contains(reqVO.getResult())) {
            throw exception(SET_NOISE_RECORD_RESULT_INVALID);
        }
    }

}
