package cn.iocoder.txgy.module.mes.service.set.envreport;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport.MesSetEnvReportDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.envreport.MesSetEnvReportMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_ENV_REPORT_NOT_EXISTS;

/**
 * MES 安全环保检测-环保检测报告 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEnvReportServiceImpl implements MesSetEnvReportService {

    @Resource
    private MesSetEnvReportMapper envReportMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEnvReport(MesSetEnvReportSaveReqVO createReqVO) {
        // 1. 校验报告编号唯一
        validateReportNoUnique(null, createReqVO.getReportNo());
        // 2. 插入记录
        MesSetEnvReportDO envReport = BeanUtils.toBean(createReqVO, MesSetEnvReportDO.class);
        envReportMapper.insert(envReport);
        return envReport.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnvReport(MesSetEnvReportSaveReqVO updateReqVO) {
        // 1. 校验存在 + 报告编号唯一
        validateEnvReportExists(updateReqVO.getId());
        validateReportNoUnique(updateReqVO.getId(), updateReqVO.getReportNo());
        // 2. 更新
        MesSetEnvReportDO updateObj = BeanUtils.toBean(updateReqVO, MesSetEnvReportDO.class);
        envReportMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnvReport(Long id) {
        // 1. 校验存在
        validateEnvReportExists(id);
        // 2. 删除
        envReportMapper.deleteById(id);
    }

    @Override
    public void validateEnvReportExists(Long id) {
        if (envReportMapper.selectById(id) == null) {
            throw exception(SET_ENV_REPORT_NOT_EXISTS);
        }
    }

    @Override
    public MesSetEnvReportDO getEnvReport(Long id) {
        return envReportMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEnvReportDO> getEnvReportPage(MesSetEnvReportPageReqVO pageReqVO) {
        return envReportMapper.selectPage(pageReqVO);
    }

    // ==================== 校验方法 ====================

    /**
     * 校验报告编号是否唯一（更新时排除自身）
     *
     * @param id 编号
     * @param reportNo 报告编号
     */
    private void validateReportNoUnique(Long id, String reportNo) {
        MesSetEnvReportDO exist = envReportMapper.selectByReportNo(reportNo);
        if (exist != null && !exist.getId().equals(id)) {
            throw exception(SET_ENV_REPORT_NO_DUPLICATE);
        }
    }

}
