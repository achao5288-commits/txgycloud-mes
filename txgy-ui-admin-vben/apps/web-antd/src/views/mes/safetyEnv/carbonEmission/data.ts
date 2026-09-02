import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetCarbonEmissionApi } from '#/api/mes/safetyEnv/carbonEmission';


/** 新增/修改碳排放核算的表单 */
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
      fieldName: 'calcNo',
      label: '核算批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入核算批次号',
      },
      rules: 'required',
    },
    {
      fieldName: 'periodType',
      label: '核算周期',
      component: 'Select',
      componentProps: {
        options: [{ label: "日核算", value: "DAILY" }, { label: "月核算", value: "MONTHLY" }, { label: "年核算", value: "YEARLY" }],
        placeholder: '请选择核算周期',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'periodStart',
      label: '周期开始日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'periodEnd',
      label: '周期结束日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'energyType',
      label: '能源类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "电力", value: "ELECTRICITY" }, { label: "原煤", value: "COAL" }, { label: "天然气", value: "NATURAL_GAS" }, { label: "柴油", value: "DIESEL" }, { label: "汽油", value: "GASOLINE" }, { label: "蒸汽", value: "STEAM" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择能源类型',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'consumption',
      label: '能源消耗量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'emissionFactor',
      label: '排放因子',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'carbonEmission',
      label: '碳排放量=consumption*factor',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'unit',
      label: '单位 tCO2',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单位 tCO2',
      },
    },
    {
      fieldName: 'processEmission',
      label: '工艺排放',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'totalEmission',
      label: '合计',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'periodType',
      label: '核算周期',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "日核算", value: "DAILY" }, { label: "月核算", value: "MONTHLY" }, { label: "年核算", value: "YEARLY" }],
        placeholder: '请选择核算周期',
      },
    },
    {
      fieldName: 'energyType',
      label: '能源类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "电力", value: "ELECTRICITY" }, { label: "原煤", value: "COAL" }, { label: "天然气", value: "NATURAL_GAS" }, { label: "柴油", value: "DIESEL" }, { label: "汽油", value: "GASOLINE" }, { label: "蒸汽", value: "STEAM" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择能源类型',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetCarbonEmissionApi.CarbonEmission>['columns'] {
  return [
    { field: 'calcNo', title: '核算批次号', minWidth: 150 },
    { field: 'periodType', title: '核算周期', minWidth: 150 },
    { field: 'periodStart', title: '周期开始日期', width: 130 },
    { field: 'periodEnd', title: '周期结束日期', width: 130 },
    { field: 'energyType', title: '能源类型', minWidth: 150 },
    { field: 'consumption', title: '能源消耗量', width: 130 },
    { field: 'carbonEmission', title: '碳排放量=consumption*factor', width: 130 },
    { field: 'totalEmission', title: '合计', width: 130 },
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