import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetStandardApi } from '#/api/mes/safetyEnv/standard';

/** 检测域选项 */
export const DOMAIN_OPTIONS = [
  { label: '安全', value: 'SAFETY' },
  { label: '环保', value: 'ENV' },
  { label: '职业健康', value: 'HEALTH' },
];

/** 检测域文案（EXHAUST/GAS 是历史脏值，只留渲染兜底，不再出现在选项里） */
export const DOMAIN_MAP: Record<string, { color: string; text: string; }> = {
  SAFETY: { text: '安全', color: 'error' },
  ENV: { text: '环保', color: 'success' },
  HEALTH: { text: '职业健康', color: 'warning' },
  EXHAUST: { text: '废气', color: 'default' },
  GAS: { text: '废气', color: 'default' },
};

/** 周期类型选项 */
export const PERIOD_TYPE_OPTIONS = [
  { label: '日', value: 'DAILY' },
  { label: '周', value: 'WEEKLY' },
  { label: '月', value: 'MONTHLY' },
  { label: '季', value: 'QUARTERLY' },
  { label: '年', value: 'YEARLY' },
  { label: '事件触发', value: 'EVENT' },
];

/** 周期类型文案（YEAR 是历史脏值，只留渲染兜底） */
export const PERIOD_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  DAILY: { text: '日', color: 'processing' },
  WEEKLY: { text: '周', color: 'processing' },
  MONTHLY: { text: '月', color: 'processing' },
  QUARTERLY: { text: '季', color: 'processing' },
  YEARLY: { text: '年', color: 'processing' },
  EVENT: { text: '事件触发', color: 'warning' },
  YEAR: { text: '年', color: 'default' },
};

export const TEST_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  GAS: { text: '气体检测', color: 'processing' },
  NOISE: { text: '噪声检测', color: 'purple' },
  DUST: { text: '粉尘检测', color: 'orange' },
  RADIATION: { text: '辐射检测', color: 'error' },
  ELECTRICAL: { text: '电气安全检测', color: 'blue' },
  FIRE: { text: '消防检测', color: 'red' },
  CHEMICAL: { text: '化学安全检测', color: 'cyan' },
  PPE: { text: '劳保用品检测', color: 'green' },
  PRESSURE: { text: '压力容器检测', color: 'gold' },
  OCCUPATIONAL: { text: '职业健康检测', color: 'volcano' },
};

export const STATUS_MAP: Record<string, { color: string; text: string; }> = {
  DRAFT: { text: '草稿', color: 'default' },
  ACTIVE: { text: '启用', color: 'success' },
  OBSOLETE: { text: '作废', color: 'error' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'standardNo',
      label: '标准编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入标准编号',
      },
    },
    {
      fieldName: 'standardName',
      label: '标准名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入标准名称',
      },
    },
    {
      fieldName: 'domain',
      label: '检测域',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: DOMAIN_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测类型',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: PERIOD_TYPE_OPTIONS,
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
export function useGridColumns(): VxeTableGridOptions<MesSetStandardApi.Standard>['columns'] {
  return [
    { field: 'standardNo', title: '标准编号', minWidth: 170, showOverflow: true },
    { field: 'standardName', title: '标准名称', minWidth: 170, showOverflow: true },
    { field: 'domain', title: '检测域', minWidth: 170, showOverflow: true, slots: { default: 'domain' } },
    { field: 'testType', title: '检测类型', minWidth: 170, showOverflow: true, slots: { default: 'testType' } },
    { field: 'refStandard', title: '引用国标编号', minWidth: 170, showOverflow: true },
    { field: 'method', title: '检测方法描述', minWidth: 170, showOverflow: true },
    { field: 'periodType', title: '周期类型', minWidth: 170, showOverflow: true, slots: { default: 'periodType' } },
    { field: 'applicableArea', title: '适用区域/工序', minWidth: 170, showOverflow: true },
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
      fieldName: 'standardNo',
      label: '标准编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入标准编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'standardName',
      label: '标准名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入标准名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'domain',
      label: '检测域',
      component: 'Select',
      componentProps: {
        options: DOMAIN_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测类型',
      },
    },
    {
      fieldName: 'refStandard',
      label: '引用国标编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用国标编号',
      },
    },
    {
      fieldName: 'limitsConfig',
      label: '限值配置',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入限值配置',
      },
    },
    {
      fieldName: 'method',
      label: '检测方法描述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测方法描述',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型',
      component: 'Select',
      componentProps: {
        options: PERIOD_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'triggerConfig',
      label: '事件触发配置',
      component: 'Input',
      componentProps: {
        placeholder: '请输入事件触发配置',
      },
    },
    {
      fieldName: 'applicableArea',
      label: '适用区域/工序',
      component: 'Input',
      componentProps: {
        placeholder: '请输入适用区域/工序',
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
