<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart } from '@vben/utils';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePollutionCheck,
  exportPollutionCheck,
  getPollutionCheckPage,
} from '#/api/mes/safetyEnv/pollutionCheck';
import { $t } from '#/locales';

import TraceDrawer from '../pollutionTrace/modules/trace-drawer.vue';
import {
  AI_RESULT_MAP,
  FINISHED_RESULT_MAP,
  REVIEW_RESULT_MAP,
  STAGE_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import Form from './modules/form.vue';
import HistoryDrawer from './modules/history-drawer.vue';
import Review from './modules/review.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [ReviewModal, reviewModalApi] = useVbenModal({
  connectedComponent: Review,
  destroyOnClose: true,
});

const [HistoryDrawerComp, historyDrawerApi] = useVbenDrawer({
  connectedComponent: HistoryDrawer,
  destroyOnClose: true,
});

const [TraceDrawerComp, traceDrawerApi] = useVbenDrawer({
  connectedComponent: TraceDrawer,
  destroyOnClose: true,
});

/** 打开判定履历(全链追溯) */
function handleHistory(row: MesSetPollutionCheckApi.PollutionCheck) {
  historyDrawerApi.setData({ row }).open();
}

/** 打开跨模块整链抽屉(本复核 + 随后每步台账处置流转) */
function handleTrace(row: MesSetPollutionCheckApi.PollutionCheck) {
  traceDrawerApi.setData({ row: { ...row, bizNo: row.recordNo, bizType: 'CHECK' } }).open();
}

/** 导出判定记录(导出当前搜索条件下的全量) */
async function handleExport() {
  const data = await exportPollutionCheck(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '污染判定记录.xls', source: data });
}

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建污染判定 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑污染判定(仅待复核) */
function handleEdit(row: MesSetPollutionCheckApi.PollutionCheck) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 发起变更(仅已复核且未被替代)：复核后内容冻死，要改只能新开一份记录接替它 */
function handleAmend(row: MesSetPollutionCheckApi.PollutionCheck) {
  formModalApi.setData({ id: row.id, formType: 'amend' }).open();
}

/** 人工复核(仅待复核) */
function handleReview(row: MesSetPollutionCheckApi.PollutionCheck) {
  reviewModalApi.setData({ row }).open();
}

/** 删除污染判定(仅待复核) */
async function handleDelete(row: MesSetPollutionCheckApi.PollutionCheck) {
  const label = row.recordNo ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deletePollutionCheck(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [label]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) =>
          await getPollutionCheckPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          }),
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
  } as VxeTableGridOptions<MesSetPollutionCheckApi.PollutionCheck>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <ReviewModal @success="handleRefresh" />
    <HistoryDrawerComp />
    <TraceDrawerComp />
    <Grid table-title="污染判定列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建污染判定',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-pollution-check:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['mes:set-pollution-check:query'],
              onClick: handleExport,
            },
          ]"
        />
      </template>
      <!--
        变更链的两头都必须显形，否则读列表的人会拿一条已被推翻的结论当真：
        已被替代的行（supersededBy 非空）结论作废，变更单（originRecordNo 非空）才是现行有效的那份。
      -->
      <template #recordNo="{ row }">
        <span>{{ row.recordNo }}</span>
        <!-- 事由是变更单上最该被看见的东西，列表放不下就进 tooltip -->
        <Tag
          v-if="row.originRecordNo"
          class="ml-1"
          color="processing"
          :title="`变更自 ${row.originRecordNo}${row.amendReason ? `｜事由：${row.amendReason}` : ''}`"
        >
          变更单
        </Tag>
        <Tag v-if="row.supersededBy" class="ml-1" :title="`已被 ${row.supersededBy} 替代`">
          已被替代
        </Tag>
      </template>
      <template #stage="{ row }">
        <span>{{ STAGE_MAP[row.stage ?? ''] ?? row.stage }}</span>
      </template>
      <!-- 没有 batchId 的行 = 人工打字的批次号，join 不回库存，冻结/台账都不认它。必须显形。 -->
      <template #batchNo="{ row }">
        <Tag v-if="row.batchId == null && row.batchNo" color="warning">未关联</Tag>
        <span>{{ row.batchNo ?? '-' }}</span>
      </template>
      <template #aiResult="{ row }">
        <Tag v-if="row.aiResult" :color="AI_RESULT_MAP[row.aiResult]?.color">
          {{ AI_RESULT_MAP[row.aiResult]?.text }}
        </Tag>
        <span v-else>-</span>
      </template>
      <template #reviewResult="{ row }">
        <Tag
          v-if="row.reviewResult"
          :color="REVIEW_RESULT_MAP[row.reviewResult]?.color"
        >
          {{ REVIEW_RESULT_MAP[row.reviewResult]?.text }}
        </Tag>
        <Tag v-else>待复核</Tag>
      </template>
      <template #finishedResult="{ row }">
        <Tag v-if="row.finishedResult" :color="FINISHED_RESULT_MAP[row.finishedResult]?.color">
          {{ FINISHED_RESULT_MAP[row.finishedResult]?.text }}
        </Tag>
        <span v-else>-</span>
      </template>
      <template #marked="{ row }">
        <Tag v-if="row.marked" color="orange">已标记</Tag>
        <span v-else>-</span>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '履历',
              type: 'link',
              icon: ACTION_ICON.BOOK,
              auth: ['mes:set-pollution-check:query'],
              onClick: handleHistory.bind(null, row),
            },
            {
              label: '复核',
              type: 'link',
              icon: ACTION_ICON.AUDIT,
              auth: ['mes:set-pollution-check:review'],
              ifShow: () => !row.reviewResult,
              onClick: handleReview.bind(null, row),
            },
            {
              label: '追溯',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['mes:set-pollution-trace:query'],
              ifShow: () => !!row.reviewResult,
              onClick: handleTrace.bind(null, row),
            },
            {
              // 复核一签字内容就冻死，所以「改」对已复核行不再可用——只能新开一份记录。
              // 本节整块是 :actions 的属性值，注释里**不许出现半角双引号**：它会把属性提前收尾，
              // 报出来的是后面几行莫名其妙的 TS1005（我在这一行上连踩两次，引号就写在本注释里）。
              label: '发起变更',
              type: 'link',
              icon: ACTION_ICON.COPY,
              auth: ['mes:set-pollution-check:create'],
              // 已被替代的连变更也不能再发：一条链只许向前，否则会开出两张并列的新单
              ifShow: () => !!row.reviewResult && !row.supersededBy,
              onClick: handleAmend.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-pollution-check:update'],
              ifShow: () => !row.reviewResult,
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-pollution-check:delete'],
              ifShow: () => !row.reviewResult,
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.recordNo]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>