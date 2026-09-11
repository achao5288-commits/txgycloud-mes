import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetElectricalRecordApi } from '#/api/mes/safetyEnv/electricalrecord';

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '记录编号 ELEC-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号 ELEC-YYYYMMDD-NNN',
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
      label: '检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE',
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
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetElectricalRecordApi.ElectricalRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号 ELEC-YYYYMMDD-NNN', minWidth: 170, showOverflow: true },
    { field: 'location', title: '检测位置(配电柜/线路区域)', minWidth: 170, showOverflow: true },
    { field: 'checkItem', title: '检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE', minWidth: 170, showOverflow: true },
    { field: 'measuredValue', title: '实测值', width: 120 },
    { field: 'unit', title: '单位 MΩ/Ω/mA/ms/V', minWidth: 170, showOverflow: true },
    { field: 'limitValue', title: '标准限值', width: 120 },
    { field: 'result', title: '结果：PASS/FAIL', minWidth: 170, showOverflow: true },
    { field: 'instrumentCalibOk', title: '仪器校准状态快照：1已校准/0未校准', width: 120 },
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
      label: '记录编号 ELEC-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        placeholder: '请输入记录编号 ELEC-YYYYMMDD-NNN',
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
      label: '检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE',
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
      label: '单位 MΩ/Ω/mA/ms/V',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单位 MΩ/Ω/mA/ms/V',
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
      label: '结果：PASS/FAIL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入结果：PASS/FAIL',
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
      label: '仪器校准状态快照：1已校准/0未校准',
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
