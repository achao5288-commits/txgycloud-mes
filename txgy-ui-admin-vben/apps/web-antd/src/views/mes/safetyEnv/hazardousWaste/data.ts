import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetHazardousWasteApi } from '#/api/mes/safetyEnv/hazardousWaste';


/** 新增/修改危险废物管理的表单 */
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
      fieldName: 'manifestNo',
      label: '转移联单编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入转移联单编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'wasteCode',
      label: '危废代码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危废代码',
      },
    },
    {
      fieldName: 'wasteName',
      label: '危废名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危废名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'quantity',
      label: '数量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
    },
    {
      fieldName: 'quantityUnit',
      label: '数量单位',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入数量单位',
      },
    },
    {
      fieldName: 'stage',
      label: '状态阶段',
      component: 'Select',
      componentProps: {
        options: [{ label: "已产生", value: "GENERATED" }, { label: "已贮存", value: "STORED" }, { label: "已转移", value: "TRANSFERRED" }, { label: "已处置", value: "DISPOSED" }],
        placeholder: '请选择状态阶段',
        allowClear: true,
      },
    },
    {
      fieldName: 'storageLocation',
      label: '储存地点',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入储存地点',
      },
    },
    {
      fieldName: 'counterparty',
      label: '接收方/处置方',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入接收方/处置方',
      },
    },
    {
      fieldName: 'handleTime',
      label: '处理时间',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '请选择时间',
        showTime: true,
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'handler',
      label: '处理人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入处理人',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "草稿", value: "DRAFT" }, { label: "已审批", value: "APPROVED" }, { label: "已驳回", value: "REJECTED" }],
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
      fieldName: 'wasteCode',
      label: '危废代码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危废代码',
      },
    },
    {
      fieldName: 'wasteName',
      label: '危废名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入危废名称',
      },
    },
    {
      fieldName: 'stage',
      label: '状态阶段',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "已产生", value: "GENERATED" }, { label: "已贮存", value: "STORED" }, { label: "已转移", value: "TRANSFERRED" }, { label: "已处置", value: "DISPOSED" }],
        placeholder: '请选择状态阶段',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "草稿", value: "DRAFT" }, { label: "已审批", value: "APPROVED" }, { label: "已驳回", value: "REJECTED" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetHazardousWasteApi.HazardousWaste>['columns'] {
  return [
    { field: 'manifestNo', title: '转移联单编号', minWidth: 150 },
    { field: 'wasteName', title: '危废名称', minWidth: 150 },
    { field: 'wasteCode', title: '危废代码', minWidth: 150 },
    { field: 'quantity', title: '数量', width: 130 },
    { field: 'quantityUnit', title: '数量单位', minWidth: 150 },
    { field: 'stage', title: '状态阶段', minWidth: 150 },
    { field: 'storageLocation', title: '储存地点', minWidth: 150 },
    { field: 'status', title: '状态', minWidth: 150 },
    { field: 'handleTime', title: '处理时间', width: 180, formatter: 'formatDateTime' },
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