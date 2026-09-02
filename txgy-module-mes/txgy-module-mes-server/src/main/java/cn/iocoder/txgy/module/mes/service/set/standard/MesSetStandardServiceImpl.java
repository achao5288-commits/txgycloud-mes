package cn.iocoder.txgy.module.mes.service.set.standard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.standard.MesSetStandardMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_STANDARD_NOT_EXISTS;

/**
 * MES 安全环保检测-检测标准 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetStandardServiceImpl implements MesSetStandardService {

    @Resource
    private MesSetStandardMapper standardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStandard(MesSetStandardSaveReqVO createReqVO) {
        // 1. 校验标准编号唯一
        validateStandardNoUnique(null, createReqVO.getStandardNo());
        // 2. 插入记录（状态为空时由 DB 默认 DRAFT）
        MesSetStandardDO standard = BeanUtils.toBean(createReqVO, MesSetStandardDO.class);
        standardMapper.insert(standard);
        return standard.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStandard(MesSetStandardSaveReqVO updateReqVO) {
        // 1. 校验存在 + 标准编号唯一
        validateStandardExists(updateReqVO.getId());
        validateStandardNoUnique(updateReqVO.getId(), updateReqVO.getStandardNo());
        // 2. 更新
        MesSetStandardDO updateObj = BeanUtils.toBean(updateReqVO, MesSetStandardDO.class);
        standardMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStandard(Long id) {
        // 1. 校验存在
        validateStandardExists(id);
        // 2. 删除
        standardMapper.deleteById(id);
    }

    @Override
    public void validateStandardExists(Long id) {
        if (standardMapper.selectById(id) == null) {
            throw exception(SET_STANDARD_NOT_EXISTS);
        }
    }

    @Override
    public MesSetStandardDO getStandard(Long id) {
        return standardMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetStandardDO> getStandardPage(MesSetStandardPageReqVO pageReqVO) {
        return standardMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesSetStandardDO> getStandardList() {
        return standardMapper.selectListByStatus("ACTIVE");
    }

    // ==================== 校验方法 ====================

    /**
     * 校验标准编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param standardNo 标准编号
     */
    private void validateStandardNoUnique(Long id, String standardNo) {
        MesSetStandardDO exist = standardMapper.selectOne(MesSetStandardDO::getStandardNo, standardNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_STANDARD_NO_DUPLICATE);
        }
    }

}
