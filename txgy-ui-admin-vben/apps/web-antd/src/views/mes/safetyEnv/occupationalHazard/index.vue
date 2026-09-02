<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetOccupationalHazardApi } from '#/api/mes/safetyEnv/occupationalHazard';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteOccupationalHazard,
  getOccupationalHazardPage,
} from '#/api/mes/safetyEnv/occupationalHazard';
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

/** 创建职业病危害检测 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑职业病危害检测 */
function handleEdit(row: MesSetOccupationalHazardApi.OccupationalHazard) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除职业病危害检测 */
async function handleDelete(row: MesSetOccupationalHazardApi.OccupationalHazard) {
  const label = row.recordNo ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteOccupationalHazard(row.id!);
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
          await getOccupationalHazardPage({
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
  } as VxeTableGridOptions<MesSetOccupationalHazardApi.OccupationalHazard>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="职业病危害检测列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['职业病危害检测']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-occupational-hazard:create'],
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
              auth: ['mes:set-occupational-hazard:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-occupational-hazard:delete'],
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