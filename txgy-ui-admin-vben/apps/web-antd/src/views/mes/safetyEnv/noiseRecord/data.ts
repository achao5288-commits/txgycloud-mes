import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetNoiseRecordApi } from '#/api/mes/safetyEnv/noiserecord';

/** 采集方式：IOT_AUTO/MANUAL选项 */
export const COLLECTION_MODE_OPTIONS = [
  { label: '自动', value: 'AUTO' },
  { label: '手工', value: 'MANUAL' },
];

/** 采集方式：IOT_AUTO/MANUAL文案 */
export const COLLECTION_MODE_MAP: Record<string, { color: string; text: string; }> = {
  AUTO: { text: '自动', color: 'success' },
  MANUAL: { text: '手工', color: 'error' },
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

export const SOURCE_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  STATIONARY: { text: '固定式声级计', color: 'processing' },
  PERSONAL: { text: '个体剂量计', color: 'warning' },
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
      fieldName: 'sourceType',
      label: '监测类型',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入监测类型',
      },
    },
    {
      fieldName: 'location',
      label: '检测位置/区域',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置/区域',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: COLLECTION_MODE_OPTIONS,
        placeholder: '请选择',
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
export function useGridColumns(): VxeTableGridOptions<MesSetNoiseRecordApi.NoiseRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'sourceType', title: '监测类型', minWidth: 170, showOverflow: true, slots: { default: 'sourceType' } },
    { field: 'location', title: '检测位置/区域', minWidth: 170, showOverflow: true },
    { field: 'collectionMode', title: '采集方式', minWidth: 170, showOverflow: true, slots: { default: 'collectionMode' } },
    { field: 'lex8h', title: '8小时等效声级', width: 120 },
    { field: 'lpeak', title: '峰值声级', width: 120 },
    { field: 'limitLex8h', title: '限值', width: 120 },
    { field: 'limitLpeak', title: '限值', width: 120 },
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
      fieldName: 'woId',
      label: '关联工单编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工单编号',
      },
    },
    {
      fieldName: 'operationId',
      label: '关联工序编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工序编号',
      },
    },
    {
      fieldName: 'deviceId',
      label: '关联设备编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联设备编号',
      },
    },
    {
      fieldName: 'empId',
      label: '关联人员编号(个体剂量计佩戴人)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联人员编号(个体剂量计佩戴人)',
      },
    },
    {
      fieldName: 'sourceType',
      label: '监测类型',
      component: 'Input',
      componentProps: {
        placeholder: '请输入监测类型',
      },
      rules: 'required',
    },
    {
      fieldName: 'location',
      label: '检测位置/区域',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测位置/区域',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式',
      component: 'Select',
      componentProps: {
        options: COLLECTION_MODE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'lex8h',
      label: '8小时等效声级',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入8小时等效声级',
        class: 'w-full',
      },
    },
    {
      fieldName: 'lpeak',
      label: '峰值声级',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入峰值声级',
        class: 'w-full',
      },
    },
    {
      fieldName: 'limitLex8h',
      label: '限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'limitLpeak',
      label: '限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'spectrum',
      label: '频谱分析',
      component: 'Input',
      componentProps: {
        placeholder: '请输入频谱分析',
      },
    },
    {
      fieldName: 'standardId',
      label: '关联检测标准编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联检测标准编号',
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
      fieldName: 'instrumentNo',
      label: '检测仪器编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测仪器编号',
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
      fieldName: 'photoUrls',
      label: '检测照片',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测照片',
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
