<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetOccupationalHazardApi } from '#/api/mes/safetyEnv/occupationalhazard';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteOccupationalHazard,
  getOccupationalHazardPage,
} from '#/api/mes/safetyEnv/occupationalhazard';
import { $t } from '#/locales';

import { FACTOR_CATEGORY_MAP, LIMIT_TYPE_MAP, RESULT_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建职业危害检测 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑职业危害检测 */
function handleEdit(row: MesSetOccupationalHazardApi.OccupationalHazard) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除职业危害检测 */
async function handleDelete(row: MesSetOccupationalHazardApi.OccupationalHazard) {
  const label = row.factorCode ?? '';
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
    <Grid table-title="职业危害检测列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建职业危害检测',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-occupational-hazard:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #factorCategory="{ row }">
        <Tag :color="FACTOR_CATEGORY_MAP[row.factorCategory]?.color">
          {{ FACTOR_CATEGORY_MAP[row.factorCategory]?.text ?? row.factorCategory }}
        </Tag>
      </template>
      <template #result="{ row }">
        <Tag :color="RESULT_MAP[row.result]?.color">
          {{ RESULT_MAP[row.result]?.text ?? row.result }}
        </Tag>
      </template>
      <template #limitType="{ row }">
        <Tag :color="LIMIT_TYPE_MAP[row.limitType]?.color">
          {{ LIMIT_TYPE_MAP[row.limitType]?.text ?? row.limitType ?? '-' }}
        </Tag>
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
                title: $t('ui.actionMessage.deleteConfirm', [row.factorCode]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
