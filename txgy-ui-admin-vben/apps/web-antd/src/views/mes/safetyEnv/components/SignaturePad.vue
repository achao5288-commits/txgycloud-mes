<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { base64ToFile } from '@vben/utils';

import { Button, Tooltip } from 'ant-design-vue';
import Vue3Signature from 'vue3-signature';

import { uploadFile } from '#/api/infra/file';

defineOptions({ name: 'MesSignaturePad' });

const signature = ref<InstanceType<typeof Vue3Signature>>();
/**
 * 空白画布的 base64。vue3-signature 没有 change 事件，所以"签没签"靠比对：
 * 撤销/清除回到空白也算没签，不会上传一张全白图当签名。
 */
const blank = ref('');

onMounted(() => {
  blank.value = signature.value?.save() ?? '';
});

/**
 * 有笔迹才上传，返回 infra 文件 URL（签名表 sign_img 是 varchar(255)，只能存 URL）；
 * 没签返回 undefined —— 后端 signImg 可空，留空即不存签名图。
 */
async function commit(): Promise<string | undefined> {
  const data = signature.value?.save();
  if (!data || data === blank.value) {
    return undefined;
  }
  return await uploadFile({ file: base64ToFile(data, '签名') });
}

/** 弹窗复用时清干净，免得上一单的笔迹被当成这一单的签名传上去 */
function clear() {
  signature.value?.clear();
}

defineExpose({ clear, commit });
</script>

<template>
  <div class="flex flex-col">
    <div class="mb-1 flex items-center justify-between">
      <span class="text-gray-500">手写签名（不签则本单不留签名图）</span>
      <div class="flex gap-2">
        <Tooltip title="撤销上一步">
          <Button size="small" @click="signature?.undo()">撤销</Button>
        </Tooltip>
        <Tooltip title="清空画布">
          <Button size="small" @click="signature?.clear()">清除</Button>
        </Tooltip>
      </div>
    </div>
    <Vue3Signature
      ref="signature"
      class="h-[180px] w-full border border-solid border-gray-300"
    />
  </div>
</template>
