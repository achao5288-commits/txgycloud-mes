<script lang="ts" setup>
import type { MesPollutionTraceApi } from '#/api/mes/safetyEnv/pollutionTrace';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart } from '@vben/utils';

import { Alert, Button, Descriptions, Empty, Input, message, Table, Tag } from 'ant-design-vue';

import { exportTraceReport, reverseTrace } from '#/api/mes/safetyEnv/pollutionTrace';

const containerCode = ref('');
const manifestNo = ref('');
const loading = ref(false);
const result = ref<MesPollutionTraceApi.TraceReverse | null>(null);

/** 源头判定 → 标签配色：无源必须是刺眼的，它就是"要去补录"的异常 */
const SOURCE_MAP: Record<string, { color: string; text: string }> = {
  EMERGENCY: { color: 'red', text: '应急处置产废' },
  FACILITY_CARBON: { color: 'blue', text: '治理设施换炭' },
  UNSOURCED: { color: 'orange', text: '无源（需补录）' },
};

const ledgerColumns = [
  { title: '联单号', dataIndex: 'manifestNo', width: 180 },
  { title: '废物', dataIndex: 'wasteName', width: 140 },
  { title: '数量', dataIndex: 'quantity', width: 100 },
  { title: '环节', dataIndex: 'stage', width: 90 },
  { title: '库位', dataIndex: 'storageLocation', width: 120 },
  { title: '来源判定', dataIndex: 'sourceType', width: 130 },
  { title: '来源单据', dataIndex: 'sourceDocNo', width: 150 },
  { title: '来源说明 / 无源原因', dataIndex: 'sourceDetail' },
];

const nodeColumns = [
  { title: '节点时间', dataIndex: 'nodeTime', width: 170 },
  { title: '动作', dataIndex: 'nodeAction', width: 180 },
  { title: '业务单号', dataIndex: 'bizNo', width: 160 },
  { title: '操作人', dataIndex: 'operatorName', width: 100 },
  { title: '状态', dataIndex: 'batchStatus', width: 110 },
  { title: '备注', dataIndex: 'extra' },
];

function fmt(t?: number) {
  return t ? new Date(t).toLocaleString('zh-CN', { hour12: false }) : '-';
}

async function doQuery() {
  if (!containerCode.value && !manifestNo.value) {
    message.warning('至少要给危废桶码或联单号之一');
    return;
  }
  loading.value = true;
  try {
    const data = await reverseTrace({
      containerCode: containerCode.value || undefined,
      manifestNo: manifestNo.value || undefined,
    });
    result.value = data;
  } finally {
    loading.value = false;
  }
}

/** 报告走接口下载（带鉴权头），不能给个 <a href> 了事——那样拿到的是未登录页 */
async function doExport() {
  const name = manifestNo.value || containerCode.value || 'unknown';
  const data = await exportTraceReport({
    containerCode: containerCode.value || undefined,
    manifestNo: manifestNo.value || undefined,
  });
  downloadFileFromBlobPart({ fileName: `追溯报告-${name}.md`, source: data });
}

const [Modal] = useVbenModal({
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      result.value = null;
    }
  },
});
</script>

<template>
  <Modal title="反向查来源（危废桶 → 它是哪来的）" class="w-4/5" :footer="null">
    <Alert
      class="mx-4 mb-3"
      type="info"
      :show-icon="true"
      message="现场扫桶上 HJ1276 贴签的二维码，把桶码填进来即可倒推来源。"
      description="查不到源头会明确报「无源」——那是需要人工补录来源的异常，不是查询失败。符合物料衡算「危废必须能反推源头」的要求。"
    />

    <div class="mx-4 mb-3 flex flex-wrap items-center gap-2">
      <Input
        v-model:value="containerCode"
        class="w-64"
        allow-clear
        placeholder="危废桶码（贴签上那个）"
        @press-enter="doQuery"
      />
      <Input
        v-model:value="manifestNo"
        class="w-64"
        allow-clear
        placeholder="联单号（与桶码至少给一个）"
        @press-enter="doQuery"
      />
      <Button type="primary" :loading="loading" @click="doQuery">查询</Button>
      <Button :disabled="!result?.found" @click="doExport">导出报告</Button>
    </div>

    <div v-if="result" class="mx-4">
      <Alert
        v-if="result.found && result.sourced"
        class="mb-3"
        type="success"
        show-icon
        message="已溯源：这只桶的每一行台账都反推到了源头"
      />
      <Alert
        v-else-if="result.found"
        class="mb-3"
        type="warning"
        show-icon
        message="存在无源行：这只桶有台账但反推不到源头"
        :description="result.unsourcedReason"
      />
      <Alert
        v-else
        class="mb-3"
        type="error"
        show-icon
        message="台账里查无此桶"
        :description="result.unsourcedReason"
      />

      <template v-if="result.found">
        <Descriptions class="mb-3" size="small" :column="3" bordered>
          <Descriptions.Item label="桶码">{{ result.containerCode || '-' }}</Descriptions.Item>
          <Descriptions.Item label="台账行数">{{ result.ledgers?.length ?? 0 }}</Descriptions.Item>
          <Descriptions.Item label="过秤记录">
            {{ result.weighRecords?.length ?? 0 }} 条
            <span v-if="!result.weighRecords?.length" class="text-gray-400">
              （换炭不走称重流程，空着不代表漏采）
            </span>
          </Descriptions.Item>
        </Descriptions>

        <div class="mb-1 font-medium">台账与来源判定</div>
        <Table
          class="mb-4"
          size="small"
          row-key="id"
          :columns="ledgerColumns"
          :data-source="result.ledgers ?? []"
          :pagination="false"
          :scroll="{ x: 1200 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'sourceType'">
              <Tag :color="SOURCE_MAP[record.sourceType as string]?.color ?? 'default'">
                {{ SOURCE_MAP[record.sourceType as string]?.text ?? record.sourceType ?? '-' }}
              </Tag>
            </template>
          </template>
        </Table>

        <div class="mb-1 font-medium">追溯时间轴</div>
        <Table
          v-if="result.traceNodes?.length"
          class="mb-4"
          size="small"
          row-key="id"
          :columns="nodeColumns"
          :data-source="result.traceNodes"
          :pagination="false"
          :scroll="{ x: 1000 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'nodeTime'">{{ fmt(record.nodeTime) }}</template>
          </template>
        </Table>
        <Empty
          v-else
          class="mb-4"
          :image="Empty.PRESENTED_IMAGE_SIMPLE"
          description="这条链上没有节点：可能环节尚未流转到会写链的步骤"
        />

        <div class="mb-1 font-medium">签字记录</div>
        <Table
          v-if="result.signRecords?.length"
          size="small"
          row-key="id"
          :columns="[
            { title: '业务单号', dataIndex: 'bizNo', width: 160 },
            { title: '角色', dataIndex: 'signRole', width: 110 },
            { title: '签字人', dataIndex: 'signUser', width: 100 },
            { title: '时间', dataIndex: 'signTime', width: 170 },
            { title: '意见', dataIndex: 'opinion' },
          ]"
          :data-source="result.signRecords"
          :pagination="false"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'signTime'">{{ fmt(record.signTime) }}</template>
          </template>
        </Table>
        <Empty
          v-else
          :image="Empty.PRESENTED_IMAGE_SIMPLE"
          description="本桶及其联单没有签字记录"
        />
      </template>
    </div>
  </Modal>
</template>
