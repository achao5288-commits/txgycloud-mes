<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPressureVesselApi } from '#/api/mes/safetyEnv/pressureVessel';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePressureVessel,
  getPressureVesselPage,
} from '#/api/mes/safetyEnv/pressureVessel';
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

/** 创建压力容器检测记录 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑压力容器检测记录 */
function handleEdit(row: MesSetPressureVesselApi.PressureVessel) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除压力容器检测记录 */
async function handleDelete(row: MesSetPressureVesselApi.PressureVessel) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.recordNo]),
    duration: 0,
  });
  try {
    await deletePressureVessel(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.recordNo]));
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
          await getPressureVesselPage({
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
  } as VxeTableGridOptions<MesSetPressureVesselApi.PressureVessel>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="压力容器检测记录列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['压力容器检测记录']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-pressure-vessel:create'],
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
              auth: ['mes:set-pressure-vessel:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-pressure-vessel:delete'],
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
