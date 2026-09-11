import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetEmergencyMaterialApi } from '#/api/mes/safetyEnv/emergencymaterial';

/** 物资类型文案 */
export const MATERIAL_TYPE_MAP: Record<string, string> = {
  DRY_SAND: '干沙',
  OIL_ABSORBENT: '吸油毡',
  CHEM_SUIT: '防化服',
  GAS_MASK: '防毒面具',
  EXPLOSION_TOOL: '防爆工具',
  SANDBAG: '围堰沙袋',
  EYE_WASH: '洗眼器',
  DRY_POWDER: '干粉灭火器',
};

/** 状态文案 + 标签色（缺货比过期更急，列表要一眼分得开） */
export const MATERIAL_STATUS_MAP: Record<string, { color: string; text: string }> = {
  NORMAL: { color: 'success', text: '正常' },
  EXPIRING: { color: 'warning', text: '临期' },
  EXPIRED: { color: 'error', text: '过期' },
  OUT: { color: 'error', text: '缺货' },
};

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'materialNo',
      label: '物资编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物资编号',
      },
    },
    {
      fieldName: 'materialName',
      label: '物资名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物资名称',
      },
    },
    {
      fieldName: 'materialType',
      label: '物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)',
      },
    },
    {
      fieldName: 'status',
      label: '状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetEmergencyMaterialApi.EmergencyMaterial>['columns'] {
  return [
    { field: 'materialNo', title: '物资编号', minWidth: 170, showOverflow: true },
    { field: 'materialName', title: '物资名称', minWidth: 170, showOverflow: true },
    { field: 'materialType', title: '物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)', minWidth: 170, showOverflow: true , slots: { default: 'materialType' } },
    { field: 'spec', title: '规格型号', minWidth: 170, showOverflow: true },
    { field: 'unit', title: '计量单位', minWidth: 170, showOverflow: true },
    { field: 'quantity', title: '在库数量', width: 120 },
    { field: 'storageLocation', title: '定点存放位置', minWidth: 170, showOverflow: true },
    { field: 'expireDate', title: '有效期至（临期预警依据）', width: 120 },
    { field: 'status', title: '状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单', minWidth: 170, showOverflow: true , slots: { default: 'status' } },
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
      fieldName: 'materialNo',
      label: '物资编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入物资编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'materialName',
      label: '物资名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入物资名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'materialType',
      label: '物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)',
      },
      rules: 'required',
    },
    {
      fieldName: 'spec',
      label: '规格型号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入规格型号',
      },
    },
    {
      fieldName: 'unit',
      label: '计量单位',
      component: 'Input',
      componentProps: {
        placeholder: '请输入计量单位',
      },
    },
    {
      fieldName: 'quantity',
      label: '在库数量',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入在库数量',
        class: 'w-full',
      },
    },
    {
      fieldName: 'storageLocation',
      label: '定点存放位置',
      component: 'Input',
      componentProps: {
        placeholder: '请输入定点存放位置',
      },
    },
    {
      fieldName: 'produceDate',
      label: '生产日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'expireDate',
      label: '有效期至（临期预警依据）',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'lastCheckDate',
      label: '最近检查日期',
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD',
        format: 'YYYY-MM-DD',
        placeholder: '选择日期',
      },
    },
    {
      fieldName: 'status',
      label: '状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单',
      component: 'Input',
      componentProps: {
        placeholder: '请输入状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单',
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
