<script lang="ts" setup>
import type { MesPollutionTraceApi } from '#/api/mes/safetyEnv/pollutionTrace';

import { ref } from 'vue';

import { useVbenDrawer, useVbenModal } from '@vben/common-ui';
import { formatDateTime } from '@vben/utils';

import {
  Button,
  Empty,
  Spin,
  Table,
  TabPane,
  Tabs,
  Tag,
  Timeline,
  TimelineItem,
} from 'ant-design-vue';

import {
  getSignRecordPage,
  getTraceChainPage,
  getWeighRecordPage,
} from '#/api/mes/safetyEnv/pollutionTrace';

import AttachmentPanel from '../../components/AttachmentPanel.vue';
import { nodeStatusMeta, TRACE_TYPE_MAP } from '../data';
import WeighModal from './weigh-modal.vue';

/** 签字角色文案 */
const SIGN_ROLE_MAP: Record<string, { text: string; color: string }> = {
  REVIEWER: { text: '复核人', color: 'green' },
  OPERATOR: { text: '录入人', color: 'blue' },
  APPROVER: { text: '审批人', color: 'orange' },
};

/** 台账终态：已复用/已排放/已处置/已解除——归档后不该再往单据上补附件 */
const LEDGER_TERMINAL = ['REUSED', 'DISCHARGED', 'DISPOSED', 'CLEARED'];
/** 点击的追溯节点行(取其业务单号作为整条链的键) */
const row = ref<MesPollutionTraceApi.TraceNode | null>(null);
/** 追溯链节点(整条生命周期：复核→每步处置流转，时间正序) */
const chain = ref<MesPollutionTraceApi.TraceNode[]>([]);
/** 签字记录 */
const signs = ref<MesPollutionTraceApi.SignRecord[]>([]);
/** 称重记录 */
const weighs = ref<MesPollutionTraceApi.WeighRecord[]>([]);
const loading = ref(false);

function fmt(t?: number): string {
  return t != null ? formatDateTime(t) : '-';
}

async function loadAll(bizNo?: string) {
  chain.value = [];
  signs.value = [];
  weighs.value = [];
  if (!bizNo) {
    return;
  }
  try {
    const [chainPage, signPage, weighPage] = await Promise.all([
      getTraceChainPage({ pageNo: 1, pageSize: 100, bizNo }),
      getSignRecordPage({ pageNo: 1, pageSize: 100, bizNo }),
      getWeighRecordPage({ pageNo: 1, pageSize: 100, bizNo }),
    ]);
    chain.value = [...(chainPage.list ?? [])].reverse(); // 库倒序 → 时间轴正序展示
    signs.value = signPage.list ?? [];
    weighs.value = weighPage.list ?? [];
  } catch {
    // 任一接口异常保持空态
  }
}

const [WeighModalComp, weighModalApi] = useVbenModal({
  connectedComponent: WeighModal,
  destroyOnClose: true,
});

/** 手工登记称重（电子秤未联网场景），登记成功后刷新称重 Tab */
function handleWeighAdd() {
  weighModalApi
    .setData({ bizNo: row.value?.bizNo, bizType: row.value?.bizType })
    .open();
}

const [Drawer, drawerApi] = useVbenDrawer({
  showConfirmButton: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      return;
    }
    const data = drawerApi.getData<{ row: MesPollutionTraceApi.TraceNode }>();
    row.value = data?.row ?? null;
    loading.value = true;
    try {
      await loadAll(row.value?.bizNo);
    } finally {
      loading.value = false;
    }
  },
});
</script>

