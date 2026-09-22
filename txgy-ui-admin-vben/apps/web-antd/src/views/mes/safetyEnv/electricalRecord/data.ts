import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetElectricalRecordApi } from '#/api/mes/safetyEnv/electricalrecord';

export const RESULT_MAP: Record<string, { color: string; text: string; }> = {
  PASS: { text: '合格', color: 'success' },
  FAIL: { text: '不合格', color: 'error' },
};

export const CALIB_OK_MAP: Record<string, { color: string; text: string; }> = {
  1: { text: '已校准', color: 'success' },
  0: { text: '未校准', color: 'error' },
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
      label: '检测位置(配电柜/线路区域)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置(配电柜/线路区域)',
      },
    },
    {
      fieldName: 'checkItem',
      label: '检测项目',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测项目',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入结果',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetElectricalRecordApi.ElectricalRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'location', title: '检测位置(配电柜/线路区域)', minWidth: 170, showOverflow: true },
    { field: 'checkItem', title: '检测项目', minWidth: 170, showOverflow: true },
    { field: 'measuredValue', title: '实测值', width: 120 },
    { field: 'unit', title: '单位', minWidth: 170, showOverflow: true },
    { field: 'limitValue', title: '标准限值', width: 120 },
    { field: 'result', title: '结果', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'instrumentCalibOk', title: '仪器校准状态快照', width: 120, slots: { default: 'instrumentCalibOk' } },
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
      fieldName: 'deviceId',
      label: '关联设备/配电设施编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联设备/配电设施编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'location',
      label: '检测位置(配电柜/线路区域)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测位置(配电柜/线路区域)',
      },
    },
    {
      fieldName: 'checkItem',
      label: '检测项目',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测项目',
      },
      rules: 'required',
    },
    {
      fieldName: 'measuredValue',
      label: '实测值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入实测值',
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
      fieldName: 'limitValue',
      label: '标准限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入标准限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Input',
      componentProps: {
        placeholder: '请输入结果',
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
      fieldName: 'instrumentCalibOk',
      label: '仪器校准状态快照',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
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
