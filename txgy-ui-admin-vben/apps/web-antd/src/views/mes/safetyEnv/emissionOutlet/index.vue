<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmissionOutletApi } from '#/api/mes/safetyEnv/emissionOutlet';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmissionOutlet,
  getEmissionOutletPage,
} from '#/api/mes/safetyEnv/emissionOutlet';
import { $t } from '#/locales';

import { MONITOR_METHOD_MAP, OUTLET_TYPE_MAP, STATUS_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建排放口 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑排放口 */
function handleEdit(row: MesSetEmissionOutletApi.EmissionOutlet) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除排放口 */
async function handleDelete(row: MesSetEmissionOutletApi.EmissionOutlet) {
  const label = row.outletName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteEmissionOutlet(row.id!);
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
          await getEmissionOutletPage({
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
  } as VxeTableGridOptions<MesSetEmissionOutletApi.EmissionOutlet>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="排放口列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建排放口',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-emission-outlet:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #outletType="{ row }">
        <Tag :color="OUTLET_TYPE_MAP[row.outletType]?.color">
          {{ OUTLET_TYPE_MAP[row.outletType]?.text ?? row.outletType }}
        </Tag>
      </template>
      <template #monitorMethod="{ row }">
        <Tag :color="MONITOR_METHOD_MAP[row.monitorMethod]?.color">
          {{ MONITOR_METHOD_MAP[row.monitorMethod]?.text ?? row.monitorMethod }}
        </Tag>
      </template>
      <template #status="{ row }">
        <Tag :color="STATUS_MAP[row.status]?.color">
          {{ STATUS_MAP[row.status]?.text ?? row.status ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-emission-outlet:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-emission-outlet:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.outletName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
