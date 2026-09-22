import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPpeCheckApi } from '#/api/mes/safetyEnv/ppecheck';

/** 检查方式：AI_VISION/MANUAL选项 */
export const CHECK_MODE_OPTIONS = [
  { label: 'AI 识别', value: 'AI_VISION' },
  { label: '手工', value: 'MANUAL' },
];

/** 检查方式：AI_VISION/MANUAL文案 */
export const CHECK_MODE_MAP: Record<string, { color: string; text: string; }> = {
  AI_VISION: { text: 'AI 识别', color: 'success' },
  MANUAL: { text: '手工', color: 'error' },
};

/** 结果：PASS/FAIL选项 */
export const RESULT_OPTIONS = [
  { label: '超标', value: 'FAIL' },
  { label: '达标', value: 'PASS' },
];

/** 结果：PASS/FAIL文案 */
export const RESULT_MAP: Record<string, { color: string; text: string; }> = {
  FAIL: { text: '超标', color: 'error' },
  PASS: { text: '达标', color: 'success' },
};

export const PPE_TYPE_MAP: Record<string, { color: string; text: string; }> = {
  HELMET: { text: '安全帽', color: 'processing' },
  GOGGLES: { text: '护目镜', color: 'processing' },
  RESPIRATOR: { text: '防毒面具', color: 'warning' },
  DUST_MASK: { text: '防尘口罩', color: 'warning' },
  ANTISTATIC_CLOTHING: { text: '防静电服', color: 'blue' },
  EARPLUG: { text: '耳塞', color: 'purple' },
  EARPLUGS: { text: '耳塞', color: 'purple' },
  GLOVES: { text: '手套', color: 'default' },
  SAFETY_SHOES: { text: '安全鞋', color: 'default' },
  CHEM_SUIT: { text: '防化服', color: 'cyan' },
  GAS_MASK: { text: '防毒面具', color: 'warning' },
};

export const YES_NO_MAP: Record<string, { color: string; text: string; }> = {
  1: { text: '是', color: 'success' },
  0: { text: '否', color: 'default' },
};

export const BLOCK_FLAG_MAP: Record<string, { color: string; text: string; }> = {
  1: { text: '阻断开工', color: 'error' },
  0: { text: '不阻断', color: 'default' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号',
      },
    },
    {
      fieldName: 'ppeType',
      label: 'PPE类别',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入PPE类别',
      },
    },
    {
      fieldName: 'checkMode',
      label: '检查方式',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: CHECK_MODE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPpeCheckApi.PpeCheck>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 170, showOverflow: true },
    { field: 'empId', title: '关联人员编号(佩戴校验对象)', width: 120 },
    { field: 'ppeType', title: 'PPE类别', minWidth: 170, showOverflow: true, slots: { default: 'ppeType' } },
    { field: 'checkMode', title: '检查方式', minWidth: 170, showOverflow: true, slots: { default: 'checkMode' } },
    { field: 'wearingOk', title: '佩戴完整性', width: 120, slots: { default: 'wearingOk' } },
    { field: 'gradeMatchOk', title: '防护等级匹配性', width: 120, slots: { default: 'gradeMatchOk' } },
    { field: 'validOk', title: '有效期/损坏检查', width: 120, slots: { default: 'validOk' } },
    { field: 'expiryDate', title: 'PPE到期日期', width: 120 },
    { field: 'result', title: '结果', minWidth: 170, showOverflow: true, slots: { default: 'result' } },
    { field: 'blockFlag', title: '是否阻断开工', width: 120, slots: { default: 'blockFlag' } },
    { field: 'checkTime', title: '检查时间', width: 120 },
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
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入记录编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'empId',
      label: '关联人员编号(佩戴校验对象)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联人员编号(佩戴校验对象)',
      },
      rules: 'required',
    },
    {
      fieldName: 'woId',
      label: '关联工单编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工单编号',
      },
    },
    {
      fieldName: 'operationId',
      label: '关联工序编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联工序编号',
      },
    },
    {
      fieldName: 'ppeType',
      label: 'PPE类别',
      component: 'Input',
      componentProps: {
        placeholder: '请输入PPE类别',
      },
      rules: 'required',
    },
    {
      fieldName: 'checkMode',
      label: '检查方式',
      component: 'Select',
      componentProps: {
        options: CHECK_MODE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'wearingOk',
      label: '佩戴完整性',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'gradeMatchOk',
      label: '防护等级匹配性',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'validOk',
      label: '有效期/损坏检查',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'expiryDate',
      label: 'PPE到期日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        options: RESULT_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'blockFlag',
      label: '是否阻断开工',
      component: 'Switch',
      componentProps: {
        checkedValue: true,
        unCheckedValue: false,
      },
    },
    {
      fieldName: 'deviceNo',
      label: 'AI识别设备编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入AI识别设备编号',
      },
    },
    {
      fieldName: 'checkerName',
      label: '检查人(人工检查时)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检查人(人工检查时)',
      },
    },
    {
      fieldName: 'checkTime',
      label: '检查时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '选择时间',
      },
      rules: 'required',
    },
    {
      fieldName: 'photoUrls',
      label: '检查照片/AI抓拍',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检查照片/AI抓拍',
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
