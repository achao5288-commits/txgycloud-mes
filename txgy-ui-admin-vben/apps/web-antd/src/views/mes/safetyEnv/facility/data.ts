import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

/** 设施类型：只列设计文档 §6.3 / §3.3 点名的三类，与后端 FACILITY_TYPES 同一份口径 */
export const FACILITY_TYPE_OPTIONS = [
  { label: '活性炭吸附', value: 'ACTIVATED_CARBON' },
  { label: '催化燃烧', value: 'CATALYTIC_COMBUSTION' },
  { label: '布袋除尘', value: 'BAG_FILTER' },
];

export const FACILITY_TYPE_MAP: Record<string, string> = Object.fromEntries(
  FACILITY_TYPE_OPTIONS.map((o) => [o.value, o.label]),
);

export const RUN_STATUS_OPTIONS = [
  { label: '运行', value: 'RUNNING' },
  { label: '停运', value: 'STOPPED' },
];

export const SHUTDOWN_STATUS_OPTIONS = [
  { label: '无', value: 'NONE' },
  { label: '待审批', value: 'PENDING' },
  { label: '已批准', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
];

export const SHUTDOWN_STATUS_MAP: Record<string, string> = Object.fromEntries(
  SHUTDOWN_STATUS_OPTIONS.map((o) => [o.value, o.label]),
);

export const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '停用', value: 'DISABLED' },
];

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'facilityNo',
      label: '设施编号',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '请输入设施编号' },
    },
    {
      fieldName: 'facilityName',
      label: '设施名称',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '请输入设施名称' },
    },
    {
      fieldName: 'facilityType',
      label: '设施类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: FACILITY_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'outletCode',
      label: '排放口',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '请输入排放口编号' },
    },
    {
      fieldName: 'runStatus',
      label: '运行状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RUN_STATUS_OPTIONS,
        placeholder: '请选择',
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
export function useGridColumns(): VxeTableGridOptions<MesFacilityApi.TreatmentFacility>['columns'] {
  return [
    { field: 'facilityNo', title: '设施编号', minWidth: 140, showOverflow: true },
    { field: 'facilityName', title: '设施名称', minWidth: 180, showOverflow: true },
    {
      field: 'facilityType',
      title: '类型',
      minWidth: 120,
      slots: { default: 'facilityType' },
    },
    { field: 'lineCode', title: '产线', minWidth: 130, showOverflow: true },
    { field: 'outletCode', title: '排放口', minWidth: 130, showOverflow: true },
    {
      field: 'runStatus',
      title: '运行状态',
      width: 110,
      slots: { default: 'runStatus' },
    },
    {
      field: 'shutdownStatus',
      title: '停运申报',
      width: 110,
      slots: { default: 'shutdownStatus' },
    },
    {
      field: 'nextReplaceDate',
      title: '下次更换',
      width: 130,
      slots: { default: 'nextReplace' },
    },
    {
      field: 'status',
      title: '状态',
      width: 90,
      slots: { default: 'status' },
    },
    {
      title: '操作',
      width: 300,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 新增/编辑表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'facilityNo',
      label: '设施编号',
      component: 'Input',
      componentProps: { placeholder: '请输入设施编号' },
      rules: 'required',
    },
    {
      fieldName: 'facilityName',
      label: '设施名称',
      component: 'Input',
      componentProps: { placeholder: '请输入设施名称' },
      rules: 'required',
    },
    {
      fieldName: 'facilityType',
      label: '设施类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: FACILITY_TYPE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'lineCode',
      label: '对应产线',
      component: 'Input',
      componentProps: { placeholder: '同开同停按产线判定，建议必填' },
    },
    {
      fieldName: 'outletCode',
      label: '对应排放口',
      component: 'Input',
      componentProps: { placeholder: '请输入排放口编号' },
    },
    {
      fieldName: 'designAirVolume',
      label: '设计风量',
      component: 'InputNumber',
      componentProps: { min: 0, class: 'w-full', placeholder: 'm³/h' },
    },
    {
      fieldName: 'consumableName',
      label: '耗材名称',
      component: 'Input',
      componentProps: { placeholder: '如：活性炭' },
    },
    {
      fieldName: 'replaceCycleDays',
      label: '更换周期(天)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        class: 'w-full',
        placeholder: '留空则不算到期时间',
      },
    },
    {
      fieldName: 'lastReplaceDate',
      label: '上次更换日',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
        class: 'w-full',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: STATUS_OPTIONS,
        placeholder: '请选择',
        class: 'w-full',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: { rows: 3, placeholder: '请输入备注' },
    },
    // 下次更换日不放进表单：它 = 上次更换日 + 周期，由后端算，手填只会填出一个跟日志对不上的日期
  ];
}
