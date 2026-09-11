<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyMaterialApi } from '#/api/mes/safetyEnv/emergencymaterial';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmergencyMaterial,
  getEmergencyMaterialPage,
} from '#/api/mes/safetyEnv/emergencymaterial';
import { $t } from '#/locales';

import {
  MATERIAL_TYPE_MAP,
  MATERIAL_STATUS_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import AlertsModal from './modules/alerts-modal.vue';
import Form from './modules/form.vue';

const [AlertsModalComp, alertsModalApi] = useVbenModal({
  connectedComponent: AlertsModal,
  destroyOnClose: true,
});
const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建应急物资 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑应急物资 */
function handleEdit(row: MesSetEmergencyMaterialApi.EmergencyMaterial) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除应急物资 */
async function handleDelete(row: MesSetEmergencyMaterialApi.EmergencyMaterial) {
  const label = row.materialName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteEmergencyMaterial(row.id!);
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
          await getEmergencyMaterialPage({
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
  } as VxeTableGridOptions<MesSetEmergencyMaterialApi.EmergencyMaterial>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <AlertsModalComp />
    <Grid table-title="应急物资列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建应急物资',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-emergency-material:create'],
              onClick: handleCreate,
            },
            {
              label: '预警',
              auth: ['mes:set-emergency-material:query'],
              onClick: () => alertsModalApi.open(),
            },
          ]"
        />
      </template>
      <template #materialType="{ row }">
        {{ MATERIAL_TYPE_MAP[row.materialType ?? ''] ?? row.materialType ?? '-' }}
      </template>
      <template #status="{ row }">
        <Tag :color="MATERIAL_STATUS_MAP[row.status ?? '']?.color ?? 'default'">
          {{ MATERIAL_STATUS_MAP[row.status ?? '']?.text ?? row.status ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-emergency-material:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-emergency-material:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.materialName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
