import { requestClient } from '#/api/request';

export namespace MesSafetyEnvStatisticsApi {
  /** 安全环保检测统计汇总 */
  export interface Summary {
    recordTotalCount: number; // 检测记录总数
    monthCount: number; // 本月新增检测记录
    passCount: number; // 合格条数
    failCount: number; // 异常条数
    passRate: number; // 合格率(0-100)
    standardCount: number; // 检测标准
    planCount: number; // 检测计划
    planActiveCount: number; // 执行中计划
    outletCount: number; // 排放口
    permitCount: number; // 排污许可
    wasteCount: number; // 危险废物台账
    envReportCount: number; // 环保报告
    carbonCount: number; // 碳排放核算
    carbonEmissionSum: number; // 碳排放总量(tCO2e)
    recordStats: RecordStat[]; // 各检测类型统计
    monthTrend: TrendPoint[]; // 近 6 个月趋势
  }

  /** 某检测类型统计 */
  export interface RecordStat {
    type: string; // 类型编码
    typeName: string; // 类型名称
    total: number; // 总条数
    fail: number; // 异常条数
  }

  /** 月度趋势点 */
  export interface TrendPoint {
    month: string; // yyyy-MM
    count: number; // 检测条数
  }
}

/** 获得安全环保检测汇总统计 */
export function getSafetyEnvStatistics() {
  return requestClient.get<MesSafetyEnvStatisticsApi.Summary>(
    '/mes/safety-env/statistics/summary',
  );
}
