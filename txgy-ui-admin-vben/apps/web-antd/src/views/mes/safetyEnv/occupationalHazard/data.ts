import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetOccupationalHazardApi } from '#/api/mes/safetyEnv/occupationalhazard';

/** 因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL选项 */
export const FACTOR_CATEGORY_OPTIONS = [
  { label: '化学因素', value: 'CHEMICAL' },
  { label: '物理因素', value: 'PHYSICAL' },
];

/** 因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL文案 */
export const FACTOR_CATEGORY_MAP: Record<string, { color: string; text: string; }> = {
  CHEMICAL: { text: '化学因素', color: 'success' },
  PHYSICAL: { text: '物理因素', color: 'error' },
};

/** 结果：PASS/FAIL选项 */
export const RESULT_OPTIONS = [
  { label: '超标', value: 'FAIL' },
  { label: '达标', value: 'PASS' },
];

/** 结果：PASS/FAIL文案 */
export const RESULT_MAP: Record<string, { color: string; text: string; }> = {
  FAIL: { text: '超标', color: 'error' },
  PASS: { text: '达标', color: 'success' },
};

export const LIMIT_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  MAC: { text: '最高容许浓度', color: 'error' },
  'PC-TWA': { text: '时间加权平均容许浓度', color: 'processing' },
  'PC-STEL': { text: '短时间接触容许浓度', color: 'warning' },
  LEX_8H: { text: '8 小时等效声级', color: 'purple' },
  NOISE: { text: '噪声', color: 'purple' },
  WBGT: { text: '湿球黑球温度', color: 'orange' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号',
      },
    },
    {
      fieldName: 'factorCategory',
      label: '因素类别',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: FACTOR_CATEGORY_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'factorCode',
      label: '具体因素',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入具体因素',
      },
    },
    {
      fieldName: 'workplace',
      label: '检测岗位/工作场所',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测岗位/工作场所',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetOccupationalHazardApi.OccupationalHazard>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'factorCategory', title: '因素类别', minWidth: 170, showOverflow: true, slots: { default: 'factorCategory' } },
    { field: 'factorCode', title: '具体因素', minWidth: 170, showOverflow: true },
    { field: 'workplace', title: '检测岗位/工作场所', minWidth: 170, showOverflow: true },
    { field: 'measuredValue', title: '实测浓度/强度', width: 120 },
    { field: 'unit', title: '单位', minWidth: 170, showOverflow: true },
    { field: 'limitType', title: '接触限值类型', minWidth: 170, showOverflow: true, slots: { default: 'limitType' } },
    { field: 'oelValue', title: '职业接触限值', width: 120 },
    { field: 'refStandard', title: '引用国标', minWidth: 170, showOverflow: true },
    { field: 'result', title: '结果', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'inspectTime', title: '检测时间', width: 120 },
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
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入记录编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'planId',
      label: '关联检测计划编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联检测计划编号',
      },
    },
    {
      fieldName: 'empId',
      label: '关联人员编号(个体暴露监测时；岗位检测可空)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联人员编号(个体暴露监测时；岗位检测可空)',
      },
    },
    {
      fieldName: 'factorCategory',
      label: '因素类别',
      component: 'Select',
      componentProps: {
        options: FACTOR_CATEGORY_OPTIONS,
        placeholder: '请选择',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'factorCode',
      label: '具体因素',
      component: 'Input',
      componentProps: {
        placeholder: '请输入具体因素',
      },
      rules: 'required',
    },
    {
      fieldName: 'workplace',
      label: '检测岗位/工作场所',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测岗位/工作场所',
      },
      rules: 'required',
    },
    {
      fieldName: 'sourceRefType',
      label: '数据来源',
      component: 'Input',
      componentProps: {
        placeholder: '请输入数据来源',
      },
    },
    {
      fieldName: 'sourceRecordId',
      label: '来源安全检测记录id(复用气体/噪声/粉尘)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入来源安全检测记录id(复用气体/噪声/粉尘)',
      },
    },
    {
      fieldName: 'measuredValue',
      label: '实测浓度/强度',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入实测浓度/强度',
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
      fieldName: 'limitType',
      label: '接触限值类型',
      component: 'Input',
      componentProps: {
        placeholder: '请输入接触限值类型',
      },
    },
    {
      fieldName: 'oelValue',
      label: '职业接触限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入职业接触限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'refStandard',
      label: '引用国标',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用国标',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        options: RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'inspector',
      label: '检测人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检测时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
      rules: 'required',
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
