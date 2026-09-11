import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetGasRecordApi } from '#/api/mes/safetyEnv/gasrecord';

/** 气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2选项 */
export const GAS_TYPE_OPTIONS = [
  { label: 'CO', value: 'CO' },
  { label: 'NO2', value: 'NO2' },
];

/** 气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2文案 */
export const GAS_TYPE_MAP: Record<string, { text: string; color: string }> = {
  CO: { text: 'CO', color: 'success' },
  NO2: { text: 'NO2', color: 'error' },
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
      label: '检测位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置',
      },
    },
    {
      fieldName: 'gasType',
      label: '气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: GAS_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'result',
      label: '结果：PASS/FAIL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入结果：PASS/FAIL',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式：IOT_AUTO/MANUAL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采集方式：IOT_AUTO/MANUAL',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetGasRecordApi.GasRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'location', title: '检测位置', minWidth: 170, showOverflow: true },
    { field: 'gasType', title: '气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2', minWidth: 170, showOverflow: true, slots: { default: 'gasType' } },
    { field: 'concentration', title: '检测浓度值', width: 120 },
    { field: 'unit', title: '单位：mg/m3 / % / %LEL', minWidth: 170, showOverflow: true },
    { field: 'limitValue', title: '限值', width: 120 },
    { field: 'result', title: '结果：PASS/FAIL', minWidth: 170, showOverflow: true },
    { field: 'collectionMode', title: '采集方式：IOT_AUTO/MANUAL', minWidth: 170, showOverflow: true },
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
      label: '关联工单编号（事件触发型）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工单编号（事件触发型）',
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
      fieldName: 'permitId',
      label: '关联作业许可编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联作业许可编号',
      },
    },
    {
      fieldName: 'location',
      label: '检测位置',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测位置',
      },
    },
    {
      fieldName: 'gasType',
      label: '气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2',
      component: 'Select',
      componentProps: {
        options: GAS_TYPE_OPTIONS,
        placeholder: '请选择',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'concentration',
      label: '检测浓度值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入检测浓度值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'unit',
      label: '单位：mg/m3 / % / %LEL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单位：mg/m3 / % / %LEL',
      },
    },
    {
      fieldName: 'limitValue',
      label: '限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'result',
      label: '结果：PASS/FAIL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入结果：PASS/FAIL',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式：IOT_AUTO/MANUAL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采集方式：IOT_AUTO/MANUAL',
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
