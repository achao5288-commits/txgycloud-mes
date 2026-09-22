<script lang="ts" setup>
import type { TableColumnsType } from 'ant-design-vue';

import type { MesPollutionTraceApi } from '#/api/mes/safetyEnv/pollutionTrace';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart } from '@vben/utils';

import {
  Alert,
  Button,
  Descriptions,
  Empty,
  Image,
  Input,
  Select,
  Spin,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  exportTraceReport,
  listAllSources,
  reverseTrace,
} from '#/api/mes/safetyEnv/pollutionTrace';

/** 源头判定 → 标签配色：无源必须是刺眼的，它就是"要去补录"的异常 */
const SOURCE_MAP: Record<string, { color: string; text: string }> = {
  EMERGENCY: { color: 'red', text: '应急处置产废' },
  FACILITY_CARBON: { color: 'blue', text: '治理设施换炭' },
  UNSOURCED: { color: 'orange', text: '无源（需补录）' },
};

const SOURCE_OPTIONS = [
  { label: '应急处置产废', value: 'EMERGENCY' },
  { label: '治理设施换炭', value: 'FACILITY_CARBON' },
  { label: '无源（需补录）', value: 'UNSOURCED' },
];

/** 全量表列：一只桶/一张联单一行，来源判定直接在行上 */
const listColumns: TableColumnsType = [
  { title: '桶码', dataIndex: 'containerCode', width: 130 },
  { title: '联单号', dataIndex: 'manifestNo', width: 190 },
  { title: '废物', dataIndex: 'wasteName', width: 150 },
  { title: '数量', dataIndex: 'quantity', width: 90 },
  { title: '环节', dataIndex: 'stage', width: 100 },
  { title: '库位', dataIndex: 'storageLocation', width: 130 },
  { title: '来源判定', dataIndex: 'sourceType', width: 140 },
  { title: '来源单据', dataIndex: 'sourceDocNo', width: 160 },
  { title: '来源说明 / 无源原因', dataIndex: 'sourceDetail' },
  { title: '操作', dataIndex: 'action', width: 140, fixed: 'right' },
];

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

const signColumns = [
  { title: '角色', dataIndex: 'signRole', width: 100 },
  { title: '签字人', dataIndex: 'signUser', width: 110 },
  { title: '时间', dataIndex: 'signTime', width: 170 },
  { title: '地点/去向', dataIndex: 'location', width: 140 },
  { title: '意见', dataIndex: 'opinion' },
  { title: '签名', dataIndex: 'signImg', width: 100 },
];

const loading = ref(false);
/** 全部危废台账行（打开即拉，不需要先输入任何东西） */
const allRows = ref<MesPollutionTraceApi.ReverseLedger[]>([]);
/** 筛选（可选；不填就是全量） */
const keyword = ref('');
const sourceFilter = ref<string | undefined>(undefined);
/** 点「查看全链」后展开的那只桶 */
const detail = ref<MesPollutionTraceApi.TraceReverse | null>(null);
const detailRow = ref<MesPollutionTraceApi.ReverseLedger | null>(null);
const detailLoading = ref(false);

/** 桶码/联单号/废物/来源单据任一命中即留；不填关键词就是全量 */
const rows = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  return allRows.value.filter((r) => {
    if (sourceFilter.value && r.sourceType !== sourceFilter.value) {
      return false;
    }
    if (!kw) {
      return true;
    }
    return [r.containerCode, r.manifestNo, r.wasteName, r.sourceDocNo].some((v) =>
      (v ?? '').toLowerCase().includes(kw),
    );
  });
});

const unsourcedCount = computed(
  () => allRows.value.filter((r) => r.sourceType === 'UNSOURCED').length,
);

function fmt(t?: number) {
  return t ? new Date(t).toLocaleString('zh-CN', { hour12: false }) : '-';
}

async function loadAll() {
  loading.value = true;
  try {
    allRows.value = await listAllSources();
  } finally {
    loading.value = false;
  }
}

async function openDetail(row: MesPollutionTraceApi.ReverseLedger) {
  detailRow.value = row;
  detail.value = null;
  detailLoading.value = true;
  try {
    detail.value = await reverseTrace({
      manifestNo: row.manifestNo || undefined,
      containerCode: row.containerCode || undefined,
    });
  } finally {
    detailLoading.value = false;
  }
}

/** 报告走接口下载（带鉴权头），不能给个 <a href> 了事——那样拿到的是未登录页 */
async function doExport() {
  const row = detailRow.value;
  if (!row) {
    return;
  }
  const data = await exportTraceReport({
    manifestNo: row.manifestNo || undefined,
    containerCode: row.containerCode || undefined,
  });
  downloadFileFromBlobPart({
    fileName: `追溯报告-${row.containerCode || row.manifestNo || 'unknown'}.md`,
    source: data,
  });
}

const [Modal] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      allRows.value = [];
      keyword.value = '';
      sourceFilter.value = undefined;
      detail.value = null;
      detailRow.value = null;
      return;
    }
    // 打开即全量：现场往往不知道该填哪个桶码，先给全表再让人自己筛
    await loadAll();
  },
});
</script>

