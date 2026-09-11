<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Alert,
  Button,
  Card,
  Descriptions,
  DescriptionsItem,
  Empty,
  Input,
  message,
  Space,
  Spin,
  Tag,
} from 'ant-design-vue';

import {
  getEmergencySopCard,
  getEmergencySopScenarios,
} from '#/api/mes/safetyEnv/emergencysop';

const scenarios = ref<[string, string][]>([]);
const chemicalCode = ref('');
const card = ref<any>(null);
const loading = ref(false);

/**
 * 扫码/选场景即看。设计文档 §八.3：处置卡随 MSDS 同步 PDA——
 * 现场戴着手套点屏幕，所以步骤一条一行、红线单独标红，不写小作文。
 */
async function pick(scenario?: string) {
  loading.value = true;
  try {
    card.value = await getEmergencySopCard({
      scenario,
      chemicalCode: chemicalCode.value || undefined,
    });
  } catch {
    card.value = null;
  } finally {
    loading.value = false;
  }
}

async function byChemical() {
  if (!chemicalCode.value) {
    message.warning('请填化学品代码');
    return;
  }
  await pick();
}

onMounted(async () => {
  try {
    scenarios.value = await getEmergencySopScenarios();
  } catch {
    scenarios.value = [];
  }
  await pick('MDI_LEAK');
});
</script>

<template>
  <Page auto-content-height>
    <Card title="泄漏应急处置卡（扫码即看）">
      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="三条内置场景 + 危化品档案"
        description="黑料（MDI）/稀释剂/废机油三条 SOP 是内置的，点场景即可看。也可填危化品代码，按档案取它的 MSDS、禁配物与应急处置措施——两个一起给，就是「预设步骤 + 这只桶里到底是什么料」。"
      />

      <Space wrap class="mb-3">
        <Button
          v-for="s in scenarios"
          :key="s[0]"
          :type="card?.scenario === s[0] ? 'primary' : 'default'"
          @click="pick(s[0])"
        >
          {{ s[1] }}
        </Button>
      </Space>

      <Space class="mb-4">
        <Input
          v-model:value="chemicalCode"
          style="width: 240px"
          placeholder="危化品代码（如 CHEM-MDI），可留空"
          allow-clear
        />
        <Button @click="byChemical">按化学品取</Button>
      </Space>

      <Spin :spinning="loading">
        <Empty v-if="!card" description="未取到处置卡" />
        <template v-else>
          <Descriptions :column="3" size="small" bordered class="mb-3">
            <DescriptionsItem label="处置卡">
              {{ card.title ?? '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="来源">
              {{ card.source ?? '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="产废类别">
              {{ card.wasteCode ?? '-' }} {{ card.wasteName ?? '' }}
            </DescriptionsItem>
            <DescriptionsItem label="化学品">
              {{ card.chemicalCode ?? '-' }} {{ card.chemicalName ?? '' }}
            </DescriptionsItem>
            <DescriptionsItem label="贮存库位">
              {{ card.storageZone ?? '-' }}
            </DescriptionsItem>
            <DescriptionsItem label="禁配物">
              <span class="text-red-500">
                {{ card.incompatibleGroups ?? '-' }}
              </span>
            </DescriptionsItem>
          </Descriptions>

          <div v-if="card.msdsUrl" class="mb-3">
            <span class="mr-2 text-gray-500">MSDS：</span>
            <a :href="card.msdsUrl" target="_blank" rel="noreferrer">
              {{ card.msdsUrl }}
            </a>
          </div>

          <div v-if="card.ppe?.length" class="mb-3">
            <div class="mb-1 font-medium">个体防护（按穿戴顺序）</div>
            <Tag v-for="p in card.ppe" :key="p" color="blue" class="mb-1">
              {{ p }}
            </Tag>
          </div>

          <div v-if="card.steps?.length" class="mb-3">
            <div class="mb-1 font-medium">处置步骤（按执行顺序）</div>
            <div
              v-for="(s, i) in card.steps"
              :key="s"
              class="leading-7"
            >
              <b>{{ i + 1 }}.</b> {{ s }}
            </div>
          </div>

          <div v-if="card.forbidden?.length" class="mb-3">
            <div class="mb-1 font-medium text-red-500">红线（最容易做错的直觉动作）</div>
            <div
              v-for="f in card.forbidden"
              :key="f"
              class="leading-7 text-red-500"
            >
              ✕ {{ f }}
            </div>
          </div>

          <div v-if="card.emergencyMeasure" class="mb-2">
            <div class="mb-1 font-medium">档案应急处置措施</div>
            <div class="whitespace-pre-wrap text-gray-600">
              {{ card.emergencyMeasure }}
            </div>
          </div>
        </template>
      </Spin>
    </Card>
  </Page>
</template>
