<script lang="ts" setup>
import { ref } from 'vue';

import { base64ToFile } from '@vben/utils';

import { Button, Tooltip } from 'ant-design-vue';
import Vue3Signature from 'vue3-signature';

import { uploadFile } from '#/api/infra/file';

defineOptions({ name: 'MesSignaturePad' });

/**
 * 标题文案由调用方给：签名有的是可选的（复核留痕），有的是硬性卡口
 *（在库环保视图/发起检测，后端缺签名直接拒），板子上写错一个字就是误导。
 */
withDefaults(defineProps<{ label?: string }>(), {
  label: '手写签名（不签则本单不留签名图）',
});

const signature = ref<InstanceType<typeof Vue3Signature>>();

/**
 * 有笔迹才上传，返回 infra 文件 URL（签名表 sign_img 是 varchar(255)，只能存 URL）；
 * 没签返回 undefined —— 后端 signImg 可空，留空即不存签名图。
 *
 * "签没签"必须问插件自己的笔迹数组（isEmpty），**不能**拿当前 base64 跟挂载时的
 * 空白 base64 比对：画布尺寸是插件挂载后按父容器量出来的（下面 h/w 两个 prop），
 * 尺寸一变 base64 就变，于是没落笔也会被判成"签了"，把一张全白图传上去当签名。
 * 撤销/清除回到空白同样是没签。
 */
async function commit(): Promise<string | undefined> {
  if (!signature.value || signature.value.isEmpty()) {
    return undefined;
  }
  const data = signature.value.save();
  return data ? await uploadFile({ file: base64ToFile(data, '签名') }) : undefined;
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
      <span class="text-gray-500">{{ label }}</span>
      <div class="flex gap-2">
        <Tooltip title="撤销上一步">
          <Button size="small" @click="signature?.undo()">撤销</Button>
        </Tooltip>
        <Tooltip title="清空画布">
          <Button size="small" @click="signature?.clear()">清除</Button>
        </Tooltip>
      </div>
    </div>
    <!--
      高度必须走插件的 h prop（它给外层 div 打成内联 height:180px），不能只写
      Tailwind 的 h-[180px]：插件的根 div 自己也带内联 height:100%，内联压过 class，
      于是"父容器高度 = 画布 100% = 父容器高度"循环解析，实测画布长到 403px（抽屉整屏），
      把下面的会签按钮顶出可视区 —— 用户画完签名找不到提交按钮，就是"签字签不了"。
    -->
    <Vue3Signature
      ref="signature"
      h="180px"
      w="100%"
      class="w-full border border-solid border-gray-300"
    />
  </div>
</template>
