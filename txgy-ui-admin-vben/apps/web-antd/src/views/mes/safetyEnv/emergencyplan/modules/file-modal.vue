<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  DatePicker,
  Descriptions,
  DescriptionsItem,
  Input,
  message,
  Space,
} from 'ant-design-vue';
import dayjs from 'dayjs';

import {
  fileEmergencyPlan,
  publishEmergencyPlan,
  reviewEmergencyPlan,
} from '#/api/mes/safetyEnv/emergencyplan';

const emit = defineEmits(['success']);

type Mode = 'publish' | 'file' | 'review';

const row = ref<any>({});
const mode = ref<Mode>('publish');
const publishDate = ref<string>(dayjs().format('YYYY-MM-DD'));
const filingNo = ref('');
const filingDate = ref<string>(dayjs().format('YYYY-MM-DD'));
const reason = ref('');
const submitting = ref(false);

/** 三个动作各自要求的状态：状态不对后端会拒，这里先把按钮禁掉，省一次往返 */
const canPublish = computed(
  () => row.value.status === 'DRAFT' || row.value.status === 'PUBLISHED',
);
const canFile = computed(() => row.value.status === 'PUBLISHED');
const canReview = computed(() => row.value.status !== 'DRAFT');

const STATUS_TEXT: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  FILED: '已备案',
};

async function doSubmit() {
  submitting.value = true;
  try {
    if (mode.value === 'publish') {
      await publishEmergencyPlan(row.value.id, publishDate.value);
      message.success('已发布，备案时限与评估周期已按发布日重算');
    } else if (mode.value === 'file') {
      if (!filingNo.value) {
        message.warning('请填备案号');
        return;
      }
      await fileEmergencyPlan(row.value.id, filingNo.value, filingDate.value);
      message.success('已登记备案');
    } else {
      await reviewEmergencyPlan(row.value.id, reason.value || undefined);
      message.success('已评估修订，状态回到已发布（需重新备案）');
    }
    await modalApi.close();
    emit('success');
  } finally {
    submitting.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      return;
    }
    row.value = modalApi.getData<any>() ?? {};
    // 默认落在"当前状态下一步该做的那件事"上
    mode.value =
      row.value.status === 'DRAFT'
        ? 'publish'
        : row.value.status === 'PUBLISHED'
          ? 'file'
          : 'review';
    publishDate.value = row.value.publishDate ?? dayjs().format('YYYY-MM-DD');
    filingNo.value = row.value.filingNo ?? '';
    filingDate.value = row.value.filingDate ?? dayjs().format('YYYY-MM-DD');
    reason.value = '';
  },
});
</script>

<template>
  <Modal title="预案发布 / 备案 / 评估修订" class="w-1/2">
    <div class="mx-4">
      <Descriptions :column="2" size="small" bordered class="mb-3">
        <DescriptionsItem label="预案编号">
          {{ row.planNo ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="预案名称">
          {{ row.planName ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="当前状态">
          {{ STATUS_TEXT[row.status ?? ''] ?? row.status ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="版本">
          {{ row.version ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="发布日期">
          {{ row.publishDate ?? '未发布' }}
        </DescriptionsItem>
        <DescriptionsItem label="备案截止日">
          <span
            :class="
              row.filingDeadline &&
              row.status !== 'FILED' &&
              row.filingDeadline < dayjs().format('YYYY-MM-DD')
                ? 'text-red-500'
                : ''
            "
          >
            {{ row.filingDeadline ?? '-' }}
          </span>
        </DescriptionsItem>
        <DescriptionsItem label="备案号">
          {{ row.filingNo ?? '未备案' }}
        </DescriptionsItem>
        <DescriptionsItem label="下次评估修订">
          {{ row.nextReviewDate ?? '-' }}
        </DescriptionsItem>
      </Descriptions>

      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="三个动作各自的前提"
        description="发布：填发布日期，备案时限（+20 个工作日）与评估周期（+3 年）都从这天起算，没发布日就不算数。备案：报生态环境部门后登记备案号；逾期仍允许备案（只登记，不拦）。评估修订：工艺/物料/法规变化或 3 年到期时走这一步，修订后状态回到已发布，需重新备案。已备案的预案不允许直接改发布日期与版本——改了备案号就对不上了。"
      />

      <Space class="mb-3">
        <Button
          :type="mode === 'publish' ? 'primary' : 'default'"
          :disabled="!canPublish"
          @click="mode = 'publish'"
        >
          发布
        </Button>
        <Button
          :type="mode === 'file' ? 'primary' : 'default'"
          :disabled="!canFile"
          @click="mode = 'file'"
        >
          备案登记
        </Button>
        <Button
          :type="mode === 'review' ? 'primary' : 'default'"
          :disabled="!canReview"
          @click="mode = 'review'"
        >
          评估修订
        </Button>
      </Space>

      <div v-if="mode === 'publish'">
        <div class="mb-1 text-gray-500">发布日期</div>
        <DatePicker
          v-model:value="publishDate"
          class="w-full"
          value-format="YYYY-MM-DD"
          format="YYYY-MM-DD"
        />
      </div>

      <div v-else-if="mode === 'file'">
        <div class="mb-1 text-gray-500">备案号</div>
        <Input v-model:value="filingNo" placeholder="如 京环备 2026-0123" allow-clear />
        <div class="mt-2 mb-1 text-gray-500">备案日期</div>
        <DatePicker
          v-model:value="filingDate"
          class="w-full"
          value-format="YYYY-MM-DD"
          format="YYYY-MM-DD"
        />
      </div>

      <div v-else>
        <div class="mb-1 text-gray-500">修订原因（工艺/物料/法规变化）</div>
        <Input.TextArea
          v-model:value="reason"
          :rows="3"
          placeholder="如：新增稀释剂储罐，泄漏风险与处置措施变化"
        />
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">取消</Button>
        <Button type="primary" :loading="submitting" @click="doSubmit">
          确定
        </Button>
      </Space>
    </template>
  </Modal>
</template>
