import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetCarbonEmissionApi } from '#/api/mes/safetyEnv/carbonEmission';

/** 能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM选项 */
export const ENERGY_TYPE_OPTIONS = [
  { label: '燃煤', value: 'COAL' },
  { label: '电', value: 'ELECTRICITY' },
];

/** 能源类型：ELECTRICITY/NATURAL_GAS/DIESEL/STEAM文案 */
export const ENERGY_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  COAL: { text: '燃煤', color: 'success' },
  ELECTRICITY: { text: '电', color: 'error' },
};

export const PERIOD_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  MONTHLY: { text: '月', color: 'success' },
  MONTH: { text: '月', color: 'success' },
  DAILY: { text: '日', color: 'success' },
  WEEKLY: { text: '周', color: 'success' },
  QUARTERLY: { text: '季', color: 'success' },
  YEARLY: { text: '年', color: 'success' },
  YEAR: { text: '年', color: 'success' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'calcNo',
      label: '核算批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入核算批次号',
      },
    },
    {
      fieldName: 'periodType',
      label: '核算周期',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入核算周期',
      },
    },
    {
      fieldName: 'periodStart',
      label: '周期开始日期',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入周期开始日期',
      },
    },
    {
      fieldName: 'energyType',
      label: '能源类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: ENERGY_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetCarbonEmissionApi.CarbonEmission>['columns'] {
  return [
    { field: 'calcNo', title: '核算批次号', minWidth: 170, showOverflow: true },
    { field: 'periodType', title: '核算周期', minWidth: 170, showOverflow: true, slots: { default: 'periodType' } },
    { field: 'energyType', title: '能源类型', minWidth: 170, showOverflow: true, slots: { default: 'energyType' } },
    { field: 'consumption', title: '能源消耗量', width: 120 },
    { field: 'emissionFactor', title: '排放因子', width: 120 },
    { field: 'carbonEmission', title: '碳排放量=consumption*factor', width: 120 },
    { field: 'unit', title: '单位', minWidth: 170, showOverflow: true },
    { field: 'totalEmission', title: '合计', width: 120 },
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
      fieldName: 'calcNo',
      label: '核算批次号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入核算批次号',
      },
      rules: 'required',
    },
    {
      fieldName: 'periodType',
      label: '核算周期',
      component: 'Input',
      componentProps: {
        placeholder: '请输入核算周期',
      },
      rules: 'required',
    },
    {
      fieldName: 'periodStart',
      label: '周期开始日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'periodEnd',
      label: '周期结束日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'woId',
      label: '关联工单编号(工单级碳足迹可空)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工单编号(工单级碳足迹可空)',
      },
    },
    {
      fieldName: 'sourceRecordId',
      label: '关联能耗记录编号(能源台账)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联能耗记录编号(能源台账)',
      },
    },
    {
      fieldName: 'energyType',
      label: '能源类型',
      component: 'Select',
      componentProps: {
        options: ENERGY_TYPE_OPTIONS,
        placeholder: '请选择',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'consumption',
      label: '能源消耗量',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入能源消耗量',
        class: 'w-full',
      },
    },
    {
      fieldName: 'emissionFactor',
      label: '排放因子',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入排放因子',
        class: 'w-full',
      },
    },
    {
      fieldName: 'carbonEmission',
      label: '碳排放量=consumption*factor',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入碳排放量=consumption*factor',
        class: 'w-full',
      },
    },
    {
      fieldName: 'unit',
      label: '单位',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单位',
      },
    },
    {
      fieldName: 'processEmission',
      label: '工艺排放',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入工艺排放',
        class: 'w-full',
      },
    },
    {
      fieldName: 'totalEmission',
      label: '合计',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入合计',
        class: 'w-full',
      },
    },
  ];
}
