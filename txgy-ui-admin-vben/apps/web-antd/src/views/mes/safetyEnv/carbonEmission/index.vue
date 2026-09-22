<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetCarbonEmissionApi } from '#/api/mes/safetyEnv/carbonEmission';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteCarbonEmission,
  getCarbonEmissionPage,
} from '#/api/mes/safetyEnv/carbonEmission';
import { $t } from '#/locales';

import { ENERGY_TYPE_MAP, PERIOD_TYPE_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建碳排放核算 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑碳排放核算 */
function handleEdit(row: MesSetCarbonEmissionApi.CarbonEmission) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除碳排放核算 */
async function handleDelete(row: MesSetCarbonEmissionApi.CarbonEmission) {
  const label = row.energyType ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteCarbonEmission(row.id!);
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
          await getCarbonEmissionPage({
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
  } as VxeTableGridOptions<MesSetCarbonEmissionApi.CarbonEmission>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="碳排放核算列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建碳排放核算',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-carbon-emission:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #energyType="{ row }">
        <Tag :color="ENERGY_TYPE_MAP[row.energyType]?.color">
          {{ ENERGY_TYPE_MAP[row.energyType]?.text ?? row.energyType }}
        </Tag>
      </template>
      <template #periodType="{ row }">
        <Tag :color="PERIOD_TYPE_MAP[row.periodType]?.color">
          {{ PERIOD_TYPE_MAP[row.periodType]?.text ?? row.periodType ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-carbon-emission:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-carbon-emission:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.energyType]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
