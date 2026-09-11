import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetFireCheckApi } from '#/api/mes/safetyEnv/firecheck';

/** 结果：PASS/FAIL选项 */
export const RESULT_OPTIONS = [
  { label: '超标', value: 'FAIL' },
  { label: '达标', value: 'PASS' },
];

/** 结果：PASS/FAIL文案 */
export const RESULT_MAP: Record<string, { text: string; color: string }> = {
  FAIL: { text: '超标', color: 'success' },
  PASS: { text: '达标', color: 'error' },
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
      label: '设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)',
      },
    },
    {
      fieldName: 'result',
      label: '结果：PASS/FAIL',
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
export function useGridColumns(): VxeTableGridOptions<MesSetFireCheckApi.FireCheck>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'location', title: '区域/位置', minWidth: 170, showOverflow: true },
    { field: 'facilityName', title: '设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)', minWidth: 170, showOverflow: true },
    { field: 'facilityCode', title: '设施编号(资产编号)', minWidth: 170, showOverflow: true },
    { field: 'checkTime', title: '检测时间', width: 120 },
    { field: 'result', title: '结果：PASS/FAIL', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'problemDesc', title: '异常/不合格描述', minWidth: 170, showOverflow: true },
    { field: 'inspector', title: '检测人', minWidth: 170, showOverflow: true },
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
      fieldName: 'location',
      label: '区域/位置',
      component: 'Input',
      componentProps: {
        placeholder: '请输入区域/位置',
      },
    },
    {
      fieldName: 'facilityName',
      label: '设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)',
      },
      rules: 'required',
    },
    {
      fieldName: 'facilityCode',
      label: '设施编号(资产编号)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设施编号(资产编号)',
      },
    },
    {
      fieldName: 'checkTime',
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
      fieldName: 'result',
      label: '结果：PASS/FAIL',
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
      fieldName: 'photoUrls',
      label: '检测照片URL(逗号分隔)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测照片URL(逗号分隔)',
      },
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
