<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetStandardApi } from '#/api/mes/safetyEnv/standard';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteStandard,
  getStandardPage,
} from '#/api/mes/safetyEnv/standard';
import { $t } from '#/locales';

import { DOMAIN_MAP, PERIOD_TYPE_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建检测标准 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑检测标准 */
function handleEdit(row: MesSetStandardApi.Standard) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除检测标准 */
async function handleDelete(row: MesSetStandardApi.Standard) {
  const label = row.standardName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteStandard(row.id!);
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
          await getStandardPage({
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
  } as VxeTableGridOptions<MesSetStandardApi.Standard>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="检测标准列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建检测标准',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-standard:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #domain="{ row }">
        <Tag :color="DOMAIN_MAP[row.domain]?.color">
          {{ DOMAIN_MAP[row.domain]?.text ?? row.domain }}
        </Tag>
      </template>
      <template #periodType="{ row }">
        <Tag :color="PERIOD_TYPE_MAP[row.periodType]?.color">
          {{ PERIOD_TYPE_MAP[row.periodType]?.text ?? row.periodType }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-standard:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-standard:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.standardName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
