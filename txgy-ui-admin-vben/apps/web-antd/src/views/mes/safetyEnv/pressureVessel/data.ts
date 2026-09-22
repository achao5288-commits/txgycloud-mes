import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPressureVesselApi } from '#/api/mes/safetyEnv/pressurevessel';

export const YES_NO_MAP: Record<string, { color: string; text: string; }> = {
  1: { text: '是', color: 'success' },
  0: { text: '否', color: 'default' },
};

export const RESULT_MAP: Record<string, { color: string; text: string; }> = {
  PASS: { text: '合格', color: 'success' },
  FAIL: { text: '不合格', color: 'error' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '检验记录/报告编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检验记录/报告编号',
      },
    },
    {
      fieldName: 'vesselRegNo',
      label: '压力容器使用登记证号(特种设备注册代码)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入压力容器使用登记证号(特种设备注册代码)',
      },
    },
    {
      fieldName: 'ndtMethods',
      label: '无损检测方法',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入无损检测方法',
      },
    },
    {
      fieldName: 'result',
      label: '综合结论',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入综合结论',
      },
    },
    {
      fieldName: 'inspectOrg',
      label: '检验机构(需资质)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检验机构(需资质)',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPressureVesselApi.PressureVessel>['columns'] {
  return [
    { field: 'recordNo', title: '检验记录/报告编号', minWidth: 170, showOverflow: true },
    { field: 'vesselRegNo', title: '压力容器使用登记证号(特种设备注册代码)', minWidth: 170, showOverflow: true },
    { field: 'wallThickness', title: '壁厚测定最小壁厚', width: 120 },
    { field: 'ndtMethods', title: '无损检测方法', minWidth: 170, showOverflow: true },
    { field: 'safetyValveOk', title: '安全阀校验合格', width: 120, slots: { default: 'safetyValveOk' } },
    { field: 'pressureTestValue', title: '耐压试验压力', width: 120 },
    { field: 'pressureTestResult', title: '耐压试验结果', minWidth: 170, showOverflow: true, slots: { default: 'pressureTestResult' } },
    { field: 'result', title: '综合结论', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'inspectOrg', title: '检验机构(需资质)', minWidth: 170, showOverflow: true },
    { field: 'nextInspectDate', title: '下次检验日期', width: 120 },
    { field: 'inspectTime', title: '检验时间', width: 120 },
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
      label: '检验记录/报告编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检验记录/报告编号',
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
      label: '关联设备编号(特种设备台账)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联设备编号(特种设备台账)',
      },
      rules: 'required',
    },
    {
      fieldName: 'vesselRegNo',
      label: '压力容器使用登记证号(特种设备注册代码)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入压力容器使用登记证号(特种设备注册代码)',
      },
    },
    {
      fieldName: 'wallThickness',
      label: '壁厚测定最小壁厚',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入壁厚测定最小壁厚',
        class: 'w-full',
      },
    },
    {
      fieldName: 'ndtMethods',
      label: '无损检测方法',
      component: 'Input',
      componentProps: {
        placeholder: '请输入无损检测方法',
      },
    },
    {
      fieldName: 'ndtResults',
      label: '无损检测结果',
      component: 'Input',
      componentProps: {
        placeholder: '请输入无损检测结果',
      },
    },
    {
      fieldName: 'safetyValveOk',
      label: '安全阀校验合格',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'pressureTestValue',
      label: '耐压试验压力',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入耐压试验压力',
        class: 'w-full',
      },
    },
    {
      fieldName: 'pressureTestResult',
      label: '耐压试验结果',
      component: 'Input',
      componentProps: {
        placeholder: '请输入耐压试验结果',
      },
    },
    {
      fieldName: 'processPressure',
      label: '工艺允许压力',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入工艺允许压力',
        class: 'w-full',
      },
    },
    {
      fieldName: 'result',
      label: '综合结论',
      component: 'Input',
      componentProps: {
        placeholder: '请输入综合结论',
      },
    },
    {
      fieldName: 'inspectOrg',
      label: '检验机构(需资质)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检验机构(需资质)',
      },
      rules: 'required',
    },
    {
      fieldName: 'nextInspectDate',
      label: '下次检验日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'reportFileUrl',
      label: '检验报告文件',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检验报告文件',
      },
    },
    {
      fieldName: 'inspector',
      label: '检验人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检验人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检验时间',
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
