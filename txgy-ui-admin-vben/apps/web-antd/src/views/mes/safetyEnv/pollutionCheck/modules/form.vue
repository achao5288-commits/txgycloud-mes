<script lang="ts" setup>
import type { MesSetPollutionCheckApi } from '#/api/mes/safetyEnv/pollutionCheck';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  amendPollutionCheck,
  createPollutionCheck,
  getPollutionCheck,
  prescreenPollutionCheck,
  updatePollutionCheck,
} from '#/api/mes/safetyEnv/pollutionCheck';
import { $t } from '#/locales';

import SignConfirmModal from '../../components/SignConfirmModal.vue';
import { AI_RESULT_MAP, useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formType = ref<'amend' | 'create' | 'update'>('create');
/**
 * 新增=发起检测、变更=新开一份记录接替原单，两者都是人为主张，后端要签字（1040818019）；
 * 改单不校验（只增不改的留痕口径）。
 */
const signRef = ref<InstanceType<typeof SignConfirmModal>>();
/** 变更模式下被替代的原单号，只用于弹窗上的说明文案 */
const originRecordNo = ref<null | string>(null);
const getTitle = computed(() => {
  if (formType.value === 'amend') {
    return '发起变更（新开一份记录）';
  }
  return formType.value === 'update'
    ? $t('ui.actionTitle.edit', ['污染判定'])
    : $t('ui.actionTitle.create', ['污染判定']);
});
/** AI 初筛预览(不落库) */
const aiSuggestion = ref<MesSetPollutionCheckApi.AiSuggestion | null>(null);
const previewing = ref(false);

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: [],
  showDefaultActions: false,
  wrapperClass: 'grid-cols-3',
});

/**
 * 按当前 formType 建 schema。
 *
 * 变更单多一个「变更事由」且必填：事由是审计上唯一解释"凭什么推翻已生效结论"的东西，
 * 后端也硬校验（1040818022）。其余字段与原单同构，所以直接复用同一份 schema，不另抄一遍。
 *
 * 只能在 formApi 存在之后调用：schema 里批次的 onChange 要回填其它字段，得闭包住 formApi。
 * 也因此每次开窗都要重叫一次 —— 变更模式与改单模式的字段集不一样。
 */
function applySchema() {
  const schema = useFormSchema(formApi);
  if (formType.value === 'amend') {
    schema.push({
      fieldName: 'amendReason',
      label: '变更事由',
      component: 'Textarea',
      componentProps: {
        rows: 2,
        placeholder: '说明为什么要改：原判定哪里错了、依据什么改（会随新单一起留档）',
      },
      rules: 'required',
      formItemClass: 'col-span-3',
    });
  }
  formApi.setState({ schema });
}
applySchema();

/** AI 初筛预览：按当前填写的物料名调用后端(不落库) */
async function handlePrescreen() {
  const values = (await formApi.getValues()) as MesSetPollutionCheckApi.PollutionCheck;
  if (!values.itemName) {
    message.warning('请先填写物料/产品名称');
    return;
  }
  previewing.value = true;
  aiSuggestion.value = null;
  try {
    aiSuggestion.value = await prescreenPollutionCheck(values);
  } finally {
    previewing.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    // 表单校验过了才要签名：先让人签完再告诉他有字段没填，是在浪费一次手写
    const isAmend = formType.value === 'amend';
    let sign: null | { opinion?: string; signImg: string } = null;
    if (formType.value === 'create' || isAmend) {
      sign = (await signRef.value?.open(
        isAmend ? '发起变更（新开一份记录）' : '发起环保检测',
        isAmend
          ? `将新开一份判定接替 ${originRecordNo.value ?? ''}：原单内容保持冻结、只标记为「已被替代」，新单重新走一遍复核。`
          : '新增判定会做实在的业务动作：判「有污染」时该批次在库库存会被冻结、批次污染戳更新。',
      )) ?? null;
      if (!sign) {
        return; // 用户没签：不提交（后端缺签名也会拒，1040818019）
      }
    }
    modalApi.lock();
    // 提交表单；后端自动执行 AI 初筛并落为待复核
    const data = (await formApi.getValues()) as MesSetPollutionCheckApi.PollutionCheck;
    if (sign) {
      data.signImg = sign.signImg;
      data.opinion = sign.opinion;
    }
    try {
      if (isAmend) {
        // 新单号由服务端生成（签名要拿它当键，前端无从预知），前端只交"改后的内容 + 事由 + 原单 id"
        await amendPollutionCheck({
          ...data,
          originId: data.id!,
          amendReason: data.amendReason!,
          // isAmend 走到这里必已签成（没签在上面就 return 了）；这里补一次是为了让 TS 看见，
          // 顺带把「变更单必须有签名」钉在类型上——AmendPayload.signImg 不是可选的
          signImg: sign!.signImg,
        });
      } else {
        await (formType.value === 'update'
          ? updatePollutionCheck(data)
          : createPollutionCheck(data));
      }
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
      aiSuggestion.value = null;
      originRecordNo.value = null;
      return;
    }
    const data = modalApi.getData<{
      formType: 'amend' | 'create' | 'update';
      id?: number;
    }>();
    formType.value = data.formType;
    applySchema(); // 变更模式要多一个「变更事由」，schema 得在回填之前就换好
    if (!data.id) {
      return;
    }
    modalApi.lock();
    try {
      const row = await getPollutionCheck(data.id);
      originRecordNo.value =
        formType.value === 'amend' ? (row.recordNo ?? null) : null;
      // 待复核行允许编辑、已复核行允许发起变更：两者都是把内容回填进表单，只是提交去向不同
      await formApi.setValues(row);
      if (formType.value === 'amend') {
        // 事由要写的是「**这一次**为什么要改」，不是上一单为什么改。原样带出来等于给一个
        // 已有的错误答案，手快的人会照着提交，审计上就变成"改判理由是抄的"——必须空着让人写。
        // （变更单这一行自己就带 amendReason，所以这条在「变更单再发起变更」时一定会触发。）
        await formApi.setFieldValue('amendReason', undefined);
      }
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle" class="w-2/3">
    <Alert
      v-if="formType === 'amend'"
      class="mx-4 mb-3"
      type="warning"
      show-icon
      message="原单已复核收口，内容不可就地修改"
      :description="`保存后将新开一份判定接替 ${originRecordNo ?? ''}：原单只标记为「已被替代」，新单回到待复核，需要重新复核签字。`"
    />
    <div class="mx-4 mb-3 flex items-start gap-3">
      <Button :loading="previewing" @click="handlePrescreen">AI 初筛预览</Button>
      <Alert
        v-if="aiSuggestion"
        class="flex-1"
        :type="
          aiSuggestion.aiResult === 'CLEAN'
            ? 'success'
            : aiSuggestion.aiResult === 'POLLUTED'
              ? 'error'
              : 'warning'
        "
        :show-icon="true"
        :message="`AI 初筛：${AI_RESULT_MAP[aiSuggestion.aiResult ?? '']?.text ?? aiSuggestion.aiResult}（置信度 ${aiSuggestion.aiConfidence ?? '-'}%）`"
        :description="`判定依据：${aiSuggestion.aiReason ?? '-'}；AI 援引法规：${aiSuggestion.aiBasis ?? '-'}；推荐存储方法：${aiSuggestion.suggestedStorage ?? '-'}`"
      />
    </div>
    <Form class="mx-4" />
  </Modal>
  <SignConfirmModal ref="signRef" />
</template>
