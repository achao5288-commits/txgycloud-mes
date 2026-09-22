import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyDrillApi } from '#/api/mes/safetyEnv/emergencydrill';

/** 演练类型文案 */
export const DRILL_TYPE_MAP: Record<string, string> = {
  COMPREHENSIVE: '综合演练',
  SPECIAL: '专项演练',
  ONSITE: '现场处置演练',
};

/** 整改状态文案 + 标签色 */
export const RECTIFY_STATUS_MAP: Record<string, { color: string; text: string }> = {
  NONE: { color: 'default', text: '无需整改' },
  PENDING: { color: 'warning', text: '待整改' },
  DONE: { color: 'success', text: '已整改' },
};

/** 状态文案 + 标签色 */
export const DRILL_STATUS_MAP: Record<string, { color: string; text: string }> = {
  PLANNED: { color: 'default', text: '已计划' },
  DONE: { color: 'processing', text: '已演练' },
  CLOSED: { color: 'success', text: '已闭环' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'drillNo',
      label: '演练编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入演练编号',
      },
    },
    {
      fieldName: 'drillName',
      label: '演练名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入演练名称',
      },
    },
    {
      fieldName: 'drillType',
      label: '演练类型',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入演练类型',
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
export function useGridColumns(): VxeTableGridOptions<MesSetEmergencyDrillApi.EmergencyDrill>['columns'] {
  return [
    { field: 'drillNo', title: '演练编号', minWidth: 170, showOverflow: true },
    { field: 'drillName', title: '演练名称', minWidth: 170, showOverflow: true },
    { field: 'drillType', title: '演练类型', minWidth: 170, showOverflow: true , slots: { default: 'drillType' } },
    { field: 'drillDate', title: '演练日期', width: 120 },
    { field: 'participantCount', title: '参加人数', width: 120 },
    { field: 'evaluation', title: '演练评估', minWidth: 170, showOverflow: true },
    { field: 'rectifyStatus', title: '整改状态', minWidth: 170, showOverflow: true , slots: { default: 'rectifyStatus' } },
    { field: 'status', title: '状态', minWidth: 170, showOverflow: true , slots: { default: 'status' } },
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
      fieldName: 'drillNo',
      label: '演练编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'drillName',
      label: '演练名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'planId',
      label: '关联预案编号（须为已发布/已备案的预案）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联预案编号（须为已发布/已备案的预案）',
      },
    },
    {
      fieldName: 'planVersion',
      label: '演练时的预案版本快照',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练时的预案版本快照',
      },
    },
    {
      fieldName: 'drillType',
      label: '演练类型',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练类型',
      },
      rules: 'required',
    },
    {
      fieldName: 'drillDate',
      label: '演练日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
      rules: 'required',
    },
    {
      fieldName: 'participantCount',
      label: '参加人数',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入参加人数',
        class: 'w-full',
      },
    },
    {
      fieldName: 'participants',
      label: '参加人员',
      component: 'Input',
      componentProps: {
        placeholder: '请输入参加人员',
      },
    },
    {
      fieldName: 'signSheetUrl',
      label: '签到表附件',
      component: 'Input',
      componentProps: {
        placeholder: '请输入签到表附件',
      },
    },
    {
      fieldName: 'photoUrl',
      label: '演练照片',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练照片',
      },
    },
    {
      fieldName: 'videoUrl',
      label: '演练视频',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练视频',
      },
    },
    {
      fieldName: 'evaluation',
      label: '演练评估',
      component: 'Input',
      componentProps: {
        placeholder: '请输入演练评估',
      },
    },
    {
      fieldName: 'rectifyRequirement',
      label: '整改要求',
      component: 'Input',
      componentProps: {
        placeholder: '请输入整改要求',
      },
    },
    {
      fieldName: 'rectifyStatus',
      label: '整改状态',
      component: 'Input',
      componentProps: {
        placeholder: '请输入整改状态',
      },
    },
    {
      fieldName: 'rectifyDoneDate',
      label: '整改完成日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'closedDate',
      label: '闭环日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
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
