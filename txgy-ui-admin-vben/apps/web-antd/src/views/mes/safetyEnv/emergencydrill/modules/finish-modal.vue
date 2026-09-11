<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  Descriptions,
  DescriptionsItem,
  Input,
  message,
  Space,
  Tag,
} from 'ant-design-vue';

import {
  closeEmergencyDrill,
  finishEmergencyDrill,
} from '#/api/mes/safetyEnv/emergencydrill';

import { DRILL_STATUS_MAP, RECTIFY_STATUS_MAP } from '../data';

const emit = defineEmits(['success']);

const row = ref<any>({});
const evaluation = ref('');
const submitting = ref(false);

const isPlanned = computed(() => row.value.status === 'PLANNED');
const isDone = computed(() => row.value.status === 'DONE');
const hasRecord = computed(
  () =>
    !!row.value.signSheetUrl ||
    !!row.value.photoUrl ||
    !!row.value.videoUrl,
);
const needRectify = computed(
  () => isDone.value && row.value.rectifyStatus === 'PENDING',
);

/** 签到表 / 照片 / 视频 三项留痕 */
const records = computed(() => [
  { label: '签到表', url: row.value.signSheetUrl },
  { label: '演练照片', url: row.value.photoUrl },
  { label: '演练视频', url: row.value.videoUrl },
]);

async function doFinish() {
  if (!hasRecord.value) {
    message.warning('签到表/照片/视频至少留一项');
    return;
  }
  if (!evaluation.value && !row.value.evaluation) {
    message.warning('请填评估结论');
    return;
  }
  submitting.value = true;
  try {
    await finishEmergencyDrill(row.value.id, evaluation.value || undefined);
    message.success('演练已完成');
    await modalApi.close();
    emit('success');
  } finally {
    submitting.value = false;
  }
}

async function doClose() {
  submitting.value = true;
  try {
    await closeEmergencyDrill(row.value.id);
    message.success('演练已闭环');
    await modalApi.close();
    emit('success');
  } finally {
    submitting.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      return;
    }
    row.value = modalApi.getData<any>() ?? {};
    evaluation.value = row.value.evaluation ?? '';
  },
});
</script>

<template>
  <Modal title="演练记录 / 评估 / 闭环" class="w-1/2">
    <div class="mx-4">
      <Descriptions :column="2" size="small" bordered class="mb-3">
        <DescriptionsItem label="演练编号">
          {{ row.drillNo ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="演练名称">
          {{ row.drillName ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="演练日期">
          {{ row.drillDate ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="参加人数">
          {{ row.participantCount ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="状态">
          <Tag :color="DRILL_STATUS_MAP[row.status ?? '']?.color ?? 'default'">
            {{ DRILL_STATUS_MAP[row.status ?? '']?.text ?? row.status ?? '-' }}
          </Tag>
        </DescriptionsItem>
        <DescriptionsItem label="整改状态">
          <Tag
            :color="
              RECTIFY_STATUS_MAP[row.rectifyStatus ?? '']?.color ?? 'default'
            "
          >
            {{
              RECTIFY_STATUS_MAP[row.rectifyStatus ?? '']?.text ??
              row.rectifyStatus ??
              '-'
            }}
          </Tag>
        </DescriptionsItem>
      </Descriptions>

      <Alert
        class="mb-3"
        :type="isPlanned && !hasRecord ? 'warning' : 'info'"
        show-icon
        message="完成 → 闭环，两步都拦"
        description="完成（已计划→已演练）：签到表/照片/视频至少留一项，且评估结论不能为空——计划→记录→评估缺一步都不算演练完成。闭环（已演练→已闭环）：有整改要求但未整改完成的不能闭环，整改闭环是演练闭环的前置。"
      />

      <div class="mb-2">
        <span class="mr-2 text-gray-500">记录留痕：</span>
        <Tag v-for="r in records" :key="r.label" :color="r.url ? 'success' : 'default'">
          {{ r.label }}{{ r.url ? ' 已有' : ' 无' }}
        </Tag>
      </div>

      <div v-if="isPlanned">
        <div class="mb-1 text-gray-500">评估结论</div>
        <Input.TextArea
          v-model:value="evaluation"
          :rows="3"
          placeholder="如：报警响应 3 分钟内到位，围堵有效；不足：洗眼器位置不明显，建议加标识"
        />
      </div>

      <div v-else-if="isDone" class="text-gray-500">
        <div>评估结论：{{ row.evaluation || '（空）' }}</div>
        <div v-if="row.rectifyRequirement" class="mt-1">
          整改要求：{{ row.rectifyRequirement }}
        </div>
        <div v-if="needRectify" class="mt-2 text-red-500">
          整改未完成，暂不能闭环。请先改演练单把整改要求清空或标记完成。
        </div>
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">取消</Button>
        <Button v-if="isPlanned" type="primary" :loading="submitting" @click="doFinish">
          标记已完成
        </Button>
        <Button
          v-else-if="isDone"
          type="primary"
          :disabled="needRectify"
          :loading="submitting"
          @click="doClose"
        >
          闭环
        </Button>
      </Space>
    </template>
  </Modal>
</template>
