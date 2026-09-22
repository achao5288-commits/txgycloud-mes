<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesHazwasteApi } from '#/api/mes/safetyEnv/hazwaste';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  advanceHazwasteStage,
  deleteHazwaste,
  getHazwastePage,
} from '#/api/mes/safetyEnv/hazwaste';
import { $t } from '#/locales';

import { STAGE_MAP, STAGE_OPTIONS, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import GateModal from './modules/gate-modal.vue';
import LabelModal from './modules/label-modal.vue';
import ManifestDrawer from './modules/manifest-drawer.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [Gate, gateApi] = useVbenModal({
  connectedComponent: GateModal,
  destroyOnClose: true,
});

const [Label, labelApi] = useVbenModal({
  connectedComponent: LabelModal,
  destroyOnClose: true,
});

// 这里必须是 useVbenDrawer：manifest-drawer.vue 内部调的是 useVbenDrawer，
// 而 useVbenModal / useVbenDrawer 的 provide 键是两个不同的 Symbol，
// 用 useVbenModal 包它 → 子组件 inject 不到父的 api，自己另起一个抽屉，
// 父的 manifestApi.open() 落在一个接不上原型对象的空 reactive 上，点击毫无反应。
const [Manifest, manifestApi] = useVbenDrawer({
  connectedComponent: ManifestDrawer,
  destroyOnClose: true,
});

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

function handleEdit(row: MesHazwasteApi.HazardousWaste) {
  formModalApi.setData({ formType: 'update', id: row.id }).open();
}

function handleLabel(row: MesHazwasteApi.HazardousWaste) {
  labelApi.setData({ id: row.id }).open();
}

function handleManifest(row: MesHazwasteApi.HazardousWaste) {
  manifestApi
    .setData({ manifestNo: row.manifestNo, waste: row })
    .open();
}

function handleGate() {
  gateApi.open();
}

/** 下一环节：环节不可回退，界面上只给"下一步"，出现回退入口本身就是引导犯错 */
function nextStage(row: MesHazwasteApi.HazardousWaste) {
  const current = STAGE_OPTIONS.findIndex((o) => o.value === row.stage);
  return STAGE_OPTIONS[current + 1];
}

async function handleAdvance(
  row: MesHazwasteApi.HazardousWaste,
  stage: string,
) {
  try {
    await advanceHazwasteStage(row.id!, stage);
    message.success('环节已推进');
    handleRefresh();
  } catch {
    // 拦截原因由全局错误提示展示（如"不得回退"），此处只需不刷新
  }
}

async function handleDelete(row: MesHazwasteApi.HazardousWaste) {
  const label = row.manifestNo ?? '';
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [label]),
    duration: 0,
  });
  try {
    await deleteHazwaste(row.id!);
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
          await getHazwastePage({
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
  } as VxeTableGridOptions<MesHazwasteApi.HazardousWaste>,
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Gate @success="handleRefresh" />
    <Label />
    <Manifest @success="handleRefresh" />
    <Grid table-title="危废台账（环节只进不退：产生→贮存→转移→处置；出厂须凭生效联单过门卫）">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新建危废',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:set-hazwaste:create'],
              onClick: handleCreate,
            },
            {
              label: '门卫放行',
              type: 'primary',
              ghost: true,
              auth: ['mes:set-hazwaste:gate'],
              onClick: handleGate,
            },
          ]"
        />
      </template>
      <template #stage="{ row }">
        <Tag :color="STAGE_MAP[row.stage]?.color">
          {{ STAGE_MAP[row.stage]?.text ?? row.stage }}
        </Tag>
      </template>
      <template #actions="{ row }">
        <!-- 「推进」的显隐必须是 ifShow：ActionItem 上只有 ifShow，写 show 会被静默忽略 →
             终态行（nextStage 为 undefined）照样渲染出一个"推进环节"，
             点确认走的是 advanceHazwasteStage(id, '')，注定失败。
             注意：Vue 模板表达式里不能写 // 注释，会直接把这段属性解析搞崩（vite 500）。 -->
        <TableAction
          :actions="[
            {
              label: '联单',
              type: 'link',
              auth: ['mes:set-hazwaste:query'],
              onClick: handleManifest.bind(null, row),
            },
            {
              label: '标签',
              type: 'link',
              auth: ['mes:set-hazwaste:query'],
              onClick: handleLabel.bind(null, row),
            },
            {
              label: nextStage(row) ? `推进到${nextStage(row)!.label}` : '推进环节',
              type: 'link',
              auth: ['mes:set-hazwaste:update'],
              ifShow: !!nextStage(row),
              popConfirm: {
                title: `确认把 ${row.manifestNo} 推进到「${nextStage(row)?.label ?? ''}」？环节不可回退。`,
                confirm: handleAdvance.bind(null, row, nextStage(row)?.value ?? ''),
              },
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:set-hazwaste:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:set-hazwaste:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.manifestNo]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
