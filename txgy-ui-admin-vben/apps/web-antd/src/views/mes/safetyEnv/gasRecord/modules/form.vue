<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createGasRecord,
  getGasRecord,
  updateGasRecord,
} from '#/api/mes/safetyEnv/gasrecord';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formType = ref<'create' | 'update'>('create');
const getTitle = computed(() =>
  formType.value === 'update'
    ? $t('ui.actionTitle.edit', ['气体检测记录'])
    : $t('ui.actionTitle.create', ['气体检测记录']),
);

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
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    const data = await formApi.getValues();
    try {
      await (formType.value === 'update'
        ? updateGasRecord(data)
        : createGasRecord(data));
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
    const data = modalApi.getData<{ formType: 'create' | 'update'; id?: number }>();
    formType.value = data.formType;
    if (!data.id) {
      return;
    }
    modalApi.lock();
    try {
      const row = await getGasRecord(data.id);
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
