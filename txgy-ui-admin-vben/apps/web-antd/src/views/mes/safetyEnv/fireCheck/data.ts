import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetFireCheckApi } from '#/api/mes/safetyEnv/fireCheck';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改消防设施检测的表单 */
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
      label: '区域/位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入区域/位置',
      },
    },
    {
      fieldName: 'facilityName',
      label: '设施名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入设施名称',
      },
    },
    {
      fieldName: 'facilityCode',
      label: '设施编号(资产编号)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入设施编号(资产编号)',
      },
    },
    {
      fieldName: 'checkTime',
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
      fieldName: 'result',
      label: '综合结论',
      component: 'Select',
      componentProps: {
        options: RESULT_OPTIONS,
        placeholder: '请选择综合结论',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'problemDesc',
      label: '异常/不合格描述',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入异常/不合格描述',
        rows: 2,
      },
      formItemClass: 'col-span-3',
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
      fieldName: 'location',
      label: '区域/位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入区域/位置',
      },
    },
    {
      fieldName: 'facilityName',
      label: '设施名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入设施名称',
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
      fieldName: 'checkTime',
      label: '检测时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetFireCheckApi.FireCheck>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'location', title: '区域/位置', minWidth: 150 },
    { field: 'facilityName', title: '设施名称', minWidth: 150 },
    { field: 'facilityCode', title: '设施编号(资产编号)', minWidth: 150 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'checkTime', title: '检测时间', width: 180, formatter: 'formatDateTime' },
    { field: 'problemDesc', title: '异常/不合格描述', minWidth: 150 },
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