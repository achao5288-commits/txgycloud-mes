import type { VbenFormApi, VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';
import type { MesWmBatchApi } from '#/api/mes/wm/batch';

import { markRaw } from 'vue';

import { getLegalBasisList } from '#/api/mes/safetyEnv/pollutionCheck';
import { getRangePickerDefaultProps } from '#/utils';
import WmBatchSelect from '#/views/mes/wm/batch/components/select.vue';
import WmWarehouseAreaSelect from '#/views/mes/wm/warehouse/components/area-select.vue';

/** 环节选项 */
export const STAGE_OPTIONS = [
  { label: '采购入库', value: 'PURCHASE_INBOUND' },
  { label: '生产领用', value: 'MATERIAL_ISSUE' },
  { label: '中间废弃物', value: 'WASTE_INTERMEDIATE' },
  { label: '成品', value: 'FINISHED_PRODUCT' },
  // 在库复查：没有发生业务动作，只是对存量批次做体检，锚点是 batchId
  { label: '在库', value: 'IN_STOCK' },
];

/** 环节文案 */
export const STAGE_MAP: Record<string, string> = Object.fromEntries(
  STAGE_OPTIONS.map((item) => [item.value, item.label]),
);

/** AI 初筛结果 */
export const AI_RESULT_OPTIONS = [
  { label: '无污染', value: 'CLEAN' },
  { label: '有污染', value: 'POLLUTED' },
  { label: '不确定', value: 'UNCERTAIN' },
];

/** AI 结果展示(色值用于 tag) */
export const AI_RESULT_MAP: Record<string, { color: string; text: string; }> = {
  CLEAN: { text: '无污染', color: 'success' },
  POLLUTED: { text: '有污染', color: 'error' },
  UNCERTAIN: { text: '不确定', color: 'warning' },
};

/** 人工复核结果(终态二值) */
export const REVIEW_RESULT_OPTIONS = [
  { label: '无污染', value: 'CLEAN' },
  { label: '有污染', value: 'POLLUTED' },
];

/** 人工结果展示 */
export const REVIEW_RESULT_MAP: Record<string, { color: string; text: string; }> = {
  CLEAN: { text: '无污染', color: 'success' },
  POLLUTED: { text: '有污染', color: 'error' },
};

/** 成品达标分支（需求 §11 用例5 / 设计 §环节5；仅成品环节） */
export const FINISHED_RESULT_OPTIONS = [
  { label: '达标（入库出厂）', value: 'QUALIFIED' },
  { label: '局部缺陷（返工为主）', value: 'REWORK' },
  { label: '整体报废（整批锁定）', value: 'SCRAPPED' },
];

/** 达标分支展示(色值用于 tag) */
export const FINISHED_RESULT_MAP: Record<string, { color: string; text: string; }> = {
  QUALIFIED: { text: '达标', color: 'success' },
  REWORK: { text: '局部缺陷·返工', color: 'warning' },
  SCRAPPED: { text: '整体报废', color: 'error' },
};

/** 处置方式选项 */
export const DISPOSITION_OPTIONS = [
  { label: '正常入库/直接存储', value: 'NORMAL_INBOUND' },
  { label: '受控存储', value: 'CONTROLLED_STORAGE' },
  { label: '允许领用', value: 'ISSUE_ALLOWED' },
  { label: '拒绝领用', value: 'REJECT_ISSUE' },
  { label: '回用', value: 'REUSE' },
  { label: '排放(需合规登记)', value: 'DISCHARGE' },
  { label: '隔离暂存/保存处理', value: 'ISOLATE_STORAGE' },
  { label: '标记存储', value: 'MARKED_STORAGE' },
];

/** 处置方式文案 */
export const DISPOSITION_MAP: Record<string, string> = Object.fromEntries(
  DISPOSITION_OPTIONS.map((item) => [item.value, item.label]),
);

/** 判定状态筛选（对应后端 reviewed：true=已复核，false=待复核；不选=全部） */
export const REVIEWED_OPTIONS = [
  { label: '待复核', value: false },
  { label: '已复核', value: true },
];

/**
 * 批次关联状态筛选（对应后端 linked）。
 * 实测 69 条在用判定里 32 条(46%)是人工打字的批次号，join 不回 mes_wm_batch——
 * 这批记录进不了库存冻结/台账，只能看，需要一个单独的筛选项把它们捞出来。
 */
export const LINKED_OPTIONS = [
  { label: '已关联批次', value: true },
  { label: '未关联（历史手工录入）', value: false },
];

/**
 * 新增/修改污染判定的表单（AI 初筛字段由后端回填，无需录入）
 *
 * @param formApi 选中批次后把物料四件套回填进表单。物料名/编码/规格改成从批次主数据带出
 *                而不是手打——手打就是本模块 46% 记录 join 不回 mes_wm_batch 的根因。
 */
export function useFormSchema(formApi?: VbenFormApi): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'stage',
      label: '环节',
      component: 'Select',
      componentProps: {
        options: STAGE_OPTIONS,
        placeholder: '请选择判定环节',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'itemName',
      label: '物料/产品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '如：含铅涂料 / 无铅锡膏',
      },
      rules: 'required',
    },
    {
      fieldName: 'itemCode',
      label: '物料/产品编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物料/产品编码',
      },
    },
    {
      fieldName: 'itemSpec',
      label: '规格',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入规格',
      },
    },
    {
      // 全链重量的源头：判定时确认一次，台账/联单/衡算都从这里取，避免多处录入对不上账
      fieldName: 'weight',
      label: '重量',
      component: 'InputNumber',
      componentProps: {
        allowClear: true,
        min: 0,
        placeholder: '请输入重量',
        precision: 3,
      },
    },
    {
      fieldName: 'bizNo',
      label: '关联单号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '入库单/领料单/报工单号',
      },
    },
    {
      // 判定锚点：选了批次，服务端就以「批次+物料主数据」回填并覆盖下面四个字段，
      // 库存冻结/台账/超期那些按 batch_no 匹配的既有逻辑才能落到真实批次上。
      fieldName: 'batchId',
      label: '关联批次',
      component: markRaw(WmBatchSelect),
      componentProps: {
        allowClear: true,
        placeholder: '入库/在库检测请选批次；中间废弃物等无批次环节可留空',
        onChange: async (batch?: MesWmBatchApi.Batch) => {
          await formApi?.setValues({
            batchNo: batch?.code,
            itemCode: batch?.itemCode,
            itemName: batch?.itemName,
            itemSpec: batch?.itemSpecification,
          });
        },
      },
    },
    {
      // 选批次时由上面的回调回填，不选批次才允许手打（历史/中间废弃物环节兼容）
      fieldName: 'batchNo',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '选批次后自动带出，未选批次可手输',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入备注',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
  ];
}

