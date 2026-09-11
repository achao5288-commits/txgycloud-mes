<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetNoiseRecordApi } from '#/api/mes/safetyEnv/noiserecord';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteNoiseRecord,
  getNoiseRecordPage,
} from '#/api/mes/safetyEnv/noiserecord';
import { $t } from '#/locales';

import { COLLECTION_MODE_MAP, RESULT_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建噪声检测记录 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑噪声检测记录 */
function handleEdit(row: MesSetNoiseRecordApi.NoiseRecord) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除噪声检测记录 */
async function handleDelete(row: MesSetNoiseRecordApi.NoiseRecord) {
  const label = row.location ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteNoiseRecord(row.id!);
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
          await getNoiseRecordPage({
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
  } as VxeTableGridOptions<MesSetNoiseRecordApi.NoiseRecord>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="噪声检测记录列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建噪声检测记录',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-noise-record:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #collectionMode="{ row }">
        <Tag :color="COLLECTION_MODE_MAP[row.collectionMode]?.color">
          {{ COLLECTION_MODE_MAP[row.collectionMode]?.text ?? row.collectionMode }}
        </Tag>
      </template>
      <template #result="{ row }">
        <Tag :color="RESULT_MAP[row.result]?.color">
          {{ RESULT_MAP[row.result]?.text ?? row.result }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-noise-record:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-noise-record:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.location]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
