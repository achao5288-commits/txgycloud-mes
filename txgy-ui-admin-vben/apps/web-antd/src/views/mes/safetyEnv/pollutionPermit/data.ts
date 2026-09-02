import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionPermitApi } from '#/api/mes/safetyEnv/pollutionPermit';


/** 新增/修改排污许可管理的表单 */
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
      fieldName: 'permitNo',
      label: '排污许可证编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排污许可证编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'enterpriseName',
      label: '企业名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入企业名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'issuingAuthority',
      label: '发证机关',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入发证机关',
      },
    },
    {
      fieldName: 'issueDate',
      label: '发证日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'startDate',
      label: '有效期开始日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'endDate',
      label: '有效期截止日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'outletCodes',
      label: '排污口编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排污口编码',
      },
    },
    {
      fieldName: 'annualLimits',
      label: '年度许可排放量',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入年度许可排放量',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'annualReports',
      label: '年度执行报告记录',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入年度执行报告记录',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "有效", value: "VALID" }, { label: "即将到期", value: "EXPIRING" }, { label: "已失效", value: "EXPIRED" }, { label: "已注销", value: "REVOKED" }],
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
      fieldName: 'permitNo',
      label: '排污许可证编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入排污许可证编号',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "有效", value: "VALID" }, { label: "即将到期", value: "EXPIRING" }, { label: "已失效", value: "EXPIRED" }, { label: "已注销", value: "REVOKED" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPollutionPermitApi.PollutionPermit>['columns'] {
  return [
    { field: 'permitNo', title: '排污许可证编号', minWidth: 150 },
    { field: 'enterpriseName', title: '企业名称', minWidth: 150 },
    { field: 'issuingAuthority', title: '发证机关', minWidth: 150 },
    { field: 'issueDate', title: '发证日期', width: 130 },
    { field: 'startDate', title: '有效期开始日期', width: 130 },
    { field: 'endDate', title: '有效期截止日期', width: 130 },
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