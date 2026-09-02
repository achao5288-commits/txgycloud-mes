import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetOccupationalHazardApi } from '#/api/mes/safetyEnv/occupationalHazard';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改职业病危害检测的表单 */
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
      fieldName: 'factorCategory',
      label: '危害因素类别',
      component: 'Select',
      componentProps: {
        options: [{ label: "化学因素", value: "CHEMICAL" }, { label: "粉尘", value: "DUST" }, { label: "物理因素", value: "PHYSICAL" }, { label: "生物因素", value: "BIOLOGICAL" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择危害因素类别',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'factorCode',
      label: '危害因素编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危害因素编码',
      },
      rules: 'required',
    },
    {
      fieldName: 'workplace',
      label: '检测岗位/工作场所',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测岗位/工作场所',
      },
      rules: 'required',
    },
    {
      fieldName: 'sourceRefType',
      label: '来源关联类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "检测计划", value: "PLAN" }, { label: "检测任务", value: "TASK" }],
        placeholder: '请选择来源关联类型',
        allowClear: true,
      },
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
      fieldName: 'limitType',
      label: '限值类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "MAC(最高容许浓度)", value: "MAC" }, { label: "PC-TWA(时间加权平均)", value: "PC-TWA" }, { label: "PC-STEL(短时间接触)", value: "PC-STEL" }, { label: "噪声限值", value: "NOISE" }],
        placeholder: '请选择限值类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'oelValue',
      label: '职业接触限值(OEL)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'refStandard',
      label: '参考标准',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入参考标准',
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
      fieldName: 'factorCode',
      label: '危害因素编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危害因素编码',
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
export function useGridColumns(): VxeTableGridOptions<MesSetOccupationalHazardApi.OccupationalHazard>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'factorCategory', title: '危害因素类别', minWidth: 150 },
    { field: 'factorCode', title: '危害因素编码', minWidth: 150 },
    { field: 'workplace', title: '检测岗位/工作场所', minWidth: 150 },
    { field: 'measuredValue', title: '测量值', width: 130 },
    { field: 'unit', title: '单位', minWidth: 150 },
    { field: 'limitType', title: '限值类型', minWidth: 150 },
    { field: 'oelValue', title: '职业接触限值(OEL)', width: 130 },
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