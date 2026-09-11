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

import {
  AI_RESULT_MAP,
  FINISHED_RESULT_MAP,
  REVIEW_RESULT_MAP,
  STAGE_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import Form from './modules/form.vue';
import TraceDrawer from '../pollutionTrace/modules/trace-drawer.vue';
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
      <template #stage="{ row }">
        <span>{{ STAGE_MAP[row.stage] ?? row.stage }}</span>
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