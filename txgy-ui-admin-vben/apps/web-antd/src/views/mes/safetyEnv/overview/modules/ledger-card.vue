<script lang="ts" setup>
import type { MesSafetyEnvStatisticsApi } from '#/api/mes/safetyEnv/overview';

import { computed } from 'vue';
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@vben/icons';

import { Card } from 'ant-design-vue';

import { LEDGER_ROUTE } from '../type-routes';

defineOptions({ name: 'MesSafetyEnvLedgerCard' });

const props = defineProps<{
  summary: MesSafetyEnvStatisticsApi.Summary;
}>();

const router = useRouter();

/** 台账档案行：点击进入对应管理列表页 */
const rows = computed(() => {
  const s = props.summary;
  return [
    { label: '检测标准', value: `${s.standardCount} 项`, route: LEDGER_ROUTE.standard },
    { label: '排放口', value: `${s.outletCount} 个`, route: LEDGER_ROUTE.outlet },
    { label: '排污许可', value: `${s.permitCount} 项`, route: LEDGER_ROUTE.permit },
    { label: '危险废物台账', value: `${s.wasteCount} 条`, route: LEDGER_ROUTE.waste },
    { label: '环保报告', value: `${s.envReportCount} 份`, route: LEDGER_ROUTE.envReport },
    {
      label: '碳排放核算',
      value: `${s.carbonCount} 条`,
      desc: `排放总量 ${s.carbonEmissionSum.toFixed(1)} tCO2e`,
      highlight: true,
      route: LEDGER_ROUTE.carbon,
    },
  ];
});

function go(route: string) {
  router.push({ name: route });
}
</script>

<template>
  <Card title="台账与合规档案" class="h-full">
    <div class="flex h-full flex-col">
      <div
        v-for="(r, i) in rows"
        :key="r.label"
        class="hover:bg-accent flex cursor-pointer items-center justify-between rounded-md px-1.5 py-3 transition-colors"
        :class="[i > 0 ? 'border-border border-t rounded-none' : '']"
        @click="go(r.route)"
      >
        <div>
          <div class="text-sm">{{ r.label }}</div>
          <div v-if="r.desc" class="text-muted-foreground text-xs">
            {{ r.desc }}
          </div>
        </div>
        <div class="flex items-center gap-1">
          <span
            class="text-base font-semibold tabular-nums"
            :class="r.highlight ? 'text-[#0D9488]' : ''"
          >
            {{ r.value }}
          </span>
          <IconifyIcon
            class="text-muted-foreground/70 size-3.5"
            icon="lucide:chevron-right"
          />
        </div>
      </div>
    </div>
  </Card>
</template>
