<script lang="ts" setup>
import type { MesInventoryEnvApi } from '#/api/mes/safetyEnv/inventoryEnv';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message } from 'ant-design-vue';

import { changePollutionControlLocation } from '#/api/mes/safetyEnv/pollutionCheck';
import SignaturePad from '#/views/mes/safetyEnv/components/SignaturePad.vue';
import WmWarehouseAreaSelect from '#/views/mes/wm/warehouse/components/area-select.vue';

const emit = defineEmits<{ success: [] }>();

/** 当前待调整的行；checkId 是唯一入参，其余字段只用于展示上下文 */
const row = ref<MesInventoryEnvApi.InventoryEnv>();
const locationId = ref<number>();
/** 手写签名板：本来就身在弹窗里，直接嵌，不再套一层签字弹窗 */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const polluted = computed(() => row.value?.reviewResult === 'POLLUTED');

/**
 * 受控库位是物理事实，复核后仍要能改（倒库、危废间扩容）。
 * 有污染/无污染的库位口径由后端门禁裁决——前端不重复实现一套，只把规则提示出来，
 * 免得两边各写一份规则后走偏（真要放宽时只改后端一处）。
 *
 * 签字必画：这是环保专员手改的物理事实，后端缺签名直接拒（1040818019）。
 * 前端拦"没画"（commit 返回 undefined），后端拦"没带"——两边各拦一半，缺一不可。
 */
async function handleSubmit() {
  const checkId = row.value?.checkId;
  if (!checkId) {
    message.warning('该批次没有已复核的判定记录，无法调整受控库位');
    return;
  }
  if (!locationId.value) {
    message.warning('请选择目标库位');
    return;
  }
  const signImg = await signPadRef.value?.commit();
  if (!signImg) {
    message.warning('请先在签名板上手写签名（空白签名不算数）');
    return;
  }
  modalApi.lock();
  try {
    await changePollutionControlLocation({ id: checkId, locationId: locationId.value, signImg });
    message.success('受控库位已调整，台账与批次污染戳已同步');
    modalApi.close();
    emit('success');
  } finally {
    modalApi.unlock();
  }
}

const [Modal, modalApi] = useVbenModal({
  onConfirm: handleSubmit,
  onOpenChange(isOpen) {
    if (!isOpen) {
      row.value = undefined;
      locationId.value = undefined;
      signPadRef.value?.clear(); // 上一行的笔迹不能当这一行的签名
      return;
    }
    row.value = modalApi.getData<MesInventoryEnvApi.InventoryEnv>();
    // 预填当前库位，让「从哪搬到哪」一眼可见；用户原样确定也不会有副作用（幂等写回同一个值）
    locationId.value = row.value?.checkLocationId;
  },
});
</script>

<template>
  <Modal :title="`受控库位调整 · ${row?.batchCode ?? ''}`">
    <Alert
      class="mb-3"
      :description="
        polluted
          ? '只能选污染管控（受控/危废）库位；下拉里带「受控库位」标签的才是。'
          : '不得占用污染管控库位，请选普通库位（防混放）。'
      "
      :message="polluted ? '该批次复核为「有污染」' : '该批次复核为「无污染」'"
      show-icon
      :type="polluted ? 'warning' : 'info'"
    />
    <div class="mb-3 text-sm text-gray-500">
      当前库位：{{ row?.checkLocation || '未指定' }}
      <span v-if="row?.checkRecordNo" class="ml-2">判定记录：{{ row.checkRecordNo }}</span>
    </div>
    <div class="mb-1 text-sm">目标库位</div>
    <WmWarehouseAreaSelect v-model="locationId" placeholder="请选择目标库位" />
    <SignaturePad ref="signPadRef" class="mt-3" label="手写签名（必画，空白不放行）" />
  </Modal>
</template>
