import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmissionOutletApi } from '#/api/mes/safetyEnv/emissionOutlet';

/** 排放类型：GAS/WASTEWATER/NOISE选项 */
export const OUTLET_TYPE_OPTIONS = [
  { label: '废气', value: 'EXHAUST_GAS' },
  { label: '废气', value: 'GAS' },
  { label: '废水', value: 'WASTE_WATER' },
];

/** 排放类型：GAS/WASTEWATER/NOISE文案 */
export const OUTLET_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  EXHAUST_GAS: { text: '废气', color: 'success' },
  GAS: { text: '废气', color: 'error' },
  WASTE_WATER: { text: '废水', color: 'warning' },
};

/** 在线监测方式：CEMS/MANUAL/NONE选项 */
export const MONITOR_METHOD_OPTIONS = [
  { label: '在线监测', value: 'CEMS' },
  { label: '手工', value: 'MANUAL' },
];

/** 在线监测方式：CEMS/MANUAL/NONE文案 */
export const MONITOR_METHOD_MAP: Record<string, { color: string; text: string; }> = {
  CEMS: { text: '在线监测', color: 'success' },
  MANUAL: { text: '手工', color: 'error' },
};

export const STATUS_MAP: Record<string, { color: string; text: string; }> = {
  ACTIVE: { text: '启用', color: 'success' },
  INACTIVE: { text: '停用', color: 'default' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'outletCode',
      label: '排放口编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排放口编号',
      },
    },
    {
      fieldName: 'outletName',
      label: '排放口名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排放口名称',
      },
    },
    {
      fieldName: 'outletType',
      label: '排放类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: OUTLET_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'monitorMethod',
      label: '在线监测方式',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: MONITOR_METHOD_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入状态',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEmissionOutletApi.EmissionOutlet>['columns'] {
  return [
    { field: 'outletCode', title: '排放口编号', minWidth: 170, showOverflow: true },
    { field: 'outletName', title: '排放口名称', minWidth: 170, showOverflow: true },
    { field: 'outletType', title: '排放类型', minWidth: 170, showOverflow: true, slots: { default: 'outletType' } },
    { field: 'stackHeight', title: '排气筒高度', width: 120 },
    { field: 'monitorMethod', title: '在线监测方式', minWidth: 170, showOverflow: true, slots: { default: 'monitorMethod' } },
    { field: 'permitNo', title: '关联排污许可证号', minWidth: 170, showOverflow: true },
    { field: 'isKeyOutlet', title: '是否重点/国控排放口', width: 120 },
    { field: 'status', title: '状态', minWidth: 170, showOverflow: true, slots: { default: 'status' } },
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
      fieldName: 'outletCode',
      label: '排放口编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放口编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletName',
      label: '排放口名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放口名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletType',
      label: '排放类型',
      component: 'Select',
      componentProps: {
        options: OUTLET_TYPE_OPTIONS,
        placeholder: '请选择',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'pollutantCodes',
      label: '主要污染物列表',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入主要污染物列表',
      },
    },
    {
      fieldName: 'location',
      label: '位置描述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入位置描述',
      },
    },
    {
      fieldName: 'longitude',
      label: '经度',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入经度',
        class: 'w-full',
      },
    },
    {
      fieldName: 'latitude',
      label: '纬度',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入纬度',
        class: 'w-full',
      },
    },
    {
      fieldName: 'stackHeight',
      label: '排气筒高度',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入排气筒高度',
        class: 'w-full',
      },
    },
    {
      fieldName: 'monitorMethod',
      label: '在线监测方式',
      component: 'Select',
      componentProps: {
        options: MONITOR_METHOD_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'permitNo',
      label: '关联排污许可证号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联排污许可证号',
      },
    },
    {
      fieldName: 'permitLimits',
      label: '许可排放限值',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入许可排放限值',
      },
    },
    {
      fieldName: 'isKeyOutlet',
      label: '是否重点/国控排放口',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Input',
      componentProps: {
        placeholder: '请输入状态',
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
