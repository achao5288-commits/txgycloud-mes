package cn.iocoder.txgy.module.mes.service.set.hazardouswaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWastePageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWasteSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazardouswaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.hazardouswaste.MesSetHazardousWasteMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_HAZARDOUS_WASTE_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_HAZARDOUS_WASTE_NOT_EXISTS;

/**
 * MES 安全环保检测-危废台账 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetHazardousWasteServiceImpl implements MesSetHazardousWasteService {

    @Resource
    private MesSetHazardousWasteMapper hazardousWasteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHazardousWaste(MesSetHazardousWasteSaveReqVO createReqVO) {
        // 1. 校验转移联单编号唯一
        validateManifestNoUnique(null, createReqVO.getManifestNo());
        // 2. 插入记录
        MesSetHazardousWasteDO hazardousWaste = BeanUtils.toBean(createReqVO, MesSetHazardousWasteDO.class);
        hazardousWasteMapper.insert(hazardousWaste);
        return hazardousWaste.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHazardousWaste(MesSetHazardousWasteSaveReqVO updateReqVO) {
        // 1. 校验存在 + 转移联单编号唯一
        validateHazardousWasteExists(updateReqVO.getId());
        validateManifestNoUnique(updateReqVO.getId(), updateReqVO.getManifestNo());
        // 2. 更新
        MesSetHazardousWasteDO updateObj = BeanUtils.toBean(updateReqVO, MesSetHazardousWasteDO.class);
        hazardousWasteMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHazardousWaste(Long id) {
        // 1. 校验存在
        validateHazardousWasteExists(id);
        // 2. 删除
        hazardousWasteMapper.deleteById(id);
    }

    @Override
    public void validateHazardousWasteExists(Long id) {
        if (hazardousWasteMapper.selectById(id) == null) {
            throw exception(SET_HAZARDOUS_WASTE_NOT_EXISTS);
        }
    }

    @Override
    public MesSetHazardousWasteDO getHazardousWaste(Long id) {
        return hazardousWasteMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetHazardousWasteDO> getHazardousWastePage(MesSetHazardousWastePageReqVO pageReqVO) {
        return hazardousWasteMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验转移联单编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param manifestNo 转移联单编号
     */
    private void validateManifestNoUnique(Long id, String manifestNo) {
        MesSetHazardousWasteDO exist = hazardousWasteMapper.selectByManifestNo(manifestNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_HAZARDOUS_WASTE_NO_DUPLICATE);
        }
    }

}
