import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmissionOutletApi } from '#/api/mes/safetyEnv/emissionOutlet';


/** 新增/修改排放口管理的表单 */
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
      fieldName: 'outletCode',
      label: '排放口编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排放口编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletName',
      label: '排放口名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排放口名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletType',
      label: '排放类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "废气", value: "GAS" }, { label: "废水", value: "WASTEWATER" }, { label: "噪声", value: "NOISE" }],
        placeholder: '请选择排放类型',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'pollutantCodes',
      label: '主要污染物列表(JSON/CSV文本)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入主要污染物列表(JSON/CSV文本)',
      },
    },
    {
      fieldName: 'location',
      label: '位置描述',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入位置描述',
      },
    },
    {
      fieldName: 'longitude',
      label: '经度',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'latitude',
      label: '纬度',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'stackHeight',
      label: '排气筒高度 m',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'monitorMethod',
      label: '在线监测方式',
      component: 'Select',
      componentProps: {
        options: [{ label: "在线监测CEMS", value: "CEMS" }, { label: "人工监测", value: "MANUAL" }, { label: "未安装", value: "NONE" }],
        placeholder: '请选择在线监测方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'permitNo',
      label: '关联排污许可证号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入关联排污许可证号',
      },
    },
    {
      fieldName: 'permitLimits',
      label: '许可排放限值JSON文本',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入许可排放限值JSON文本',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'isKeyOutlet',
      label: '是否重点/国控排放口',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择是否重点/国控排放口',
        allowClear: true,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "启用", value: "ACTIVE" }, { label: "停用", value: "INACTIVE" }],
        placeholder: '请选择状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入备注',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
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
        options: [{ label: "废气", value: "GAS" }, { label: "废水", value: "WASTEWATER" }, { label: "噪声", value: "NOISE" }],
        placeholder: '请选择排放类型',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "启用", value: "ACTIVE" }, { label: "停用", value: "INACTIVE" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEmissionOutletApi.EmissionOutlet>['columns'] {
  return [
    { field: 'outletCode', title: '排放口编号', minWidth: 150 },
    { field: 'outletName', title: '排放口名称', minWidth: 150 },
    { field: 'outletType', title: '排放类型', minWidth: 150 },
    { field: 'location', title: '位置描述', minWidth: 150 },
    { field: 'monitorMethod', title: '在线监测方式', minWidth: 150 },
    { field: 'isKeyOutlet', title: '是否重点/国控排放口', width: 130 },
    { field: 'status', title: '状态', minWidth: 150 },
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