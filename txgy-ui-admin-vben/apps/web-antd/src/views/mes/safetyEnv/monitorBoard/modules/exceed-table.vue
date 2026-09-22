<script lang="ts" setup>
import type { MesMonitorApi } from '#/api/mes/safetyEnv/monitorBoard';

import { computed } from 'vue';

import { Button, Empty, message, Popconfirm, Table, Tag } from 'ant-design-vue';

import {
  closeMonitorExceed,
  dispatchMonitorExceed,
} from '#/api/mes/safetyEnv/monitorBoard';

const props = withDefaults(
  defineProps<{
    canClose?: boolean;
    canDispatch?: boolean;
    /** 大屏里横向更挤：省掉「名称」列 */
    compact?: boolean;
    records?: MesMonitorApi.ExceedRecord[];
  }>(),
  {
    canClose: false,
    canDispatch: false,
    compact: false,
    records: () => [],
  },
);

const emit = defineEmits<{ changed: [] }>();

/** 闭环时限 24 小时：由 occurTime 推，后端不下发「是否超期」——这是展示逻辑，不是判定 */
const CLOSE_LIMIT_MS = 24 * 60 * 60 * 1000;

const HANDLE: Record<string, { color: string; text: string }> = {
  PENDING: { color: 'red', text: '待处置' },
  PROCESSING: { color: 'orange', text: '处置中' },
  CLOSED: { color: 'green', text: '已闭环' },
};

const columns = computed(() => [
  { dataIndex: 'outletNo', title: '排放口', width: 120 },
  ...(props.compact ? [] : [{ dataIndex: 'outletName', title: '名称' }]),
  { dataIndex: 'pollutantName', title: '因子', width: 100 },
  { dataIndex: 'value', title: '小时均值', width: 100 },
  { dataIndex: 'limit', title: '限值', width: 80 },
  { dataIndex: 'multiple', title: '倍数', width: 80 },
  { dataIndex: 'occurTime', title: '发生时间', width: 180 },
  { dataIndex: 'durationMin', title: '持续', width: 100 },
  { dataIndex: 'handleStatus', title: '状态', width: 100 },
  { dataIndex: 'handler', title: '处置人', width: 100 },
  { dataIndex: 'actions', fixed: 'right' as const, title: '操作', width: 150 },
]);

function fmt(t?: number) {
  return t ? new Date(t).toLocaleString('zh-CN', { hour12: false }) : '-';
}

function isOverdue(r: MesMonitorApi.ExceedRecord) {
  return (
    r.handleStatus !== 'CLOSED' &&
    !!r.occurTime &&
    Date.now() - r.occurTime > CLOSE_LIMIT_MS
  );
}

function fmtDur(min?: number) {
  const v = Math.max(0, Math.round(Number(min) || 0));
  if (v < 60) {
    return `${v} 分钟`;
  }
  const h = Math.floor(v / 60);
  return v % 60 === 0 ? `${h} 小时` : `${h} 小时 ${v % 60} 分`;
}

async function act(fn: (id: number) => Promise<unknown>, id: number, ok: string) {
  try {
    await fn(id);
    message.success(ok);
    emit('changed');
  } catch {
    // 重复派单 / 已闭环这类业务错误，后端已回中文提示且请求层弹过了，不重复打扰
  }
}
</script>

<template>
  <Table
    size="small"
    row-key="id"
    :columns="columns"
    :data-source="records"
    :pagination="false"
    :scroll="{ x: compact ? 1200 : 1500 }"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'multiple'">
        <Tag :color="(record.multiple ?? 0) >= 1.1 ? 'red' : 'orange'">
          ×{{ record.multiple }}
        </Tag>
      </template>
      <template v-else-if="column.dataIndex === 'occurTime'">
        <span :class="isOverdue(record) ? 'text-red-500' : ''">
          {{ fmt(record.occurTime) }}
          <span v-if="isOverdue(record)" class="ml-1 text-xs">（超 24h）</span>
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'durationMin'">
        {{ fmtDur(record.durationMin) }}
      </template>
      <template v-else-if="column.dataIndex === 'handleStatus'">
        <Tag :color="HANDLE[record.handleStatus as string]?.color ?? 'default'">
          {{ HANDLE[record.handleStatus as string]?.text ?? record.handleStatus }}
        </Tag>
      </template>
      <template v-else-if="column.dataIndex === 'actions'">
        <span v-if="record.handleStatus === 'CLOSED'" class="text-gray-400">
          已闭环
        </span>
        <div v-else class="flex gap-1">
          <Button
            v-if="record.handleStatus === 'PENDING' && canDispatch"
            size="small"
            type="link"
            @click="act(dispatchMonitorExceed, record.id!, '已派单')"
          >
            派单
          </Button>
          <Popconfirm
            v-if="canClose"
            ok-text="确认闭环"
            title="闭环后不再改状态，确认？"
            @confirm="act(closeMonitorExceed, record.id!, '已闭环')"
          >
            <Button danger size="small" type="link">闭环</Button>
          </Popconfirm>
          <span
            v-if="!canClose && (record.handleStatus !== 'PENDING' || !canDispatch)"
            class="text-gray-400"
          >
            无权限
          </span>
        </div>
      </template>
    </template>
    <template #emptyText>
      <Empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="没有超标记录" />
    </template>
  </Table>
</template>
