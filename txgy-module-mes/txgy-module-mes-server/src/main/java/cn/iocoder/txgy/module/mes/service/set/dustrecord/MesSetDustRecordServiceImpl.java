package cn.iocoder.txgy.module.mes.service.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.dustrecord.MesSetDustRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_DUST_RECORD_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_DUST_RECORD_NO_DUPLICATE;

/**
 * MES 安全环保检测-粉尘检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetDustRecordServiceImpl implements MesSetDustRecordService {

    @Resource
    private MesSetDustRecordMapper dustrecordMapper;

    @Override
    public Long createDustRecord(MesSetDustRecordSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetDustRecordDO obj = BeanUtils.toBean(createReqVO, MesSetDustRecordDO.class);
        dustrecordMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateDustRecord(MesSetDustRecordSaveReqVO updateReqVO) {
        MesSetDustRecordDO exist = validateDustRecordExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getRecordNo());
        dustrecordMapper.updateById(BeanUtils.toBean(updateReqVO, MesSetDustRecordDO.class));
    }

    @Override
    public void deleteDustRecord(Long id) {
        validateDustRecordExists(id);
        dustrecordMapper.deleteById(id);
    }

    @Override
    public MesSetDustRecordDO getDustRecord(Long id) {
        return dustrecordMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetDustRecordDO> getDustRecordPage(MesSetDustRecordPageReqVO pageReqVO) {
        return dustrecordMapper.selectPage(pageReqVO);
    }

    private MesSetDustRecordDO validateDustRecordExists(Long id) {
        MesSetDustRecordDO obj = dustrecordMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_DUST_RECORD_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)
     */
    private void validateBase(MesSetDustRecordSaveReqVO reqVO, String origin) {
        MesSetDustRecordDO exist = dustrecordMapper.selectByRecordNo(reqVO.getRecordNo());
        if (exist != null && !exist.getRecordNo().equals(origin)) {
            throw exception(SET_DUST_RECORD_NO_DUPLICATE);
        }
    }

}
