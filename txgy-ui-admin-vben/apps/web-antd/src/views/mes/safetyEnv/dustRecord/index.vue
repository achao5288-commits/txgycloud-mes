<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetDustRecordApi } from '#/api/mes/safetyEnv/dustrecord';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteDustRecord,
  getDustRecordPage,
} from '#/api/mes/safetyEnv/dustrecord';
import { $t } from '#/locales';

import { COLLECTION_MODE_MAP, DUST_TYPE_MAP, RESULT_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建粉尘检测记录 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑粉尘检测记录 */
function handleEdit(row: MesSetDustRecordApi.DustRecord) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除粉尘检测记录 */
async function handleDelete(row: MesSetDustRecordApi.DustRecord) {
  const label = row.dustType ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteDustRecord(row.id!);
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
          await getDustRecordPage({
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
  } as VxeTableGridOptions<MesSetDustRecordApi.DustRecord>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="粉尘检测记录列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建粉尘检测记录',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-dust-record:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #dustType="{ row }">
        <Tag :color="DUST_TYPE_MAP[row.dustType]?.color">
          {{ DUST_TYPE_MAP[row.dustType]?.text ?? row.dustType ?? '-' }}
        </Tag>
      </template>
      <template #result="{ row }">
        <Tag :color="RESULT_MAP[row.result]?.color">
          {{ RESULT_MAP[row.result]?.text ?? row.result ?? '-' }}
        </Tag>
      </template>
      <template #collectionMode="{ row }">
        <Tag :color="COLLECTION_MODE_MAP[row.collectionMode]?.color">
          {{ COLLECTION_MODE_MAP[row.collectionMode]?.text ?? row.collectionMode ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-dust-record:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-dust-record:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.dustType]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
