import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesHazwasteApi } from '#/api/mes/safetyEnv/hazwaste';

/** 环节选项（只进不退，顺序即推进顺序） */
export const STAGE_OPTIONS = [
  { label: '产生', value: 'GENERATED' },
  { label: '贮存', value: 'STORED' },
  { label: '转移', value: 'TRANSFERRED' },
  { label: '处置', value: 'DISPOSED' },
];

export const STAGE_MAP: Record<string, { text: string; color: string }> = {
  DISPOSED: { text: '处置', color: 'default' },
  GENERATED: { text: '产生', color: 'warning' },
  STORED: { text: '贮存', color: 'processing' },
  TRANSFERRED: { text: '转移', color: 'error' },
};

/** 联单状态（国家固废系统：草稿→已申报→已生效→已出厂→已归档） */
export const MANIFEST_STATUS_OPTIONS = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已申报', value: 'DECLARED' },
  { label: '已生效', value: 'EFFECTIVE' },
  { label: '已出厂', value: 'TRANSFERRED' },
  { label: '已归档', value: 'CLOSED' },
];

export const MANIFEST_STATUS_MAP: Record<string, { text: string; color: string }> = {
  CLOSED: { text: '已归档', color: 'default' },
  DECLARED: { text: '已申报', color: 'processing' },
  DRAFT: { text: '草稿', color: 'default' },
  EFFECTIVE: { text: '已生效', color: 'success' },
  TRANSFERRED: { text: '已出厂', color: 'error' },
};

/** 会签角色（门卫放行前须先有前三方） */
export const SIGN_ROLE_MAP: Record<string, string> = {
  DRIVER: '押运司机',
  GUARD: '门卫',
  HANDOVER: '移交环保员',
  RECEIVER: '接收经手人',
};

/** 五方确认角色（GENERATOR 的确认＝申报本身） */
export const PARTY_ROLE_MAP: Record<string, string> = {
  CARRIER: '运输方',
  DISPOSER: '处置方',
  RECEIVER_PARTY: '接收方',
  STORAGE: '贮存方',
};

/** 常见危废代码（设计文档锚定的天信真实清单；危险特性由后端按 HW 码带出，未收录则留空） */
export const WASTE_CODE_OPTIONS = [
  { label: 'HW06 废有机溶剂', value: 'HW06' },
  { label: 'HW08 废矿物油', value: 'HW08' },
  { label: 'HW12 漆渣', value: 'HW12' },
  { label: 'HW49 废包装桶/废活性炭', value: 'HW49' },
];

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'manifestNo',
      label: '联单号',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '模糊匹配' },
    },
    {
      fieldName: 'wasteCode',
      label: '危废代码',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: WASTE_CODE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'wasteName',
      label: '危废名称',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '模糊匹配' },
    },
    {
      fieldName: 'stage',
      label: '环节',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STAGE_OPTIONS,
        placeholder: '请选择',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesHazwasteApi.HazardousWaste>['columns'] {
  return [
    { field: 'manifestNo', title: '联单号', minWidth: 180, showOverflow: true },
    { field: 'wasteCode', title: '危废代码', width: 100 },
    { field: 'wasteName', title: '危废名称', minWidth: 140, showOverflow: true },
    {
      field: 'quantity',
      title: '数量',
      width: 110,
      formatter: ({ cellValue, row }) =>
        cellValue == null ? '-' : `${cellValue} ${row.quantityUnit ?? ''}`.trim(),
    },
    { field: 'stage', title: '环节', width: 90, slots: { default: 'stage' } },
    { field: 'containerCode', title: '容器码', minWidth: 130, showOverflow: true },
    { field: 'storageLocation', title: '贮存地点', minWidth: 130, showOverflow: true },
    { field: 'counterparty', title: '交接方', minWidth: 150, showOverflow: true },
    { field: 'handler', title: '经办人', width: 100 },
    {
      field: 'handleTime',
      title: '交接时间',
      width: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 250,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 台账 新增/编辑表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'manifestNo',
      label: '联单号',
      component: 'Input',
      componentProps: { placeholder: '与国家固废系统联单号一致' },
      rules: 'required',
    },
    {
      fieldName: 'wasteCode',
      label: '危废代码',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: WASTE_CODE_OPTIONS,
        placeholder: '如 HW49',
      },
    },
    {
      fieldName: 'wasteName',
      label: '危废名称',
      component: 'Input',
      componentProps: { placeholder: '如 废包装桶' },
      rules: 'required',
    },
    {
      fieldName: 'quantity',
      label: '数量',
      component: 'InputNumber',
      componentProps: { min: 0, precision: 3, class: 'w-full' },
    },
    {
      fieldName: 'quantityUnit',
      label: '数量单位',
      component: 'Input',
      componentProps: { placeholder: '吨' },
    },
    {
      fieldName: 'containerCode',
      label: '容器码',
      component: 'Input',
      componentProps: { placeholder: '一桶一码，危废标签二维码内容' },
    },
    {
      fieldName: 'storageLocation',
      label: '贮存地点',
      component: 'Input',
      componentProps: { placeholder: '如 危废暂存间A-01' },
    },
    {
      fieldName: 'counterparty',
      label: '交接方/接收单位',
      component: 'Input',
      componentProps: { placeholder: '如 北京绿源环保科技有限公司' },
    },
    {
      fieldName: 'handler',
      label: '经办人',
      component: 'Input',
      componentProps: { placeholder: '留空取当前登录人' },
    },
    {
      fieldName: 'handleTime',
      label: '交接时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        valueFormat: 'x',
        placeholder: '选择时间',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      componentProps: { rows: 2 },
    },
  ];
}

