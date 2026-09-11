import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetStandardApi } from '#/api/mes/safetyEnv/standard';

/** 检测域：SAFETY/ENV/HEALTH选项 */
export const DOMAIN_OPTIONS = [
  { label: 'EXHAUST', value: 'EXHAUST' },
  { label: '废气', value: 'GAS' },
  { label: '职业健康', value: 'HEALTH' },
];

/** 检测域：SAFETY/ENV/HEALTH文案 */
export const DOMAIN_MAP: Record<string, { text: string; color: string }> = {
  EXHAUST: { text: 'EXHAUST', color: 'success' },
  GAS: { text: '废气', color: 'error' },
  HEALTH: { text: '职业健康', color: 'warning' },
};

/** 周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT选项 */
export const PERIOD_TYPE_OPTIONS = [
  { label: 'YEAR', value: 'YEAR' },
  { label: '年', value: 'YEARLY' },
];

/** 周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT文案 */
export const PERIOD_TYPE_MAP: Record<string, { text: string; color: string }> = {
  YEAR: { text: 'YEAR', color: 'success' },
  YEARLY: { text: '年', color: 'error' },
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
      label: '检测域：SAFETY/ENV/HEALTH',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: DOMAIN_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: PERIOD_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'status',
      label: '状态：DRAFT/ACTIVE/OBSOLETE',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入状态：DRAFT/ACTIVE/OBSOLETE',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetStandardApi.Standard>['columns'] {
  return [
    { field: 'standardNo', title: '标准编号', minWidth: 170, showOverflow: true },
    { field: 'standardName', title: '标准名称', minWidth: 170, showOverflow: true },
    { field: 'domain', title: '检测域：SAFETY/ENV/HEALTH', minWidth: 170, showOverflow: true, slots: { default: 'domain' } },
    { field: 'testType', title: '检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等', minWidth: 170, showOverflow: true },
    { field: 'refStandard', title: '引用国标编号（GBZ/GB/T）', minWidth: 170, showOverflow: true },
    { field: 'method', title: '检测方法描述', minWidth: 170, showOverflow: true },
    { field: 'periodType', title: '周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT', minWidth: 170, showOverflow: true, slots: { default: 'periodType' } },
    { field: 'applicableArea', title: '适用区域/工序(JSON文本)', minWidth: 170, showOverflow: true },
    { field: 'status', title: '状态：DRAFT/ACTIVE/OBSOLETE', minWidth: 170, showOverflow: true },
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
      label: '检测域：SAFETY/ENV/HEALTH',
      component: 'Select',
      componentProps: {
        options: DOMAIN_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等',
      },
    },
    {
      fieldName: 'refStandard',
      label: '引用国标编号（GBZ/GB/T）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用国标编号（GBZ/GB/T）',
      },
    },
    {
      fieldName: 'limitsConfig',
      label: '限值配置(JSON文本，如 {CO:{mac,pcTWA,unit}})',
      component: 'Textarea',
      componentProps: {
        rows: 3,
        placeholder: '请输入限值配置(JSON文本，如 {CO:{mac,pcTWA,unit}})',
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
      label: '周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT',
      component: 'Select',
      componentProps: {
        options: PERIOD_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'triggerConfig',
      label: '事件触发配置(JSON文本)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入事件触发配置(JSON文本)',
      },
    },
    {
      fieldName: 'applicableArea',
      label: '适用区域/工序(JSON文本)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入适用区域/工序(JSON文本)',
      },
    },
    {
      fieldName: 'status',
      label: '状态：DRAFT/ACTIVE/OBSOLETE',
      component: 'Input',
      componentProps: {
        placeholder: '请输入状态：DRAFT/ACTIVE/OBSOLETE',
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
