<script lang="ts" setup>
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { reviewPollutionCheck } from '#/api/mes/safetyEnv/pollutionCheck';
import { $t } from '#/locales';

import AttachmentPanel from '../../components/AttachmentPanel.vue';
import SignaturePad from '../../components/SignaturePad.vue';
import { AI_RESULT_MAP, useReviewFormSchema } from '../data';

const emit = defineEmits(['success']);
/** 待复核记录(含 AI 初筛结果，只读) */
const row = ref<MesSetPollutionCheckApi.PollutionCheck | null>(null);
/** 手写签名板：确认时才上传，没签返回 undefined */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: useReviewFormSchema(),
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
    modalApi.lock();
    const values = (await formApi.getValues()) as MesSetPollutionCheckApi.ReviewPayload;
    // 多选控件的值是数组，落库要的是一个换行分隔的字符串。
    // 分隔符必须是换行：法条正文本身含「；」，用它拼接回填时会被切碎。
    const basisList = values.reviewBasis as unknown as string[] | undefined;
    try {
      // 终态收口：无污染/有污染 + 处置与去向(留空由后端按环节×结论取默认)
      await reviewPollutionCheck({
        id: row.value.id!,
        reviewResult: values.reviewResult,
        // 仅成品环节有值；后端对"非成品传值/成品缺值"都会硬拒
        finishedResult: values.finishedResult,
        // 不强制：没勾就整个字段不发，别发空串——空串会覆盖成"选了 0 条"的语义
        reviewBasis: basisList?.length ? basisList.join('\n') : undefined,
        disposition: values.disposition,
        storageMethod: values.storageMethod,
        locationId: values.locationId,
        remark: values.remark,
        signImg: await signPadRef.value?.commit(),
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
    const data = modalApi.getData<{ row: MesSetPollutionCheckApi.PollutionCheck }>();
    row.value = data.row;
    // 回填环节驱动"成品达标分支"显隐/必填(隐藏字段，仅作 dependencies 触发器)
    formApi.setValues({
      finishedResult: undefined,
      reviewResult: undefined,
      // 复核只对待复核行开放，此时 reviewBasis 必为空；清成 [] 以免复用上一次的勾选
      reviewBasis: [],
      stage: data.row.stage,
    });
  },
});
</script>

<template>
  <Modal
    :title="
      row?.stage === 'FINISHED_PRODUCT'
        ? '人工复核（污染态 + 成品达标分支）'
        : '人工复核（终态：无污染 / 有污染）'
    "
    class="w-2/3"
  >
    <Alert
      v-if="row"
      class="mx-4 mb-3"
      :type="
        row.aiResult === 'CLEAN'
          ? 'success'
          : row.aiResult === 'POLLUTED'
            ? 'error'
            : 'warning'
      "
      :show-icon="true"
      :message="`记录 ${row.recordNo ?? ''}（${row.itemName ?? '-'}）`"
      :description="`AI 初筛：${AI_RESULT_MAP[row.aiResult ?? '']?.text ?? row.aiResult ?? '-'}（置信度 ${row.aiConfidence ?? '-'}%）｜ 判定依据：${row.aiReason ?? '-'}｜ AI 援引法规：${row.aiBasis ?? '-'}｜ AI 推荐存储方法：${row.suggestedStorage ?? '-'}`"
    />
    <Form class="mx-4" />
    <SignaturePad ref="signPadRef" class="mx-4 mt-3" />
    <AttachmentPanel v-if="row?.recordNo" class="mx-4 mt-3" biz-type="CHECK" :biz-no="row.recordNo" />
  </Modal>
</template>
