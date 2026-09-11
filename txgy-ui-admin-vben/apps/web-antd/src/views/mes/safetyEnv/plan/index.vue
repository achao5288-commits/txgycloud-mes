<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPlanApi } from '#/api/mes/safetyEnv/plan';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePlan,
  getPlanPage,
} from '#/api/mes/safetyEnv/plan';
import { $t } from '#/locales';

import { PERIOD_TYPE_MAP, PLAN_TYPE_MAP, STATUS_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 新建检测计划 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑检测计划 */
function handleEdit(row: MesSetPlanApi.Plan) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除检测计划 */
async function handleDelete(row: MesSetPlanApi.Plan) {
  const label = row.planName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deletePlan(row.id!);
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
          await getPlanPage({
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
  } as VxeTableGridOptions<MesSetPlanApi.Plan>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="检测计划列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建检测计划',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-plan:create'],
              onClick: handleCreate,
            },
          ]"
        />
      </template>
      <template #planType="{ row }">
        <Tag :color="PLAN_TYPE_MAP[row.planType]?.color">
          {{ PLAN_TYPE_MAP[row.planType]?.text ?? row.planType }}
        </Tag>
      </template>
      <template #periodType="{ row }">
        <Tag :color="PERIOD_TYPE_MAP[row.periodType]?.color">
          {{ PERIOD_TYPE_MAP[row.periodType]?.text ?? row.periodType }}
        </Tag>
      </template>
      <template #status="{ row }">
        <Tag :color="STATUS_MAP[row.status]?.color">
          {{ STATUS_MAP[row.status]?.text ?? row.status }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-plan:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-plan:delete'],
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
