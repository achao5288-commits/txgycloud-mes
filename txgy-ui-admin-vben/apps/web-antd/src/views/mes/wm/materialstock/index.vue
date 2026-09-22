<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesWmMaterialStockApi } from '#/api/mes/wm/materialstock';

import { ref } from 'vue';

import { DocAlert, Page, useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart } from '@vben/utils';

import { Button, Card, message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  exportMaterialStock,
  getMaterialStockPage,
  updateMaterialStockFrozen,
} from '#/api/mes/wm/materialstock';
import { $t } from '#/locales';
import SignConfirmModal from '#/views/mes/safetyEnv/components/SignConfirmModal.vue';
import { MdItemTypeTree } from '#/views/mes/md/item/type/components';
import { WmBatchDetail } from '#/views/mes/wm/batch/components';
import AreaForm from '#/views/mes/wm/warehouse/area/modules/form.vue';

import JudgeModal from '../_pollution/judge-modal.vue';
import { useGridColumns, useGridFormSchema } from './data';

const [AreaModal, areaModalApi] = useVbenModal({
  connectedComponent: AreaForm,
  destroyOnClose: true,
});

const [PollutionJudgeModal, pollutionJudgeApi] = useVbenModal({
  connectedComponent: JudgeModal,
  destroyOnClose: true,
});

const batchDetailRef = ref<InstanceType<typeof WmBatchDetail>>();
/** 冻结开关是人为改环保数据，先签字再请求 */
const signRef = ref<InstanceType<typeof SignConfirmModal>>();

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 导出表格 */
async function handleExport() {
  const data = await exportMaterialStock({
    ...(await gridApi.formApi.getValues()),
    itemTypeId: searchItemTypeId.value,
  });
  downloadFileFromBlobPart({ fileName: '库存台账.xls', source: data });
}

/** 选择物料分类 */
const searchItemTypeId = ref<number | undefined>(undefined);
function handleTypeNodeClick(row: undefined | { id?: number }) {
  searchItemTypeId.value = row?.id;
  handleRefresh();
}

/** 打开库位详情弹窗 */
function handleOpenAreaDetail(row: MesWmMaterialStockApi.MaterialStock) {
  if (!row.areaId) {
    return;
  }
  areaModalApi.setData({ formType: 'detail', id: row.areaId }).open();
}

/** 打开批次详情弹窗 */
function handleOpenBatchDetail(row: MesWmMaterialStockApi.MaterialStock) {
  if (!row.batchId) {
    return;
  }
  batchDetailRef.value?.open(row.batchId);
}

/**
 * 在库环保检测：对这条库存的批次发起判定。
 * 与单据入口的区别是没有单据号，锚点直接用 batchId —— 判定落 batchId 后，
 * 库存冻结/台账/超期那些按 batch_no 匹配的既有投影才认这条判定。
 */
function handleJudge(row: MesWmMaterialStockApi.MaterialStock) {
  if (!row.batchId) {
    message.warning('该库存行没有关联批次，无法发起在库检测');
    return;
  }
  pollutionJudgeApi
    .setData({
      title: `库存 ${row.batchCode ?? ''}`,
      stage: 'IN_STOCK',
      bizNo: '',
      lines: [
        {
          batchId: row.batchId,
          batchNo: row.batchCode,
          itemCode: row.itemCode,
          itemName: row.itemName,
          itemSpec: row.specification,
          weight: row.quantity,
        },
      ],
    })
    .open();
}

/**
 * 处理冻结状态切换。返回 false 会让 vxe 的开关回弹（等于"这次没改成"）。
 *
 * 人工冻结/解冻是改环保数据的动作，必须手写签名：后端缺签名直接拒（1040703015）。
 * 另外 frozen 是批次污染的投影，人工改动会被下一次投影重算盖掉 —— 签字证明的是
 * "谁点过这个开关"，不是"这批货冻着"。
 */
async function handleFrozenChange(
  newFrozen: boolean,
  row: MesWmMaterialStockApi.MaterialStock,
): Promise<boolean | undefined> {
  const text = newFrozen ? '冻结' : '解冻';
  const sign = await signRef.value?.open(
    `人工${text}库存`,
    `${row.itemName ?? '-'}｜批次 ${row.batchCode || '-'}｜库位 ${row.locationName ?? '-'}。`
      + `注意：库存冻结状态是批次污染的投影，人工${text}只是临时覆盖，`
      + `下一次污染判定重算时会被改回去。`,
  );
  if (!sign) {
    return false;
  }
  // 更新冻结状态
  await updateMaterialStockFrozen({
    id: row.id!,
    frozen: newFrozen,
    signImg: sign.signImg,
    opinion: sign.opinion,
  });
  // 提示并返回成功
  message.success(`${text}成功`);
  return true;
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(handleFrozenChange),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getMaterialStockPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
            itemTypeId: searchItemTypeId.value,
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
  } as VxeTableGridOptions<MesWmMaterialStockApi.MaterialStock>,
});
</script>

<template>
  <Page auto-content-height>
    <template #doc>
      <DocAlert
        title="【仓库】批次管理、库存现有量、库存事务"
        url="https://doc.iocoder.cn/mes/wm/stock/"
      />
    </template>

    <AreaModal />
    <WmBatchDetail ref="batchDetailRef" />
    <SignConfirmModal ref="signRef" />

    <!-- 判定弹窗每次建单/复核都会 emit success：不接这个事件，列表要手动刷新才看得到判定结果 -->
    <PollutionJudgeModal @success="handleRefresh" />

    <div class="flex h-full w-full">
      <!-- 左侧物料分类树 -->
      <Card class="mr-4 h-full w-1/6">
        <MdItemTypeTree @node-click="handleTypeNodeClick" />
      </Card>
      <!-- 右侧库存台账列表 -->
      <div class="w-5/6">
        <Grid table-title="库存台账列表">
          <template #toolbar-tools>
            <TableAction
              :actions="[
                {
                  label: $t('ui.actionTitle.export'),
                  type: 'primary',
                  icon: ACTION_ICON.DOWNLOAD,
                  auth: ['mes:wm-material-stock:export'],
                  onClick: handleExport,
                },
              ]"
            />
          </template>
          <template #batchCode="{ row }">
            <Button
              v-if="row.batchId"
              :title="row.batchCode"
              size="small"
              type="link"
              @click="handleOpenBatchDetail(row)"
            >
              {{ row.batchCode }}
            </Button>
            <span v-else>-</span>
          </template>
          <template #areaName="{ row }">
            <Button
              v-if="row.areaId"
              :title="row.areaName"
              size="small"
              type="link"
              @click="handleOpenAreaDetail(row)"
            >
              {{ row.areaName }}
            </Button>
            <span v-else>-</span>
          </template>
          <template #actions="{ row }">
            <TableAction
              :actions="[
                {
                  label: '环保判定',
                  type: 'link',
                  auth: ['mes:set-pollution-check:create'],
                  onClick: handleJudge.bind(null, row),
                },
              ]"
            />
          </template>
        </Grid>
      </div>
    </div>
  </Page>
</template>
