<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetFireCheckApi } from '#/api/mes/safetyEnv/fireCheck';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteFireCheck,
  getFireCheckPage,
} from '#/api/mes/safetyEnv/fireCheck';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建消防设施检测 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑消防设施检测 */
function handleEdit(row: MesSetFireCheckApi.FireCheck) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除消防设施检测 */
async function handleDelete(row: MesSetFireCheckApi.FireCheck) {
  const label = row.recordNo ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteFireCheck(row.id!);
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
          await getFireCheckPage({
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
  } as VxeTableGridOptions<MesSetFireCheckApi.FireCheck>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="消防设施检测列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['消防设施检测']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-fire-check:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #result="{ row }">
        <Tag v-if="row.result" :color="row.result === 'PASS' ? 'success' : 'error'">
          {{ row.result === 'PASS' ? '合格' : '不合格' }}
        </Tag>
        <span v-else>-</span>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-fire-check:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-fire-check:delete'],
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