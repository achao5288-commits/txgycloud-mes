<script lang="ts" setup>
import type { MesChemicalApi } from '#/api/mes/safetyEnv/chemical';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Input, InputNumber, message, Space, Tag } from 'ant-design-vue';

import {
  checkChemicalStorage,
  getChemicalByNo,
  stockInChemical,
} from '#/api/mes/safetyEnv/chemical';

const emit = defineEmits(['success']);

const profileNo = ref('');
const storageLocation = ref('');
const quantity = ref<number>();
const result = ref<MesChemicalApi.StorageCheckResult | null>(null);
const checked = ref(false);

/** 四道校验逐条的过/不过，做成一眼能看懂的清单 */
function items(r: MesChemicalApi.StorageCheckResult) {
  return [
    { key: 'msdsOk', label: '① MSDS 已挂载', ok: r.msdsOk },
    { key: 'zoneOk', label: '② 储存专区符合', ok: r.zoneOk },
    {
      key: 'incompatible',
      label: '③ 同库位无禁配物料',
      ok: (r.incompatibleWith?.length ?? 0) === 0,
    },
    {
      key: 'quotaOk',
      label: '④ 未超储量上限',
      // 没给本次入库量时后端不判量，这里明确显示"未判"，不当成通过
      ok: r.quotaOk === null || r.quotaOk === undefined ? null : r.quotaOk,
    },
  ];
}

async function doCheck() {
  if (!profileNo.value) {
    message.warning('请先填档案编号');
    return;
  }
  result.value = await checkChemicalStorage({
    profileNo: profileNo.value,
    storageLocation: storageLocation.value || undefined,
    quantity: quantity.value ?? undefined,
  });
  checked.value = true;
}

async function doStockIn() {
  if (!profileNo.value) {
    message.warning('请先填档案编号');
    return;
  }
  if (!quantity.value || quantity.value <= 0) {
    message.warning('请填入库量（须大于 0）');
    return;
  }
  try {
    result.value = await stockInChemical({
      profileNo: profileNo.value,
      quantity: quantity.value,
      storageLocation: storageLocation.value || undefined,
    });
    checked.value = true;
    message.success('已入库，存量已累加');
    emit('success');
  } catch {
    // 具体拦截码（未挂 MSDS / 专区不符 / 禁配 / 超量）由全局提示展示
    checked.value = false;
  }
}

/** 带出物料名，帮库管确认没扫错货 */
async function doLoadName() {
  if (!profileNo.value) {
    return;
  }
  try {
    const p = await getChemicalByNo(profileNo.value);
    result.value = { profileNo: p.profileNo, chemicalName: p.chemicalName };
    checked.value = false;
  } catch {
    result.value = null;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      profileNo.value = '';
      storageLocation.value = '';
      quantity.value = undefined;
      result.value = null;
      checked.value = false;
      return;
    }
    // 从列表行点进来时带上档案编号与默认库位，省得库管照着手抄
    const data = modalApi.getData<{ profileNo?: string; storageLocation?: string }>();
    if (data?.profileNo) {
      profileNo.value = data.profileNo;
      storageLocation.value = data.storageLocation ?? '';
      await doLoadName();
    }
  },
});
</script>

<template>
  <Modal title="危化品库位校验 / 入库（四道校验）" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="四道校验缺一不放行"
        description="① 已挂载 MSDS（无 MSDS 的危化品不得入库）② 储存专区符合（稀释剂等易燃品须进防爆区）③ 同库位无禁配物料（黑料与白料禁同区）④ 不超储量上限。任一条不过即拒，且**拦在写库之前**——不返回 passed=false 却把货记进去。"
      />
      <Space direction="vertical" class="w-full">
        <Input
          v-model:value="profileNo"
          placeholder="危化品档案编号（如 CH-2026-001）"
          allow-clear
          @blur="doLoadName"
        />
        <Input
          v-model:value="storageLocation"
          placeholder="目标库位（留空则用档案上的库位）"
          allow-clear
        />
        <InputNumber
          v-model:value="quantity"
          class="w-full"
          :min="0"
          placeholder="本次入库量（留空则第④条不判）"
        />
      </Space>

      <div v-if="result" class="mt-4">
        <div class="mb-2">
          <span v-if="result.chemicalName" class="font-medium">
            {{ result.chemicalName }}
          </span>
          <span v-if="result.compatGroupName" class="ml-2 text-gray-500">
            相容组：{{ result.compatGroupName }}
          </span>
          <span v-if="result.storageLimit !== undefined && result.storageLimit !== null" class="ml-2 text-gray-500">
            上限 {{ result.storageLimit }} / 现有 {{ result.stockQuantity ?? 0 }}
          </span>
        </div>

        <div v-if="checked">
          <div v-for="it in items(result)" :key="it.key" class="leading-7">
            <Tag :color="it.ok === null ? 'default' : it.ok ? 'success' : 'error'">
              {{ it.ok === null ? '未判' : it.ok ? '通过' : '拦截' }}
            </Tag>
            {{ it.label }}
          </div>

          <div v-if="result.incompatibleWith?.length" class="mt-1 text-red-500">
            <div v-for="x in result.incompatibleWith" :key="x">· 禁配：{{ x }}</div>
          </div>
          <div v-if="result.reasons?.length" class="mt-2 text-red-500">
            <div v-for="r in result.reasons" :key="r">· {{ r }}</div>
          </div>
          <div v-else class="mt-2 text-green-600">四道校验全部通过</div>
        </div>
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button @click="doCheck">仅校验</Button>
        <Button type="primary" danger @click="doStockIn">入库</Button>
      </Space>
    </template>
  </Modal>
</template>
