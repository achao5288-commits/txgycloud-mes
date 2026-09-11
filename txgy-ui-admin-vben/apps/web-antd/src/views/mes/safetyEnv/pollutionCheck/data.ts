import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';

import { getRangePickerDefaultProps } from '#/utils';
import WmWarehouseAreaSelect from '#/views/mes/wm/warehouse/components/area-select.vue';

/** 环节选项 */
export const STAGE_OPTIONS = [
  { label: '采购入库', value: 'PURCHASE_INBOUND' },
  { label: '生产领用', value: 'MATERIAL_ISSUE' },
  { label: '中间废弃物', value: 'WASTE_INTERMEDIATE' },
  { label: '成品', value: 'FINISHED_PRODUCT' },
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
export const AI_RESULT_MAP: Record<string, { text: string; color: string }> = {
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
export const REVIEW_RESULT_MAP: Record<string, { text: string; color: string }> = {
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
export const FINISHED_RESULT_MAP: Record<string, { text: string; color: string }> = {
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

/** 新增/修改污染判定的表单（AI 初筛字段由后端回填，无需录入） */
export function useFormSchema(): VbenFormSchema[] {
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
        placeholder: '如：含铅涂料 / 无铅锡膏（AI 据此初筛）',
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
      label: '重量(kg)',
      component: 'InputNumber',
      componentProps: {
        allowClear: true,
        min: 0,
        placeholder: '请输入重量(kg)',
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
      fieldName: 'batchNo',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入批次号',
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
    { field: 'recordNo', title: '记录编号', minWidth: 200 },
    { field: 'bizNo', title: '关联单号', minWidth: 160, showOverflow: true },
    { field: 'stage', title: '环节', width: 110, slots: { default: 'stage' } },
    { field: 'batchNo', title: '批次号', minWidth: 150 },
    { field: 'itemName', title: '物料/产品名称', minWidth: 160 },
    { field: 'weight', title: '重量(kg)', width: 110, formatter: ({ cellValue }) => (cellValue != null ? `${cellValue}` : '-') },
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
      width: 220,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}
