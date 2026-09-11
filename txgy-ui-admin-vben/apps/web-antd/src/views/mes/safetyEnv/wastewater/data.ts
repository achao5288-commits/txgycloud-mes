import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetWastewaterApi } from '#/api/mes/safetyEnv/wastewater';

/** 采集方式：ONLINE_AUTO/LAB_MANUAL选项 */
export const COLLECTION_MODE_OPTIONS = [
  { label: '实验室手工', value: 'LAB_MANUAL' },
  { label: '手工', value: 'MANUAL' },
];

/** 采集方式：ONLINE_AUTO/LAB_MANUAL文案 */
export const COLLECTION_MODE_MAP: Record<string, { text: string; color: string }> = {
  LAB_MANUAL: { text: '实验室手工', color: 'success' },
  MANUAL: { text: '手工', color: 'error' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '记录编号 WW-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号 WW-YYYYMMDD-NNN',
      },
    },
    {
      fieldName: 'outletId',
      label: '关联排放口编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入关联排放口编号',
      },
    },
    {
      fieldName: 'pollutantCode',
      label: '污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等',
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
      label: '采集方式：ONLINE_AUTO/LAB_MANUAL',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: COLLECTION_MODE_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetWastewaterApi.Wastewater>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号 WW-YYYYMMDD-NNN', minWidth: 170, showOverflow: true },
    { field: 'outletId', title: '关联排放口编号', width: 120 },
    { field: 'pollutantCode', title: '污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等', minWidth: 170, showOverflow: true },
    { field: 'concentration', title: '检测浓度 mg/L(pH无量纲)', width: 120 },
    { field: 'limitValue', title: '限值', width: 120 },
    { field: 'result', title: '结果：PASS/FAIL', minWidth: 170, showOverflow: true },
    { field: 'collectionMode', title: '采集方式：ONLINE_AUTO/LAB_MANUAL', minWidth: 170, showOverflow: true, slots: { default: 'collectionMode' } },
    { field: 'monitorTime', title: '监测时间', width: 120 },
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
      label: '记录编号 WW-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        placeholder: '请输入记录编号 WW-YYYYMMDD-NNN',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletId',
      label: '关联排放口编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联排放口编号',
      },
      rules: 'required',
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
      fieldName: 'sampleNo',
      label: '实验室样品编号(手工检测时)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入实验室样品编号(手工检测时)',
      },
    },
    {
      fieldName: 'pollutantCode',
      label: '污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等',
      component: 'Input',
      componentProps: {
        placeholder: '请输入污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等',
      },
      rules: 'required',
    },
    {
      fieldName: 'concentration',
      label: '检测浓度 mg/L(pH无量纲)',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入检测浓度 mg/L(pH无量纲)',
        class: 'w-full',
      },
    },
    {
      fieldName: 'unit',
      label: '单位 mg/L(pH留空)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单位 mg/L(pH留空)',
      },
    },
    {
      fieldName: 'flowRate',
      label: '排放流量 m3/h',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入排放流量 m3/h',
        class: 'w-full',
      },
    },
    {
      fieldName: 'emissionAmount',
      label: '排放量 kg/h',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入排放量 kg/h',
        class: 'w-full',
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
      label: '采集方式：ONLINE_AUTO/LAB_MANUAL',
      component: 'Select',
      componentProps: {
        options: COLLECTION_MODE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'monitorTime',
      label: '监测时间',
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
      fieldName: 'instrumentNo',
      label: '在线监测仪/化验设备编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入在线监测仪/化验设备编号',
      },
    },
    {
      fieldName: 'inspector',
      label: '化验员(手工时)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入化验员(手工时)',
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
