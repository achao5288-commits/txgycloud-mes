import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEnvReportApi } from '#/api/mes/safetyEnv/envReport';


/** 新增/修改环保检测报告的表单 */
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
      fieldName: 'reportNo',
      label: '报告编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入报告编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'reportName',
      label: '报告名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入报告名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'reportType',
      label: '报告类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "月度报告", value: "MONTHLY" }, { label: "季度报告", value: "QUARTERLY" }, { label: "年度报告", value: "ANNUAL" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择报告类型',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'reportCategory',
      label: '报告类别',
      component: 'Select',
      componentProps: {
        options: [{ label: "自行监测报告", value: "SELF_MONITOR" }, { label: "合规性报告", value: "COMPLIANCE" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择报告类别',
        allowClear: true,
      },
    },
    {
      fieldName: 'periodStart',
      label: '报告周期开始日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'periodEnd',
      label: '报告周期结束日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'reportDate',
      label: '报告生成日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'dataSummary',
      label: '数据汇总摘要',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入数据汇总摘要',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'fileUrl',
      label: '报告文件 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入报告文件 URL',
      },
    },
    {
      fieldName: 'signUrl',
      label: '签章文件 URL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入签章文件 URL',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "草稿", value: "DRAFT" }, { label: "已提交", value: "SUBMITTED" }, { label: "已审批", value: "APPROVED" }, { label: "已驳回", value: "REJECTED" }],
        placeholder: '请选择状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'auditBy',
      label: '审核人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入审核人',
      },
    },
    {
      fieldName: 'auditTime',
      label: '审核时间',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '请选择时间',
        showTime: true,
        valueFormat: 'x',
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
      fieldName: 'reportType',
      label: '报告类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "月度报告", value: "MONTHLY" }, { label: "季度报告", value: "QUARTERLY" }, { label: "年度报告", value: "ANNUAL" }, { label: "其他", value: "OTHER" }],
        placeholder: '请选择报告类型',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "草稿", value: "DRAFT" }, { label: "已提交", value: "SUBMITTED" }, { label: "已审批", value: "APPROVED" }, { label: "已驳回", value: "REJECTED" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEnvReportApi.EnvReport>['columns'] {
  return [
    { field: 'reportNo', title: '报告编号', minWidth: 150 },
    { field: 'reportName', title: '报告名称', minWidth: 150 },
    { field: 'reportType', title: '报告类型', minWidth: 150 },
    { field: 'reportCategory', title: '报告类别', minWidth: 150 },
    { field: 'reportDate', title: '报告生成日期', width: 130 },
    { field: 'status', title: '状态', minWidth: 150 },
    { field: 'auditBy', title: '审核人', minWidth: 150 },
    { field: 'auditTime', title: '审核时间', width: 180, formatter: 'formatDateTime' },
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