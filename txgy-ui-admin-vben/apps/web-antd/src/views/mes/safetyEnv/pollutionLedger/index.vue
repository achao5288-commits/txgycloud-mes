<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesPollutionLedgerApi } from '#/api/mes/safetyEnv/pollutionLedger';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';

import { message, Modal, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  getPollutionLedgerPage,
  updatePollutionLedgerMark,
} from '#/api/mes/safetyEnv/pollutionLedger';
import { STAGE_MAP } from '../pollutionCheck/data';

import { LEDGER_STATUS_MAP, useGridColumns, useGridFormSchema } from './data';
import TraceDrawer from '../pollutionTrace/modules/trace-drawer.vue';
import HistoryDrawer from './modules/history-drawer.vue';
import StatusModal from './modules/status-modal.vue';

const [StatusModalComp, statusModalApi] = useVbenModal({
  connectedComponent: StatusModal,
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

/** 打开台账流转历史(全链追溯) */
function handleHistory(row: MesPollutionLedgerApi.Ledger) {
  historyDrawerApi.setData({ row }).open();
}

/** 打开跨模块整链抽屉(来源判定复核 + 本台账每步处置流转) */
function handleTrace(row: MesPollutionLedgerApi.Ledger) {
  traceDrawerApi.setData({ row: { ...row, bizNo: row.sourceRecordNo, bizType: 'LEDGER' } }).open();
}

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 处置流转(暂存/处置中 → 终态) */
function handleFlow(row: MesPollutionLedgerApi.Ledger) {
  statusModalApi.setData({ row }).open();
}

/** 标记品终审(环保专员专属)：标记=禁止正常出库/销售，解除标记=恢复正常流转 */
function handleMark(row: MesPollutionLedgerApi.Ledger) {
  const next = !row.marked;
  Modal.confirm({
    title: next ? '标记为受控品（禁止正常出库）' : '解除标记（恢复正常出库）',
    content: `${row.sourceRecordNo ?? ''}｜${row.itemName ?? '-'}｜批次 ${row.batchNo || '-'}`,
    okText: '确认终审',
    cancelText: '取消',
    onOk: async () => {
      await updatePollutionLedgerMark({ id: row.id!, marked: next });
      gridApi.query();
      message.success(next ? '已标记' : '已解除标记');
    },
  });
}

/** 闭环状态(处置终态/无污染自动解除)：不再提供处置流转入口 */
const CLOSED_STATUSES = ['REUSED', 'DISCHARGED', 'DISPOSED', 'CLEARED'];

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
          await getPollutionLedgerPage({
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
  } as VxeTableGridOptions<MesPollutionLedgerApi.Ledger>,
});
</script>
<template>
  <Page auto-content-height>
    <StatusModalComp @success="handleRefresh" />
    <HistoryDrawerComp />
    <TraceDrawerComp />
    <Grid table-title="污染/危废暂存台账">
      <template #stage="{ row }">
        <span>{{ STAGE_MAP[row.stage] ?? row.stage }}</span>
      </template>
      <template #marked="{ row }">
        <Tag v-if="row.marked" color="orange">已标记</Tag>
        <span v-else>-</span>
      </template>
      <template #status="{ row }">
        <Tag v-if="row.status" :color="LEDGER_STATUS_MAP[row.status]?.color">
          {{ LEDGER_STATUS_MAP[row.status]?.text ?? row.status }}
        </Tag>
        <span v-else>-</span>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '历史',
              type: 'link',
              icon: ACTION_ICON.BOOK,
              auth: ['mes:set-pollution-check:query'],
              onClick: handleHistory.bind(null, row),
            },
            {
              label: '追溯',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['mes:set-pollution-trace:query'],
              onClick: handleTrace.bind(null, row),
            },
            {
              label: '处置流转',
              type: 'link',
              icon: ACTION_ICON.AUDIT,
              auth: ['mes:set-pollution-check:review'],
              ifShow: () => !CLOSED_STATUSES.includes(row.status ?? ''),
              onClick: handleFlow.bind(null, row),
            },
            {
              // 标记品终审：只授环保专员角色，企业超级管理员看不到（权限点独立）
              label: row.marked ? '解除标记' : '标记终审',
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-pollution-mark:update'],
              onClick: handleMark.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
