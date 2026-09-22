<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import { Alert, Button, Input, message, Select, Space } from 'ant-design-vue';

import { createSignRecord } from '#/api/mes/safetyEnv/pollutionTrace';
import { getSimpleUserList } from '#/api/system/user';

import SignaturePad from '../../components/SignaturePad.vue';

const emit = defineEmits(['success']);

/** 签字角色：与 SIGN_ROLE_MAP（trace-drawer）同源，改一处要改两处 */
const ROLE_OPTIONS = [
  { label: '复核人', value: 'REVIEWER' },
  { label: '录入人', value: 'OPERATOR' },
  { label: '审批人', value: 'APPROVER' },
];

const userStore = useUserStore();

const bizType = ref('');
const bizNo = ref('');
const signUserId = ref<number | undefined>(undefined);
const signRole = ref('REVIEWER');
const location = ref('');
const opinion = ref('');
const userOptions = ref<{ label: string; value: number }[]>([]);
const submitting = ref(false);
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

/** 代签提示：选的不是当前登录人时，明确告知这一笔会记成代签 */
const isProxy = ref(false);

function checkProxy() {
  const me = Number(userStore.userInfo?.userId ?? 0);
  isProxy.value = !!signUserId.value && signUserId.value !== me;
}

async function loadUsers() {
  const users = await getSimpleUserList();
  userOptions.value = (users ?? []).map((u) => ({
    label: u.nickname ?? String(u.id),
    value: Number(u.id),
  }));
}

async function doSubmit() {
  if (!signUserId.value) {
    message.warning('请选择签字人');
    return;
  }
  submitting.value = true;
  try {
    await createSignRecord({
      bizType: bizType.value,
      bizNo: bizNo.value,
      signRole: signRole.value,
      signUserId: signUserId.value,
      location: location.value || undefined,
      opinion: opinion.value || undefined,
      signImg: await signPadRef.value?.commit(),
    });
    message.success('已签字');
    emit('success');
    modalApi.close();
  } finally {
    submitting.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      bizType.value = '';
      bizNo.value = '';
      signUserId.value = undefined;
      signRole.value = 'REVIEWER';
      location.value = '';
      opinion.value = '';
      isProxy.value = false;
      signPadRef.value?.clear();
      return;
    }
    const data = modalApi.getData<{ bizNo?: string; bizType?: string; }>();
    bizType.value = data?.bizType ?? '';
    bizNo.value = data?.bizNo ?? '';
    // 默认签自己：多数场景就是本人点一下，代签才需要动手改
    signUserId.value = Number(userStore.userInfo?.userId ?? 0) || undefined;
    checkProxy();
    if (userOptions.value.length === 0) {
      await loadUsers();
    }
  },
});
</script>

<template>
  <Modal title="签字" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="签字落在当前追溯对象上，与复核自动留的那条走同一个业务键"
        description="签名图由服务端存链接；签字人姓名按所选账号反查，前端传的名字不作数。"
      />

      <Space direction="vertical" class="w-full">
        <div class="text-muted-foreground text-xs">
          业务单号：<span class="font-medium">{{ bizNo || '-' }}</span>
          <span class="ml-2">{{ bizType === 'LEDGER' ? '台账处置' : '污染判定' }}</span>
        </div>
        <Select
          v-model:value="signUserId"
          class="w-full"
          show-search
          option-filter-prop="label"
          :options="userOptions"
          placeholder="签字人（默认当前登录人）"
          @change="checkProxy"
        />
        <Alert v-if="isProxy" type="warning" show-icon message="你正在代签：系统会同时记录实际操作账号" />
        <Select v-model:value="signRole" class="w-full" :options="ROLE_OPTIONS" placeholder="签字角色" />
        <Input v-model:value="location" allow-clear placeholder="签字地点/去向（可选）" />
        <Input v-model:value="opinion" allow-clear placeholder="签署意见（可选）" />
      </Space>

      <SignaturePad ref="signPadRef" class="mt-3" />
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">取消</Button>
        <Button type="primary" :loading="submitting" @click="doSubmit">确认签字</Button>
      </Space>
    </template>
  </Modal>
</template>
