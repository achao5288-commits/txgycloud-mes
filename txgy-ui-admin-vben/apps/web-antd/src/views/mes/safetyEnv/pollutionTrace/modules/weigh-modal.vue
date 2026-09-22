<script lang="ts" setup>
import type { VbenFormSchema } from '#/adapter/form';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createWeighRecord } from '#/api/mes/safetyEnv/pollutionTrace';
import { $t } from '#/locales';

const emit = defineEmits(['success']);

/** 追溯键(从抽屉带入，自动带上业务单号) */
const biz = ref<{ bizNo?: string; bizType?: string }>({});

const schema: VbenFormSchema[] = [
  {
    fieldName: 'weighType',
    label: '称重类型',
    component: 'Select',
    componentProps: {
      options: [
        { label: '产废称重', value: 'PRODUCE' },
        { label: '出厂称重', value: 'FACTORY' },
      ],
      placeholder: '请选择',
    },
    rules: 'required',
  },
  {
    fieldName: 'containerCode',
    label: '容器编号',
    component: 'Input',
    componentProps: { allowClear: true, placeholder: '桶/袋编号' },
  },
  {
    fieldName: 'batchCode',
    label: '批次号',
    component: 'Input',
    componentProps: { allowClear: true, placeholder: '可留空' },
  },
  {
    fieldName: 'plateNo',
    label: '车牌号',
    component: 'Input',
    componentProps: { allowClear: true, placeholder: '出厂称重常用' },
  },
  {
    fieldName: 'grossWeight',
    label: '毛重',
    component: 'InputNumber',
    componentProps: { class: 'w-full', min: 0, precision: 2 },
    rules: 'required',
  },
  {
    fieldName: 'tareWeight',
    label: '皮重',
    component: 'InputNumber',
    componentProps: { class: 'w-full', min: 0, precision: 2 },
  },
  {
    fieldName: 'reason',
    label: '备注',
    component: 'Textarea',
    componentProps: { placeholder: '如：电子秤未联网，手工登记', rows: 2 },
    formItemClass: 'col-span-2',
  },
];

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: { class: 'w-full' },
    formItemClass: 'col-span-1',
    labelWidth: 100,
  },
  layout: 'horizontal',
  schema,
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

/** 净重预览（最终以服务端计算为准） */
const netPreview = computed(() => {
  const { grossWeight, tareWeight } = formApi.form.values as {
    grossWeight?: number;
    tareWeight?: number;
  };
  if (grossWeight == null) {
    return null;
  }
  return Number((grossWeight - (tareWeight ?? 0)).toFixed(2));
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    const values = await formApi.getValues();
    try {
      await createWeighRecord({
        ...values,
        bizNo: biz.value.bizNo,
        bizType: biz.value.bizType,
      } as any);
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      await formApi.resetForm();
      return;
    }
    const data = modalApi.getData<{ bizNo?: string; bizType?: string }>();
    biz.value = data ?? {};
    formApi.setValues({ weighType: 'PRODUCE' });
  },
});
</script>

<template>
  <Modal title="手工登记称重" class="w-1/2">
    <Alert
      class="mx-4 mb-3"
      type="info"
      :show-icon="true"
      :message="`业务单号：${biz.bizNo ?? '-'}`"
      description="净重 = 毛重 − 皮重，由服务端计算；电子秤直采（AUTO）接入后自动写入，此入口保留给未联网场景。"
    />
    <div v-if="netPreview != null" class="mx-4 mb-2 text-sm">
      净重预览：<span class="font-medium">{{ netPreview }} kg</span>
    </div>
    <Form class="mx-4" />
  </Modal>
</template>
