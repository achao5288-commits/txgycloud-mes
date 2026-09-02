import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPlanApi } from '#/api/mes/safetyEnv/plan';


/** 新增/修改检测计划管理的表单 */
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
      fieldName: 'planNo',
      label: '计划编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入计划编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'planName',
      label: '计划名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入计划名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'planType',
      label: '触发类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "周期触发", value: "PERIODIC" }, { label: "事件触发", value: "EVENT" }],
        placeholder: '请选择触发类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型(周期型)',
      component: 'Select',
      componentProps: {
        options: [{ label: "每日", value: "DAILY" }, { label: "每周", value: "WEEKLY" }, { label: "每月", value: "MONTHLY" }, { label: "每季度", value: "QUARTERLY" }, { label: "每年", value: "YEARLY" }],
        placeholder: '请选择周期类型(周期型)',
        allowClear: true,
      },
    },
    {
      fieldName: 'startDate',
      label: '生效开始日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'endDate',
      label: '生效结束日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "草稿", value: "DRAFT" }, { label: "启用", value: "ACTIVE" }, { label: "已停用", value: "STOPPED" }],
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
      fieldName: 'planName',
      label: '计划名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入计划名称',
      },
    },
    {
      fieldName: 'planType',
      label: '触发类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "周期触发", value: "PERIODIC" }, { label: "事件触发", value: "EVENT" }],
        placeholder: '请选择触发类型',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "草稿", value: "DRAFT" }, { label: "启用", value: "ACTIVE" }, { label: "已停用", value: "STOPPED" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPlanApi.Plan>['columns'] {
  return [
    { field: 'planNo', title: '计划编号', minWidth: 150 },
    { field: 'planName', title: '计划名称', minWidth: 150 },
    { field: 'planType', title: '触发类型', minWidth: 150 },
    { field: 'periodType', title: '周期类型(周期型)', minWidth: 150 },
    { field: 'startDate', title: '生效开始日期', width: 130 },
    { field: 'endDate', title: '生效结束日期', width: 130 },
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