/** 人工复核的表单 */
export function useReviewFormSchema(): VbenFormSchema[] {
  return [
    {
      // 只用于驱动成品达标分支的显隐/必填，不提交
      fieldName: 'stage',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      // 成品环节必填：达标 / 局部缺陷返工 / 整体报废（后端硬校验，非成品环节拒绝传值）
      fieldName: 'finishedResult',
      label: '成品达标分支',
      component: 'RadioGroup',
      componentProps: {
        options: FINISHED_RESULT_OPTIONS,
      },
      dependencies: {
        triggerFields: ['stage'],
        show: (values) => values.stage === 'FINISHED_PRODUCT',
        // 显隐只是 v-show，字段仍在校验树里：非成品环节必须把必填规则摘掉
        rules: (values) =>
          values.stage === 'FINISHED_PRODUCT' ? 'selectRequired' : null,
        required: (values) => values.stage === 'FINISHED_PRODUCT',
      },
    },
    {
      fieldName: 'reviewResult',
      label: '人工复核结论',
      component: 'RadioGroup',
      componentProps: {
        options: REVIEW_RESULT_OPTIONS,
      },
      rules: 'required',
    },
    {
      // 判定依据：与 AI 初筛共用同一份法规白名单（后端 /legal-basis），不复刻一份前端常量。
      // **不强制**：填了就随复核落库、对外可引，没填也放行——结论本身才是终态权威，
      // 逼着凑一条法条比留空更糟（现场特征只勾了"其他"时候选本来就是空的）。
      // 控件值是数组，提交时由 review.vue 以**换行**拼成字符串（法条正文含「；」，不能用它做分隔）。
      // 标签刻意叫「复核依据」不叫「判定依据」：后者已被 aiReason 占用（复核弹窗/录入弹窗/履历三处），
      // 同名会让"这条到底是 AI 引的还是人勾的"看不出来。
      fieldName: 'reviewBasis',
      label: '复核依据',
      component: 'ApiSelect',
      componentProps: {
        allowClear: true,
        api: getLegalBasisList,
        labelField: 'label',
        mode: 'multiple',
        placeholder: '选填：可勾选本次判定援引的法规条款',
        valueField: 'value',
      },
      formItemClass: 'col-span-2',
      help: '留空不记录；勾选后随本行一同留档，供环保检查追溯',
    },
    {
      fieldName: 'disposition',
      label: '处置方式',
      component: 'Select',
      componentProps: {
        options: DISPOSITION_OPTIONS,
        placeholder: '请选择(留空按环节+结论取默认)',
        allowClear: true,
      },
    },
    {
      fieldName: 'storageMethod',
      label: '最终存储方法',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '可沿用 AI 推荐',
      },
    },
    {
      // 受控存储门禁（需求 5.4）：库位取自主数据，有污染只能选污染管控库位，无污染不得占用受控库位。
      // 值 = 库位编号，库位名称由后端按编号回写快照。
      fieldName: 'locationId',
      label: '去向/库位',
      component: WmWarehouseAreaSelect,
      componentProps: {
        allowClear: true,
        placeholder: '请选择库位（有污染必须选受控/危废库位）',
      },
    },
    {
      fieldName: 'remark',
      label: '复核备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入复核备注',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'reviewed',
      label: '判定状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: REVIEWED_OPTIONS,
        placeholder: '待复核队列：选「待复核」',
      },
    },
    {
      fieldName: 'stage',
      label: '环节',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STAGE_OPTIONS,
        placeholder: '请选择环节',
      },
    },
    {
      fieldName: 'bizNo',
      label: '关联单号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '采购入库/领料/产品入库等单据号',
      },
    },
    {
      fieldName: 'itemName',
      label: '物料名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '物料名称模糊',
      },
    },
    {
      fieldName: 'batchNo',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入批次号',
      },
    },
    {
      fieldName: 'linked',
      label: '批次关联',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: LINKED_OPTIONS,
        placeholder: '未关联=join 不回库存的历史记录',
      },
    },
    {
      fieldName: 'aiResult',
      label: 'AI 初筛',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: AI_RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'reviewResult',
      label: '复核分类',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: REVIEW_RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'finishedResult',
      label: '达标分支',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: FINISHED_RESULT_OPTIONS,
        placeholder: '仅成品环节有值',
      },
    },
    {
      fieldName: 'reviewBy',
      label: '复核人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '复核人昵称(精确)',
      },
    },
    {
      fieldName: 'createTime',
      label: '判定时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPollutionCheckApi.PollutionCheck>['columns'] {
  return [
    // 260 才装得下编号 + 「变更单/已被替代」两个标签：只给 200 时标签会被 .vxe-cell 裁掉半截
    { field: 'recordNo', title: '记录编号', minWidth: 260, slots: { default: 'recordNo' } },
    { field: 'bizNo', title: '关联单号', minWidth: 160, showOverflow: true },
    { field: 'stage', title: '环节', width: 110, slots: { default: 'stage' } },
    // 未关联的记录占存量 46%，必须一眼能认出来，否则会被当成有效判定读
    { field: 'batchNo', title: '批次号', minWidth: 150, slots: { default: 'batchNo' } },
    { field: 'itemName', title: '物料/产品名称', minWidth: 160 },
    // 必须带单位：weight 是来源单据的数量原值，质量单位(kg/g/mg/t)已在建单时折成 KG，
    // 其余(个/箱/米)原样保留。只显示裸数字的话 123 到底是 123kg 还是 123 个读不出来。
    {
      field: 'weight',
      title: '重量',
      width: 120,
      formatter: ({ cellValue, row }) =>
        cellValue == null ? '-' : `${cellValue}${row.unitName ? ` ${row.unitName}` : ''}`,
    },
    { field: 'aiResult', title: 'AI 初筛', width: 110, slots: { default: 'aiResult' } },
    { field: 'aiConfidence', title: '置信度', width: 90, formatter: ({ cellValue }) => (cellValue != null ? `${cellValue}%` : '-') },
    { field: 'suggestedStorage', title: 'AI 推荐存储方法', minWidth: 200, showOverflow: true },
    { field: 'reviewResult', title: '人工复核', width: 110, slots: { default: 'reviewResult' } },
    { field: 'finishedResult', title: '达标分支', width: 130, slots: { default: 'finishedResult' } },
    { field: 'disposition', title: '处置方式', width: 140, formatter: ({ cellValue }) => (cellValue ? (DISPOSITION_MAP[cellValue] ?? cellValue) : '-') },
    { field: 'storageMethod', title: '最终存储方法', minWidth: 150, showOverflow: true },
    { field: 'location', title: '去向/库位', minWidth: 130 },
    { field: 'marked', title: '标记', width: 90, slots: { default: 'marked' } },
    { field: 'reviewBy', title: '复核人', width: 100 },
    { field: 'reviewTime', title: '复核时间', width: 170, formatter: 'formatDateTime' },
    {
      title: '操作',
      // 220 装不下（实测 224px + 单元格内边距），删除按钮被 .vxe-cell 裁掉。
      // 现在按钮多了「发起变更」，但它与 复核/修改/删除 互斥（前者只在已复核时出、后三者只在
      // 未复核时出），单行同时最多 3 个 —— 260 仍有富余。
      width: 260,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}
