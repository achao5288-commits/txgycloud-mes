<script lang="ts" setup>
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Modal as AModal, Button, Input, message, Popconfirm, Radio, Table, Tag, Tooltip } from 'ant-design-vue';

import {
  createPollutionCheck,
  deletePollutionCheck,
  getPollutionCheckPage,
  reviewPollutionCheck,
} from '#/api/mes/safetyEnv/pollutionCheck';
import SignConfirmModal from '#/views/mes/safetyEnv/components/SignConfirmModal.vue';
import WmWarehouseAreaSelect from '#/views/mes/wm/warehouse/components/area-select.vue';

import { AI_RESULT_MAP, REVIEW_RESULT_MAP, STAGE_MAP } from '../../safetyEnv/pollutionCheck/data';

/** 单据真实物料行（由调用方从单据行查询归一） */
interface DocLine {
  lineId?: number;
  itemCode?: string;
  itemName?: string;
  itemSpec?: string;
  /** 批次主数据 id：有它才对得上库存，有则优先用作匹配键 */
  batchId?: number;
  batchNo?: string;
  /**
   * 来源单据的数量 → 台账「重量」列。取的是原值，**没有单位换算**
   *（mes_md_item 只有 unit_measure_id，本仓没有任何换算率表）。
   * 不传的话台账那一列永远是 `-`：判定表建单时 weight 只从请求里拿，服务端不会自己查单据行。
   */
  weight?: number;
}

/** 打开时入参 */
interface JudgePayload {
  title?: string;
  stage: string;
  /** 单据入口传单据号；在库入口没有单据，靠 lines[0].batchId 锚定 */
  bizNo: string;
  lines: DocLine[];
}

/** 视图行：单据物料行 + 已绑定的污染判定记录(可能无) */
interface RowItem extends DocLine {
  rec?: MesSetPollutionCheckApi.PollutionCheck;
}

const emit = defineEmits<{ success: [] }>();

/** 签字弹窗：发起检测（建判定）是人为主张，后端逐单要签名 */
const signRef = ref<InstanceType<typeof SignConfirmModal>>();
/**
 * 本次弹窗内已拿到的签名。一次「环保判定」通常要判多行，签名框弹一次覆盖
 * 本次所有建单（每单后端仍各留一条签字记录），关窗即失效——
 * 否则判 5 行要人画 5 遍，只会把人逼回去走别的路。
 */
let sessionSign: null | { opinion?: string; signImg: string } = null;

/** 弹窗内状态 */
const bizNo = ref('');
const stage = ref('');
const docTitle = ref('');
const lines = ref<DocLine[]>([]);
const records = ref<MesSetPollutionCheckApi.PollutionCheck[]>([]);
const loading = ref(false);

/** 复核弹窗状态 */
const reviewOpen = ref(false);
const reviewRecord = ref<MesSetPollutionCheckApi.PollutionCheck | null>(null);
const reviewResult = ref<string>();
/** 受控库位：id 是权威值（落 mes_set_pollution_check.location_id），名称只是显示快照 */
const reviewLocationId = ref<number>();
const reviewLocation = ref('');
const reviewRemark = ref('');
const reviewSaving = ref(false);

/** 记录按 物料编码+批次 索引（同单同料多次只取最新） */
const recordMap = computed(() => {
  const map = new Map<string, MesSetPollutionCheckApi.PollutionCheck>();
  for (const rec of records.value) {
    const key = joinKey(rec);
    if (!map.has(key)) {
      map.set(key, rec);
    }
  }
  return map;
});

const rows = computed<RowItem[]>(() =>
  lines.value.map((line) => ({ ...line, rec: recordMap.value.get(joinKey(line)) })),
);

const stageLabel = computed(() => STAGE_MAP[stage.value] ?? stage.value);
const modalTitle = computed(() => `${docTitle.value} · 环保判定`);
/**
 * 该行没有批次锚点——物料没启用批次管理，或单据行没关联批次。
 * 判定照样建、照样复核，但**锚不到任何批次**：库存不冻结、批次污染戳不更新、
 * 在库环保视图里也不会出现这条判定。不说清楚就是"判完了环保那边什么都没有"。
 */
