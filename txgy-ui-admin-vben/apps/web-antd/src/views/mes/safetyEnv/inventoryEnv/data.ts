import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesInventoryEnvApi } from '#/api/mes/safetyEnv/inventoryEnv';

import { markRaw } from 'vue';

import { DICT_TYPE } from '@vben/constants';

import { MdItemTypeSelect } from '#/views/mes/md/item/type/components';
import { WmWarehouseSelect } from '#/views/mes/wm/warehouse/components';

/** 检测状态（四项判据的前三项，第四项「混放」单独一个开关） */
export const INSPECT_STATUS_OPTIONS = [
  { label: '待检（命中任一项）', value: 'PENDING' },
  { label: '未检', value: 'NOT_CHECKED' },
  { label: '已检', value: 'CHECKED' },
  { label: '超期未检', value: 'OVERDUE' },
  { label: '积压', value: 'STOCKPILE' },
];

/** 污染投影：注意 NULL 不是「干净」，是「未判定」（设计 F16） */
export const POLLUTION_STATUS_OPTIONS = [
  { label: '已判有污染', value: 'POLLUTED' },
  { label: '已判无污染', value: 'CLEAN' },
  { label: '未判定', value: 'UNKNOWN' },
];

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'inspectStatus',
      label: '检测状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: INSPECT_STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'pollutionStatus',
      label: '污染投影',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: POLLUTION_STATUS_OPTIONS,
        placeholder: '请选择',
      },
    },
    {
      fieldName: 'minStockDays',
      label: '在库天数 >',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '如 180',
        style: { width: '100%' },
      },
    },
    {
      // 单选即「只看混放」，清空即不限——后端 mixed=false 与不传等价
      fieldName: 'mixed',
      label: '混放',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: '只看混放', value: true }],
        placeholder: '不限',
      },
    },
    {
      fieldName: 'warehouseId',
      label: '仓库',
      component: markRaw(WmWarehouseSelect),
      componentProps: {
        placeholder: '请选择仓库',
      },
    },
    {
      fieldName: 'itemTypeId',
      label: '物料分类',
      component: markRaw(MdItemTypeSelect),
      componentProps: {
        placeholder: '请选择分类',
      },
    },
    {
      fieldName: 'batchCode',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入批次号',
      },
    },
    {
      fieldName: 'itemName',
      label: '物料',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '编码或名称',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesInventoryEnvApi.InventoryEnv>['columns'] {
  return [
    // 勾选列：gridOptions 里配了 checkboxConfig，但 vxe 不会自己长出勾选框来，
    // 少了这一列 getCheckboxRecords() 永远是空 —— 「批量发起检测」就永远只会回
    // "请先勾选要发起检测的库存行"，而界面上根本没有能勾的地方。
    {
      type: 'checkbox',
      width: 50,
    },
    {
      field: 'batchCode',
      title: '批次号',
      minWidth: 140,
      slots: { default: 'batchCode' },
    },
    {
      field: 'itemCode',
      title: '物料编码',
      minWidth: 120,
    },
    {
      field: 'itemName',
      title: '物料名称',
      minWidth: 140,
    },
    {
      field: 'specification',
      title: '规格型号',
      minWidth: 110,
    },
    {
      field: 'quantity',
      title: '在库数量',
      width: 100,
    },
    {
      field: 'unitMeasureName',
      title: '单位',
      width: 70,
    },
    {
      field: 'warehouseName',
      title: '仓库',
      minWidth: 100,
    },
    {
      field: 'locationName',
      title: '库区',
      minWidth: 100,
    },
    {
      field: 'areaName',
      title: '库位',
      minWidth: 110,
      slots: { default: 'areaName' },
    },
    {
      field: 'receiptTime',
      title: '入库时间',
      width: 165,
      formatter: 'formatDateTime',
    },
    {
      field: 'stockDays',
      title: '在库天数',
      width: 95,
    },
    {
      field: 'frozen',
      title: '冻结',
      width: 70,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.INFRA_BOOLEAN_STRING },
      },
    },
    {
      title: '污染投影',
      minWidth: 130,
      slots: { default: 'pollution' },
    },
    {
      title: '最近判定',
      minWidth: 150,
      slots: { default: 'check' },
    },
    {
      title: '体检判据',
      minWidth: 200,
      slots: { default: 'criteria' },
    },
    {
      title: '操作',
      width: 190,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
