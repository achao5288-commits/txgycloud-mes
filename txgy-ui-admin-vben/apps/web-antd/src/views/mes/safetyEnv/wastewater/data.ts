import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetWastewaterApi } from '#/api/mes/safetyEnv/wastewater';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改废水排放检测的表单 */
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
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletId',
      label: '关联排放口编号',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
      rules: 'required',
    },
    {
      fieldName: 'sampleNo',
      label: '实验室样品编号(手工检测时)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入实验室样品编号(手工检测时)',
      },
    },
    {
      fieldName: 'pollutantCode',
      label: '污染物',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入污染物',
      },
      rules: 'required',
    },
    {
      fieldName: 'concentration',
      label: '检测浓度 mg/L(pH无量纲)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'unit',
      label: '单位 mg/L(pH留空)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单位 mg/L(pH留空)',
      },
    },
    {
      fieldName: 'flowRate',
      label: '排放流量 m3/h',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'emissionAmount',
      label: '排放量 kg/h',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'limitValue',
      label: '限值',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'result',
      label: '综合结论',
      component: 'Select',
      componentProps: {
        options: RESULT_OPTIONS,
        placeholder: '请选择综合结论',
        allowClear: true,
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式',
      component: 'Select',
      componentProps: {
        options: [{ label: "在线自动监测", value: "ONLINE_AUTO" }, { label: "化验室人工检测", value: "LAB_MANUAL" }],
        placeholder: '请选择采集方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'monitorTime',
      label: '监测时间',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '请选择时间',
        showTime: true,
        valueFormat: 'x',
      },
      rules: 'required',
    },
    {
      fieldName: 'instrumentNo',
      label: '在线监测仪/化验设备编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入在线监测仪/化验设备编号',
      },
    },
    {
      fieldName: 'inspector',
      label: '化验员(手工时)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入化验员(手工时)',
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

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'pollutantCode',
      label: '污染物',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入污染物',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择结果',
      },
    },
    {
      fieldName: 'monitorTime',
      label: '监测时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetWastewaterApi.Wastewater>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'outletId', title: '关联排放口编号', width: 130 },
    { field: 'pollutantCode', title: '污染物', minWidth: 150 },
    { field: 'concentration', title: '检测浓度 mg/L(pH无量纲)', width: 130 },
    { field: 'unit', title: '单位 mg/L(pH留空)', minWidth: 150 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'monitorTime', title: '监测时间', width: 180, formatter: 'formatDateTime' },
    {
      title: '操作',
      width: 160,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}