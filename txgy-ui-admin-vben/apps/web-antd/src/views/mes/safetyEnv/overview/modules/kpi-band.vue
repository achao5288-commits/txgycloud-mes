<script lang="ts" setup>
import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@vben/icons';

import { Card, Modal } from 'ant-design-vue';

import {
  DETECT_RECORD_TYPES,
  LEDGER_ROUTE,
} from '../type-routes';

defineOptions({ name: 'MesSafetyEnvKpiBand' });

const props = defineProps<{
  summary: MesSafetyEnvStatisticsApi.Summary;
}>();

const router = useRouter();
const pickerOpen = ref(false);

/** hex 转带透明度的背景色 */
function bg(color: string, alpha: number): string {
  const n = Number.parseInt(color.slice(1), 16);
  return `rgba(${(n >> 16) & 255}, ${(n >> 8) & 255}, ${n & 255}, ${alpha})`;
}

/** 站内跳转到指定列表页 */
function go(route: string) {
  pickerOpen.value = false;
  router.push({ name: route });
}

const failTypeCount = computed(
  () => props.summary.recordStats.filter((s) => s.fail > 0).length,
);

const carbonDisplay = computed(() => props.summary.carbonEmissionSum.toFixed(1));

/** 11 类检测记录（含跳转路由），供类型选择弹层展示 */
const recordRows = computed(() => {
  const byType = new Map(props.summary.recordStats.map((s) => [s.type, s]));
  return DETECT_RECORD_TYPES.map((t) => {
    const stat = byType.get(t.type);
    return { ...t, total: stat?.total ?? 0, fail: stat?.fail ?? 0 };
  });
});

const cards = computed(() => {
  const s = props.summary;
  return [
    {
      color: '#0D9488',
      icon: 'lucide:clipboard-check',
      kind: 'records',
      label: '检测记录',
      unit: '条',
      value: String(s.recordTotalCount),
      sub: `本月新增 ${s.monthCount} 条`,
    },
    {
      color: '#16A34A',
      icon: 'lucide:badge-check',
      kind: 'records',
      label: '合格率',
      unit: '%',
      value: String(s.passRate),
      sub: `合格 ${s.passCount} / ${s.recordTotalCount} 条`,
    },
    {
      color: '#DC2626',
      icon: 'lucide:triangle-alert',
      kind: 'records',
      label: '异常记录',
      unit: '条',
      value: String(s.failCount),
      sub:
        s.recordTotalCount === 0
          ? '暂无检测记录'
          : `${failTypeCount.value} 类存在异常`,
    },
    {
      color: '#D97706',
      icon: 'lucide:calendar-clock',
      kind: 'plan',
      label: '执行中计划',
      unit: '项',
      value: String(s.planActiveCount),
      sub: `计划共 ${s.planCount} 项`,
    },
    {
      color: '#64748B',
      icon: 'lucide:leaf',
      kind: 'carbon',
      label: '碳排放核算',
      unit: 'tCO2e',
      value: carbonDisplay.value,
      sub: s.carbonCount > 0 ? `核算 ${s.carbonCount} 条` : '暂无核算数据',
    },
  ];
});

/** 点击 KPI 卡：记录类打开类型选择，计划/碳排放直接进列表 */
function handleClick(kind: string) {
  if (kind === 'records') {
    pickerOpen.value = true;
  } else if (kind === 'plan') {
    go(LEDGER_ROUTE.plan);
  } else {
    go(LEDGER_ROUTE.carbon);
  }
}
</script>

<template>
  <div class="grid grid-cols-2 gap-4 lg:grid-cols-3 xl:grid-cols-5">
    <Card
      v-for="c in cards"
      :key="c.label"
      class="cursor-pointer transition-all hover:-translate-y-1 hover:shadow-md"
      @click="handleClick(c.kind)"
    >
      <div class="flex items-center gap-3">
        <div
          class="flex size-11 flex-shrink-0 items-center justify-center rounded-xl"
          :style="{ backgroundColor: bg(c.color, 0.12) }"
        >
          <IconifyIcon
            class="size-6"
            :icon="c.icon"
            :style="{ color: c.color }"
          />
        </div>
        <div class="min-w-0 flex-1">
          <div class="text-muted-foreground text-xs">{{ c.label }}</div>
          <div class="flex items-baseline gap-1">
            <span class="text-xl font-semibold" :style="{ color: c.color }">
              {{ c.value }}
            </span>
            <span v-if="c.unit" class="text-muted-foreground text-xs">
              {{ c.unit }}
            </span>
          </div>
          <div class="text-muted-foreground mt-0.5 truncate text-xs">
            {{ c.sub }}
          </div>
        </div>
        <IconifyIcon
          class="text-muted-foreground/70 size-4 flex-shrink-0"
          icon="lucide:chevron-right"
        />
      </div>
    </Card>
  </div>

  <!-- 检测记录类指标：选择 11 类中的一类进入对应记录列表 -->
  <Modal
    v-model:open="pickerOpen"
    :footer="null"
    title="选择检测类型查看记录明细"
    width="520px"
  >
    <div class="pb-1">
      <div
        v-for="r in recordRows"
        :key="r.type"
        class="hover:bg-accent flex cursor-pointer items-center justify-between rounded-lg px-2 py-2.5 transition-colors"
        @click="go(r.route)"
      >
        <div class="flex items-center gap-2">
          <span class="text-sm">{{ r.typeName }}</span>
          <span v-if="r.fail > 0" class="text-xs text-[#DC2626]">
            异常 {{ r.fail }}
          </span>
        </div>
        <div class="flex items-center gap-1 text-xs">
          <span class="text-muted-foreground">{{ r.total }} 条</span>
          <IconifyIcon
            class="text-muted-foreground/70 size-3.5"
            icon="lucide:chevron-right"
          />
        </div>
      </div>
    </div>
  </Modal>
</template>
