import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetDustRecordApi } from '#/api/mes/safetyEnv/dustRecord';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改粉尘浓度检测的表单 */
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
      fieldName: 'location',
      label: '检测位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置',
      },
    },
    {
      fieldName: 'dustType',
      label: '粉尘类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "煤尘", value: "煤尘" }, { label: "矽尘", value: "矽尘" }, { label: "水泥尘", value: "水泥尘" }, { label: "木尘", value: "木尘" }, { label: "其他", value: "其他" }],
        placeholder: '请选择粉尘类型',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'concentration',
      label: '粉尘浓度值',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'sio2Content',
      label: '游离二氧化硅含量(%)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'unit',
      label: '单位',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单位',
      },
    },
    {
      fieldName: 'limitValue',
      label: '限值(mg/m3)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'refStandard',
      label: '执行标准',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入执行标准',
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
        options: [{ label: "IOT自动采集", value: "IOT_AUTO" }, { label: "人工采集", value: "MANUAL" }],
        placeholder: '请选择采集方式',
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
      fieldName: 'dustType',
      label: '粉尘类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "煤尘", value: "煤尘" }, { label: "矽尘", value: "矽尘" }, { label: "水泥尘", value: "水泥尘" }, { label: "木尘", value: "木尘" }, { label: "其他", value: "其他" }],
        placeholder: '请选择粉尘类型',
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
export function useGridColumns(): VxeTableGridOptions<MesSetDustRecordApi.DustRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'dustType', title: '粉尘类型', minWidth: 150 },
    { field: 'location', title: '检测位置', minWidth: 150 },
    { field: 'concentration', title: '粉尘浓度值', width: 130 },
    { field: 'unit', title: '单位', minWidth: 150 },
    { field: 'limitValue', title: '限值(mg/m3)', width: 130 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'inspectTime', title: '检测时间', width: 180, formatter: 'formatDateTime' },
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