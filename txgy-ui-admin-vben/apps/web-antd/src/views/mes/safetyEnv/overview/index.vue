<script lang="ts" setup>
import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { Col, Row } from 'ant-design-vue';

import { getSafetyEnvStatistics } from '#/api/mes/safetyEnv/overview';

import KpiBand from './modules/kpi-band.vue';
import LedgerCard from './modules/ledger-card.vue';
import RateDonut from './modules/rate-donut.vue';
import TrendChart from './modules/trend-chart.vue';
import TypeChart from './modules/type-chart.vue';

defineOptions({ name: 'MesSafetyEnvOverview' });

/** 加载完成前的占位空数据（各图表自行空态降级） */
const defaultSummary: MesSafetyEnvStatisticsApi.Summary = {
  recordTotalCount: 0,
  monthCount: 0,
  passCount: 0,
  failCount: 0,
  passRate: 0,
  standardCount: 0,
  planCount: 0,
  planActiveCount: 0,
  outletCount: 0,
  permitCount: 0,
  wasteCount: 0,
  envReportCount: 0,
  carbonCount: 0,
  carbonEmissionSum: 0,
  recordStats: [],
  monthTrend: [],
};

const summary = ref<MesSafetyEnvStatisticsApi.Summary>(defaultSummary);

/** 加载汇总统计 */
async function loadSummary() {
  summary.value = await getSafetyEnvStatistics();
}

onMounted(() => {
  loadSummary();
});
</script>

<template>
  <Page>
    <!-- 第一行：核心 KPI -->
    <KpiBand :summary="summary" class="mb-4" />

    <!-- 第二行：类型分布 + 合格率环形 -->
    <Row :gutter="16" class="mb-4">
      <Col :lg="16" :md="24" :sm="24" :xl="16" :xs="24" class="mb-4">
        <TypeChart :stats="summary.recordStats" />
      </Col>
      <Col :lg="8" :md="24" :sm="24" :xl="8" :xs="24" class="mb-4">
        <RateDonut
          :fail-count="summary.failCount"
          :pass-count="summary.passCount"
          :pass-rate="summary.passRate"
        />
      </Col>
    </Row>

    <!-- 第三行：月度趋势 + 台账档案 -->
    <Row :gutter="16">
      <Col :lg="16" :md="24" :sm="24" :xl="16" :xs="24" class="mb-4">
        <TrendChart :trend="summary.monthTrend" />
      </Col>
      <Col :lg="8" :md="24" :sm="24" :xl="8" :xs="24" class="mb-4">
        <LedgerCard :summary="summary" />
      </Col>
    </Row>
  </Page>
</template>
