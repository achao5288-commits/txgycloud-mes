package cn.iocoder.txgy.module.mes.service.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionpermit.MesSetPollutionPermitMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_NOT_EXISTS;

/**
 * MES 安全环保检测-排污许可管理 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPollutionPermitServiceImpl implements MesSetPollutionPermitService {

    @Resource
    private MesSetPollutionPermitMapper pollutionPermitMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPollutionPermit(MesSetPollutionPermitSaveReqVO createReqVO) {
        // 1. 校验排污许可证编号唯一
        validatePermitNoUnique(null, createReqVO.getPermitNo());
        // 2. 插入记录
        MesSetPollutionPermitDO pollutionPermit = BeanUtils.toBean(createReqVO, MesSetPollutionPermitDO.class);
        pollutionPermitMapper.insert(pollutionPermit);
        return pollutionPermit.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePollutionPermit(MesSetPollutionPermitSaveReqVO updateReqVO) {
        // 1. 校验存在 + 排污许可证编号唯一
        validatePollutionPermitExists(updateReqVO.getId());
        validatePermitNoUnique(updateReqVO.getId(), updateReqVO.getPermitNo());
        // 2. 更新
        MesSetPollutionPermitDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPollutionPermitDO.class);
        pollutionPermitMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePollutionPermit(Long id) {
        // 1. 校验存在
        validatePollutionPermitExists(id);
        // 2. 删除
        pollutionPermitMapper.deleteById(id);
    }

    @Override
    public void validatePollutionPermitExists(Long id) {
        if (pollutionPermitMapper.selectById(id) == null) {
            throw exception(SET_POLLUTION_PERMIT_NOT_EXISTS);
        }
    }

    @Override
    public MesSetPollutionPermitDO getPollutionPermit(Long id) {
        return pollutionPermitMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPollutionPermitDO> getPollutionPermitPage(MesSetPollutionPermitPageReqVO pageReqVO) {
        return pollutionPermitMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验排污许可证编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param permitNo 排污许可证编号
     */
    private void validatePermitNoUnique(Long id, String permitNo) {
        MesSetPollutionPermitDO exist = pollutionPermitMapper.selectByPermitNo(permitNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_POLLUTION_PERMIT_NO_DUPLICATE);
        }
    }

}
