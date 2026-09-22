<script lang="ts" setup>
import type { MesPollutionLedgerApi } from '#/api/mes/safetyEnv/pollutionLedger';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, Tag } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { updatePollutionLedgerStatus } from '#/api/mes/safetyEnv/pollutionLedger';
import { $t } from '#/locales';

import SignConfirmModal from '../../components/SignConfirmModal.vue';
import {
  LEDGER_STATUS_MAP,
  useStatusFormSchema,
} from '../data';

const emit = defineEmits(['success']);
/** 待流转台账行 */
const row = ref<MesPollutionLedgerApi.Ledger | null>(null);
/**
 * 处置流转是危废台账上最有后果的一步（写终态 / 落排放合规流水 / 重投影批次污染戳），
 * 后端缺签名直接拒（1040819005）。签字弹窗挂在常驻页面上、从本弹窗里再叠一层。
 */
const signRef = ref<InstanceType<typeof SignConfirmModal>>();

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: useStatusFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    if (!row.value) {
      return;
    }
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    const values = (await formApi.getValues()) as MesPollutionLedgerApi.StatusPayload;
    // 表单校验过了才要签名：先让人签完再告诉他有字段没填，是在浪费一次手写
    const sign = (await signRef.value?.open(
      `处置流转：${LEDGER_STATUS_MAP[row.value.status ?? '']?.text ?? row.value.status} → ${LEDGER_STATUS_MAP[values.status ?? '']?.text ?? values.status}`,
      `${row.value.sourceRecordNo ?? ''}｜${row.value.itemName ?? '-'}｜批次 ${row.value.batchNo || '-'}。流转到终态会清掉该批次污染戳并落排放合规流水。`,
    )) ?? null;
    if (!sign) {
      return; // 用户没签：不提交（后端缺签名也会拒，1040819005）
    }
    modalApi.lock();
    try {
      // 流转到处置中/已回用/已排放/已处置(终态自动清批次污染戳)；已排放随带 去向/标准 落排放流水
      await updatePollutionLedgerStatus({
        id: row.value.id!,
        status: values.status,
        remark: values.remark,
        destination: values.destination,
        standard: values.standard,
        signImg: sign.signImg,
        opinion: sign.opinion,
      });
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      row.value = null;
      await formApi.resetForm();
      return;
    }
    const data = modalApi.getData<{ row: MesPollutionLedgerApi.Ledger }>();
    row.value = data.row;
    formApi.setValues({ status: undefined, destination: undefined, standard: undefined });
  },
});
</script>

<template>
  <Modal title="处置流转" class="w-1/2">
    <div v-if="row" class="mx-4 mb-3 flex items-center gap-2">
      <span class="text-sm text-muted-foreground">
        批次 {{ row.batchNo ?? '-' }}（{{ row.itemName ?? '-' }}）
      </span>
      <Tag :color="LEDGER_STATUS_MAP[row.status ?? '']?.color">
        {{ LEDGER_STATUS_MAP[row.status ?? '']?.text ?? row.status }}
      </Tag>
      <span class="text-sm text-muted-foreground">去向：{{ row.location ?? '-' }}</span>
    </div>
    <Form class="mx-4" />
  </Modal>
  <SignConfirmModal ref="signRef" />
</template>