<template>
  <Modal title="反向查来源（危废桶 → 它是哪来的）" class="w-4/5" :footer="null">
    <!-- ========== 视图一：全量列表（默认） ========== -->
    <template v-if="!detailRow">
      <Alert
        class="mx-4 mb-3"
        type="info"
        show-icon
        message="打开即是全部危废台账行，每行直接给来源判定，不用先输桶码"
        description="要缩小范围再填下面的筛选（可留空）。判定「无源」的是需要人工补录来源的异常，不是查询失败——符合物料衡算「危废必须能反推源头」的要求。"
      />

      <div class="mx-4 mb-3 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          class="w-64"
          allow-clear
          placeholder="筛选：桶码 / 联单号 / 废物 / 来源单据"
        />
        <Select
          v-model:value="sourceFilter"
          class="w-48"
          allow-clear
          :options="SOURCE_OPTIONS"
          placeholder="来源判定（不选=全部）"
        />
        <Button :loading="loading" @click="loadAll">刷新</Button>
        <span class="text-muted-foreground text-xs">
          共 {{ allRows.length }} 行 ｜ 当前显示 {{ rows.length }} 行
          <template v-if="unsourcedCount">
            ｜ <span class="text-orange-500">无源 {{ unsourcedCount }} 行待补录</span>
          </template>
        </span>
      </div>

      <div class="mx-4">
        <Spin :spinning="loading">
          <Table
            size="small"
            row-key="id"
            :columns="listColumns"
            :data-source="rows"
            :pagination="{ pageSize: 20, showSizeChanger: true }"
            :scroll="{ x: 1400 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'sourceType'">
                <Tag :color="SOURCE_MAP[record.sourceType as string]?.color ?? 'default'">
                  {{ SOURCE_MAP[record.sourceType as string]?.text ?? record.sourceType ?? '-' }}
                </Tag>
              </template>
              <template v-else-if="column.dataIndex === 'sourceDetail'">
                <span class="text-xs">{{ record.sourceDetail ?? '-' }}</span>
              </template>
              <template v-else-if="column.dataIndex === 'action'">
                <Button type="link" size="small" @click="openDetail(record)">查看全链</Button>
              </template>
            </template>
          </Table>
        </Spin>
      </div>
    </template>

    <!-- ========== 视图二：单只桶全链 ========== -->
    <template v-else>
      <div class="mx-4 mb-3 flex flex-wrap items-center gap-2">
        <Button @click="detailRow = null">← 返回全量列表</Button>
        <span class="font-medium">
          {{ detailRow?.containerCode || detailRow?.manifestNo }}
        </span>
        <Button :disabled="!detail?.found" @click="doExport">导出报告</Button>
      </div>

      <Spin :spinning="detailLoading">
        <div v-if="detail" class="mx-4">
          <Alert
            v-if="detail.found && detail.sourced"
            class="mb-3"
            type="success"
            show-icon
            message="已溯源：这只桶的每一行台账都反推到了源头"
          />
          <Alert
            v-else-if="detail.found"
            class="mb-3"
            type="warning"
            show-icon
            message="存在无源行：这只桶有台账但反推不到源头"
            :description="detail.unsourcedReason"
          />
          <Alert
            v-else
            class="mb-3"
            type="error"
            show-icon
            message="台账里查无此桶"
            :description="detail.unsourcedReason"
          />

          <template v-if="detail.found">
            <Descriptions class="mb-3" size="small" :column="3" bordered>
              <Descriptions.Item label="桶码">{{ detail.containerCode || '-' }}</Descriptions.Item>
              <Descriptions.Item label="台账行数">{{ detail.ledgers?.length ?? 0 }}</Descriptions.Item>
              <Descriptions.Item label="过秤记录">
                {{ detail.weighRecords?.length ?? 0 }} 条
                <span v-if="!detail.weighRecords?.length" class="text-gray-400">
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
              :data-source="detail.ledgers ?? []"
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
              v-if="detail.traceNodes?.length"
              class="mb-4"
              size="small"
              row-key="id"
              :columns="nodeColumns"
              :data-source="detail.traceNodes"
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

            <div class="mb-1 font-medium">签字记录（点签名可放大）</div>
            <Table
              v-if="detail.signRecords?.length"
              size="small"
              row-key="id"
              :columns="signColumns"
              :data-source="detail.signRecords"
              :pagination="false"
              :scroll="{ x: 900 }"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'signTime'">{{ fmt(record.signTime) }}</template>
                <template v-else-if="column.dataIndex === 'signImg'">
                  <Image
                    v-if="record.signImg"
                    :src="record.signImg"
                    :width="72"
                    class="border border-solid border-gray-200"
                  />
                  <span v-else class="text-gray-400">未签</span>
                </template>
              </template>
            </Table>
            <Empty
              v-else
              :image="Empty.PRESENTED_IMAGE_SIMPLE"
              description="本桶及其联单没有签字记录"
            />
          </template>
        </div>
      </Spin>
    </template>
  </Modal>
</template>
