import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEnvReportApi } from '#/api/mes/safetyEnv/envReport';

/** 报告来源：THIRD_PARTY/INTERNAL选项 */
export const REPORT_TYPE_OPTIONS = [
  { label: '月', value: 'MONTHLY' },
  { label: '季', value: 'QUARTERLY' },
  { label: '第三方', value: 'THIRD_PARTY' },
];

/** 报告来源：THIRD_PARTY/INTERNAL文案 */
export const REPORT_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  MONTHLY: { text: '月', color: 'success' },
  QUARTERLY: { text: '季', color: 'error' },
  THIRD_PARTY: { text: '第三方', color: 'warning' },
};

/** 状态：DRAFT/APPROVED/REJECTED/ARCHIVED选项 */
export const STATUS_OPTIONS = [
  { label: '已通过', value: 'APPROVED' },
  { label: '草稿', value: 'DRAFT' },
];

/** 状态：DRAFT/APPROVED/REJECTED/ARCHIVED文案 */
export const STATUS_MAP: Record<string, { color: string; text: string; }> = {
  APPROVED: { text: '已通过', color: 'success' },
  DRAFT: { text: '草稿', color: 'error' },
};

export const REPORT_CATEGORY_MAP: Record<string, { color: string; text: string; }> = {
  EXHAUST_GAS: { text: '废气', color: 'processing' },
  WASTEWATER: { text: '废水', color: 'blue' },
  NOISE: { text: '噪声', color: 'purple' },
  SOLID_WASTE: { text: '固废', color: 'orange' },
  AMBIENT: { text: '环境空气', color: 'cyan' },
  ENVIRONMENT: { text: '环境', color: 'cyan' },
  SAFETY: { text: '安全', color: 'gold' },
  COMPREHENSIVE: { text: '综合', color: 'default' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'reportNo',
      label: '报告编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入报告编号',
      },
    },
    {
      fieldName: 'reportName',
      label: '报告名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入报告名称',
      },
    },
    {
      fieldName: 'reportType',
      label: '报告来源',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: REPORT_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'reportCategory',
      label: '类别',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入类别',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEnvReportApi.EnvReport>['columns'] {
  return [
    { field: 'reportNo', title: '报告编号', minWidth: 170, showOverflow: true },
    { field: 'reportName', title: '报告名称', minWidth: 170, showOverflow: true },
    { field: 'reportType', title: '报告来源', minWidth: 170, showOverflow: true, slots: { default: 'reportType' } },
    { field: 'reportCategory', title: '类别', minWidth: 170, showOverflow: true, slots: { default: 'reportCategory' } },
    { field: 'periodStart', title: '报告统计期起', width: 120 },
    { field: 'periodEnd', title: '报告统计期止', width: 120 },
    { field: 'reportDate', title: '报告日期', width: 120 },
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
      fieldName: 'reportNo',
      label: '报告编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入报告编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'reportName',
      label: '报告名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入报告名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'reportType',
      label: '报告来源',
      component: 'Select',
      componentProps: {
        options: REPORT_TYPE_OPTIONS,
        placeholder: '请选择',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'reportCategory',
      label: '类别',
      component: 'Input',
      componentProps: {
        placeholder: '请输入类别',
      },
    },
    {
      fieldName: 'templateId',
      label: '复用质检报告模板编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入复用质检报告模板编号',
      },
    },
    {
      fieldName: 'periodStart',
      label: '报告统计期起',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'periodEnd',
      label: '报告统计期止',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'reportDate',
      label: '报告日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'dataSummary',
      label: '检测结果摘要',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入检测结果摘要',
      },
    },
    {
      fieldName: 'fileUrl',
      label: '报告文件',
      component: 'Input',
      componentProps: {
        placeholder: '请输入报告文件',
      },
    },
    {
      fieldName: 'signUrl',
      label: '电子签名文件',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电子签名文件',
      },
    },
    {
      fieldName: 'formId',
      label: '自定义表单配置id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入自定义表单配置id',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'auditBy',
      label: '审核人(终审)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入审核人(终审)',
      },
    },
    {
      fieldName: 'auditTime',
      label: '审核时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
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
