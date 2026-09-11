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

    /**
     * 按统计期自动取数汇总，回写 data_summary 并返回 JSON 文本。
     *
     * 与建单时的自动取数是有意不同的两态（§15.3）：建单缺统计期**静默跳过**（草稿阶段统计期常后补），
     * 显式调本方法缺统计期则抛 {@code SET_ENV_REPORT_PERIOD_MISSING}——人主动要求补数，缺前提就必须响。
     *
     * @param id 报告编号
     * @return data_summary 的 JSON 文本
     */
    String autoSummary(Long id);

    /**
     * 把报告摘要落成 `.json` 存进 infra 文件服务，URL 回写 file_url 并返回（§16.4）。
     * 摘要为空时先自动取数；统计期也没有 → {@code SET_ENV_REPORT_PERIOD_MISSING}，**不产出空壳文件**。
     * 文件服务不可用 → {@code SET_ENV_REPORT_ARCHIVE_FAILED}：报告标成"已上报"却取不回文件，比当场报错危险。
     *
     * @param id 报告编号
     * @return 文件服务 URL
     */
    String archiveSummary(Long id);

}
