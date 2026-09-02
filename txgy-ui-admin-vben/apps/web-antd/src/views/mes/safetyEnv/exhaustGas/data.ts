import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetExhaustGasApi } from '#/api/mes/safetyEnv/exhaustGas';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改废气排放检测的表单 */
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
      label: '排放浓度 mg/m3',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'unit',
      label: '单位 mg/m3等',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单位 mg/m3等',
      },
    },
    {
      fieldName: 'flowRate',
      label: '标态干烟气流量 m3/h',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'emissionAmount',
      label: '折算排放速率 kg/h',
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
        options: [{ label: "CEMS在线自动", value: "CEMS_AUTO" }, { label: "人工监测", value: "MANUAL" }],
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
      label: 'CEMS设备编号/采样仪器号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入CEMS设备编号/采样仪器号',
      },
    },
    {
      fieldName: 'inspector',
      label: '监测人(手工时)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入监测人(手工时)',
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
export function useGridColumns(): VxeTableGridOptions<MesSetExhaustGasApi.ExhaustGas>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'outletId', title: '关联排放口编号', width: 130 },
    { field: 'pollutantCode', title: '污染物', minWidth: 150 },
    { field: 'concentration', title: '排放浓度 mg/m3', width: 130 },
    { field: 'unit', title: '单位 mg/m3等', minWidth: 150 },
    { field: 'flowRate', title: '标态干烟气流量 m3/h', width: 130 },
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