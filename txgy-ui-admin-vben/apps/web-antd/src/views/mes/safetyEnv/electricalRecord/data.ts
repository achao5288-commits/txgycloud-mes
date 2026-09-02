import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetElectricalRecordApi } from '#/api/mes/safetyEnv/electricalRecord';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改电气安全检测的表单 */
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
      fieldName: 'deviceId',
      label: '关联设备编号',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
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
      fieldName: 'checkItem',
      label: '检测项目',
      component: 'Select',
      componentProps: {
        options: [{ label: "接地电阻", value: "接地电阻" }, { label: "绝缘电阻", value: "绝缘电阻" }, { label: "漏电保护", value: "漏电保护" }, { label: "等电位", value: "等电位" }, { label: "其他", value: "其他" }],
        placeholder: '请选择检测项目',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'measuredValue',
      label: '测量值',
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
      fieldName: 'instrumentNo',
      label: '检测仪器编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测仪器编号',
      },
    },
    {
      fieldName: 'instrumentCalibOk',
      label: '仪器校准状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "已校准", value: 1 }, { label: "未校准", value: 0 }],
        placeholder: '请选择仪器校准状态',
        allowClear: true,
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
      fieldName: 'checkItem',
      label: '检测项目',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "接地电阻", value: "接地电阻" }, { label: "绝缘电阻", value: "绝缘电阻" }, { label: "漏电保护", value: "漏电保护" }, { label: "等电位", value: "等电位" }, { label: "其他", value: "其他" }],
        placeholder: '请选择检测项目',
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
export function useGridColumns(): VxeTableGridOptions<MesSetElectricalRecordApi.ElectricalRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'deviceId', title: '关联设备编号', width: 130 },
    { field: 'location', title: '检测位置', minWidth: 150 },
    { field: 'checkItem', title: '检测项目', minWidth: 150 },
    { field: 'measuredValue', title: '测量值', width: 130 },
    { field: 'unit', title: '单位', minWidth: 150 },
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