import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesPollutionTraceApi } from '#/api/mes/safetyEnv/pollutionTrace';

import { REVIEW_RESULT_MAP } from '../pollutionCheck/data';
import { LEDGER_STATUS_MAP } from '../pollutionLedger/data';

/** 追溯对象类型(业务节点来源) */
export const TRACE_TYPE_OPTIONS = [
  { label: '污染判定', value: 'CHECK' },
  { label: '台账处置', value: 'LEDGER' },
];

/** 类型文案(色值用于 tag) */
export const TRACE_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  CHECK: { text: '污染判定', color: 'blue' },
  LEDGER: { text: '台账处置', color: 'green' },
};

/** 环节选项(含处置流转 DISPOSAL) */
export const NODE_STAGE_OPTIONS = [
  { label: '采购入库', value: 'PURCHASE_INBOUND' },
  { label: '生产领用', value: 'MATERIAL_ISSUE' },
  { label: '中间废弃物', value: 'WASTE_INTERMEDIATE' },
  { label: '成品', value: 'FINISHED_PRODUCT' },
  { label: '处置流转', value: 'DISPOSAL' },
];

/** 环节文案(含 DISPOSAL) */
export const NODE_STAGE_MAP: Record<string, string> = Object.fromEntries(
  NODE_STAGE_OPTIONS.map((item) => [item.value, item.label]),
);

/** 节点状态 tag（CHECK 复核终值取污染判定文案，LEDGER 取台账状态文案） */
export function nodeStatusMeta(node: MesPollutionTraceApi.TraceNode): { color: string; text: string; } {
  const status = node.batchStatus;
  if (!status) {
    return { text: '-', color: 'default' };
  }
  if (node.bizType === 'CHECK') {
    return REVIEW_RESULT_MAP[status] ?? { text: status, color: 'default' };
  }
  return LEDGER_STATUS_MAP[status] ?? { text: status, color: 'default' };
}

/** 列表搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'batchNo',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '批次全链：输入批次号看该批采购→领用→中间物→成品去向链',
      },
    },
    {
      fieldName: 'traceCode',
      label: '追溯对象码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: 'PC-... / 批次码',
      },
    },
    {
      fieldName: 'bizNo',
      label: '业务单号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: 'PC-2026...',
      },
    },
    {
      fieldName: 'bizType',
      label: '节点来源',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: TRACE_TYPE_OPTIONS,
        placeholder: '污染判定/台账处置',
      },
    },
    {
      fieldName: 'nodeStage',
      label: '环节',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: NODE_STAGE_OPTIONS,
        placeholder: '请选择环节',
      },
    },
  ];
}

/** 列表字段(追溯节点流水，按节点时间倒序) */
export function useGridColumns(): VxeTableGridOptions<MesPollutionTraceApi.TraceNode>['columns'] {
  return [
    { field: 'nodeTime', title: '节点时间', width: 165, formatter: 'formatDateTime' },
    { field: 'bizNo', title: '业务单号', minWidth: 180, showOverflow: true },
    { field: 'bizType', title: '来源', width: 100, slots: { default: 'bizType' } },
    { field: 'nodeStage', title: '环节', width: 110, slots: { default: 'nodeStage' } },
    { field: 'nodeAction', title: '节点动作', minWidth: 220, showOverflow: true },
    { field: 'batchStatus', title: '节点状态', width: 110, slots: { default: 'batchStatus' } },
    { field: 'operatorName', title: '操作人', width: 110 },
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}
