package cn.iocoder.txgy.module.mes.service.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.occupationalhazard.MesSetOccupationalHazardMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_OCCUPATIONAL_HAZARD_NOT_EXISTS;

/**
 * MES 安全环保检测-职业病危害因素检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetOccupationalHazardServiceImpl implements MesSetOccupationalHazardService {

    @Resource
    private MesSetOccupationalHazardMapper occupationalHazardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOccupationalHazard(MesSetOccupationalHazardSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetOccupationalHazardDO occupationalHazard = BeanUtils.toBean(createReqVO, MesSetOccupationalHazardDO.class);
        occupationalHazardMapper.insert(occupationalHazard);
        return occupationalHazard.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOccupationalHazard(MesSetOccupationalHazardSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateOccupationalHazardExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetOccupationalHazardDO updateObj = BeanUtils.toBean(updateReqVO, MesSetOccupationalHazardDO.class);
        occupationalHazardMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOccupationalHazard(Long id) {
        // 1. 校验存在
        validateOccupationalHazardExists(id);
        // 2. 删除
        occupationalHazardMapper.deleteById(id);
    }

    @Override
    public void validateOccupationalHazardExists(Long id) {
        if (occupationalHazardMapper.selectById(id) == null) {
            throw exception(SET_OCCUPATIONAL_HAZARD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetOccupationalHazardDO getOccupationalHazard(Long id) {
        return occupationalHazardMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetOccupationalHazardDO> getOccupationalHazardPage(MesSetOccupationalHazardPageReqVO pageReqVO) {
        return occupationalHazardMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetOccupationalHazardDO exist = occupationalHazardMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_OCCUPATIONAL_HAZARD_NO_DUPLICATE);
        }
    }

}
