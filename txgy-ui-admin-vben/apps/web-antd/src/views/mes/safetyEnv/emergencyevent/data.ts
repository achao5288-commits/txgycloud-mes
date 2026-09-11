import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyEventApi } from '#/api/mes/safetyEnv/emergencyevent';

/** 事件类型文案 */
export const EVENT_TYPE_MAP: Record<string, string> = {
  LEAK: '泄漏',
  EXCEED: '超标',
  FACILITY_FAULT: '设施故障',
  OTHER: '其他',
};

/** 泄漏场景文案 */
export const SCENARIO_MAP: Record<string, string> = {
  MDI_LEAK: '黑料（MDI）泄漏',
  THINNER_LEAK: '稀释剂泄漏',
  WASTE_OIL_LEAK: '废机油泄漏',
};

/** 状态文案 + 标签色（§291 状态机：上报→处置中→待报告→闭环） */
export const EVENT_STATUS_MAP: Record<string, { color: string; text: string }> = {
  REPORTED: { color: 'error', text: '已上报' },
  DISPOSING: { color: 'warning', text: '处置中' },
  PENDING_REPORT: { color: 'processing', text: '待报告' },
  CLOSED: { color: 'success', text: '已闭环' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'eventNo',
      label: '事件编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入事件编号',
      },
    },
    {
      fieldName: 'eventType',
      label: '事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)',
      },
    },
    {
      fieldName: 'location',
      label: '发生地点',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入发生地点',
      },
    },
    {
      fieldName: 'status',
      label: '状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEmergencyEventApi.EmergencyEvent>['columns'] {
  return [
    { field: 'eventNo', title: '事件编号', minWidth: 170, showOverflow: true },
    { field: 'eventType', title: '事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)', minWidth: 170, showOverflow: true , slots: { default: 'eventType' } },
    { field: 'scenario', title: '泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK', minWidth: 170, showOverflow: true , slots: { default: 'scenario' } },
    { field: 'occurTime', title: '发生时间', width: 120 },
    { field: 'location', title: '发生地点', minWidth: 170, showOverflow: true },
    { field: 'chemicalCode', title: '涉事化学品编码（关联危化品档案取 MSDS/禁配）', minWidth: 170, showOverflow: true },
    { field: 'leakQuantity', title: '泄漏量', width: 120 },
    { field: 'handler', title: '处置人（派发时指定）', minWidth: 170, showOverflow: true },
    { field: 'status', title: '状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)', minWidth: 170, showOverflow: true , slots: { default: 'status' } },
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
      fieldName: 'eventNo',
      label: '事件编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入事件编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'eventType',
      label: '事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)',
      },
      rules: 'required',
    },
    {
      fieldName: 'scenario',
      label: '泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK',
      component: 'Input',
      componentProps: {
        placeholder: '请输入泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK',
      },
    },
    {
      fieldName: 'occurTime',
      label: '发生时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'location',
      label: '发生地点',
      component: 'Input',
      componentProps: {
        placeholder: '请输入发生地点',
      },
    },
    {
      fieldName: 'chemicalCode',
      label: '涉事化学品编码（关联危化品档案取 MSDS/禁配）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入涉事化学品编码（关联危化品档案取 MSDS/禁配）',
      },
    },
    {
      fieldName: 'leakQuantity',
      label: '泄漏量',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入泄漏量',
        class: 'w-full',
      },
    },
    {
      fieldName: 'impactScope',
      label: '影响范围',
      component: 'Input',
      componentProps: {
        placeholder: '请输入影响范围',
      },
    },
    {
      fieldName: 'reportUser',
      label: '上报人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入上报人',
      },
    },
    {
      fieldName: 'reportTime',
      label: '上报时间（PDA 一键上报）',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'handler',
      label: '处置人（派发时指定）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入处置人（派发时指定）',
      },
    },
    {
      fieldName: 'dispatchTime',
      label: '派发时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'disposeNote',
      label: '处置说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入处置说明',
      },
    },
    {
      fieldName: 'disposePhotoUrl',
      label: '处置照片',
      component: 'Input',
      componentProps: {
        placeholder: '请输入处置照片',
      },
    },
    {
      fieldName: 'wasteCount',
      label: '应急废物桶数（处置时按明细回填，非人工填写）',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入应急废物桶数（处置时按明细回填，非人工填写）',
        class: 'w-full',
      },
    },
    {
      fieldName: 'wasteQuantity',
      label: '应急废物合计净重 kg（处置时按明细回填，非人工填写）',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入应急废物合计净重 kg（处置时按明细回填，非人工填写）',
        class: 'w-full',
      },
    },
    {
      fieldName: 'reportContent',
      label: '事件报告（原因/数量/处置/整改）',
      component: 'Input',
      componentProps: {
        placeholder: '请输入事件报告（原因/数量/处置/整改）',
      },
    },
    {
      fieldName: 'approver',
      label: '负责人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入负责人',
      },
    },
    {
      fieldName: 'approveTime',
      label: '签字时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'closedTime',
      label: '闭环时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'status',
      label: '状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)',
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
