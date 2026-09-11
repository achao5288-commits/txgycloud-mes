<script lang="ts" setup>
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  Descriptions,
  DescriptionsItem,
  Input,
  InputNumber,
  message,
  Space,
} from 'ant-design-vue';

import { getFacility, replaceConsumable } from '#/api/mes/safetyEnv/facility';

const emit = defineEmits(['success']);

const facilityId = ref<number>();
const facility = ref<MesFacilityApi.TreatmentFacility>({});
const quantity = ref<number>();
const replaceDate = ref('');
const containerCode = ref('');
const storageLocation = ref('');
const result = ref<MesFacilityApi.ReplaceResult | null>(null);

async function doReplace() {
  if (!facilityId.value) {
    return;
  }
  if (!quantity.value || quantity.value <= 0) {
    message.warning('请填换下的耗材量（须大于 0）—— 换炭必须同时记危废');
    return;
  }
  try {
    result.value = await replaceConsumable({
      id: facilityId.value,
      quantity: quantity.value,
      replaceDate: replaceDate.value || undefined,
      containerCode: containerCode.value || undefined,
      storageLocation: storageLocation.value || undefined,
    });
    message.success('已换炭，废活性炭 HW49 已同时登记入桶');
    emit('success');
  } catch {
    // 量不合法等拦截码由全局提示展示
    result.value = null;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      facilityId.value = undefined;
      facility.value = {};
      quantity.value = undefined;
      replaceDate.value = '';
      containerCode.value = '';
      storageLocation.value = '';
      result.value = null;
      return;
    }
    const data = modalApi.getData<{ id?: number }>();
    if (!data?.id) {
      return;
    }
    facilityId.value = data.id;
    modalApi.lock();
    try {
      facility.value = await getFacility(data.id);
      containerCode.value = `${facility.value.facilityNo ?? ''}-CTN`;
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal title="耗材更换（换炭 → 废活性炭 HW49）" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="换炭与危废登记是同一件事"
        description="换下的活性炭就是 HW49 废活性炭，本操作在**同一个事务**里登记危废台账并顺延下次更换日期——换了炭不记危废在结构上做不到。量必须大于 0。"
      />
      <Descriptions :column="2" size="small" bordered class="mb-3">
        <DescriptionsItem label="设施编号">
          {{ facility.facilityNo ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="设施名称">
          {{ facility.facilityName ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="耗材">
          {{ facility.consumableName ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="更换周期">
          {{ facility.replaceCycleDays ? `${facility.replaceCycleDays} 天` : '未设' }}
        </DescriptionsItem>
        <DescriptionsItem label="上次更换">
          {{ facility.lastReplaceDate ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="下次更换">
          {{ facility.nextReplaceDate ?? '-' }}
        </DescriptionsItem>
      </Descriptions>

      <Space direction="vertical" class="w-full">
        <InputNumber
          v-model:value="quantity"
          class="w-full"
          :min="0"
          placeholder="换下的耗材量（kg，须大于 0）"
        />
        <Input
          v-model:value="replaceDate"
          placeholder="更换日期 yyyy-MM-dd（留空取今天）"
          allow-clear
        />
        <Input v-model:value="containerCode" placeholder="容器编码" allow-clear />
        <Input
          v-model:value="storageLocation"
          placeholder="暂存库位（如：危废暂存间A）"
          allow-clear
        />
      </Space>

      <div v-if="result" class="mt-3 text-green-600">
        <div>{{ result.message }}</div>
        <div class="text-gray-500">
          危废联单号 {{ result.wasteManifestNo }} · 危废台账 #{{ result.wasteLedgerId }} ·
          下次更换 {{ result.nextReplaceDate }}
        </div>
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button type="primary" danger @click="doReplace">确认换炭并登记危废</Button>
      </Space>
    </template>
  </Modal>
</template>