function noBatch(x: DocLine) {
  return x.batchId == null && !x.batchNo;
}

const summaryText = computed(() => {
  const total = rows.value.length;
  const reviewed = rows.value.filter((r) => r.rec?.reviewResult).length;
  const polluted = rows.value.filter((r) => r.rec?.reviewResult === 'POLLUTED').length;
  const noBatchLines = rows.value.filter((r) => noBatch(r)).length;
  if (total === 0) {
    return '该单暂无物料行，请先在单据中添加行';
  }
  // 无批次行单独提示：它是"判完了台账有、库存侧什么都没有"的唯一原因
  return `共 ${total} 个物料行 · 已复核 ${reviewed} · 其中有污染 ${polluted}`
    + (noBatchLines > 0 ? ` · ${noBatchLines} 行无批次（不冻结库存、不进在库环保视图）` : '');
});

/**
 * 匹配键：有 batchId 就用它——批次号字符串是人工录入的，实测半数对不上真实批次；
 * 没有 batchId 的历史记录退回「物料编码|批次号」，保持原有行为不变。
 */
function joinKey(x: { batchId?: number; batchNo?: string; itemCode?: string; }) {
  return x.batchId != null ? `b${x.batchId}` : `${x.itemCode ?? ''}|${x.batchNo ?? ''}`;
}

/** 在库入口没有单据号，退回以批次为锚点拉该批已有判定 */
const anchorBatchId = computed(() => (bizNo.value ? undefined : lines.value[0]?.batchId));

/** 拉取本单/本批已有判定 */
async function refresh() {
  if (!bizNo.value && anchorBatchId.value == null) {
    return;
  }
  loading.value = true;
  try {
    const result = await getPollutionCheckPage({
      pageNo: 1,
      pageSize: 200,
      ...(bizNo.value ? { bizNo: bizNo.value } : { batchId: anchorBatchId.value }),
    });
    records.value = result.list ?? [];
  } finally {
    loading.value = false;
  }
}

/** 对某物料行发起判定：服务端自动 AI 初筛 → 待复核 */
async function handleJudge(line: DocLine) {
  sessionSign ??= (await signRef.value?.open(
    '发起环保检测',
    `对「${line.itemName ?? ''}」建一条待复核判定。注意：这是实在的业务动作——`
      + `判「有污染」时该批在库库存会被冻结、批次污染戳更新，判「无污染」后才解冻。`,
  )) ?? null;
  if (!sessionSign) {
    return; // 用户没签：不建单（后端缺签名也会拒，1040818019）
  }
  const hide = message.loading(`正在对「${line.itemName}」AI 初筛…`, 0);
  try {
    await createPollutionCheck({
      stage: stage.value,
      bizNo: bizNo.value,
      batchId: line.batchId,
      batchNo: line.batchNo,
      itemCode: line.itemCode,
      itemName: line.itemName,
      itemSpec: line.itemSpec,
      weight: line.weight,
      signImg: sessionSign.signImg,
      opinion: sessionSign.opinion,
    });
    message.success('已生成判定记录（AI 初筛完成，待人工复核）');
    await refresh();
    const rec = recordMap.value.get(joinKey(line));
    if (rec) {
      openReview(rec);
    }
    emit('success');
  } finally {
    hide();
  }
}

/** 打开复核 */
function openReview(rec: MesSetPollutionCheckApi.PollutionCheck) {
  reviewRecord.value = rec;
  reviewResult.value = undefined;
  reviewLocationId.value = rec.locationId ?? undefined;
  reviewLocation.value = rec.location ?? '';
  reviewRemark.value = '';
  reviewOpen.value = true;
}

/** 用户改选了受控库位：id 落库，名称跟着更新（后端只存请求里的快照，不回写名称） */
function handleLocationPicked(area?: { id?: number; name?: string; }) {
  reviewLocationId.value = area?.id;
  reviewLocation.value = area?.name ?? '';
}

