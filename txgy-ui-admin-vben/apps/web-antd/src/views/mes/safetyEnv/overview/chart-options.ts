import type { EChartsOption } from '@vben/plugins/echarts';

import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

// 语义色：合格 / 异常 / 主强调
const PASS_COLOR = '#16A34A';
const FAIL_COLOR = '#DC2626';
const ACCENT_COLOR = '#0D9488';

/** 检测类型分布（横向堆叠柱：合格 + 异常） */
export function getTypeDistChartOptions(
  stats: MesSafetyEnvStatisticsApi.RecordStat[],
): EChartsOption {
  const items = stats.filter((s) => s.total > 0);
  // 数量大的类型放顶部，阅读顺序更自然
  const names = items.map((s) => s.typeName).reverse();
  const passData = items.map((s) => s.total - s.fail).reverse();
  const failData = items.map((s) => s.fail).reverse();
  return {
    color: [PASS_COLOR, FAIL_COLOR],
    grid: { bottom: 36, left: 10, right: 24, top: 16, containLabel: true },
    legend: { bottom: 0, data: ['合格', '异常'] },
    series: [
      {
        barMaxWidth: 14,
        data: passData,
        itemStyle: { color: PASS_COLOR, borderRadius: [0, 0, 0, 0] },
        name: '合格',
        type: 'bar',
        stack: 'total',
      },
      {
        barMaxWidth: 14,
        data: failData,
        itemStyle: { color: FAIL_COLOR, borderRadius: [3, 3, 0, 0] },
        name: '异常',
        type: 'bar',
        stack: 'total',
      },
    ],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { minInterval: 1, type: 'value' },
    yAxis: { data: names, type: 'category' },
  };
}

/** 检测合格率环形图 */
export function getRateDonutChartOptions(
  pass: number,
  fail: number,
  rate: number,
): EChartsOption {
  return {
    color: [PASS_COLOR, FAIL_COLOR],
    legend: { bottom: 0, data: ['合格', '异常'] },
    series: [
      {
        avoidLabelOverlap: true,
        data: [
          { name: '合格', value: pass },
          { name: '异常', value: fail },
        ],
        label: { show: false },
        radius: ['58%', '78%'],
        type: 'pie',
      },
    ],
    title: {
      left: 'center',
      subtext: '合格率',
      text: `${rate}%`,
      top: '38%',
    },
    tooltip: { formatter: '{b}: {c} 条 ({d}%)', trigger: 'item' },
  };
}

/** 近 6 个月检测趋势 */
export function getTrendChartOptions(
  trend: MesSafetyEnvStatisticsApi.TrendPoint[],
): EChartsOption {
  return {
    color: [ACCENT_COLOR],
    grid: { bottom: 28, left: 40, right: 20, top: 24 },
    series: [
      {
        areaStyle: {
          color: {
            colorStops: [
              { color: 'rgba(13,148,136,0.28)', offset: 0 },
              { color: 'rgba(13,148,136,0.02)', offset: 1 },
            ],
            type: 'linear',
            x: 0,
            x2: 0,
            y: 0,
            y2: 1,
          },
        },
        data: trend.map((p) => p.count),
        itemStyle: { color: ACCENT_COLOR },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        type: 'line',
      },
    ],
    tooltip: { axisPointer: { type: 'line' }, trigger: 'axis' },
    xAxis: { boundaryGap: false, data: trend.map((p) => p.month), type: 'category' },
    yAxis: { minInterval: 1, type: 'value' },
  };
}
