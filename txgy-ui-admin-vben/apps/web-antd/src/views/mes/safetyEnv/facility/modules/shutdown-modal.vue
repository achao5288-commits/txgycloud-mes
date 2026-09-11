<script lang="ts" setup>
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  Descriptions,
  DescriptionsItem,
  message,
  Space,
  Tag,
  Textarea,
} from 'ant-design-vue';

import {
  approveShutdown,
  declareShutdown,
  getFacility,
  resumeFacility,
} from '#/api/mes/safetyEnv/facility';

import AttachmentPanel from '../../components/AttachmentPanel.vue';
import SignaturePad from '../../components/SignaturePad.vue';
import { SHUTDOWN_STATUS_MAP } from '../data';

const emit = defineEmits(['success']);

const facilityId = ref<number>();
const facility = ref<MesFacilityApi.TreatmentFacility>({});
const reason = ref('');
const opinion = ref('');
/** 手写签名板：审批时才上传，没签返回 undefined */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const status = computed(() => facility.value.shutdownStatus ?? 'NONE');
const runStatus = computed(() => facility.value.runStatus ?? '-');

/** 状态色：待审批要显眼，已驳回要能一眼看出没停成 */
const statusColor = computed(() => {
  switch (status.value) {
    case 'APPROVED': {
      return 'error';
    }
    case 'PENDING': {
      return 'warning';
    }
    case 'REJECTED': {
      return 'default';
    }
    default: {
      return 'success';
    }
  }
});

async function reload() {
  if (facilityId.value) {
    facility.value = await getFacility(facilityId.value);
  }
}

async function doDeclare() {
  if (!facilityId.value) {
    return;
  }
  if (!reason.value.trim()) {
    message.warning('请填停运原因');
    return;
  }
  try {
    await declareShutdown({ id: facilityId.value, shutdownReason: reason.value });
    reason.value = '';
    await reload();
    emit('success');
    message.success('已提交停运申报，待审批（设施仍在运行）');
  } catch {
    // 重复申报等拦截码由全局提示展示
  }
}

async function doApprove(approved: boolean) {
  if (!facilityId.value) {
    return;
  }
  try {
    await approveShutdown({
      id: facilityId.value,
      approved,
      opinion: opinion.value || undefined,
      signImg: await signPadRef.value?.commit(),
    });
    opinion.value = '';
    signPadRef.value?.clear();
    await reload();
    emit('success');
    message.success(approved ? '已批准，设施转停运' : '已驳回，设施保持运行');
  } catch {
    // 无待审申报等拦截码由全局提示展示
  }
}

async function doResume() {
  if (!facilityId.value) {
    return;
  }
  try {
    await resumeFacility(facilityId.value);
    await reload();
    emit('success');
    message.success('已复运');
  } catch {
    // 由全局提示展示
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      facilityId.value = undefined;
      facility.value = {};
      reason.value = '';
      opinion.value = '';
      signPadRef.value?.clear();
      return;
    }
    const data = modalApi.getData<{ id?: number }>();
    if (!data?.id) {
      return;
    }
    facilityId.value = data.id;
    modalApi.lock();
    try {
      await reload();
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal title="停运申报 / 审批 / 复运" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="申报 ≠ 停运"
        description="提交申报后设施**仍在运行**；批准后才转停运，驳回则保持运行。改档案接口无法修改运行状态与申报状态——想停就得走这里，绕不过去。"
      />
      <Descriptions :column="2" size="small" bordered class="mb-3">
        <DescriptionsItem label="设施编号">
          {{ facility.facilityNo ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="设施名称">
          {{ facility.facilityName ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="运行状态">
          <Tag :color="runStatus === 'RUNNING' ? 'success' : 'error'">
            {{ runStatus === 'RUNNING' ? '运行' : '停运' }}
          </Tag>
        </DescriptionsItem>
        <DescriptionsItem label="停运申报">
          <Tag :color="statusColor">
            {{ SHUTDOWN_STATUS_MAP[status] ?? status }}
          </Tag>
        </DescriptionsItem>
        <DescriptionsItem label="申报原因" :span="2">
          {{ facility.shutdownReason || '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="审批人" :span="2">
          {{ facility.shutdownApprover || '-' }}
        </DescriptionsItem>
      </Descriptions>

      <template v-if="status !== 'PENDING'">
        <Textarea
          v-model:value="reason"
          :rows="2"
          placeholder="停运原因（如：活性炭更换 / 设备检修）"
          class="mb-2"
        />
        <Button type="primary" danger block @click="doDeclare">
          提交停运申报
        </Button>
      </template>

      <template v-else>
        <Textarea
          v-model:value="opinion"
          :rows="2"
          placeholder="审批意见（可留空）"
          class="mb-2"
        />
        <Space class="w-full">
          <Button type="primary" danger @click="doApprove(true)">
            批准并停运
          </Button>
          <Button @click="doApprove(false)">驳回</Button>
        </Space>
        <SignaturePad ref="signPadRef" class="mt-3" />
      </template>

      <div v-if="runStatus === 'STOPPED'" class="mt-3">
        <Button block @click="doResume">复运（回到运行）</Button>
      </div>

      <div class="mt-4">
        <AttachmentPanel
          biz-type="FACILITY_SHUTDOWN"
          :biz-no="facility.facilityNo"
        />
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
      </Space>
    </template>
  </Modal>
</template>
