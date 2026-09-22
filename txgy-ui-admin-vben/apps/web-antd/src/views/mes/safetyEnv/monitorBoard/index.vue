<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';

import type { MesMonitorApi } from '#/api/mes/safetyEnv/monitorBoard';

import { computed, onBeforeUnmount, onMounted, ref } from 'vue';

import { useAccess } from '@vben/access';
import { Page } from '@vben/common-ui';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import { Alert, Button, Card, Empty, Modal, Select, Spin, Table, Tag } from 'ant-design-vue';

import { getMonitorBoard, getMonitorLive } from '#/api/mes/safetyEnv/monitorBoard';

import ExceedTable from './modules/exceed-table.vue';

const { hasAccessByCodes } = useAccess();
const canDispatch = hasAccessByCodes(['mes:set-monitor:dispatch']);
const canClose = hasAccessByCodes(['mes:set-monitor:close']);

const loading = ref(true);
const board = ref<MesMonitorApi.Board>({});
const range = ref('24h');
const factor = ref<string | undefined>(undefined);

/** 标题跟着档位走：写死「24 小时趋势」时切到近 7 天，图上是 7 个点而标题还写着 24 小时 */
const rangeLabel = computed(
  () =>
    ({ '24h': '24 小时', '7d': '近 7 天', '30d': '近 30 天' })[range.value] ?? '',
);

/** 因子下拉的选项来自后端实际返回的因子，换一批排口/因子前端不用改 */
const factorOptions = computed(() =>
  (board.value.metrics ?? []).map((m) => ({
    label: m.pollutantName ?? '',
    value: m.pollutantName ?? '',
  })),
);

async function load() {
  loading.value = true;
  try {
    board.value = await getMonitorBoard({
      range: range.value,
      factor: factor.value,
    });
    await drawTrend();
  } finally {
    loading.value = false;
  }
}

onMounted(load);

// ==================== 24 小时趋势 ====================

const trendRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(trendRef);

function drawTrend() {
  const points = board.value.hourTrend ?? [];
  return renderEcharts({
    grid: { bottom: 30, left: 55, right: 20, top: 34 },
    legend: { data: ['小时均值', '限值'], right: 10, top: 0 },
    series: [
      {
        // connectNulls:false —— 掉线的小时断开，不连过去。
        // 连起来看着「一直很平稳」，恰恰把「这段时间根本没数据」藏掉了
        connectNulls: false,
        data: points.map((p) => p.value ?? null),
        name: '小时均值',
        smooth: true,
        type: 'line',
      },
      {
        data: points.map(() => points[0]?.limit ?? null),
        lineStyle: { type: 'dashed' },
        name: '限值',
        symbol: 'none',
        type: 'line',
      },
    ],
    tooltip: { trigger: 'axis' },
    xAxis: {
      boundaryGap: false,
      data: points.map((p) => p.hour ?? ''),
      type: 'category',
    },
    yAxis: { name: '浓度', type: 'value' },
  });
}

// ==================== 大屏（每 2 秒轮询） ====================

const liveOpen = ref(false);
const liveLoading = ref(false);
const live = ref<MesMonitorApi.Board>({});
let lvTimer: null | ReturnType<typeof setInterval> = null;

/**
 * 轮询只在**大屏打开时**跑，切回看板即停表。
 * 挂屏背后的会话是登录态，但人在看别的页面时的那些请求是纯白送的。
 */
async function tick() {
  try {
    live.value = await getMonitorLive({ range: range.value });
  } catch {
    // 偶发失败保留上一屏：挂屏一断就黑，比数字陈旧更糟
  }
}

async function openLive() {
  liveOpen.value = true;
  liveLoading.value = true;
  await tick();
  liveLoading.value = false;
  lvTimer = setInterval(tick, 2000);
}

function closeLive() {
  liveOpen.value = false;
  if (lvTimer !== null) {
    clearInterval(lvTimer);
    lvTimer = null;
  }
}

