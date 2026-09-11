import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetDustRecordApi } from '#/api/mes/safetyEnv/dustrecord';

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'recordNo',
      label: '记录编号 DUST-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入记录编号 DUST-YYYYMMDD-NNN',
      },
    },
    {
      fieldName: 'location',
      label: '检测位置/作业区域',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测位置/作业区域',
      },
    },
    {
      fieldName: 'dustType',
      label: '检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2',
      },
    },
    {
      fieldName: 'result',
      label: '结果：PASS/FAIL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入结果：PASS/FAIL',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式：IOT_AUTO/MANUAL',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采集方式：IOT_AUTO/MANUAL',
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetDustRecordApi.DustRecord>['columns'] {
  return [
    { field: 'recordNo', title: '记录编号 DUST-YYYYMMDD-NNN', minWidth: 170, showOverflow: true },
    { field: 'location', title: '检测位置/作业区域', minWidth: 170, showOverflow: true },
    { field: 'dustType', title: '检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2', minWidth: 170, showOverflow: true },
    { field: 'concentration', title: '检测浓度/含量', width: 120 },
    { field: 'sio2Content', title: '游离SiO2含量%(总尘且需矽尘分级时)', width: 120 },
    { field: 'unit', title: '单位 mg/m3/%', minWidth: 170, showOverflow: true },
    { field: 'limitValue', title: '限值', width: 120 },
    { field: 'result', title: '结果：PASS/FAIL', minWidth: 170, showOverflow: true },
    { field: 'collectionMode', title: '采集方式：IOT_AUTO/MANUAL', minWidth: 170, showOverflow: true },
    { field: 'inspectTime', title: '检测时间', width: 120 },
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
      label: '记录编号 DUST-YYYYMMDD-NNN',
      component: 'Input',
      componentProps: {
        placeholder: '请输入记录编号 DUST-YYYYMMDD-NNN',
      },
      rules: 'required',
    },
    {
      fieldName: 'planId',
      label: '关联检测计划编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联检测计划编号',
      },
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
      fieldName: 'deviceId',
      label: '关联设备编号(除尘/产尘设备)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入关联设备编号(除尘/产尘设备)',
      },
    },
    {
      fieldName: 'location',
      label: '检测位置/作业区域',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测位置/作业区域',
      },
    },
    {
      fieldName: 'dustType',
      label: '检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2',
      },
      rules: 'required',
    },
    {
      fieldName: 'concentration',
      label: '检测浓度/含量',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入检测浓度/含量',
        class: 'w-full',
      },
    },
    {
      fieldName: 'sio2Content',
      label: '游离SiO2含量%(总尘且需矽尘分级时)',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入游离SiO2含量%(总尘且需矽尘分级时)',
        class: 'w-full',
      },
    },
    {
      fieldName: 'unit',
      label: '单位 mg/m3/%',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单位 mg/m3/%',
      },
    },
    {
      fieldName: 'limitValue',
      label: '限值',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入限值',
        class: 'w-full',
      },
    },
    {
      fieldName: 'refStandard',
      label: '引用国标',
      component: 'Input',
      componentProps: {
        placeholder: '请输入引用国标',
      },
    },
    {
      fieldName: 'result',
      label: '结果：PASS/FAIL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入结果：PASS/FAIL',
      },
    },
    {
      fieldName: 'collectionMode',
      label: '采集方式：IOT_AUTO/MANUAL',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采集方式：IOT_AUTO/MANUAL',
      },
    },
    {
      fieldName: 'instrumentNo',
      label: '检测仪器编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测仪器编号',
      },
    },
    {
      fieldName: 'inspector',
      label: '检测人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测人',
      },
    },
    {
      fieldName: 'inspectTime',
      label: '检测时间',
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
      label: '检测照片URL(逗号分隔)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入检测照片URL(逗号分隔)',
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
