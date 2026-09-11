<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesPollutionTraceApi } from '#/api/mes/safetyEnv/pollutionTrace';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';

import { Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getTraceChainPage } from '#/api/mes/safetyEnv/pollutionTrace';

import {
  nodeStatusMeta,
  NODE_STAGE_MAP,
  TRACE_TYPE_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import ReverseModal from './modules/reverse-modal.vue';
import TraceDrawer from './modules/trace-drawer.vue';

const [DrawerComp, drawerApi] = useVbenDrawer({
  connectedComponent: TraceDrawer,
  destroyOnClose: true,
});

const [ReverseComp, reverseApi] = useVbenModal({
  connectedComponent: ReverseModal,
  destroyOnClose: true,
});

/** 打开追溯详情(以该节点业务单号为键，拉全生命周期链) */
function handleTrace(row: MesPollutionTraceApi.TraceNode) {
  drawerApi.setData({ row }).open();
}

const [Grid] = useVbenVxeGrid({
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
          await getTraceChainPage({
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
  } as VxeTableGridOptions<MesPollutionTraceApi.TraceNode>,
});
</script>

<template>
  <Page auto-content-height>
    <DrawerComp />
    <ReverseComp />
    <Grid table-title="污染追溯 · 节点流水（复核收口与台账每步处置自动留痕）">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '反向查来源',
              type: 'primary',
              icon: ACTION_ICON.VIEW,
              auth: ['mes:set-pollution-trace:query'],
              onClick: () => reverseApi.open(),
            },
          ]"
        />
      </template>
      <template #bizType="{ row }">
        <Tag :color="TRACE_TYPE_MAP[row.bizType]?.color ?? 'default'">
          {{ TRACE_TYPE_MAP[row.bizType]?.text ?? row.bizType ?? '-' }}
        </Tag>
      </template>
      <template #nodeStage="{ row }">
        <span>{{ NODE_STAGE_MAP[row.nodeStage] ?? row.nodeStage ?? '-' }}</span>
      </template>
      <template #batchStatus="{ row }">
        <Tag :color="nodeStatusMeta(row).color">
          {{ nodeStatusMeta(row).text }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '追溯详情',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['mes:set-pollution-trace:query'],
              onClick: handleTrace.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
