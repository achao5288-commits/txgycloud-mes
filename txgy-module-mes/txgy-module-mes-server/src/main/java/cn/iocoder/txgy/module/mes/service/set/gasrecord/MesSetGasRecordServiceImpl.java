package cn.iocoder.txgy.module.mes.service.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.gasrecord.MesSetGasRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_GAS_RECORD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_GAS_RECORD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_GAS_RECORD_GAS_TYPE_INVALID;

/**
 * MES 安全环保检测-气体检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetGasRecordServiceImpl implements MesSetGasRecordService {

    /**
     * 气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2
     */
    private static final Set<String> GAS_TYPES = new HashSet<>(Arrays.asList("CO", "NO2"));

    @Resource
    private MesSetGasRecordMapper gasrecordMapper;

    @Override
    public Long createGasRecord(MesSetGasRecordSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetGasRecordDO obj = BeanUtils.toBean(createReqVO, MesSetGasRecordDO.class);
        gasrecordMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateGasRecord(MesSetGasRecordSaveReqVO updateReqVO) {
        MesSetGasRecordDO exist = validateGasRecordExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        gasrecordMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetGasRecordDO.class));
    }

    @Override
    public void deleteGasRecord(Long id) {
        validateGasRecordExists(id);
        gasrecordMapper.deleteById(id);
    }

    @Override
    public MesSetGasRecordDO getGasRecord(Long id) {
        return gasrecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetGasRecordDO> getGasRecordPage(MesSetGasRecordPageReqVO pageReqVO) {
        return gasrecordMapper.selectPage(pageReqVO);
    }

    private MesSetGasRecordDO validateGasRecordExists(Long id) {
        MesSetGasRecordDO obj = gasrecordMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_GAS_RECORD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2枚举
     */
    private void validateBase(MesSetGasRecordSaveReqVO reqVO, String origin) {
        MesSetGasRecordDO exist = gasrecordMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_GAS_RECORD_NO_DUPLICATE);
        }
        if (reqVO.getGasType() != null && !GAS_TYPES.contains(reqVO.getGasType())) {
            throw exception(SET_GAS_RECORD_GAS_TYPE_INVALID);
        }
    }

}
