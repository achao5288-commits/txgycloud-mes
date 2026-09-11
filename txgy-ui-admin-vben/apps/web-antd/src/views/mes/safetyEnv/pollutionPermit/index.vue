<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionPermitApi } from '#/api/mes/safetyEnv/pollutionPermit';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePollutionPermit,
  getPollutionPermitPage,
} from '#/api/mes/safetyEnv/pollutionPermit';
import { $t } from '#/locales';

import { PERMIT_STATUS_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建排污许可证 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑排污许可证 */
function handleEdit(row: MesSetPollutionPermitApi.Permit) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除排污许可证 */
async function handleDelete(row: MesSetPollutionPermitApi.Permit) {
  const label = row.permitNo ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deletePollutionPermit(row.id!);
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
          await getPollutionPermitPage({
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
  } as VxeTableGridOptions<MesSetPollutionPermitApi.Permit>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="排污许可证列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建排污许可证',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-pollution-permit:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #status="{ row }">
        <Tag :color="PERMIT_STATUS_MAP[row.status]?.color">
          {{ PERMIT_STATUS_MAP[row.status]?.text ?? row.status }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-pollution-permit:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-pollution-permit:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.permitNo]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
