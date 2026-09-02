package cn.iocoder.txgy.module.mes.service.set.envreport;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport.MesSetEnvReportDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-环保检测报告 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetEnvReportService {

    /**
     * 创建环保检测报告
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEnvReport(@Valid MesSetEnvReportSaveReqVO createReqVO);

    /**
     * 更新环保检测报告
     *
     * @param updateReqVO 更新信息
     */
    void updateEnvReport(@Valid MesSetEnvReportSaveReqVO updateReqVO);

    /**
     * 删除环保检测报告
     *
     * @param id 编号
     */
    void deleteEnvReport(Long id);

    /**
     * 校验环保检测报告存在
     *
     * @param id 编号
     */
    void validateEnvReportExists(Long id);

    /**
     * 获得环保检测报告
     *
     * @param id 编号
     * @return 环保检测报告
     */
    MesSetEnvReportDO getEnvReport(Long id);

    /**
     * 获得环保检测报告分页
     *
     * @param pageReqVO 分页查询
     * @return 环保检测报告分页
     */
    PageResult<MesSetEnvReportDO> getEnvReportPage(MesSetEnvReportPageReqVO pageReqVO);

}