<template>
  <Drawer title="污染追溯 · 全生命周期链" class="w-[720px]">
    <Spin :spinning="loading">
      <!-- 追溯键概要 -->
      <div v-if="row" class="mb-3 rounded-md border border-border bg-card px-4 py-2.5 text-sm leading-7">
        <div class="flex flex-wrap items-center gap-2">
          <span class="font-medium">{{ row.bizNo ?? '-' }}</span>
          <Tag :color="TRACE_TYPE_MAP[row.bizType ?? '']?.color">
            {{ TRACE_TYPE_MAP[row.bizType ?? '']?.text ?? row.bizType ?? '-' }}
          </Tag>
          <span class="text-xs text-muted-foreground">
            链上 {{ chain.length }} 节点 ｜ 签字 {{ signs.length }} ｜ 称重 {{ weighs.length }}
          </span>
        </div>
        <div class="text-xs text-muted-foreground">本业务单号下全部追溯节点（复核收口与每步台账处置流转，只增不改）</div>
      </div>

      <Tabs default-active-key="chain">
        <TabPane key="chain" :tab="`追溯链${chain.length ? ` (${chain.length})` : ''}`">
          <Timeline v-if="chain.length">
            <TimelineItem
              v-for="node in chain"
              :key="node.id"
              :color="TRACE_TYPE_MAP[node.bizType ?? '']?.color ?? 'gray'"
            >
              <div class="flex flex-wrap items-center gap-2">
                <Tag :color="TRACE_TYPE_MAP[node.bizType ?? '']?.color ?? 'default'" class="!mr-0">
                  {{ TRACE_TYPE_MAP[node.bizType ?? '']?.text ?? node.bizType ?? '-' }}
                </Tag>
                <span class="text-sm font-medium">{{ node.nodeAction ?? '-' }}</span>
                <Tag :color="nodeStatusMeta(node).color" class="!mr-0">
                  {{ nodeStatusMeta(node).text }}
                </Tag>
              </div>
              <div class="text-xs text-muted-foreground">
                {{ node.operatorName ?? '-' }} @ {{ fmt(node.nodeTime) }}
                <template v-if="node.extra"> ｜ {{ node.extra }}</template>
              </div>
            </TimelineItem>
          </Timeline>
          <Empty v-else description="暂无追溯节点（复核/处置流转后自动留痕）" />
        </TabPane>

        <TabPane key="sign" :tab="`签字记录${signs.length ? ` (${signs.length})` : ''}`">
          <Table
            v-if="signs.length"
            :data-source="signs"
            :pagination="false"
            row-key="id"
            size="small"
            :columns="[
              { title: '角色', dataIndex: 'signRole', width: 90 },
              { title: '签字人', dataIndex: 'signUser', width: 110 },
              { title: '签字时间', dataIndex: 'signTime', width: 170 },
              { title: '地点/去向', dataIndex: 'location' },
              { title: '意见', dataIndex: 'opinion' },
              { title: '签名', dataIndex: 'signImg', width: 110 },
            ]"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'signRole'">
                <Tag :color="SIGN_ROLE_MAP[record.signRole]?.color ?? 'default'">
                  {{ SIGN_ROLE_MAP[record.signRole]?.text ?? record.signRole ?? '-' }}
                </Tag>
              </template>
              <template v-else-if="column.dataIndex === 'signUser'">{{ record.signUser ?? '-' }}</template>
              <template v-else-if="column.dataIndex === 'signTime'">{{ fmt(record.signTime) }}</template>
              <template v-else-if="column.dataIndex === 'signImg'">
                <img v-if="record.signImg" :src="record.signImg" class="h-10 border border-solid border-gray-200" alt="签名" />
                <span v-else class="text-gray-400">未签</span>
              </template>
              <template v-else>{{ record[column.dataIndex] ?? '-' }}</template>
            </template>
          </Table>
          <Empty v-else description="暂无签字记录（复核收口自动留档）" />
        </TabPane>

        <TabPane key="attachment" tab="附件">
          <AttachmentPanel
            v-if="row?.bizType && row?.bizNo"
            :biz-type="row.bizType"
            :biz-no="row.bizNo"
            :readonly="
              row.bizType === 'LEDGER' &&
              LEDGER_TERMINAL.includes(row.batchStatus ?? '')
            "
          />
          <Empty v-else description="本节点无业务单号，无法挂附件" />
        </TabPane>

        <TabPane key="weigh" :tab="`称重记录${weighs.length ? ` (${weighs.length})` : ''}`">
          <div class="mb-2 flex justify-end">
            <Button
              size="small"
              type="primary"
              :disabled="!row"
              @click="handleWeighAdd"
            >
              手工登记称重
            </Button>
          </div>
          <Table
            v-if="weighs.length"
            :data-source="weighs"
            :pagination="false"
            row-key="id"
            size="small"
            :columns="[
              { title: '类型', dataIndex: 'weighType', width: 90 },
              { title: '容器', dataIndex: 'containerCode', width: 130 },
              { title: '净重(kg)', dataIndex: 'netWeight', width: 110 },
              { title: '来源', dataIndex: 'dataSource', width: 90 },
              { title: '称重人', dataIndex: 'operatorName', width: 110 },
              { title: '称重时间', dataIndex: 'weighTime', width: 170 },
            ]"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'weighTime'">{{ fmt(record.weighTime) }}</template>
              <template v-else>{{ record[column.dataIndex] ?? '-' }}</template>
            </template>
          </Table>
          <Empty v-else description="暂无称重记录（待电子秤直采接入后留痕）" />
        </TabPane>
      </Tabs>
    </Spin>
    <WeighModalComp @success="loadAll(row?.bizNo)" />
  </Drawer>
</template>
