import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPressureVesselApi } from '#/api/mes/safetyEnv/pressureVessel';

import { markRaw } from 'vue';

import { getRangePickerDefaultProps } from '#/utils';
import { DvMachinerySelect } from '#/views/mes/dv/machinery/components';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 无损检测方法 UT/RT/MT/PT */
const NDT_METHOD_OPTIONS = [
  { label: '超声检测(UT)', value: 'UT' },
  { label: '射线检测(RT)', value: 'RT' },
  { label: '磁粉检测(MT)', value: 'MT' },
  { label: '渗透检测(PT)', value: 'PT' },
];

/** 新增/修改压力容器检测记录的表单 */
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
      fieldName: 'deviceId',
      label: '关联设备',
      component: markRaw(DvMachinerySelect),
      componentProps: {
        placeholder: '请选择设备',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'vesselRegNo',
      label: '登记证号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入压力容器使用登记证号',
      },
    },
    {
      fieldName: 'inspectOrg',
      label: '检验机构',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检验机构',
      },
      rules: 'required',
    },
    {
      fieldName: 'inspector',
      label: '检验人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检验人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检验时间',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '请选择检验时间',
        showTime: true,
        valueFormat: 'x',
      },
      rules: 'required',
    },
    {
      fieldName: 'nextInspectDate',
      label: '下次检验日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择下次检验日期',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'ndtMethods',
      label: '无损检测方法',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: NDT_METHOD_OPTIONS,
        placeholder: '请选择无损检测方法',
      },
    },
    {
      fieldName: 'wallThickness',
      label: '最小壁厚(mm)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入最小壁厚',
        precision: 2,
      },
    },
    {
      fieldName: 'safetyValveOk',
      label: '安全阀校验',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [
          { label: '合格', value: 1 },
          { label: '不合格', value: 0 },
        ],
        placeholder: '请选择安全阀校验结果',
      },
    },
    {
      fieldName: 'pressureTestValue',
      label: '耐压试验压力(MPa)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入耐压试验压力',
        precision: 2,
      },
    },
    {
      fieldName: 'pressureTestResult',
      label: '耐压试验结果',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择耐压试验结果',
      },
    },
    {
      fieldName: 'processPressure',
      label: '工艺允许压力(MPa)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入工艺允许压力',
        precision: 2,
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
      fieldName: 'reportFileUrl',
      label: '报告文件',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检验报告文件 URL',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-3',
      componentProps: {
        placeholder: '请输入备注',
        rows: 3,
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'result',
      label: '综合结论',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择综合结论',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检验时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPressureVesselApi.PressureVessel>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 140 },
    { field: 'deviceId', title: '设备ID', width: 100 },
    { field: 'vesselRegNo', title: '登记证号', minWidth: 140 },
    { field: 'inspectOrg', title: '检验机构', minWidth: 150 },
    { field: 'inspector', title: '检验人', width: 100 },
    {
      field: 'result',
      title: '综合结论',
      width: 100,
      slots: {
        default: 'result',
      },
    },
    { field: 'nextInspectDate', title: '下次检验日期', width: 130 },
    {
      field: 'inspectTime',
      title: '检验时间',
      width: 180,
      formatter: 'formatDateTime',
    },
    {
      field: 'createTime',
      title: '创建时间',
      width: 180,
      formatter: 'formatDateTime',
    },
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
