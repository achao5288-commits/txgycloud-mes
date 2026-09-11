import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyPlanApi } from '#/api/mes/safetyEnv/emergencyplan';

/** 预案类型文案 */
export const PLAN_TYPE_MAP: Record<string, string> = {
  COMPREHENSIVE: '综合应急预案',
  SPECIAL: '专项应急预案',
  ONSITE: '现场处置方案',
};

/** 状态文案 + 标签色 */
export const PLAN_STATUS_MAP: Record<string, { color: string; text: string }> = {
  DRAFT: { color: 'default', text: '草稿' },
  PUBLISHED: { color: 'processing', text: '已发布' },
  FILED: { color: 'success', text: '已备案' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'planNo',
      label: '预案编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入预案编号',
      },
    },
    {
      fieldName: 'planName',
      label: '预案名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入预案名称',
      },
    },
    {
      fieldName: 'planType',
      label: '预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)',
      },
    },
    {
      fieldName: 'status',
      label: '状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEmergencyPlanApi.EmergencyPlan>['columns'] {
  return [
    { field: 'planNo', title: '预案编号', minWidth: 170, showOverflow: true },
    { field: 'planName', title: '预案名称', minWidth: 170, showOverflow: true },
    { field: 'planType', title: '预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)', minWidth: 170, showOverflow: true , slots: { default: 'planType' } },
    { field: 'version', title: '版本号', minWidth: 170, showOverflow: true },
    { field: 'publishDate', title: '发布日期（备案时限与评估周期的起算点）', width: 120 },
    { field: 'filingDeadline', title: '备案截止日（发布 + 20 个工作日，派生值不进表单）', width: 120 },
    { field: 'filingNo', title: '备案号（报生态环境部门后登记）', minWidth: 170, showOverflow: true },
    { field: 'nextReviewDate', title: '下次评估修订日期（发布或上次修订 + 3 年，派生值不进表单）', width: 120 },
    { field: 'status', title: '状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)', minWidth: 170, showOverflow: true , slots: { default: 'status' } },
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
      fieldName: 'planNo',
      label: '预案编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入预案编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'planName',
      label: '预案名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入预案名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'planType',
      label: '预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)',
      },
      rules: 'required',
    },
    {
      fieldName: 'version',
      label: '版本号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入版本号',
      },
    },
    {
      fieldName: 'publishDate',
      label: '发布日期（备案时限与评估周期的起算点）',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'filingDeadline',
      label: '备案截止日（发布 + 20 个工作日，派生值不进表单）',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'filingNo',
      label: '备案号（报生态环境部门后登记）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入备案号（报生态环境部门后登记）',
      },
    },
    {
      fieldName: 'filingDate',
      label: '备案日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'attachUrl',
      label: '预案附件地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入预案附件地址',
      },
    },
    {
      fieldName: 'lastReviewDate',
      label: '上次评估修订日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'nextReviewDate',
      label: '下次评估修订日期（发布或上次修订 + 3 年，派生值不进表单）',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'reviewReason',
      label: '修订原因（工艺/物料/法规变化）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入修订原因（工艺/物料/法规变化）',
      },
    },
    {
      fieldName: 'status',
      label: '状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)',
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
