<script lang="ts" setup>
import type { MesHazwasteApi } from '#/api/mes/safetyEnv/hazwaste';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Input, message, Space, Tag } from 'ant-design-vue';

import {
  checkHazwasteGate,
  releaseHazwasteGate,
} from '#/api/mes/safetyEnv/hazwaste';

import SignaturePad from '../../components/SignaturePad.vue';
import { SIGN_ROLE_MAP } from '../data';

const emit = defineEmits(['success']);

const manifestNo = ref('');
const vehicleNo = ref('');
const result = ref<MesSetGateResult | null>(null);
const checked = ref(false);
/** 门卫手写板：放行时才上传，没签返回 undefined */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();
type MesSetGateResult = MesHazwasteApi.GateCheckResult;

async function doCheck() {
  if (!manifestNo.value) {
    message.warning('请先扫/填联单号');
    return;
  }
  result.value = await checkHazwasteGate({
    manifestNo: manifestNo.value,
    vehicleNo: vehicleNo.value || undefined,
  });
  checked.value = true;
}

async function doRelease() {
  if (!manifestNo.value) {
    message.warning('请先扫/填联单号');
    return;
  }
  try {
    result.value = await releaseHazwasteGate({
      manifestNo: manifestNo.value,
      vehicleNo: vehicleNo.value || undefined,
      signImg: await signPadRef.value?.commit(),
    });
    signPadRef.value?.clear();
    checked.value = true;
    message.success('已放行，联单与台账均推至「已转移」');
    emit('success');
  } catch {
    // 具体拦截原因（联单不存在/未生效/车牌不符/签字不齐）由全局提示展示
    checked.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      manifestNo.value = '';
      vehicleNo.value = '';
      result.value = null;
      checked.value = false;
      signPadRef.value?.clear();
    }
  },
});
</script>

<template>
  <Modal title="门卫硬放行（危废出厂）" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="四项缺一不放行"
        description="① 联单号存在 ② 联单已生效（已申报且运输方、接收方均确认）③ 车牌与联单登记一致 ④ 移交环保员/押运司机/接收经手人三方已签字。放行即补签门卫，并联单与台账推至「已转移」。"
      />
      <Space direction="vertical" class="w-full">
        <Input v-model:value="manifestNo" placeholder="扫/填 国家固废系统联单号" allow-clear />
        <Input v-model:value="vehicleNo" placeholder="扫/填 车牌号（可留空，留空则不核对车牌）" allow-clear />
      </Space>

      <SignaturePad ref="signPadRef" class="mt-3" />

      <div v-if="checked && result" class="mt-4">
        <div class="mb-2">
          <Tag :color="result.passed ? 'success' : 'error'">
            {{ result.passed ? '准予放行' : '拦截' }}
          </Tag>
          <span v-if="result.wasteName" class="ml-2">{{ result.wasteName }}</span>
        </div>
        <div v-if="result.reasons?.length" class="text-red-500">
          <div v-for="r in result.reasons" :key="r">· {{ r }}</div>
        </div>
        <div v-else class="text-green-600">四项校验全部通过</div>
        <div v-if="result.missingSignRoles?.length" class="mt-1 text-gray-500">
          缺签字：
          {{ result.missingSignRoles.map((r) => SIGN_ROLE_MAP[r] ?? r).join('、') }}
        </div>
        <div v-if="result.gateReleaseTime" class="mt-1 text-gray-500">
          已放行：{{ new Date(result.gateReleaseTime).toLocaleString() }} / {{ result.gateGuard }}
        </div>
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button @click="doCheck">仅校验</Button>
        <Button type="primary" danger @click="doRelease">放行</Button>
      </Space>
    </template>
  </Modal>
</template>
