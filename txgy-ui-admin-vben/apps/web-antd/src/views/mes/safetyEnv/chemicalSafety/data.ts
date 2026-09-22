import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetChemicalSafetyApi } from '#/api/mes/safetyEnv/chemicalSafety';

/** 结果：PASS/FAIL选项 */
export const RESULT_OPTIONS = [
  { label: '超标', value: 'FAIL' },
  { label: '达标', value: 'PASS' },
];

/** 结果：PASS/FAIL文案 */
export const RESULT_MAP: Record<string, { color: string; text: string; }> = {
  FAIL: { text: '超标', color: 'success' },
  PASS: { text: '达标', color: 'error' },
};

export const YES_NO_MAP: Record<string, { color: string; text: string; }> = {
  1: { text: '是', color: 'success' },
  0: { text: '否', color: 'default' },
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
      fieldName: 'chemicalCode',
      label: '危化品编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危化品编码',
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
export function useGridColumns(): VxeTableGridOptions<MesSetChemicalSafetyApi.ChemicalSafety>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'chemicalCode', title: '危化品编码', minWidth: 170, showOverflow: true },
    { field: 'chemicalName', title: '危化品名称', minWidth: 170, showOverflow: true },
    { field: 'storageLocation', title: '存储地点', minWidth: 170, showOverflow: true },
    { field: 'labelOk', title: '标识完整性', width: 120, slots: { default: 'labelOk' } },
    { field: 'msdsOk', title: 'MSDS有效性', width: 120, slots: { default: 'msdsOk' } },
    { field: 'storageOk', title: '储存条件合格', width: 120, slots: { default: 'storageOk' } },
    { field: 'result', title: '结果', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'inspector', title: '检测人', minWidth: 170, showOverflow: true },
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
      fieldName: 'chemicalCode',
      label: '危化品编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入危化品编码',
      },
    },
    {
      fieldName: 'chemicalName',
      label: '危化品名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入危化品名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'storageLocation',
      label: '存储地点',
      component: 'Input',
      componentProps: {
        placeholder: '请输入存储地点',
      },
    },
    {
      fieldName: 'labelOk',
      label: '标识完整性',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'msdsOk',
      label: 'MSDS有效性',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'storageOk',
      label: '储存条件合格',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'separationOk',
      label: '禁忌物分离合格',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
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
      fieldName: 'problemDesc',
      label: '异常/不合格描述',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入异常/不合格描述',
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
