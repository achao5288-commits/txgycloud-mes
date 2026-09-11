<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyDrillApi } from '#/api/mes/safetyEnv/emergencydrill';

import { Page, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmergencyDrill,
  getEmergencyDrillPage,
} from '#/api/mes/safetyEnv/emergencydrill';
import { $t } from '#/locales';

import {
  DRILL_TYPE_MAP,
  RECTIFY_STATUS_MAP,
  DRILL_STATUS_MAP,
  useGridColumns,
  useGridFormSchema,
} from './data';
import CoverageModal from './modules/coverage-modal.vue';
import FinishModal from './modules/finish-modal.vue';
import Form from './modules/form.vue';

const [FinishModalComp, finishModalApi] = useVbenModal({
  connectedComponent: FinishModal,
  destroyOnClose: true,
});
const [CoverageModalComp, coverageModalApi] = useVbenModal({
  connectedComponent: CoverageModal,
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

/** 新建应急演练 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 编辑应急演练 */
function handleEdit(row: MesSetEmergencyDrillApi.EmergencyDrill) {
  formModalApi.setData({ id: row.id, formType: 'update' }).open();
}

/** 删除应急演练 */
async function handleDelete(row: MesSetEmergencyDrillApi.EmergencyDrill) {
  const label = row.drillName ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteEmergencyDrill(row.id!);
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
          await getEmergencyDrillPage({
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
  } as VxeTableGridOptions<MesSetEmergencyDrillApi.EmergencyDrill>,
});
</script>
<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <FinishModalComp @success="handleRefresh" />
    <CoverageModalComp />
    <Grid table-title="应急演练列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建应急演练',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-emergency-drill:create'],
              onClick: handleCreate,
            },
            {
              label: '年度覆盖',
              auth: ['mes:set-emergency-drill:query'],
              onClick: () => coverageModalApi.open(),
            },
          ]"
        />
      </template>
      <template #drillType="{ row }">
        {{ DRILL_TYPE_MAP[row.drillType ?? ''] ?? row.drillType ?? '-' }}
      </template>
      <template #rectifyStatus="{ row }">
        <Tag :color="RECTIFY_STATUS_MAP[row.rectifyStatus ?? '']?.color ?? 'default'">
          {{ RECTIFY_STATUS_MAP[row.rectifyStatus ?? '']?.text ?? row.rectifyStatus ?? '-' }}
        </Tag>
      </template>
      <template #status="{ row }">
        <Tag :color="DRILL_STATUS_MAP[row.status ?? '']?.color ?? 'default'">
          {{ DRILL_STATUS_MAP[row.status ?? '']?.text ?? row.status ?? '-' }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-emergency-drill:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: '记录/闭环',
              type: 'link',
              auth: ['mes:set-emergency-drill:close'],
              onClick: () => finishModalApi.setData(row).open(),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-emergency-drill:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.drillName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