/** 联单 新增/编辑表单 */
export function useManifestFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'manifestNo',
      label: '联单号',
      component: 'Input',
      componentProps: { placeholder: '国家固废系统电子转移联单号' },
      rules: 'required',
    },
    {
      fieldName: 'wasteCode',
      label: '危废代码',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: WASTE_CODE_OPTIONS,
        placeholder: '如 HW49',
      },
    },
    {
      fieldName: 'wasteName',
      label: '危废名称',
      component: 'Input',
      componentProps: { placeholder: '如 废包装桶' },
    },
    {
      fieldName: 'quantity',
      label: '申报转移量',
      component: 'InputNumber',
      componentProps: { min: 0, precision: 3, class: 'w-full' },
    },
    {
      fieldName: 'quantityUnit',
      label: '数量单位',
      component: 'Input',
      componentProps: { placeholder: '吨' },
    },
    {
      fieldName: 'generateUnit',
      label: '产生单位',
      component: 'Input',
      componentProps: { placeholder: '五方之一' },
    },
    {
      fieldName: 'carrierUnit',
      label: '运输单位',
      component: 'Input',
      componentProps: { placeholder: '须确认后方可生效' },
    },
    {
      fieldName: 'receiveUnit',
      label: '接收单位',
      component: 'Input',
      componentProps: { placeholder: '须确认后方可生效' },
    },
    {
      fieldName: 'storageUnit',
      label: '贮存单位',
      component: 'Input',
      componentProps: { placeholder: '可空' },
    },
    {
      fieldName: 'disposeUnit',
      label: '处置单位',
      component: 'Input',
      componentProps: { placeholder: '可空' },
    },
    {
      fieldName: 'vehicleNo',
      label: '车牌号',
      component: 'Input',
      componentProps: { placeholder: '门卫扫牌核对此号' },
    },
    {
      fieldName: 'netWeight',
      label: '地磅净重(吨)',
      component: 'InputNumber',
      componentProps: { min: 0, precision: 3, class: 'w-full' },
    },
    {
      fieldName: 'declareDeadline',
      label: '申报/确认时限',
      component: 'DatePicker',
      componentProps: { showTime: true, valueFormat: 'x', placeholder: '国家平台倒排提醒基准' },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      formItemClass: 'col-span-2',
      componentProps: { rows: 2 },
    },
  ];
}
