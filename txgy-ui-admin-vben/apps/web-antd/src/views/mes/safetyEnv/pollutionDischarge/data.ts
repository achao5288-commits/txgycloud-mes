import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesPollutionDischargeApi } from '#/api/mes/safetyEnv/pollutionDischarge';

import { STAGE_MAP, STAGE_OPTIONS } from '../pollutionCheck/data';

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'sourceRecordNo',
      label: '来源判定记录',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: 'PC-...',
      },
    },
    {
      fieldName: 'stage',
      label: '环节',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: STAGE_OPTIONS,
        placeholder: '请选择环节',
      },
    },
    {
      fieldName: 'batchNo',
      label: '批次号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入批次号',
      },
    },
    {
      fieldName: 'itemName',
      label: '物料/产品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '模糊匹配',
      },
    },
    {
      fieldName: 'destination',
      label: '排放去向',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '模糊匹配',
      },
    },
  ];
}

/** 列表字段（排放合规流水，按登记时间倒序；只读） */
export function useGridColumns(): VxeTableGridOptions<MesPollutionDischargeApi.DischargeRecord>['columns'] {
  return [
    { field: 'dischargeTime', title: '登记时间', width: 165, formatter: 'formatDateTime' },
    { field: 'sourceRecordNo', title: '来源判定记录', minWidth: 180, showOverflow: true },
    { field: 'stage', title: '环节', width: 110, formatter: ({ cellValue }) => (cellValue ? (STAGE_MAP[cellValue] ?? cellValue) : '-') },
    { field: 'batchNo', title: '批次号', minWidth: 140 },
    { field: 'itemName', title: '物料/产品名称', minWidth: 150, showOverflow: true },
    { field: 'destination', title: '排放去向', minWidth: 150, showOverflow: true },
    { field: 'standard', title: '执行标准', minWidth: 150, showOverflow: true },
    { field: 'creator', title: '登记人', width: 100 },
    { field: 'remark', title: '备注', minWidth: 150, showOverflow: true },
  ];
}
