import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPollutionPermitApi } from '#/api/mes/safetyEnv/pollutionPermit';

/** 许可证状态选项 */
export const PERMIT_STATUS_OPTIONS = [
  { label: '有效', value: 'ACTIVE' },
  { label: '已过期', value: 'EXPIRED' },
  { label: '已注销', value: 'REVOKED' },
];

/** 许可证状态文案 */
export const PERMIT_STATUS_MAP: Record<string, { text: string; color: string }> = {
  ACTIVE: { text: '有效', color: 'success' },
  EXPIRED: { text: '已过期', color: 'error' },
  REVOKED: { text: '已注销', color: 'default' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'permitNo',
      label: '许可证编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '如 P-110108',
      },
    },
    {
      fieldName: 'enterpriseName',
      label: '持证单位',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入持证单位名称',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: PERMIT_STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPollutionPermitApi.Permit>['columns'] {
  return [
    { field: 'permitNo', title: '许可证编号', minWidth: 170, showOverflow: true },
    { field: 'enterpriseName', title: '持证单位', minWidth: 180, showOverflow: true },
    { field: 'issuingAuthority', title: '发证机关', minWidth: 150, showOverflow: true },
    { field: 'startDate', title: '有效期起始', width: 120 },
    { field: 'endDate', title: '有效期止', width: 120 },
    { field: 'outletCodes', title: '排放口', minWidth: 130, showOverflow: true },
    { field: 'status', title: '状态', width: 90, slots: { default: 'status' } },
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
      fieldName: 'permitNo',
      label: '许可证编号',
      component: 'Input',
      componentProps: {
        placeholder: '如 P-110108-2026-0001',
      },
      rules: 'required',
    },
    {
      fieldName: 'enterpriseName',
      label: '持证单位',
      component: 'Input',
      componentProps: {
        placeholder: '请输入持证单位名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'issuingAuthority',
      label: '发证机关',
      component: 'Input',
      componentProps: {
        placeholder: '如 北京市生态环境局',
      },
    },
    {
      fieldName: 'issueDate',
      label: '发证日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择发证日期',
      },
    },
    {
      fieldName: 'startDate',
      label: '有效期起始',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择起始日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'endDate',
      label: '有效期止',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择截止日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'outletCodes',
      label: '排放口',
      component: 'Input',
      componentProps: {
        placeholder: '逗号分隔，如 DA001,DA002',
      },
      rules: 'required',
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: PERMIT_STATUS_OPTIONS,
        placeholder: '默认有效',
      },
    },
    {
      fieldName: 'annualLimits',
      label: '许可年排放总量(JSON)',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      componentProps: {
        rows: 3,
        placeholder: '[{"pollutantCode":"VOCs","pollutantName":"挥发性有机物","annualLimitT":12,"annualUsedT":0}]',
      },
    },
    {
      fieldName: 'annualReports',
      label: '执行报告配置(JSON)',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      componentProps: {
        rows: 2,
        placeholder: '[{"reportType":"QUARTERLY","periodStart":"Q1","periodEnd":"2026-03-31"}]',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      componentProps: {
        rows: 2,
        placeholder: '证载变更记录等',
      },
    },
  ];
}