onBeforeUnmount(closeLive);

/** 大屏首帧还没回来时先沿用看板数据，避免一屏空白 */
const liveBoard = computed(() => (live.value.outletTotal ? live.value : board.value));

// ==================== 展示工具 ====================

const OUTLET_STATUS: Record<string, { color: string; text: string }> = {
  ONLINE: { color: 'green', text: '在线' },
  STALE: { color: 'orange', text: '数据延迟' },
  OFFLINE: { color: 'default', text: '掉线' },
};

/** 负荷率阈值只有这一份，表格与图表共用，不会出现「表里是黄的、别处是青的」 */
function loadTag(v?: null | number, limit?: number) {
  if (v == null || !limit) {
    return { color: 'default', text: '无有效值' };
  }
  const pct = (v / limit) * 100;
  if (pct >= 100) {
    return { color: 'red', text: `负荷 ${pct.toFixed(0)}%` };
  }
  if (pct >= 80) {
    return { color: 'orange', text: `负荷 ${pct.toFixed(0)}%` };
  }
  return { color: 'green', text: `负荷 ${pct.toFixed(0)}%` };
}

const kpis = computed(() => {
  const b = board.value;
  const d = b.delta ?? {};
  const delta = (v?: null | number, unit = '') =>
    v === null || v === undefined || v === 0
      ? ''
      : `${v > 0 ? '↑' : '↓'} ${Math.abs(v)}${unit}`;
  return [
    {
      label: '排口在线率',
      value: b.onlineRate == null ? '-' : `${b.onlineRate}%`,
      hint: `${b.outletOnline ?? 0} / ${b.outletTotal ?? 0} 个排口在线`,
      delta: delta(d.onlineRate, ' pt'),
      danger: (b.outletTotal ?? 0) > 0 && b.onlineRate != null && b.onlineRate < 90,
    },
    {
      label: '当前超标排口',
      value: b.exceedCount ?? 0,
      hint: '最新一条有效读数越限',
      delta: delta(d.exceedCount),
      danger: (b.exceedCount ?? 0) > 0,
    },
    {
      label: '今日超标次数',
      value: b.todayExceedTimes ?? 0,
      hint: '按小时均值判定，同一小时计 1 次',
      delta: delta(d.todayExceedTimes),
      danger: (b.todayExceedTimes ?? 0) > 0,
    },
    {
      label: '数据完整率',
      value: b.dataCompleteRate == null ? '-' : `${b.dataCompleteRate}%`,
      hint: '仅 CEMS 口径 · 低于 90% 视为采集缺失',
      delta: delta(d.dataCompleteRate, ' pt'),
      danger: b.dataCompleteRate != null && b.dataCompleteRate < 90,
    },
  ];
});
</script>

