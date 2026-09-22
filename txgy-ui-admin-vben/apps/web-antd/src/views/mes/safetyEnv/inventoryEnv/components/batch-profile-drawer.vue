<script lang="ts" setup>
import type { MesInventoryEnvApi } from '#/api/mes/safetyEnv/inventoryEnv';

import { computed, ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { Alert, Button, Descriptions, DescriptionsItem, Spin, Tag } from 'ant-design-vue';

import { getBatchProfile } from '#/api/mes/safetyEnv/inventoryEnv';

const profile = ref<MesInventoryEnvApi.BatchProfile>();
const loading = ref(false);

/** 抽出来单独用：抽屉已开时再调 open() 不会再触发 onOpenChange，「以此为起点」得直接调它 */
async function loadProfile(batchCode?: string) {
  if (!batchCode) {
    return;
  }
  drawerApi.lock();
  loading.value = true;
  try {
    profile.value = await getBatchProfile(batchCode);
  } finally {
    loading.value = false;
    drawerApi.unlock();
  }
}

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(isOpen) {
    if (!isOpen) {
      profile.value = undefined;
      return;
    }
    await loadProfile(drawerApi.getData<{ batchCode?: string }>()?.batchCode);
  },
});

/**
 * 「当前投影」和「最近判定结论」是两件事，必须分开显示：
 * 判过 POLLUTED 但台账已处置闭环 → 投影会清空，此时该批当前是干净的，历史结论仍是有污染。
 */
function projectionText(node: MesInventoryEnvApi.BatchProfileNode) {
  if (node.pollutionStatus === 'POLLUTED') {
    return node.pollutionLocation ? `污染·${node.pollutionLocation}` : '污染';
  }
  return '无污染管控';
}

function judgedText(node: MesInventoryEnvApi.BatchProfileNode) {
  if (!node.judged) {
    return '未判定';
  }
  return node.lastReviewResult === 'POLLUTED' ? '判有污染' : '判无污染';
}

const chain = computed(() => [
  { key: 'backward', title: '后向（本批由什么来）', nodes: profile.value?.backward ?? [] },
  { key: 'forward', title: '前向（本批流向哪去）', nodes: profile.value?.forward ?? [] },
]);

defineExpose({
  open: (batchCode: string) => drawerApi.setData({ batchCode }).open(),
});
</script>

<template>
  <Drawer :title="`批次档案 ${profile?.focus?.batchCode ?? ''}`" class="w-[720px]">
    <Spin :spinning="loading">
      <template v-if="profile">
        <!-- 未判定 ≠ 无污染：这两个数字分开报，别让看的人自己推 -->
        <Alert
          v-if="profile.unjudgedCount"
          class="mb-3"
          type="warning"
          show-icon
          :message="`链上还有 ${profile.unjudgedCount} 个批次从未判定`"
          description="未判定不等于无污染，只是没人判过，不能当作放行依据。"
        />

        <Descriptions bordered :column="2" size="small" title="档案主体">
          <DescriptionsItem label="批次号">{{ profile.focus?.batchCode ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="物料">{{ profile.focus?.itemName ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="当前污染投影">
            <Tag :color="profile.focus?.pollutionStatus === 'POLLUTED' ? 'error' : 'default'">
              {{ projectionText(profile.focus ?? {}) }}
            </Tag>
            <Tag v-if="profile.focus?.pollutionMarked" color="volcano">终审标记</Tag>
          </DescriptionsItem>
          <DescriptionsItem label="最近判定">
            <Tag :color="profile.focus?.judged ? (profile.focus?.lastReviewResult === 'POLLUTED' ? 'error' : 'success') : 'default'">
              {{ judgedText(profile.focus ?? {}) }}
            </Tag>
            <span v-if="profile.focus?.lastCheckRecordNo" class="ml-1 text-gray-500">
              {{ profile.focus.lastCheckRecordNo }}
            </span>
          </DescriptionsItem>
          <DescriptionsItem label="来源判定单">{{ profile.focus?.pollutionSrcRecord ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="是否在库">
            <Tag :color="profile.focus?.inStock ? 'success' : 'default'">
              {{ profile.focus?.inStock ? '在库' : '已出库/无库存' }}
            </Tag>
          </DescriptionsItem>
        </Descriptions>

        <div v-for="group in chain" :key="group.key" class="mt-4">
          <div class="mb-2 font-medium">
            {{ group.title }}
            <span class="ml-1 text-gray-400">共 {{ group.nodes.length }} 个</span>
          </div>
          <div v-if="group.nodes.length === 0" class="text-gray-400">无关联批次</div>
          <div
            v-for="node in group.nodes"
            :key="node.batchCode"
            class="mb-2 flex flex-wrap items-center gap-2 rounded border border-gray-200 px-3 py-2"
          >
            <span class="font-medium">{{ node.batchCode }}</span>
            <span class="text-gray-500">{{ node.itemName ?? '-' }}</span>
            <Tag :color="node.pollutionStatus === 'POLLUTED' ? 'error' : 'default'">
              {{ projectionText(node) }}
            </Tag>
            <Tag :color="node.judged ? (node.lastReviewResult === 'POLLUTED' ? 'error' : 'success') : 'warning'">
              {{ judgedText(node) }}
            </Tag>
            <Tag v-if="node.pollutionMarked" color="volcano">终审标记</Tag>
            <Tag :color="node.inStock ? 'success' : 'default'">{{ node.inStock ? '在库' : '不在库' }}</Tag>
            <Button size="small" type="link" @click="loadProfile(node.batchCode)">
              以此为起点
            </Button>
          </div>
        </div>
      </template>
    </Spin>
  </Drawer>
</template>
