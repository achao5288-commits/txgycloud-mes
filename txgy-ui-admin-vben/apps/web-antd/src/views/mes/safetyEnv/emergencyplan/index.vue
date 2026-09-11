<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyPlanApi } from '#/api/mes/safetyEnv/emergencyplan';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmergencyPlan,
  getEmergencyPlanPage,
} from '#/api/mes/safetyEnv/emergencyplan';
import { $t } from '#/locales';

import {
  PLAN_TYPE_MAP,
  PLAN_STATUS_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import FileModal from './modules/file-modal.vue';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});
const [FileModalComp, fileModalApi] = useVbenModal({
  connectedComponent: FileModal,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建应急预案 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑应急预案 */
function handleEdit(row: MesSetEmergencyPlanApi.EmergencyPlan) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除应急预案 */
async function handleDelete(row: MesSetEmergencyPlanApi.EmergencyPlan) {
  const label = row.planName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteEmergencyPlan(row.id!);
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
          await getEmergencyPlanPage({
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
  } as VxeTableGridOptions<MesSetEmergencyPlanApi.EmergencyPlan>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <FileModalComp @success="handleRefresh" />
    <Grid table-title="应急预案列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建应急预案',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-emergency-plan:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #planType="{ row }">
        {{ PLAN_TYPE_MAP[row.planType ?? ''] ?? row.planType ?? '-' }}
      </template>
      <template #status="{ row }">
        <Tag :color="PLAN_STATUS_MAP[row.status ?? '']?.color ?? 'default'">
          {{ PLAN_STATUS_MAP[row.status ?? '']?.text ?? row.status ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-emergency-plan:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '发布/备案',
              type: 'link',
              auth: ['mes:set-emergency-plan:file'],
              onClick: () => fileModalApi.setData(row).open(),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-emergency-plan:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.planName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
