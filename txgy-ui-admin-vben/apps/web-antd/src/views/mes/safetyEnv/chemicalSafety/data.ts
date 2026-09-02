import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetChemicalSafetyApi } from '#/api/mes/safetyEnv/chemicalSafety';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改危化品安全管理的表单 */
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
      fieldName: 'chemicalCode',
      label: '危化品代码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危化品代码',
      },
    },
    {
      fieldName: 'chemicalName',
      label: '危化品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危化品名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'storageLocation',
      label: '储存地点',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入储存地点',
      },
    },
    {
      fieldName: 'labelOk',
      label: '标识标签齐全',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择标识标签齐全',
        allowClear: true,
      },
    },
    {
      fieldName: 'msdsOk',
      label: 'MSDS',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择MSDS',
        allowClear: true,
      },
    },
    {
      fieldName: 'storageOk',
      label: '储存条件符合要求',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择储存条件符合要求',
        allowClear: true,
      },
    },
    {
      fieldName: 'separationOk',
      label: '分类存放/隔离存放合规',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择分类存放/隔离存放合规',
        allowClear: true,
      },
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
      label: '巡查人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入巡查人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '巡查时间',
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
      fieldName: 'chemicalCode',
      label: '危化品代码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危化品代码',
      },
    },
    {
      fieldName: 'chemicalName',
      label: '危化品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危化品名称',
      },
    },
    {
      fieldName: 'storageLocation',
      label: '储存地点',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入储存地点',
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
      label: '巡查时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetChemicalSafetyApi.ChemicalSafety>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'chemicalCode', title: '危化品代码', minWidth: 150 },
    { field: 'chemicalName', title: '危化品名称', minWidth: 150 },
    { field: 'storageLocation', title: '储存地点', minWidth: 150 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'inspectTime', title: '巡查时间', width: 180, formatter: 'formatDateTime' },
    { field: 'inspector', title: '巡查人', minWidth: 150 },
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