<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetWastewaterApi } from '#/api/mes/safetyEnv/wastewater';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteWastewater,
  getWastewaterPage,
} from '#/api/mes/safetyEnv/wastewater';
import { $t } from '#/locales';

import { COLLECTION_MODE_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建废水监测记录 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑废水监测记录 */
function handleEdit(row: MesSetWastewaterApi.Wastewater) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除废水监测记录 */
async function handleDelete(row: MesSetWastewaterApi.Wastewater) {
  const label = row.pollutantCode ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteWastewater(row.id!);
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
          await getWastewaterPage({
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
  } as VxeTableGridOptions<MesSetWastewaterApi.Wastewater>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="废水监测记录列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建废水监测记录',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-wastewater:create'],
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
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-wastewater:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-wastewater:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.pollutantCode]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
