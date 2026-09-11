<script lang="ts" setup>
import type { MesHazwasteApi } from '#/api/mes/safetyEnv/hazwaste';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { useQRCode } from '@vueuse/integrations/useQRCode';

import { Button, message, Space, Tag } from 'ant-design-vue';

import {
  archiveHazwasteLabel,
  getHazwasteLabel,
} from '#/api/mes/safetyEnv/hazwaste';

const emit = defineEmits(['success']);

const label = ref<MesHazwasteApi.HazwasteLabel | null>(null);
const qr = useQRCode(() => label.value?.qrContent ?? '');
const currentId = ref<number>(0);

async function archive() {
  try {
    const url = await archiveHazwasteLabel(currentId.value);
    label.value = await getHazwasteLabel(currentId.value);
    message.success('标签已归档到文件服务');
    emit('success');
    return url;
  } catch {
    // 归档失败原因由全局提示展示
    return undefined;
  }
}

/** 只打印标签本身：开一个干净窗口，不牵扯外层页面的样式 */
function print() {
  const el = document.querySelector('#hj1276-label');
  if (!el) {
    return;
  }
  const win = window.open('', '_blank', 'width=640,height=800');
  if (!win) {
    message.warning('浏览器拦截了打印窗口，请允许弹窗后重试');
    return;
  }
  win.document.write(`<html><head><title>危险废物标签</title>
    <style>body{margin:0;padding:16px;font-family:sans-serif}</style>
    </head><body>${el.outerHTML}</body></html>`);
  win.document.close();
  win.focus();
  win.print();
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      label.value = null;
      currentId.value = 0;
      return;
    }
    const data = modalApi.getData<{ id: number }>();
    currentId.value = data.id;
    label.value = await getHazwasteLabel(data.id);
  },
});
</script>

<template>
  <Modal title="危险废物标签（HJ 1276-2022）" class="w-1/2">
    <div class="mx-4">
      <div
        id="hj1276-label"
        class="mx-auto w-[420px] border-2 border-black p-3 text-black"
      >
        <div class="flex items-center justify-between border-b border-black pb-2">
          <div class="text-2xl font-bold tracking-widest">危险废物</div>
          <div class="text-sm">HJ 1276-2022</div>
        </div>

        <div class="mt-2 flex gap-3">
          <div class="flex-1 text-sm leading-7">
            <div>废物名称：{{ label?.wasteName ?? '-' }}</div>
            <div>废物类别：{{ label?.wasteCode ?? '-' }}</div>
            <div>
              危险特性：<Tag v-if="label?.hazardTraits" color="red">{{ label?.hazardTraits }}</Tag>
              <span v-else class="text-gray-400">未配置</span>
            </div>
            <div>产生单位：{{ label?.generateUnit ?? '-' }}</div>
            <div>容器码：{{ label?.containerCode ?? label?.qrContent ?? '-' }}</div>
            <div>
              重量：{{ label?.quantity ?? '-' }} {{ label?.quantityUnit ?? '' }}
            </div>
            <div>日期：{{ label?.labelDate ?? '-' }}</div>
          </div>
          <div class="flex w-[110px] flex-col items-center justify-center">
            <img v-if="qr" :src="qr" alt="容器码" class="h-[104px] w-[104px]" />
            <div class="mt-1 text-center text-[11px] text-gray-600">
              扫码查该桶全程
            </div>
          </div>
        </div>
      </div>

      <div v-if="label?.labelUrl" class="mt-3 text-xs text-gray-500 break-all">
        已归档：{{ label.labelUrl }}
      </div>
      <div v-if="!label?.hazardTraits" class="mt-3 text-xs text-gray-500">
        危险特性取自 HW 代码映射，未收录的代码刻意留空——写错比留空更危险，请按现行《国家危险废物名录》人工补录。
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button @click="archive">归档标签</Button>
        <Button type="primary" @click="print">打印</Button>
      </Space>
    </template>
  </Modal>
</template>