<template>
  <Page auto-content-height>
    <Spin :spinning="loading">
      <div class="mb-3 flex flex-wrap items-center gap-3">
        <Select
          v-model:value="range"
          :options="[
            { label: '近 24 小时', value: '24h' },
            { label: '近 7 天', value: '7d' },
            { label: '近 30 天', value: '30d' },
          ]"
          class="w-32"
          @change="load"
        />
        <Select
          v-model:value="factor"
          :allow-clear="true"
          :options="factorOptions"
          class="w-44"
          placeholder="监控因子（默认取读数最多）"
          @change="load"
        />
        <Button @click="load">重新加载</Button>
        <Button type="primary" @click="openLive">进入实时监控大屏</Button>
      </div>

      <div class="mb-3 grid grid-cols-2 gap-3 lg:grid-cols-4">
        <Card v-for="k in kpis" :key="k.label" size="small">
          <div class="text-sm text-gray-500">{{ k.label }}</div>
          <div
            class="mt-1 text-2xl font-semibold"
            :class="k.danger ? 'text-red-500' : ''"
          >
            {{ k.value }}
          </div>
          <div class="flex flex-wrap gap-2 text-xs text-gray-400">
            <span>{{ k.hint }}</span>
            <span v-if="k.delta" class="text-orange-500">环比 {{ k.delta }}</span>
          </div>
        </Card>
      </div>

      <Alert
        v-if="board.meta"
        class="mb-3"
        type="info"
        show-icon
        :message="`数据来源：${board.meta.source} · ${board.meta.freq}`"
        :description="board.meta.caliber"
      />

      <Card
        v-if="(board.hourTrend ?? []).length > 0"
        class="mb-3"
        size="small"
        :title="`${rangeLabel}浓度趋势`"
      >
        <EchartsUI ref="trendRef" height="260px" />
      </Card>
      <Card v-else class="mb-3" size="small" :title="`${rangeLabel}浓度趋势`">
        <Empty
          :image="Empty.PRESENTED_IMAGE_SIMPLE"
          description="该窗口没有读数"
        />
      </Card>

      <div class="mb-3 grid grid-cols-1 gap-3 xl:grid-cols-2">
        <Card size="small" title="因子实时值">
          <Table
            v-if="(board.metrics ?? []).length > 0"
            size="small"
            row-key="pollutantName"
            :columns="[
              { title: '监控因子', dataIndex: 'pollutantName' },
              { title: '实测值', dataIndex: 'value', width: 110 },
              { title: '限值', dataIndex: 'limit', width: 100 },
              { title: '负荷', dataIndex: 'load', width: 130 },
            ]"
            :data-source="board.metrics ?? []"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'load'">
                <Tag :color="loadTag(record.value, record.limit).color">
                  {{ loadTag(record.value, record.limit).text }}
                </Tag>
              </template>
            </template>
          </Table>
          <Empty
            v-else
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="没有可判定的因子"
          />
        </Card>

        <Card size="small">
          <template #title>
            各排口达标率
            <span class="ml-2 text-xs font-normal text-gray-400">
              分子分母均排除掉线 / 数据延迟排口
            </span>
          </template>
          <Table
            v-if="(board.outletRates ?? []).length > 0"
            size="small"
            row-key="outletNo"
            :columns="[
              { title: '排放口', dataIndex: 'outletNo', width: 130 },
              { title: '名称', dataIndex: 'outletName' },
              { title: '达标率', dataIndex: 'passRate', width: 110 },
              { title: '超标次数', dataIndex: 'exceedCount', width: 100 },
            ]"
            :data-source="board.outletRates ?? []"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'passRate'">
                <!-- 没数据不等于达标：null 显示成「—」，绝不能显示成 100% -->
                <span v-if="record.passRate == null">—</span>
                <span
                  v-else
                  :class="record.passRate < 90 ? 'font-semibold text-red-500' : ''"
                >
                  {{ record.passRate }}%
                </span>
              </template>
            </template>
          </Table>
          <Empty
            v-else
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="没有在线排口可考核"
          />
        </Card>
      </div>

      <Card class="mb-3" size="small" title="排口实时状态">
        <Table
          size="small"
          row-key="outletNo"
          :columns="[
            { title: '排放口', dataIndex: 'outletNo', width: 130 },
            { title: '名称', dataIndex: 'outletName' },
            { title: '主因子', dataIndex: 'pollutantName', width: 110 },
            { title: '最新实测', dataIndex: 'value', width: 160 },
            { title: '限值', dataIndex: 'limit', width: 90 },
            { title: '今日超标', dataIndex: 'todayExceed', width: 100 },
            { title: '最近上报', dataIndex: 'lastReport', width: 130 },
            { title: '状态', dataIndex: 'status', width: 110 },
          ]"
          :data-source="board.outletLive ?? []"
          :pagination="false"
          :scroll="{ x: 1100 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'value'">
              <!-- 掉线/数据延迟显示成「—」：填 0 会把它读成一个真实的零浓度 -->
              <span v-if="record.value == null" class="text-gray-400">—</span>
              <Tag v-else :color="loadTag(record.value, record.limit).color">
                {{ record.value }} {{ loadTag(record.value, record.limit).text }}
              </Tag>
            </template>
            <template v-else-if="column.dataIndex === 'todayExceed'">
              <span :class="(record.todayExceed ?? 0) > 0 ? 'text-red-500' : ''">
                {{ record.todayExceed ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="OUTLET_STATUS[record.status as string]?.color ?? 'default'">
                {{ OUTLET_STATUS[record.status as string]?.text ?? record.status }}
              </Tag>
            </template>
          </template>
        </Table>
      </Card>

      <Card size="small" title="超标明细">
        <ExceedTable
          :can-close="canClose"
          :can-dispatch="canDispatch"
          :records="board.exceedRecords ?? []"
          @changed="load"
        />
      </Card>
    </Spin>

    <Modal
      v-model:open="liveOpen"
      :footer="null"
      :mask-closable="false"
      :title="`实时监控大屏 · ${liveBoard.meta?.source ?? ''} · 每 2 秒刷新`"
      :width="'100%'"
      destroy-on-close
      wrap-class-name="monitor-live-modal"
    >
      <Spin :spinning="liveLoading">
        <div class="mb-3 grid grid-cols-2 gap-3 lg:grid-cols-4">
          <Card v-for="k in kpis" :key="k.label" size="small">
            <div class="text-sm text-gray-500">{{ k.label }}</div>
            <div
              class="mt-1 text-3xl font-semibold"
              :class="k.danger ? 'text-red-500' : ''"
            >
              {{ k.value }}
            </div>
          </Card>
        </div>
        <Card class="mb-3" size="small" title="排口实时状态">
          <Table
            size="small"
            row-key="outletNo"
            :columns="[
              { title: '排放口', dataIndex: 'outletNo', width: 120 },
              { title: '主因子', dataIndex: 'pollutantName', width: 100 },
              { title: '最新实测', dataIndex: 'value', width: 150 },
              { title: '限值', dataIndex: 'limit', width: 90 },
              { title: '今日超标', dataIndex: 'todayExceed', width: 100 },
              { title: '最近上报', dataIndex: 'lastReport', width: 130 },
              { title: '状态', dataIndex: 'status', width: 110 },
            ]"
            :data-source="liveBoard.outletLive ?? []"
            :pagination="false"
            :scroll="{ x: 900 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'value'">
                <span v-if="record.value == null" class="text-gray-400">—</span>
                <Tag v-else :color="loadTag(record.value, record.limit).color">
                  {{ record.value }} {{ loadTag(record.value, record.limit).text }}
                </Tag>
              </template>
              <template v-else-if="column.dataIndex === 'todayExceed'">
                <span :class="(record.todayExceed ?? 0) > 0 ? 'text-red-500' : ''">
                  {{ record.todayExceed ?? 0 }}
                </span>
              </template>
              <template v-else-if="column.dataIndex === 'status'">
                <Tag
                  :color="OUTLET_STATUS[record.status as string]?.color ?? 'default'"
                >
                  {{ OUTLET_STATUS[record.status as string]?.text ?? record.status }}
                </Tag>
              </template>
            </template>
          </Table>
        </Card>
        <Card size="small" title="超标明细">
          <ExceedTable
            :can-close="canClose"
            :can-dispatch="canDispatch"
            :records="liveBoard.exceedRecords ?? []"
            compact
            @changed="tick"
          />
        </Card>
      </Spin>
    </Modal>
  </Page>
</template>

<style>
/* 大屏占满整屏：Modal 默认的上下留白会把最后一行排口切掉，
   而挂屏上没人在意留白好不好看，在意的是最后一个排口看不看得见 */
.monitor-live-modal .ant-modal {
  max-width: 100vw;
  padding-bottom: 0;
  top: 0;
}
</style>
