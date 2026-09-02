import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesSetStandardApi } from '#/api/mes/safetyEnv/standard';


/** 新增/修改检测标准管理的表单 */
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
      fieldName: 'standardNo',
      label: '标准编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入标准编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'standardName',
      label: '标准名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入标准名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'domain',
      label: '检测域',
      component: 'Select',
      componentProps: {
        options: [{ label: "安全", value: "SAFETY" }, { label: "环保", value: "ENV" }, { label: "健康", value: "HEALTH" }],
        placeholder: '请选择检测域',
        allowClear: true,
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "气体", value: "GAS" }, { label: "噪声", value: "NOISE" }, { label: "粉尘", value: "DUST" }, { label: "辐射", value: "RADIATION" }, { label: "电气", value: "ELECTRICAL" }, { label: "消防", value: "FIRE" }, { label: "危化", value: "CHEMICAL" }, { label: "防护", value: "PPE" }, { label: "压力", value: "PRESSURE" }],
        placeholder: '请选择检测类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'refStandard',
      label: '引用国标编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入引用国标编号',
      },
    },
    {
      fieldName: 'limitsConfig',
      label: '限值配置(JSON 文本)',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入限值配置(JSON 文本)',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'method',
      label: '检测方法描述',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入检测方法描述',
      },
    },
    {
      fieldName: 'periodType',
      label: '周期类型',
      component: 'Select',
      componentProps: {
        options: [{ label: "每日", value: "DAILY" }, { label: "每周", value: "WEEKLY" }, { label: "每月", value: "MONTHLY" }, { label: "每季度", value: "QUARTERLY" }, { label: "每年", value: "YEARLY" }],
        placeholder: '请选择周期类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'triggerConfig',
      label: '事件触发配置(JSON 文本)',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入事件触发配置(JSON 文本)',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'applicableArea',
      label: '适用区域/工序(JSON 文本)',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入适用区域/工序(JSON 文本)',
        rows: 2,
      },
      formItemClass: 'col-span-3',
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: [{ label: "草稿", value: "DRAFT" }, { label: "在用", value: "ACTIVE" }, { label: "已作废", value: "OBSOLETE" }],
        placeholder: '请选择状态',
        allowClear: true,
      },
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
      fieldName: 'standardName',
      label: '标准名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入标准名称',
      },
    },
    {
      fieldName: 'domain',
      label: '检测域',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "安全", value: "SAFETY" }, { label: "环保", value: "ENV" }, { label: "健康", value: "HEALTH" }],
        placeholder: '请选择检测域',
      },
    },
    {
      fieldName: 'testType',
      label: '检测类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "气体", value: "GAS" }, { label: "噪声", value: "NOISE" }, { label: "粉尘", value: "DUST" }, { label: "辐射", value: "RADIATION" }, { label: "电气", value: "ELECTRICAL" }, { label: "消防", value: "FIRE" }, { label: "危化", value: "CHEMICAL" }, { label: "防护", value: "PPE" }, { label: "压力", value: "PRESSURE" }],
        placeholder: '请选择检测类型',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [{ label: "草稿", value: "DRAFT" }, { label: "在用", value: "ACTIVE" }, { label: "已作废", value: "OBSOLETE" }],
        placeholder: '请选择状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MesSetStandardApi.Standard>['columns'] {
  return [
    { field: 'standardNo', title: '标准编号', minWidth: 150 },
    { field: 'standardName', title: '标准名称', minWidth: 150 },
    { field: 'domain', title: '检测域', minWidth: 150 },
    { field: 'testType', title: '检测类型', minWidth: 150 },
    { field: 'periodType', title: '周期类型', minWidth: 150 },
    { field: 'status', title: '状态', minWidth: 150 },
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