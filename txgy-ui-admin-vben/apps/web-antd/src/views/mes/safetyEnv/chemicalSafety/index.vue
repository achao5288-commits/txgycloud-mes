<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetChemicalSafetyApi } from '#/api/mes/safetyEnv/chemicalSafety';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteChemicalSafety,
  getChemicalSafetyPage,
} from '#/api/mes/safetyEnv/chemicalSafety';
import { $t } from '#/locales';

import { RESULT_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建危化品安全检查 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑危化品安全检查 */
function handleEdit(row: MesSetChemicalSafetyApi.ChemicalSafety) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除危化品安全检查 */
async function handleDelete(row: MesSetChemicalSafetyApi.ChemicalSafety) {
  const label = row.chemicalName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteChemicalSafety(row.id!);
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
          await getChemicalSafetyPage({
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
  } as VxeTableGridOptions<MesSetChemicalSafetyApi.ChemicalSafety>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="危化品安全检查列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建危化品安全检查',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-chemical-safety:create'],
              onClick: handleCreate,
            },
          ]"
        />
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
              auth: ['mes:set-chemical-safety:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-chemical-safety:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.chemicalName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
