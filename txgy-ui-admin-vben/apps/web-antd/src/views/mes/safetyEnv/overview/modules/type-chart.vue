<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

import { computed, onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import { Card, Empty } from 'ant-design-vue';

import { getTypeDistChartOptions } from '../chart-options';

defineOptions({ name: 'MesSafetyEnvTypeChart' });

const props = defineProps<{
  stats: MesSafetyEnvStatisticsApi.RecordStat[];
}>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/** 是否有可绘制的检测类型（总数 > 0） */
const hasData = computed(() => props.stats.some((s) => s.total > 0));

function renderChart() {
  if (hasData.value) {
    renderEcharts(getTypeDistChartOptions(props.stats));
  }
}

onMounted(renderChart);
watch(() => props.stats, renderChart);
</script>

<template>
  <Card title="各类检测记录分布" class="h-full">
    <EchartsUI v-if="hasData" ref="chartRef" class="h-[300px] w-full" />
    <div v-else class="flex h-[300px] items-center justify-center">
      <Empty description="暂无可统计的检测类型" />
    </div>
  </Card>
</template>