/** 提交复核（终态二值；存储/处置留空由后端按环节×结论补默认） */
async function handleReviewOk() {
  if (!reviewRecord.value) {
    return;
  }
  if (!reviewResult.value) {
    message.warning('请选择人工复核结论（无污染 / 有污染）');
    return;
  }
  reviewSaving.value = true;
  try {
    await reviewPollutionCheck({
      id: reviewRecord.value.id!,
      reviewResult: reviewResult.value,
      locationId: reviewLocationId.value,
      location: reviewLocation.value || undefined,
      remark: reviewRemark.value || undefined,
    });
    message.success('复核完成');
    reviewOpen.value = false;
    await refresh();
    emit('success');
  } finally {
    reviewSaving.value = false;
  }
}

/** 删除待复核记录 */
async function handleDelete(rec: MesSetPollutionCheckApi.PollutionCheck) {
  await deletePollutionCheck(rec.id!);
  message.success('已删除该判定记录');
  await refresh();
  emit('success');
}

const [JudgeModal, modalApi] = useVbenModal({
  destroyOnClose: true,
  // 必须给 onConfirm：ModalApi.onCancel() 在没有 onCancel 时会兜底 this.close()，
  // 而 onConfirm() 只有 `this.api.onConfirm?.()` —— 没传就是纯空转。
  // 表现为「确定」按钮看得见、点得动、毫无反应（不报错、不关闭），
  // 这个弹窗的判定是逐行提交的，页脚没有表单要交，确定=关闭。
  onConfirm() {
    // 关之前先让宿主列表重拉一次：这个弹窗里可能刚判过/重检过若干行，
    // 只 close 不 emit，列表就还停在打开弹窗前的样子——表现就是"点了确定，判定完成没出来"
    emit('success');
    modalApi.close();
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      sessionSign = null;
      return;
    }
    const data = modalApi.getData<JudgePayload>();
    docTitle.value = data.title ?? '';
    stage.value = data.stage;
    bizNo.value = data.bizNo;
    lines.value = data.lines ?? [];
    reviewOpen.value = false;
    await refresh();
  },
});

/** antd 表格列（内容用 #bodyCell 渲染） */
const columns = [
  { key: 'itemCode', title: '物料编码', dataIndex: 'itemCode', width: 140 },
  { key: 'itemName', title: '物料/产品名称', dataIndex: 'itemName', width: 180 },
  { key: 'itemSpec', title: '规格', dataIndex: 'itemSpec', width: 160, ellipsis: true },
  { key: 'batchNo', title: '批次', dataIndex: 'batchNo', width: 160 },
  { key: 'status', title: '环保判定状态', width: 260 },
  { key: 'action', title: '操作', width: 200 },
];
</script>

