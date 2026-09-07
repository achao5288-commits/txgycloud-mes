package cn.iocoder.txgy.module.mes.service.safetyenvstatistics;

import cn.iocoder.txgy.module.mes.controller.admin.safetyenvstatistics.vo.MesSafetyEnvStatisticsRespVO;

/**
 * MES 安全环保检测统计 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSafetyEnvStatisticsService {

    /**
     * 获得安全环保检测汇总统计
     *
     * @return 汇总统计数据
     */
    MesSafetyEnvStatisticsRespVO getMesSafetyEnvStatistics();

}
