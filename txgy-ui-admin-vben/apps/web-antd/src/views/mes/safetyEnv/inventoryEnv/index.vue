<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesInventoryEnvApi } from '#/api/mes/safetyEnv/inventoryEnv';

import { h, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { Alert, Button, message, Modal, Progress, Tag, Tooltip } from 'ant-design-vue';

import { TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getInventoryEnvPage } from '#/api/mes/safetyEnv/inventoryEnv';
import { createPollutionCheck } from '#/api/mes/safetyEnv/pollutionCheck';
import { updatePollutionLedgerMark } from '#/api/mes/safetyEnv/pollutionLedger';
import { updateMaterialStockFrozen } from '#/api/mes/wm/materialstock';

import JudgeModal from '../../wm/_pollution/judge-modal.vue';
import { AI_RESULT_MAP, REVIEW_RESULT_MAP } from '../pollutionCheck/data';
import SignConfirmModal from '../components/SignConfirmModal.vue';
import BatchProfileDrawer from './components/batch-profile-drawer.vue';
import ControlLocationModal from './components/control-location-modal.vue';
import EnvMetrics from './components/env-metrics.vue';
import { useGridColumns, useGridFormSchema } from './data';

const [PollutionJudgeModal, pollutionJudgeApi] = useVbenModal({
  connectedComponent: JudgeModal,
  destroyOnClose: true,
});

const [ControlLocation, controlLocationApi] = useVbenModal({
  connectedComponent: ControlLocationModal,
  destroyOnClose: true,
});

const batchProfileRef = ref<InstanceType<typeof BatchProfileDrawer>>();
const metricsRef = ref<InstanceType<typeof EnvMetrics>>();
/**
 * 本页三类按钮（冻结/解冻、标记终审、发起检测）都是人为改环保数据，
 * 一律先签字再请求 —— 后端对应接口缺签名直接拒，绕不过去。
 */
const signRef = ref<InstanceType<typeof SignConfirmModal>>();

/** 批量发起检测：并发太多会把 AI 网关打满，逐批串行 + 进度条 */
const judging = ref(false);
const judgeProgress = ref({ done: 0, total: 0, failed: 0 });

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
  metricsRef.value?.refresh();
}

/**
 * 看板下钻：先清空再设值。
 * setValues 是合并语义，不清空会把上一次下钻的条件留在表单里，两次条件叠加后筛出空列表。
 */
async function handleDrill(filter: null | Record<string, any>) {
  await gridApi.formApi.resetForm();
  if (filter) {
    await gridApi.formApi.setValues(filter);
  }
  gridApi.query();
}

