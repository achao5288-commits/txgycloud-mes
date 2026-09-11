<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyEventApi } from '#/api/mes/safetyEnv/emergencyevent';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmergencyEvent,
  getEmergencyEventPage,
} from '#/api/mes/safetyEnv/emergencyevent';
import { $t } from '#/locales';

import {
  EVENT_TYPE_MAP,
  SCENARIO_MAP,
  EVENT_STATUS_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import DisposeModal from './modules/dispose-modal.vue';
import Form from './modules/form.vue';

const [DisposeModalComp, disposeModalApi] = useVbenModal({
  connectedComponent: DisposeModal,
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

/** 新建应急事件 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑应急事件 */
function handleEdit(row: MesSetEmergencyEventApi.EmergencyEvent) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除应急事件 */
async function handleDelete(row: MesSetEmergencyEventApi.EmergencyEvent) {
  const label = row.location ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteEmergencyEvent(row.id!);
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
          await getEmergencyEventPage({
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
  } as VxeTableGridOptions<MesSetEmergencyEventApi.EmergencyEvent>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <DisposeModalComp @success="handleRefresh" />
    <Grid table-title="应急事件列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建应急事件',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-emergency-event:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #eventType="{ row }">
        {{ EVENT_TYPE_MAP[row.eventType ?? ''] ?? row.eventType ?? '-' }}
      </template>
      <template #scenario="{ row }">
        {{ SCENARIO_MAP[row.scenario ?? ''] ?? row.scenario ?? '-' }}
      </template>
      <template #status="{ row }">
        <Tag :color="EVENT_STATUS_MAP[row.status ?? '']?.color ?? 'default'">
          {{ EVENT_STATUS_MAP[row.status ?? '']?.text ?? row.status ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-emergency-event:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '处置/闭环',
              type: 'link',
              auth: ['mes:set-emergency-event:close'],
              onClick: () => disposeModalApi.setData(row).open(),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-emergency-event:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.location]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
