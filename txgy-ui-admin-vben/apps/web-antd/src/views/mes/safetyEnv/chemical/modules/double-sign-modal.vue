<script lang="ts" setup>
import type { MesChemicalApi } from '#/api/mes/safetyEnv/chemical';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Input, message, Select, Space, Tag } from 'ant-design-vue';

import {
  doubleSignChemical,
  getDoubleSignStatus,
} from '#/api/mes/safetyEnv/chemical';

import SignaturePad from '../../components/SignaturePad.vue';
import { DOUBLE_SIGN_ACTION_OPTIONS } from '../data';

const emit = defineEmits(['success']);

const bizNo = ref('');
const signAction = ref('RECEIVE');
const opinion = ref('');
const status = ref<MesChemicalApi.DoubleSignResult | null>(null);
/** 手写签名板：确认时才上传，没签返回 undefined */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const ACTION_MAP: Record<string, string> = Object.fromEntries(
  DOUBLE_SIGN_ACTION_OPTIONS.map((o) => [o.value, o.label]),
);

async function refresh() {
  if (!bizNo.value) {
    return;
  }
  status.value = await getDoubleSignStatus(bizNo.value, signAction.value);
}

async function doSign() {
  if (!bizNo.value) {
    message.warning('请先填作业单号');
    return;
  }
  try {
    status.value = await doubleSignChemical({
      bizNo: bizNo.value,
      signAction: signAction.value,
      opinion: opinion.value || undefined,
      signImg: await signPadRef.value?.commit(),
    });
    signPadRef.value?.clear();
    status.value.complete
      ? message.success('双人已签齐')
      : message.info('已记一次签名，还差第二个人');
    emit('success');
  } catch {
    // 同一账号连签两次（1040830007）由全局提示展示
    await refresh();
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      bizNo.value = '';
      signAction.value = 'RECEIVE';
      opinion.value = '';
      status.value = null;
      signPadRef.value?.clear();
      return;
    }
    const data = modalApi.getData<{ bizNo?: string }>();
    if (data?.bizNo) {
      bizNo.value = data.bizNo;
      await refresh();
    }
  },
});
</script>

<template>
  <Modal title="五双双人签字" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="双人制：同一作业同一动作，须两个不同账号各签一次"
        description="同一账号连签两次不算双人——系统按登录账号判重（不是按姓名，重名或改名都骗不过）。双人收发 / 双人保管 / 双人双锁 / 双人领料 / 双人运输，各动作分别计签，互不干扰。"
      />
      <Space direction="vertical" class="w-full">
        <Input v-model:value="bizNo" placeholder="作业单号（领料单/收发单号）" allow-clear @blur="refresh" />
        <Select
          v-model:value="signAction"
          class="w-full"
          :options="DOUBLE_SIGN_ACTION_OPTIONS"
          @change="refresh"
        />
        <Input v-model:value="opinion" placeholder="签字意见（可选）" allow-clear />
      </Space>

      <SignaturePad ref="signPadRef" class="mt-3" />

      <div v-if="status" class="mt-4">
        <div class="mb-2">
          <Tag :color="status.complete ? 'success' : 'warning'">
            {{ status.complete ? '双人已齐' : '尚未签齐' }}
          </Tag>
          <span class="ml-2 text-gray-500">
            {{ ACTION_MAP[status.signAction ?? ''] ?? status.signAction }}
            · 已签 {{ status.signCount ?? 0 }} 次 / {{ status.signers?.length ?? 0 }} 人
          </span>
        </div>
        <div v-if="status.signers?.length" class="text-gray-500">
          签字账号：{{ status.signers.join('、') }}
        </div>
        <div v-if="status.lastSignTime" class="text-gray-500">
          最后签字：{{ new Date(status.lastSignTime).toLocaleString() }}
        </div>
        <div class="mt-2 text-gray-500">
          提示：请换第二个账号登录后再签一次，同一账号再签会被拦。
        </div>
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button @click="refresh">查状态</Button>
        <Button type="primary" @click="doSign">签字</Button>
      </Space>
    </template>
  </Modal>
</template>