/** 在库环保检测：锚点是 batchId，没有批次的库存行直接拦掉（判定落不下去） */
function handleJudge(row: MesInventoryEnvApi.InventoryEnv) {
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
 * 批量发起在库检测。
 *
 * 这里刻意不做一个批量后端接口：发起检测 = 建一条待复核判定，而建单会连带
 * 「待检即冻」把该批在库行冻上（见 MesPollutionControlServiceImpl.syncStockFrozenByBatch），
 * 是实打实的业务动作而不是只读扫描。逐批串行调既有接口，能按批反馈失败、能中途看清冻了哪些，
 * 比一个跑到一半超时、不知道成功了几批的批量接口好用。
 *
 * ponytail: 串行且不落任务表，所以关掉页面就中断；要能离屏跑再上定时任务。
 */
async function handleBatchJudge() {
  const rows = (gridApi.grid?.getCheckboxRecords() ?? []) as MesInventoryEnvApi.InventoryEnv[];
  if (rows.length === 0) {
    message.warning('请先勾选要发起检测的库存行');
    return;
  }
  // 同一批次可能在多行里出现，按 batchId 去重，否则会给同批建多条待复核判定
  const targets = new Map<number, MesInventoryEnvApi.InventoryEnv>();
  let skipped = 0;
  for (const row of rows) {
    if (!row.batchId) {
      skipped++;
      continue;
    }
    if (!targets.has(row.batchId)) {
      targets.set(row.batchId, row);
    }
  }
  if (targets.size === 0) {
    message.warning('勾选的行都没有关联批次，无法发起在库检测');
    return;
  }

  // 一次签名覆盖这一批（已确认的口径）：下面串行建单每单复用同一份签名图，
  // 后端按单各落一条签字记录，人不必画 N 遍。
  const sign = await signRef.value?.open(
    '发起在库环保检测',
    `将按 ${targets.size} 个批次各建一条待复核判定（已去重${
      skipped > 0 ? `，跳过 ${skipped} 行无批次` : ''
    }）。注意：判定落库后这些批次的在库库存会被立即冻结，判「无污染」后才解冻。`,
  );
  if (!sign) {
    return;
  }

  judging.value = true;
  judgeProgress.value = { done: 0, total: targets.size, failed: 0 };
  const failures: string[] = [];
  for (const row of targets.values()) {
    try {
      await createPollutionCheck({
        stage: 'IN_STOCK',
        bizNo: '',
        batchId: row.batchId,
        batchNo: row.batchCode,
        itemCode: row.itemCode,
        itemName: row.itemName,
        itemSpec: row.specification,
        signImg: sign.signImg,
        opinion: sign.opinion,
      });
    } catch (error: any) {
      judgeProgress.value.failed++;
      failures.push(`${row.batchCode ?? row.batchId}：${error?.message ?? '未知错误'}`);
    }
    judgeProgress.value.done++;
  }
  judging.value = false;

  if (failures.length === 0) {
    message.success(`${targets.size} 个批次已发起检测，请到「污染判定」复核`);
  } else {
    // 失败原因逐条列出来：批量操作最怕「失败了但不知道是哪些」
    Modal.warning({
      title: `${targets.size - failures.length} 个成功，${failures.length} 个失败`,
      content: () =>
        h(
          'div',
          { style: 'max-height: 240px; overflow: auto' },
          failures.map((text) => h('div', { style: 'font-size: 12px' }, text)),
        ),
      width: 520,
    });
  }
  handleRefresh();
}

/** 冻结/解冻：走库存台账既有接口，判定为污染时后端另会自动冻结 */
async function handleFrozen(row: MesInventoryEnvApi.InventoryEnv) {
  const next = !row.frozen;
  const text = next ? '冻结' : '解冻';
  const sign = await signRef.value?.open(
    `人工${text}库存`,
    `批次 ${row.batchCode ?? ''}（${row.itemName ?? '-'}）。`
      + `注意：库存冻结状态是批次污染的投影，人工${text}只是临时覆盖，`
      + `下一次污染判定重算时会被改回去。`,
  );
  if (!sign) {
    return;
  }
  await updateMaterialStockFrozen({
    id: row.id!,
    frozen: next,
    signImg: sign.signImg,
    opinion: sign.opinion,
  });
  message.success(`${text}成功`);
  handleRefresh();
}

/** 标记终审 / 解除：标的是污染台账行（标记后禁止正常出库），不是库存行 */
async function handleMark(row: MesInventoryEnvApi.InventoryEnv) {
  if (!row.ledgerId) {
    message.warning('该批次没有污染台账行，无需标记终审');
    return;
  }
  const next = !row.ledgerMarked;
  const sign = await signRef.value?.open(
    next ? '标记为终审品' : '解除终审标记',
    next
      ? `批次 ${row.batchCode ?? ''}：标记后禁止正常出库/销售。签字即代表这次终审由你定夺。`
      : `批次 ${row.batchCode ?? ''}：解除后恢复正常出库/销售。`,
  );
  if (!sign) {
    return;
  }
  // remark 后端拿它当签字说明存
  await updatePollutionLedgerMark({
    id: row.ledgerId,
    marked: next,
    remark: sign.opinion,
    signImg: sign.signImg,
  });
  message.success(next ? '已标记终审' : '已解除标记');
  handleRefresh();
}

/**
 * 受控库位调整：只对**已复核**的判定开放。
 * 未复核时库位还没落到台账、批次戳也还没投影，改它没人读——该走「环保判定」那条复核路径。
 *
 * 收口（发起变更）之后这个入口**自然就是这个禁态**，不必再加判断：变更单是新建的一行、还没复核，
 * 本视图的"最近一次判定"取的正是它，于是 reviewResult 为空、按钮直接置灰。原单已被替代这条
 * 只在服务端兜底（1040818021 会告诉你去看新单）——本行不暴露 supersededBy，为一个走不到的分支
 * 去改在库环保视图的联表不划算。
 * 另外两类服务端拒绝同理：台账已闭环（1_040_818_018「东西早就不在那儿了」）本行也不暴露。
 */
function handleControlLocation(row: MesInventoryEnvApi.InventoryEnv) {
  if (!row.reviewResult) {
    message.warning('该批次尚无已复核的判定，请先完成环保判定与复核');
    return;
  }
  controlLocationApi.setData(row).open();
}

/** 查看批次档案（前向/后向链 + 每节点环保档案） */
function handleBatchDetail(row: MesInventoryEnvApi.InventoryEnv) {
  if (!row.batchCode) {
    return;
  }
  batchProfileRef.value?.open(row.batchCode);
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    checkboxConfig: {
      // 无批次的库存行发起不了检测，勾上只会白跑一趟，直接不给勾
      checkMethod: ({ row }: { row: MesInventoryEnvApi.InventoryEnv }) => !!row.batchId,
      highlight: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getInventoryEnvPage({
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
  } as VxeTableGridOptions<MesInventoryEnvApi.InventoryEnv>,
});
</script>

<template>
  <Page auto-content-height>
    <!-- 判定弹窗每次建单/复核都会 emit success：不接这个事件，列表要手动刷新才看得到判定结果 -->
    <PollutionJudgeModal @success="handleRefresh" />
    <ControlLocation @success="handleRefresh" />
    <BatchProfileDrawer ref="batchProfileRef" />
    <!-- 本页三类人工改数据的动作都从它这里拿签名（环保判定在 judge-modal 内部签） -->
    <SignConfirmModal ref="signRef" />

    <EnvMetrics ref="metricsRef" class="mb-3" @drill="handleDrill" />

    <!-- 清单不完整这件事必须写在脸上，否则会被当成"全检过了" -->
    <Alert
      class="mb-3"
      type="info"
      show-icon
      message="体检覆盖范围"
      description="判据含：未检测 / 超期未检 / 积压 / 混放。「包装破损」无数据源，不在本清单内，需现场另行巡检。"
    />

    <div v-if="judging" class="mb-2">
      <Progress
        :percent="Math.round((judgeProgress.done / Math.max(judgeProgress.total, 1)) * 100)"
        :format="() => `${judgeProgress.done}/${judgeProgress.total}${judgeProgress.failed > 0 ? `（失败 ${judgeProgress.failed}）` : ''}`"
      />
    </div>

    <Grid table-title="在库环保视图">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '批量发起检测',
              type: 'primary',
              auth: ['mes:set-pollution-check:create'],
              loading: judging,
              onClick: handleBatchJudge,
            },
          ]"
        />
      </template>

      <template #batchCode="{ row }">
        <Button v-if="row.batchCode" size="small" type="link" @click="handleBatchDetail(row)">
          {{ row.batchCode }}
        </Button>
        <Tooltip v-else title="该库存行没有关联批次，发起不了在库检测">
          <Tag color="warning">无批次</Tag>
        </Tooltip>
      </template>

      <template #areaName="{ row }">
        <span>{{ row.areaName ?? '-' }}</span>
        <Tag v-if="row.areaPollutionControl" class="ml-1" color="volcano">受控</Tag>
      </template>

      <template #pollution="{ row }">
        <Tag v-if="row.pollutionStatus === 'POLLUTED'" color="error">污染</Tag>
        <span v-else class="text-gray-400">未判定</span>
        <Tag v-if="row.pollutionMarked" class="ml-1" color="volcano">批次标记</Tag>
      </template>

      <template #check="{ row }">
        <template v-if="!row.checkId">
          <span class="text-gray-400">未检测</span>
        </template>
        <template v-else>
          <Tag :color="AI_RESULT_MAP[row.aiResult ?? '']?.color">
            AI·{{ AI_RESULT_MAP[row.aiResult ?? '']?.text ?? row.aiResult }}
          </Tag>
          <Tag v-if="row.reviewResult" :color="REVIEW_RESULT_MAP[row.reviewResult]?.color">
            人工·{{ REVIEW_RESULT_MAP[row.reviewResult]?.text ?? row.reviewResult }}
          </Tag>
          <Tag v-else color="warning">待复核</Tag>
          <!-- 存量判定 100% 是靠批次号字符串对上的（建记录时还没有 batchId），得让人看见 -->
          <Tooltip v-if="row.checkByBatchNo" title="该判定靠批次号字符串匹配，未锚定批次主数据">
            <Tag color="default">未锚定</Tag>
          </Tooltip>
        </template>
      </template>

      <template #criteria="{ row }">
        <Tag v-if="row.notChecked" color="warning">未检测</Tag>
        <Tag v-if="row.overdue" color="error">超期未检</Tag>
        <Tag v-if="row.stockpiled" color="volcano">积压 {{ row.stockDays }} 天</Tag>
        <Tag v-if="row.mixed" color="error">混放</Tag>
        <span v-if="!row.notChecked && !row.overdue && !row.stockpiled && !row.mixed" class="text-gray-400">
          正常
        </span>
      </template>

      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '环保判定',
              type: 'link',
              auth: ['mes:set-pollution-check:create'],
              disabled: !row.batchId,
              onClick: handleJudge.bind(null, row),
            },
            {
              label: '受控库位',
              type: 'link',
              auth: ['mes:set-pollution-check:review'],
              disabled: !row.reviewResult,
              onClick: handleControlLocation.bind(null, row),
            },
            {
              label: row.frozen ? '解冻' : '冻结',
              type: 'link',
              auth: ['mes:wm-material-stock:update'],
              onClick: handleFrozen.bind(null, row),
            },
            {
              label: row.ledgerMarked ? '解除标记' : '标记终审',
              type: 'link',
              auth: ['mes:set-pollution-mark:update'],
              disabled: !row.ledgerId,
              onClick: handleMark.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
