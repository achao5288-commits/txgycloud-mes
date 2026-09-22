<script lang="ts" setup>
import type { MesInventoryEnvApi } from '#/api/mes/safetyEnv/inventoryEnv';

import { computed, onMounted, ref } from 'vue';

import { Button, message, Spin, Tag, Tooltip } from 'ant-design-vue';

import { getInventoryEnvDashboard, inspectInventoryEnv } from '#/api/mes/safetyEnv/inventoryEnv';

const emit = defineEmits<{
  /** 下钻：把某个判据作为筛选条件带回列表（null 表示清空筛选） */
  drill: [filter: null | Record<string, any>];
}>();

const dashboard = ref<MesInventoryEnvApi.Dashboard>();
const loading = ref(false);
/** 立即体检的结果；与看板同源但由用户手动触发，显示的是「我按这一下时」的快照 */
const summary = ref<MesInventoryEnvApi.InspectSummary>();

async function loadDashboard() {
  loading.value = true;
  try {
    dashboard.value = await getInventoryEnvDashboard();
    summary.value = dashboard.value.summary;
  } finally {
    loading.value = false;
  }
}

/** 全库体检：只读重算，不落体检批次。有反馈就行，数字跟看板本来就同源 */
async function handleInspect() {
  loading.value = true;
  try {
    summary.value = await inspectInventoryEnv();
    message.success('体检完成');
  } finally {
    loading.value = false;
  }
}

function percent(value?: number) {
  return `${Math.round((value ?? 0) * 1000) / 10}%`;
}

/** 指标卡：可下钻的都带 filter，点了就回列表筛出来 */
const cards = computed(() => {
  const s = summary.value;
  return [
    {
      key: 'pending',
      label: '待检',
      value: s?.pending ?? 0,
      hint: '命中未检/超期/积压/混放任一项的行数',
      filter: { inspectStatus: 'PENDING' },
    },
    {
      key: 'notChecked',
      label: '未检测',
      value: s?.notChecked ?? 0,
      hint: '该批次从未有过判定',
      filter: { inspectStatus: 'NOT_CHECKED' },
    },
    {
      key: 'overdue',
      label: '超期未检',
      value: s?.overdue ?? 0,
      hint: '距上次复核已超过检测周期',
      filter: { inspectStatus: 'OVERDUE' },
    },
    {
      key: 'stockpiled',
      label: '积压',
      value: s?.stockpiled ?? 0,
      hint: '入库超过积压阈值（与判没判过无关）',
      filter: { inspectStatus: 'STOCKPILE' },
    },
    {
      key: 'mixed',
      label: '混放',
      value: s?.mixed ?? 0,
      hint: '同库位下既有污染批次又有非污染批次',
      filter: { mixed: true },
    },
    {
      // 卡片值必须是**行数**：点它下钻到的是列表，用批次数会「卡片显示 3、点开 7 行」
      key: 'polluted',
      label: '已判有污染',
      value: s?.pollutedRows ?? 0,
      hint: `当前污染投影为有污染的行数（涉及 ${s?.pollutedBatchCount ?? 0} 个批次）`,
      filter: { pollutionStatus: 'POLLUTED' },
    },
  ];
});

/** 下钻用的 filter 里可能带 undefined 键，清掉再发，免得后端把 undefined 当筛选值 */
function drill(filter: Record<string, any>) {
  const cleaned: Record<string, any> = {};
  for (const [key, value] of Object.entries(filter)) {
    if (value !== undefined) {
      cleaned[key] = value;
    }
  }
  emit('drill', Object.keys(cleaned).length > 0 ? cleaned : null);
}

onMounted(loadDashboard);

defineExpose({ refresh: loadDashboard });
</script>

<template>
  <Spin :spinning="loading">
    <div class="mb-3 rounded border border-gray-200 p-3">
      <div class="mb-2 flex flex-wrap items-center gap-2">
        <span class="font-medium">合规看板</span>
        <Tag color="blue">覆盖率 {{ percent(dashboard?.coverRate) }}</Tag>
        <Tooltip title="复核为「有污染」的批次数 / 已复核批次数。未判过的批次不计入分母，否则未检越多异常率越低。">
          <Tag color="red">异常率 {{ percent(dashboard?.pollutedRate) }}</Tag>
        </Tooltip>
        <Tooltip title="待复核的判定会冻结对应批次在库，积压越久冻得越久">
          <Tag :color="(dashboard?.pendingReview?.count ?? 0) > 0 ? 'orange' : 'default'">
            待复核 {{ dashboard?.pendingReview?.count ?? 0 }} 条（最长 {{ dashboard?.pendingReview?.maxWaitDays ?? 0 }} 天）
          </Tag>
        </Tooltip>
        <Tooltip title="污染台账从登记到离开「已暂存」的天数，只统计已闭环的行">
          <Tag color="default">
            处置时效 均 {{ dashboard?.ledgerDuration?.avgDays ?? 0 }} 天 / 最长
            {{ dashboard?.ledgerDuration?.maxDays ?? 0 }} 天
          </Tag>
        </Tooltip>
        <div class="ml-auto flex gap-2">
          <Button size="small" :loading="loading" @click="handleInspect">立即体检</Button>
          <Button size="small" @click="emit('drill', null)">清空筛选</Button>
        </div>
      </div>

      <div class="flex flex-wrap gap-2">
        <Tooltip v-for="card in cards" :key="card.key" :title="`${card.hint}（点击下钻到列表）`">
          <button
            class="cursor-pointer rounded border border-gray-200 px-3 py-1.5 text-left transition hover:border-blue-400"
            type="button"
            @click="drill(card.filter)"
          >
            <div class="text-xs text-gray-500">{{ card.label }}</div>
            <div class="text-lg font-semibold">
              {{ card.value }}<span class="ml-0.5 text-xs font-normal text-gray-400">行</span>
            </div>
          </button>
        </Tooltip>
      </div>

      <div v-if="dashboard?.byWarehouse?.length" class="mt-3 flex flex-wrap items-center gap-2">
        <span class="text-xs text-gray-500">按仓库下钻：</span>
        <Tag
          v-for="item in dashboard.byWarehouse"
          :key="item.dimId"
          class="cursor-pointer"
          @click="drill({ warehouseId: item.dimId })"
        >
          {{ item.dimName ?? '未知仓库' }} · 待检
          {{ (item.notChecked ?? 0) + (item.stockpiled ?? 0) }}
        </Tag>
      </div>

      <div v-if="dashboard?.byItemType?.length" class="mt-2 flex flex-wrap items-center gap-2">
        <span class="text-xs text-gray-500">按物料分类下钻：</span>
        <Tag
          v-for="item in dashboard.byItemType"
          :key="item.dimId"
          class="cursor-pointer"
          @click="drill({ itemTypeId: item.dimId })"
        >
          {{ item.dimName ?? '未知分类' }} · 待检
          {{ (item.notChecked ?? 0) + (item.stockpiled ?? 0) }}
        </Tag>
      </div>
    </div>
  </Spin>
</template>
