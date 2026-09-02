package cn.iocoder.txgy.module.mes.service.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.wastewater.MesSetWastewaterMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_WASTEWATER_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_WASTEWATER_NOT_EXISTS;

/**
 * MES 安全环保检测-废水排放检测记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetWastewaterServiceImpl implements MesSetWastewaterService {

    @Resource
    private MesSetWastewaterMapper wastewaterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWastewater(MesSetWastewaterSaveReqVO createReqVO) {
        // 1. 校验记录编号唯一
        validateRecordNoUnique(null, createReqVO.getRecordNo());
        // 2. 插入记录
        MesSetWastewaterDO wastewater = BeanUtils.toBean(createReqVO, MesSetWastewaterDO.class);
        wastewaterMapper.insert(wastewater);
        return wastewater.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWastewater(MesSetWastewaterSaveReqVO updateReqVO) {
        // 1. 校验存在 + 记录编号唯一
        validateWastewaterExists(updateReqVO.getId());
        validateRecordNoUnique(updateReqVO.getId(), updateReqVO.getRecordNo());
        // 2. 更新
        MesSetWastewaterDO updateObj = BeanUtils.toBean(updateReqVO, MesSetWastewaterDO.class);
        wastewaterMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWastewater(Long id) {
        // 1. 校验存在
        validateWastewaterExists(id);
        // 2. 删除
        wastewaterMapper.deleteById(id);
    }

    @Override
    public void validateWastewaterExists(Long id) {
        if (wastewaterMapper.selectById(id) == null) {
            throw exception(SET_WASTEWATER_NOT_EXISTS);
        }
    }

    @Override
    public MesSetWastewaterDO getWastewater(Long id) {
        return wastewaterMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetWastewaterDO> getWastewaterPage(MesSetWastewaterPageReqVO pageReqVO) {
        return wastewaterMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验记录编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param recordNo 记录编号
     */
    private void validateRecordNoUnique(Long id, String recordNo) {
        MesSetWastewaterDO exist = wastewaterMapper.selectByRecordNo(recordNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_WASTEWATER_NO_DUPLICATE);
        }
    }

}
