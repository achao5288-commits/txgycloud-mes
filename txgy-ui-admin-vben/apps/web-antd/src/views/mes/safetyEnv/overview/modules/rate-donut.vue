<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';

import { computed, onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import { Card, Empty } from 'ant-design-vue';

import { getRateDonutChartOptions } from '../chart-options';

defineOptions({ name: 'MesSafetyEnvRateDonut' });

const props = defineProps<{
  passCount: number;
  failCount: number;
  passRate: number;
}>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/** 是否存在检测记录 */
const hasData = computed(() => props.passCount + props.failCount > 0);

function renderChart() {
  if (hasData.value) {
    renderEcharts(
      getRateDonutChartOptions(
        props.passCount,
        props.failCount,
        props.passRate,
      ),
    );
  }
}

onMounted(renderChart);
watch(
  () => [props.passCount, props.failCount, props.passRate],
  renderChart,
);
</script>

<template>
  <Card title="检测合格率" class="h-full">
    <EchartsUI v-if="hasData" ref="chartRef" class="h-[300px] w-full" />
    <div v-else class="flex h-[300px] items-center justify-center">
      <Empty description="暂无检测记录" />
    </div>
  </Card>
</template>
