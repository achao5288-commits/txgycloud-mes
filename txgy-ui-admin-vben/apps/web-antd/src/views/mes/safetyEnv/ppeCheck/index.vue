<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPpeCheckApi } from '#/api/mes/safetyEnv/ppecheck';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePpeCheck,
  getPpeCheckPage,
} from '#/api/mes/safetyEnv/ppecheck';
import { $t } from '#/locales';

import { CHECK_MODE_MAP, RESULT_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建劳保用品检查 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑劳保用品检查 */
function handleEdit(row: MesSetPpeCheckApi.PpeCheck) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除劳保用品检查 */
async function handleDelete(row: MesSetPpeCheckApi.PpeCheck) {
  const label = row.ppeType ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deletePpeCheck(row.id!);
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
          await getPpeCheckPage({
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
  } as VxeTableGridOptions<MesSetPpeCheckApi.PpeCheck>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="劳保用品检查列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建劳保用品检查',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-ppe-check:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #checkMode="{ row }">
        <Tag :color="CHECK_MODE_MAP[row.checkMode]?.color">
          {{ CHECK_MODE_MAP[row.checkMode]?.text ?? row.checkMode }}
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
              auth: ['mes:set-ppe-check:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-ppe-check:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.ppeType]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