<template>
  <JudgeModal :title="modalTitle" class="w-[960px]">
    <div class="mx-1">
      <div class="mb-3 text-sm text-gray-500">
        <span>环节：{{ stageLabel }}<template v-if="bizNo">（{{ bizNo }}）</template></span>
        <span class="mx-2">｜</span>
        <span>{{ summaryText }}</span>
      </div>
      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="false"
        :row-key="(_row: RowItem, index: number) => `${joinKey(_row)}-${index}`"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <!-- 批次：没有就明确标出来，别留一格空白让人以为只是没填 -->
          <template v-if="column.key === 'batchNo'">
            <span v-if="record.batchNo">{{ record.batchNo }}</span>
            <Tooltip
              v-else
              title="该物料行没有批次（物料未启用批次管理，或单据行未关联批次）。判定照常生效并进污染判定台账，但不会锚到批次：库存不冻结、批次污染戳不更新、在库环保视图里也不会出现。判「有污染」时，除中间废弃物环节外会被服务端拦下、要求先有批次。"
            >
              <Tag color="warning">无批次</Tag>
            </Tooltip>
          </template>

          <!-- 判定状态 -->
          <template v-else-if="column.key === 'status'">
            <template v-if="!record.rec">
              <Tag>未判定</Tag>
            </template>
            <template v-else>
              <div class="flex flex-wrap items-center gap-1">
                <Tag
                  :color="AI_RESULT_MAP[record.rec.aiResult ?? '']?.color"
                  :title="`AI 依据：${record.rec.aiReason ?? '-'}`"
                >
                  AI·{{ AI_RESULT_MAP[record.rec.aiResult ?? '']?.text ?? record.rec.aiResult }}
                  {{ record.rec.aiConfidence != null ? `${record.rec.aiConfidence}%` : '' }}
                </Tag>
                <template v-if="record.rec.reviewResult">
                  <Tag :color="REVIEW_RESULT_MAP[record.rec.reviewResult]?.color">
                    人工·{{ REVIEW_RESULT_MAP[record.rec.reviewResult]?.text ?? record.rec.reviewResult }}
                  </Tag>
                  <Tag v-if="record.rec.marked" color="volcano">标记</Tag>
                </template>
                <template v-else>
                  <Tag color="warning">待复核</Tag>
                </template>
                <span class="text-xs text-gray-400" :title="record.rec.aiReason">
                  {{ record.rec.aiReason }}
                </span>
              </div>
            </template>
          </template>

          <!-- 操作 -->
          <template v-else-if="column.key === 'action'">
            <Button v-if="!record.rec" type="link" size="small" @click="handleJudge(record)">
              AI 初筛判定
            </Button>
            <template v-else-if="!record.rec.reviewResult">
              <Button type="link" size="small" @click="openReview(record.rec)">
                复核
              </Button>
              <Popconfirm
                title="确认删除该待复核判定记录？"
                ok-text="删除"
                cancel-text="取消"
                @confirm="handleDelete(record.rec)"
              >
                <Button type="link" size="small" danger>删除</Button>
              </Popconfirm>
            </template>
            <template v-else>
              <span class="text-xs text-gray-400">
                已复核
                <span v-if="record.rec.location"> · {{ record.rec.location }}</span>
              </span>
              <!-- 体检是周期性的：旧判定（可能半年前）不能当成"永远合格"，
                   同批次可再发一条，最新一条即当前有效判定（列表按 id 倒序取首条）。 -->
              <Button type="link" size="small" @click="handleJudge(record)">重新检测</Button>
            </template>
          </template>
        </template>
        <template #emptyText>
          <span class="text-gray-400">暂无物料行</span>
        </template>
      </Table>
    </div>
  </JudgeModal>

  <!-- 本弹窗内所有"建判定"动作共用这一个签字框（签一次覆盖本次多行） -->
  <SignConfirmModal ref="signRef" />

  <!-- 人工复核 -->
  <AModal
    v-model:open="reviewOpen"
    :title="`人工复核 · ${reviewRecord?.itemName ?? ''}${reviewRecord?.bizNo ? `（${reviewRecord.bizNo}）` : ''}`"
    :confirm-loading="reviewSaving"
    ok-text="提交复核"
    cancel-text="取消"
    @ok="handleReviewOk"
  >
    <template v-if="reviewRecord">
      <div class="mb-3 rounded-md bg-gray-50 p-3 text-sm text-black">
        <div>记录：{{ reviewRecord.recordNo }}</div>
        <div>
          AI 初筛：{{ AI_RESULT_MAP[reviewRecord.aiResult ?? '']?.text ?? reviewRecord.aiResult }}（置信度
          {{ reviewRecord.aiConfidence ?? '-' }}%）
        </div>
        <div v-if="reviewRecord.aiReason">依据：{{ reviewRecord.aiReason }}</div>
        <div v-if="reviewRecord.suggestedStorage">AI 推荐存储：{{ reviewRecord.suggestedStorage }}</div>
      </div>
      <div class="space-y-3">
        <div>
          <div class="mb-1 text-sm">人工复核结论（终态：无污染 / 有污染）</div>
          <Radio.Group v-model:value="reviewResult">
            <Radio.Button value="CLEAN">无污染</Radio.Button>
            <Radio.Button value="POLLUTED">有污染</Radio.Button>
          </Radio.Group>
        </div>
        <div>
          <div class="mb-1 text-sm">去向/库位（留空按环节×结论取默认存储）</div>
          <WmWarehouseAreaSelect
            v-model="reviewLocationId"
            allow-clear
            placeholder="请选择库位（有污染必须选受控/危废库位）"
            @change="handleLocationPicked"
          />
        </div>
        <div>
          <div class="mb-1 text-sm">复核备注</div>
          <Input.TextArea v-model:value="reviewRemark" :rows="2" placeholder="可补充说明" />
        </div>
      </div>
    </template>
  </AModal>
</template>
