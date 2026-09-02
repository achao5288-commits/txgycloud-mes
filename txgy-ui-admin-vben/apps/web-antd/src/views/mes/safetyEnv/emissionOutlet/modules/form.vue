<script lang="ts" setup>
import type { MesSetEmissionOutletApi } from '#/api/mes/safetyEnv/emissionOutlet';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createEmissionOutlet,
  getEmissionOutlet,
  updateEmissionOutlet,
} from '#/api/mes/safetyEnv/emissionOutlet';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formType = ref<'create' | 'detail' | 'update'>('create');
const isDetail = computed(() => formType.value === 'detail');
const getTitle = computed(() => {
  if (formType.value === 'detail') {
    return '查看排放口管理';
  }
  return formType.value === 'update'
    ? $t('ui.actionTitle.edit', ['排放口管理'])
    : $t('ui.actionTitle.create', ['排放口管理']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-3',
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    if (isDetail.value) {
      await modalApi.close();
      return;
    }
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    // 提交表单
    const data = (await formApi.getValues()) as MesSetEmissionOutletApi.EmissionOutlet;
    try {
      await (formType.value === 'update'
        ? updateEmissionOutlet(data)
        : createEmissionOutlet(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formType.value = 'create';
      return;
    }
    // 加载数据
    const data = modalApi.getData<{
      formType: 'create' | 'detail' | 'update';
      id?: number;
    }>();
    formType.value = data.formType;
    formApi.setDisabled(isDetail.value);
    modalApi.setState({ showConfirmButton: !isDetail.value });
    if (!data.id) {
      return;
    }
    modalApi.lock();
    try {
      const row = await getEmissionOutlet(data.id);
      // 设置到 values
      await formApi.setValues(row);
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle" class="w-2/3">
    <Form class="mx-4" />
  </Modal>
</template>