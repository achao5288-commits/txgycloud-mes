import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetNoiseRecordApi } from '#/api/mes/safetyEnv/noiseRecord';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改噪声检测记录的表单 */
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
      fieldName: 'sourceType',
      label: '噪声源类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "固定声源", value: "固定声源" }, { label: "流动声源", value: "流动声源" }, { label: "冲击噪声", value: "冲击噪声" }, { label: "其他", value: "其他" }],
        placeholder: '请选择噪声源类型',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'location',
      label: '检测位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式',
      component: 'Select',
      componentProps: {
        options: [{ label: "IOT自动采集", value: "IOT_AUTO" }, { label: "人工采集", value: "MANUAL" }],
        placeholder: '请选择采集方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'lex8h',
      label: '8小时等效声级 Lex8h(dB(A))',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'lpeak',
      label: '峰值声级 Lpeak(dB(C))',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'limitLex8h',
      label: '8小时等效声级限值(dB(A))',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'limitLpeak',
      label: '峰值声级限值(dB(C))',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'spectrum',
      label: '频谱分析结果',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入频谱分析结果',
        rows: 2,
      },
      formItemClass: 'col-span-3',
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
      fieldName: 'instrumentNo',
      label: '检测仪器编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测仪器编号',
      },
    },
    {
      fieldName: 'inspector',
      label: '检测人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检测时间',
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
      fieldName: 'photoUrls',
      label: '检测照片 URL',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入检测照片 URL',
        rows: 2,
      },
      formItemClass: 'col-span-3',
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
      fieldName: 'sourceType',
      label: '噪声源类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "固定声源", value: "固定声源" }, { label: "流动声源", value: "流动声源" }, { label: "冲击噪声", value: "冲击噪声" }, { label: "其他", value: "其他" }],
        placeholder: '请选择噪声源类型',
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
      fieldName: 'inspectTime',
      label: '检测时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetNoiseRecordApi.NoiseRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'sourceType', title: '噪声源类型', minWidth: 150 },
    { field: 'location', title: '检测位置', minWidth: 150 },
    { field: 'lex8h', title: '8小时等效声级 Lex8h(dB(A))', width: 130 },
    { field: 'lpeak', title: '峰值声级 Lpeak(dB(C))', width: 130 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'inspectTime', title: '检测时间', width: 180, formatter: 'formatDateTime' },
    { field: 'inspector', title: '检测人', minWidth: 150 },
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