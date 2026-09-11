<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesChemicalApi } from '#/api/mes/safetyEnv/chemical';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteChemical,
  getChemicalPage,
} from '#/api/mes/safetyEnv/chemical';
import { $t } from '#/locales';

import {
  COMPAT_GROUP_MAP,
  STORAGE_ZONE_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import AlertsModal from './modules/alerts-modal.vue';
import CheckModal from './modules/check-modal.vue';
import DoubleSignModal from './modules/double-sign-modal.vue';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});
const [CheckModalComp, checkModalApi] = useVbenModal({
  connectedComponent: CheckModal,
  destroyOnClose: true,
});
const [SignModalComp, signModalApi] = useVbenModal({
  connectedComponent: DoubleSignModal,
  destroyOnClose: true,
});
const [AlertsModalComp, alertsModalApi] = useVbenModal({
  connectedComponent: AlertsModal,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

function handleEdit(row: MesChemicalApi.ChemicalProfile) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

async function handleDelete(row: MesChemicalApi.ChemicalProfile) {
  const label = row.chemicalName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteChemical(row.id!);
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
          await getChemicalPage({
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
  } as VxeTableGridOptions<MesChemicalApi.ChemicalProfile>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <CheckModalComp @success="handleRefresh" />
    <SignModalComp @success="handleRefresh" />
    <AlertsModalComp />
    <Grid table-title="危化品档案列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建危化品档案',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-chemical:create'],
              onClick: handleCreate,
            },
            {
              label: '库位校验/入库',
              auth: ['mes:set-chemical:check'],
              onClick: () => checkModalApi.open(),
            },
            {
              label: '五双签字',
              auth: ['mes:set-chemical:sign'],
              onClick: () => signModalApi.open(),
            },
            {
              label: '预警',
              auth: ['mes:set-chemical:query'],
              onClick: () => alertsModalApi.open(),
            },
          ]"
        />
      </template>
      <template #compatGroup="{ row }">
        {{ COMPAT_GROUP_MAP[row.compatGroup ?? ''] ?? row.compatGroup ?? '-' }}
      </template>
      <template #storageZone="{ row }">
        {{ STORAGE_ZONE_MAP[row.storageZone ?? ''] ?? row.storageZone ?? '-' }}
      </template>
      <template #quota="{ row }">
        <span v-if="row.storageLimit === null || row.storageLimit === undefined">
          不限量
        </span>
        <span
          v-else
          :class="
            (row.stockQuantity ?? 0) > row.storageLimit ? 'text-red-500' : ''
          "
        >
          {{ row.stockQuantity ?? 0 }} / {{ row.storageLimit }}
          {{ row.storageUnit ?? '' }}
        </span>
      </template>
      <template #msds="{ row }">
        <Tag :color="row.msdsUrl ? 'success' : 'error'">
          {{ row.msdsUrl ? '已挂载' : '缺失' }}
        </Tag>
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
              auth: ['mes:set-chemical:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '校验/入库',
              type: 'link',
              auth: ['mes:set-chemical:check'],
              onClick: () =>
                checkModalApi
                  .setData({
                    profileNo: row.profileNo,
                    storageLocation: row.storageLocation,
                  })
                  .open(),
            },
            {
              label: '签字',
              type: 'link',
              auth: ['mes:set-chemical:sign'],
              onClick: () =>
                signModalApi.setData({ bizNo: row.profileNo }).open(),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-chemical:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.chemicalName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
