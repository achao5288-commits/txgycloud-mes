import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesChemicalApi } from '#/api/mes/safetyEnv/chemical';

/**
 * 相容组：只列设计文档 §5.2 / §130 点名的物料族，与后端 GROUP_NAMES 同一份口径。
 * 没收录的组，后端按原值回显、不猜——前端也不给"看起来像"的选项。
 */
export const COMPAT_GROUP_OPTIONS = [
  { label: '异氰酸酯(黑料)', value: 'ISOCYANATE' },
  { label: '组合聚醚(白料)', value: 'POLYOL' },
  { label: '稀释剂', value: 'THINNER' },
  { label: '环氧粉末', value: 'EPOXY' },
  { label: '水', value: 'WATER' },
  { label: '醇类', value: 'ALCOHOL' },
  { label: '胺类', value: 'AMINE' },
];

export const COMPAT_GROUP_MAP: Record<string, string> = Object.fromEntries(
  COMPAT_GROUP_OPTIONS.map((o) => [o.value, o.label]),
);

/** 禁配矩阵里出现的组——选中即提示"须分间存放" */
export const INCOMPATIBLE_GROUPS = new Set([
  'ISOCYANATE',
  'POLYOL',
  'WATER',
  'ALCOHOL',
  'AMINE',
]);

export const STORAGE_ZONE_OPTIONS = [
  { label: '一般库位', value: 'GENERAL' },
  { label: '防爆区', value: 'EXPLOSION_PROOF' },
  { label: '隔离存放区', value: 'ISOLATION' },
  { label: '专用库（黑料间等）', value: 'SPECIAL' },
];

export const STORAGE_ZONE_MAP: Record<string, string> = Object.fromEntries(
  STORAGE_ZONE_OPTIONS.map((o) => [o.value, o.label]),
);

export const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '停用', value: 'DISABLED' },
];

/** 五双作业类型（与后端 DOUBLE_SIGN_ACTIONS 一致） */
export const DOUBLE_SIGN_ACTION_OPTIONS = [
  { label: '双人收发', value: 'RECEIVE' },
  { label: '双人保管', value: 'KEEP' },
  { label: '双人双锁', value: 'LOCK' },
  { label: '双人领料', value: 'ISSUE' },
  { label: '双人运输', value: 'TRANSPORT' },
];

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'profileNo',
      label: '档案编号',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '请输入档案编号' },
    },
    {
      fieldName: 'chemicalName',
      label: '危化品名称',
      component: 'Input',
      componentProps: { allowClear: true, placeholder: '请输入危化品名称' },
    },
    {
      fieldName: 'compatGroup',
      label: '相容组',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: COMPAT_GROUP_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'storageZone',
      label: '储存专区',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STORAGE_ZONE_OPTIONS,
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
export function useGridColumns(): VxeTableGridOptions<MesChemicalApi.ChemicalProfile>['columns'] {
  return [
    { field: 'profileNo', title: '档案编号', minWidth: 150, showOverflow: true },
    { field: 'chemicalName', title: '危化品名称', minWidth: 160, showOverflow: true },
    {
      field: 'compatGroup',
      title: '相容组',
      minWidth: 140,
      slots: { default: 'compatGroup' },
    },
    {
      field: 'storageZone',
      title: '储存专区',
      minWidth: 130,
      slots: { default: 'storageZone' },
    },
    { field: 'storageLocation', title: '库位', minWidth: 160, showOverflow: true },
    {
      field: 'storageLimit',
      title: '储量上限',
      minWidth: 120,
      slots: { default: 'quota' },
    },
    {
      field: 'msdsUrl',
      title: 'MSDS',
      width: 110,
      slots: { default: 'msds' },
    },
    {
      field: 'status',
      title: '状态',
      width: 90,
      slots: { default: 'status' },
    },
    {
      title: '操作',
      width: 260,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 新增/编辑表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'profileNo',
      label: '档案编号',
      component: 'Input',
      componentProps: { placeholder: '请输入档案编号' },
      rules: 'required',
    },
    {
      fieldName: 'chemicalName',
      label: '危化品名称',
      component: 'Input',
      componentProps: { placeholder: '请输入危化品名称' },
      rules: 'required',
    },
    {
      fieldName: 'chemicalCode',
      label: '危化品编码',
      component: 'Input',
      componentProps: { placeholder: '请输入危化品编码' },
    },
    {
      fieldName: 'casNo',
      label: 'CAS 号',
      component: 'Input',
      componentProps: { placeholder: '请输入 CAS 号' },
    },
    {
      fieldName: 'compatGroup',
      label: '相容组',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: COMPAT_GROUP_OPTIONS,
        placeholder: '请选择（决定禁配判定）',
      },
    },
    {
      fieldName: 'hazardClass',
      label: '危险性类别',
      component: 'Input',
      componentProps: { placeholder: '请输入危险性类别' },
    },
    {
      fieldName: 'storageZone',
      label: '储存专区',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STORAGE_ZONE_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'storageLocation',
      label: '具体库位',
      component: 'Input',
      componentProps: { placeholder: '如：危化专库-黑料间' },
    },
    {
      fieldName: 'storageLimit',
      label: '储量上限',
      component: 'InputNumber',
      componentProps: { min: 0, class: 'w-full', placeholder: '留空表示不限量' },
    },
    {
      fieldName: 'storageUnit',
      label: '储量单位',
      component: 'Input',
      componentProps: { placeholder: '如：吨 / kg / L' },
    },
    {
      fieldName: 'explosionProof',
      label: '须防爆存放',
      component: 'Switch',
      componentProps: { checkedValue: true, unCheckedValue: false },
    },
    {
      fieldName: 'incompatibleGroups',
      label: '追加禁配组',
      component: 'Input',
      componentProps: {
        placeholder: '逗号分隔，如 ISOCYANATE,WATER（矩阵之外的人工补充）',
      },
    },
    {
      fieldName: 'msdsUrl',
      label: 'MSDS 地址',
      component: 'Input',
      componentProps: { placeholder: '未挂载 MSDS 的危化品不得入库' },
    },
    {
      fieldName: 'msdsExpireDate',
      label: 'MSDS 到期日',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
        class: 'w-full',
      },
    },
    {
      fieldName: 'expireManage',
      label: '效期管理',
      component: 'Switch',
      componentProps: { checkedValue: true, unCheckedValue: false },
    },
    {
      fieldName: 'shelfLifeDays',
      label: '保质期(天)',
      component: 'InputNumber',
      componentProps: { min: 0, class: 'w-full' },
    },
    {
      fieldName: 'emergencyMeasure',
      label: '应急处置',
      component: 'Textarea',
      componentProps: { rows: 3, placeholder: '泄漏/着火时的处置措施' },
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
  ];
}
