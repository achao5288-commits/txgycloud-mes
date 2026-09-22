import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPlanApi } from '#/api/mes/safetyEnv/plan';

/** 触发类型：PERIODIC(周期)/EVENT(事件)选项 */
export const PLAN_TYPE_OPTIONS = [
  { label: '事件触发', value: 'EVENT' },
  { label: '周期', value: 'PERIODIC' },
  { label: '年', value: 'YEAR' },
];

/** 触发类型：PERIODIC(周期)/EVENT(事件)文案 */
export const PLAN_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  EVENT: { text: '事件触发', color: 'success' },
  PERIODIC: { text: '周期', color: 'error' },
  YEAR: { text: '年', color: 'warning' },
};

/** 周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY选项 */
export const PERIOD_TYPE_OPTIONS = [
  { label: '月', value: 'MONTHLY' },
  { label: '年', value: 'YEAR' },
];

/** 周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY文案 */
export const PERIOD_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  MONTHLY: { text: '月', color: 'success' },
  YEAR: { text: '年', color: 'error' },
};

/** 状态：DRAFT/ACTIVE/STOPPED选项 */
export const STATUS_OPTIONS = [
  { label: '有效', value: 'ACTIVE' },
  { label: '草稿', value: 'DRAFT' },
];

/** 状态：DRAFT/ACTIVE/STOPPED文案 */
export const STATUS_MAP: Record<string, { color: string; text: string; }> = {
  ACTIVE: { text: '有效', color: 'success' },
  DRAFT: { text: '草稿', color: 'error' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'planNo',
      label: '计划编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入计划编号',
      },
    },
    {
      fieldName: 'planName',
      label: '计划名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入计划名称',
      },
    },
    {
      fieldName: 'planType',
      label: '触发类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: PLAN_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型(周期型)',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: PERIOD_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPlanApi.Plan>['columns'] {
  return [
    { field: 'planNo', title: '计划编号', minWidth: 170, showOverflow: true },
    { field: 'planName', title: '计划名称', minWidth: 170, showOverflow: true },
    { field: 'planType', title: '触发类型', minWidth: 170, showOverflow: true, slots: { default: 'planType' } },
    { field: 'periodType', title: '周期类型(周期型)', minWidth: 170, showOverflow: true, slots: { default: 'periodType' } },
    { field: 'startDate', title: '生效开始日期', width: 120 },
    { field: 'endDate', title: '生效结束日期', width: 120 },
    { field: 'assigneeId', title: '责任人/执行人编号', width: 120 },
    { field: 'status', title: '状态', minWidth: 170, showOverflow: true, slots: { default: 'status' } },
    {
      title: '操作',
      width: 150,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}

/** 新增/编辑表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'planNo',
      label: '计划编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入计划编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'planName',
      label: '计划名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入计划名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'planType',
      label: '触发类型',
      component: 'Select',
      componentProps: {
        options: PLAN_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型(周期型)',
      component: 'Select',
      componentProps: {
        options: PERIOD_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'startDate',
      label: '生效开始日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'endDate',
      label: '生效结束日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'machineryId',
      label: '关联设备编号(事件/设备型)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联设备编号(事件/设备型)',
      },
    },
    {
      fieldName: 'operationId',
      label: '关联工序编号(事件/工单型)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工序编号(事件/工单型)',
      },
    },
    {
      fieldName: 'standardId',
      label: '关联检测标准编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联检测标准编号',
      },
    },
    {
      fieldName: 'assigneeId',
      label: '责任人/执行人编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入责任人/执行人编号',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入备注',
      },
    },
  ];
}
