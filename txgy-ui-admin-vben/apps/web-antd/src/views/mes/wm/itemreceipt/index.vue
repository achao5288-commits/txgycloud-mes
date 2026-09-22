<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesWmItemReceiptApi } from '#/api/mes/wm/itemreceipt';

import { DocAlert, Page, useVbenModal } from '@vben/common-ui';
import { MesWmItemReceiptStatusEnum } from '@vben/constants';
import { downloadFileFromBlobPart } from '@vben/utils';

import { Button, message, Tag, Tooltip } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  cancelItemReceipt,
  deleteItemReceipt,
  exportItemReceipt,
  getItemReceiptPage,
} from '#/api/mes/wm/itemreceipt';
import { getItemReceiptLinePage } from '#/api/mes/wm/itemreceipt/line';
import { $t } from '#/locales';

import JudgeModal from '../_pollution/judge-modal.vue';
import { POLLUTION_STATUS_MAP, useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [PollutionJudgeModal, pollutionJudgeApi] = useVbenModal({
  connectedComponent: JudgeModal,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建采购入库单 */
function handleCreate() {
  formModalApi.setData({ formType: 'create' }).open();
}

/** 查看采购入库单 */
function handleDetail(row: MesWmItemReceiptApi.ItemReceipt) {
  formModalApi.setData({ formType: 'detail', id: row.id }).open();
}

/** 编辑采购入库单 */
function handleEdit(row: MesWmItemReceiptApi.ItemReceipt) {
  formModalApi.setData({ formType: 'update', id: row.id }).open();
}

/** 执行上架 */
function handleStock(row: MesWmItemReceiptApi.ItemReceipt) {
  formModalApi.setData({ formType: 'stock', id: row.id }).open();
}

/** 执行入库 */
function handleFinish(row: MesWmItemReceiptApi.ItemReceipt) {
  formModalApi.setData({ formType: 'finish', id: row.id }).open();
}

/** 环保判定：按该单真实物料行逐行 AI 初筛→人工复核 */
async function handleJudge(row: MesWmItemReceiptApi.ItemReceipt) {
  const { list } = await getItemReceiptLinePage({
    receiptId: row.id!,
    pageNo: 1,
    pageSize: 200,
  });
  pollutionJudgeApi
    .setData({
      title: '采购入库环保判定',
      stage: 'PURCHASE_INBOUND',
      bizNo: row.code ?? '',
      lines: (list ?? []).map((line) => ({
        lineId: line.id,
        itemCode: line.itemCode,
        itemName: line.itemName,
        itemSpec: line.specification,
        batchId: line.batchId,
        batchNo: line.batchCode,
        weight: line.receivedQuantity,
      })),
    })
    .open();
}

/**
 * 判定状态列的悬浮说明：把「判定去了哪」写清楚。
 * 只看这一列会以为结果只停在本单，实际上判定行同时进了安全环保侧的判定台账，
 * 批次在库之后还会出现在在库环保视图——不说清楚就会出现"判完了环保那边怎么没有"。
 */
function pollutionTip(row: MesWmItemReceiptApi.ItemReceipt) {
  const judged = row.pollutionJudgedLines ?? 0;
  const total = row.pollutionLineCount ?? 0;
  const pending = row.pollutionPendingLines ?? 0;
  const parts = [`已判定 ${judged}/${total} 个物料行`];
  if (pending > 0) {
    parts.push(`待复核 ${pending} 行（判 CLEAN 才解冻，见库存冻结）`);
  }
  parts.push('判定记录已进「安全环保 › 污染管控 › 污染判定」台账；该批次入库后同步显示在「在库环保视图」');
  return parts.join('；');
}

/** 删除采购入库单 */
async function handleDelete(row: MesWmItemReceiptApi.ItemReceipt) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.code]),
    duration: 0,
  });
  try {
    await deleteItemReceipt(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.code]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 取消采购入库单 */
async function handleCancel(row: MesWmItemReceiptApi.ItemReceipt) {
  await cancelItemReceipt(row.id!);
  message.success('取消成功');
  handleRefresh();
}

/** 导出表格 */
async function handleExport() {
  const data = await exportItemReceipt(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '采购入库单.xls', source: data });
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
        query: async ({ page }, formValues) => {
          return await getItemReceiptPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
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
  } as VxeTableGridOptions<MesWmItemReceiptApi.ItemReceipt>,
});
</script>

<template>
  <Page auto-content-height>
    <template #doc>
      <DocAlert
        title="【仓库】到货通知、采购入库、采购退货"
        url="https://doc.iocoder.cn/mes/wm/purchase-in/"
      />
    </template>

    <FormModal @success="handleRefresh" />

    <!-- 判定弹窗每次建单/复核都会 emit success：不接这个事件，列表要手动刷新才看得到判定结果 -->
    <PollutionJudgeModal @success="handleRefresh" />

    <Grid table-title="采购入库单列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['采购入库单']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['mes:wm-item-receipt:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['mes:wm-item-receipt:export'],
              onClick: handleExport,
            },
          ]"
        />
      </template>
      <template #code="{ row }">
        <Button type="link" @click="handleDetail(row)">
          {{ row.code }}
        </Button>
      </template>
      <template #pollutionStatus="{ row }">
        <Tooltip :title="pollutionTip(row)">
          <Tag
            class="cursor-pointer"
            :color="POLLUTION_STATUS_MAP[row.pollutionStatus ?? '']?.color"
            @click="handleJudge(row)"
          >
            {{ POLLUTION_STATUS_MAP[row.pollutionStatus ?? '']?.text ?? '未判定' }}
          </Tag>
        </Tooltip>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['mes:wm-item-receipt:update'],
              ifShow: row.status === MesWmItemReceiptStatusEnum.PREPARE,
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['mes:wm-item-receipt:delete'],
              ifShow: row.status === MesWmItemReceiptStatusEnum.PREPARE,
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.code]),
                confirm: handleDelete.bind(null, row),
              },
            },
            {
              label: '执行上架',
              type: 'link',
              auth: ['mes:wm-item-receipt:update'],
              ifShow: row.status === MesWmItemReceiptStatusEnum.APPROVING,
              onClick: handleStock.bind(null, row),
            },
            {
              label: '执行入库',
              type: 'link',
              auth: ['mes:wm-item-receipt:finish'],
              ifShow: row.status === MesWmItemReceiptStatusEnum.APPROVED,
              onClick: handleFinish.bind(null, row),
            },
            {
              label: '环保判定',
              type: 'link',
              auth: ['mes:set-pollution-check:create'],
              onClick: handleJudge.bind(null, row),
            },
            {
              label: '取消',
              type: 'link',
              danger: true,
              auth: ['mes:wm-item-receipt:update'],
              ifShow:
                row.status === MesWmItemReceiptStatusEnum.APPROVING ||
                row.status === MesWmItemReceiptStatusEnum.APPROVED,
              popConfirm: {
                title: '确认取消该采购入库单？取消后不可恢复。',
                confirm: handleCancel.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
