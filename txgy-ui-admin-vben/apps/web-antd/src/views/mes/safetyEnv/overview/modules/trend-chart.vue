<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import { Card } from 'ant-design-vue';

import { getTrendChartOptions } from '../chart-options';

defineOptions({ name: 'MesSafetyEnvTrendChart' });

const props = defineProps<{
  trend: MesSafetyEnvStatisticsApi.TrendPoint[];
}>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/** 后端已补零至近 6 个月，月份轴始终存在，直接绘制 */
function renderChart() {
  renderEcharts(getTrendChartOptions(props.trend));
}

onMounted(renderChart);
watch(() => props.trend, renderChart);
</script>

<template>
  <Card title="近 6 个月检测趋势" class="h-full">
    <EchartsUI ref="chartRef" class="h-[300px] w-full" />
  </Card>
</template>
