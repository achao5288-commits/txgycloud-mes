<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteFacility, getFacilityPage } from '#/api/mes/safetyEnv/facility';
import { $t } from '#/locales';

import {
  FACILITY_TYPE_MAP,
  SHUTDOWN_STATUS_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import CoRunModal from './modules/co-run-modal.vue';
import DueModal from './modules/due-modal.vue';
import Form from './modules/form.vue';
import ReplaceModal from './modules/replace-modal.vue';
import ShutdownModal from './modules/shutdown-modal.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});
const [ReplaceModalComp, replaceModalApi] = useVbenModal({
  connectedComponent: ReplaceModal,
  destroyOnClose: true,
});
const [ShutdownModalComp, shutdownModalApi] = useVbenModal({
  connectedComponent: ShutdownModal,
  destroyOnClose: true,
});
const [CoRunModalComp, coRunModalApi] = useVbenModal({
  connectedComponent: CoRunModal,
  destroyOnClose: true,
});
const [DueModalComp, dueModalApi] = useVbenModal({
  connectedComponent: DueModal,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

function handleEdit(row: MesFacilityApi.TreatmentFacility) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

async function handleDelete(row: MesFacilityApi.TreatmentFacility) {
  const label = row.facilityName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteFacility(row.id!);
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
          await getFacilityPage({
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
  } as VxeTableGridOptions<MesFacilityApi.TreatmentFacility>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <ReplaceModalComp @success="handleRefresh" />
    <ShutdownModalComp @success="handleRefresh" />
    <CoRunModalComp />
    <DueModalComp />
    <Grid table-title="治污设施台账">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建治污设施',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-facility:create'],
              onClick: handleCreate,
            },
            {
              label: '同开同停校验',
              auth: ['mes:set-facility:query'],
              onClick: () => coRunModalApi.open(),
            },
            {
              label: '换炭到期预警',
              auth: ['mes:set-facility:query'],
              onClick: () => dueModalApi.open(),
            },
          ]"
        />
      </template>
      <template #facilityType="{ row }">
        {{ FACILITY_TYPE_MAP[row.facilityType ?? ''] ?? row.facilityType ?? '-' }}
      </template>
      <template #runStatus="{ row }">
        <Tag :color="row.runStatus === 'RUNNING' ? 'success' : 'error'">
          {{ row.runStatus === 'RUNNING' ? '运行' : '停运' }}
        </Tag>
      </template>
      <template #shutdownStatus="{ row }">
        <Tag
          :color="
            row.shutdownStatus === 'APPROVED'
              ? 'error'
              : row.shutdownStatus === 'PENDING'
                ? 'warning'
                : 'default'
          "
        >
          {{ SHUTDOWN_STATUS_MAP[row.shutdownStatus ?? ''] ?? row.shutdownStatus ?? '-' }}
        </Tag>
      </template>
      <template #nextReplace="{ row }">
        <span v-if="!row.nextReplaceDate" class="text-gray-400">未设周期</span>
        <span v-else>{{ row.nextReplaceDate }}</span>
      </template>
      <template #status="{ row }">
        <Tag :color="row.status === 'ENABLED' ? 'success' : 'default'">
          {{ row.status === 'ENABLED' ? '启用' : '停用' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-facility:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '换炭',
              type: 'link',
              auth: ['mes:set-facility:operate'],
              onClick: () => replaceModalApi.setData({ id: row.id }).open(),
            },
            {
              label: '停运申报',
              type: 'link',
              auth: ['mes:set-facility:operate'],
              onClick: () => shutdownModalApi.setData({ id: row.id }).open(),
            },
            {
              label: '同开同停',
              type: 'link',
              auth: ['mes:set-facility:query'],
              onClick: () => coRunModalApi.setData({ id: row.id }).open(),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-facility:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.facilityName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
