import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetPpeCheckApi } from '#/api/mes/safetyEnv/ppeCheck';

import { getRangePickerDefaultProps } from '#/utils';

/** 检测结论 PASS/FAIL */
const RESULT_OPTIONS = [
  { label: '合格', value: 'PASS' },
  { label: '不合格', value: 'FAIL' },
];

/** 新增/修改PPE防护检查的表单 */
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
      fieldName: 'recordNo',
      label: '记录编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'empId',
      label: '关联人员编号',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入',
        precision: 2,
      },
      rules: 'required',
    },
    {
      fieldName: 'ppeType',
      label: 'PPE类别',
      component: 'Select',
      componentProps: {
        options: [{ label: "头部防护", value: "头部防护" }, { label: "呼吸防护", value: "呼吸防护" }, { label: "眼面防护", value: "眼面防护" }, { label: "手部防护", value: "手部防护" }, { label: "足部防护", value: "足部防护" }, { label: "身体防护", value: "身体防护" }, { label: "坠落防护", value: "坠落防护" }, { label: "其他", value: "其他" }],
        placeholder: '请选择PPE类别',
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'checkMode',
      label: '检查方式',
      component: 'Select',
      componentProps: {
        options: [{ label: "AI视觉识别", value: "AI_VISION" }, { label: "人工检查", value: "MANUAL" }],
        placeholder: '请选择检查方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'wearingOk',
      label: '佩戴完整性',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择佩戴完整性',
        allowClear: true,
      },
    },
    {
      fieldName: 'gradeMatchOk',
      label: '防护等级匹配性',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择防护等级匹配性',
        allowClear: true,
      },
    },
    {
      fieldName: 'validOk',
      label: '有效期/损坏检查',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择有效期/损坏检查',
        allowClear: true,
      },
    },
    {
      fieldName: 'expiryDate',
      label: 'PPE到期日期',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD',
        placeholder: '请选择日期',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'result',
      label: '综合结论',
      component: 'Select',
      componentProps: {
        options: RESULT_OPTIONS,
        placeholder: '请选择综合结论',
        allowClear: true,
      },
    },
    {
      fieldName: 'blockFlag',
      label: '是否阻断开工',
      component: 'Select',
      componentProps: {
        options: [{ label: "是", value: 1 }, { label: "否", value: 0 }],
        placeholder: '请选择是否阻断开工',
        allowClear: true,
      },
    },
    {
      fieldName: 'deviceNo',
      label: 'AI识别设备编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入AI识别设备编号',
      },
    },
    {
      fieldName: 'checkerName',
      label: '检查人(人工检查时)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检查人(人工检查时)',
      },
    },
    {
      fieldName: 'checkTime',
      label: '检查时间',
      component: 'DatePicker',
      componentProps: {
        format: 'YYYY-MM-DD HH:mm:ss',
        placeholder: '请选择时间',
        showTime: true,
        valueFormat: 'x',
      },
      rules: 'required',
    },
    {
      fieldName: 'photoUrls',
      label: '检查照片/AI抓拍URL',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入检查照片/AI抓拍URL',
        rows: 2,
      },
      formItemClass: 'col-span-3',
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
      fieldName: 'ppeType',
      label: 'PPE类别',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "头部防护", value: "头部防护" }, { label: "呼吸防护", value: "呼吸防护" }, { label: "眼面防护", value: "眼面防护" }, { label: "手部防护", value: "手部防护" }, { label: "足部防护", value: "足部防护" }, { label: "身体防护", value: "身体防护" }, { label: "坠落防护", value: "坠落防护" }, { label: "其他", value: "其他" }],
        placeholder: '请选择PPE类别',
      },
    },
    {
      fieldName: 'result',
      label: '结果',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: RESULT_OPTIONS,
        placeholder: '请选择结果',
      },
    },
    {
      fieldName: 'checkTime',
      label: '检查时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetPpeCheckApi.PpeCheck>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号', minWidth: 150 },
    { field: 'empId', title: '关联人员编号', width: 130 },
    { field: 'ppeType', title: 'PPE类别', minWidth: 150 },
    { field: 'checkMode', title: '检查方式', minWidth: 150 },
    { field: 'wearingOk', title: '佩戴完整性', width: 130 },
    { field: 'expiryDate', title: 'PPE到期日期', width: 130 },
    { field: 'result', title: '综合结论', width: 110, slots: { default: 'result' } },
    { field: 'checkTime', title: '检查时间', width: 180, formatter: 'formatDateTime' },
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