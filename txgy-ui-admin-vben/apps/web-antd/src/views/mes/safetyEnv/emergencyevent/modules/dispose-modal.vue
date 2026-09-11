<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  Descriptions,
  DescriptionsItem,
  Input,
  InputNumber,
  message,
  Space,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  closeEmergencyEvent,
  dispatchEmergencyEvent,
  disposeEmergencyEvent,
  getEmergencyEventDetail,
} from '#/api/mes/safetyEnv/emergencyevent';

import AttachmentPanel from '../../components/AttachmentPanel.vue';
import SignaturePad from '../../components/SignaturePad.vue';
import { EVENT_STATUS_MAP, EVENT_TYPE_MAP, SCENARIO_MAP } from '../data';

const emit = defineEmits(['success']);

/** 场景 → 默认产废类别，与后端 SCENARIO_WASTE 保持一致（前端只做预填，落库以服务端为准） */
const SCENARIO_WASTE: Record<string, { name: string; wasteCode: string }> = {
  MDI_LEAK: { name: '泄漏吸附物（异氰酸酯）', wasteCode: 'HW49' },
  THINNER_LEAK: { name: '废稀释剂及吸附物', wasteCode: 'HW06' },
  WASTE_OIL_LEAK: { name: '废矿物油及吸附物', wasteCode: 'HW08' },
};

const event = ref<any>({});
const wastes = ref<any[]>([]);
const submitting = ref(false);

const handler = ref('');
const disposeNote = ref('');
const disposePhotoUrl = ref('');
/** 本次待登记的桶：一桶一行，服务端各写一条称重 + 一条台账 */
const buckets = ref<any[]>([]);

const reportContent = ref('');
const approver = ref('');
/** 手写签名板：签发闭环时才上传，没签返回 undefined */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const st = computed(() => event.value.status);
const canDispatch = computed(() => st.value === 'REPORTED');
const canDispose = computed(
  () => st.value === 'REPORTED' || st.value === 'DISPOSING',
);
const canClose = computed(() => st.value === 'PENDING_REPORT');

const wasteColumns = [
  { dataIndex: 'containerCode', key: 'containerCode', title: '桶码', width: 150 },
  { dataIndex: 'wasteCode', key: 'wasteCode', title: '类别', width: 80 },
  { dataIndex: 'wasteName', key: 'wasteName', title: '名称', width: 160 },
  {
    dataIndex: 'netWeight',
    key: 'netWeight',
    title: '净重kg',
    width: 90,
    customRender: ({ text }: any) => text ?? '-',
  },
  {
    dataIndex: 'storageLocation',
    key: 'storageLocation',
    title: '暂存库位',
    width: 130,
  },
  { dataIndex: 'manifestNo', key: 'manifestNo', title: '台账号', width: 140 },
];

async function load() {
  const id = event.value.id;
  if (!id) {
    return;
  }
  try {
    const d = await getEmergencyEventDetail(id);
    event.value = d.event ?? {};
    wastes.value = d.wastes ?? [];
  } catch {
    // 拉不到详情要说一声：闸门是按状态算的，静默失败会让所有人看着一份空单据猜哪步能点
    message.error('加载事件详情失败，请关闭后重试');
  }
}

function addBucket() {
  const preset = SCENARIO_WASTE[event.value.scenario ?? ''];
  buckets.value.push({
    containerCode: '',
    wasteCode: preset?.wasteCode ?? '',
    wasteName: preset?.name ?? '',
    netWeight: undefined,
    storageLocation: '',
  });
}

function removeBucket(i: number) {
  buckets.value.splice(i, 1);
}

async function doDispatch() {
  submitting.value = true;
  try {
    await dispatchEmergencyEvent(event.value.id, handler.value || undefined);
    message.success('已派发');
    await load();
    emit('success');
  } finally {
    submitting.value = false;
  }
}

async function doDispose() {
  for (const [i, b] of buckets.value.entries()) {
    if (!b.containerCode) {
      message.warning(`第 ${i + 1} 桶缺少桶码`);
      return;
    }
    if (!b.netWeight || b.netWeight <= 0) {
      message.warning(`第 ${i + 1} 桶净重须大于 0`);
      return;
    }
  }
  submitting.value = true;
  try {
    await disposeEmergencyEvent({
      id: event.value.id,
      disposeNote: disposeNote.value || undefined,
      disposePhotoUrl: disposePhotoUrl.value || undefined,
      wastes: buckets.value.map((b) => ({
        containerCode: b.containerCode,
        wasteCode: b.wasteCode || undefined,
        wasteName: b.wasteName || undefined,
        netWeight: b.netWeight,
        storageLocation: b.storageLocation || undefined,
      })),
    });
    message.success(
      buckets.value.length
        ? `已处置：${buckets.value.length} 桶已过秤贴签入危废台账`
        : '已处置：本次未产生应急废物',
    );
    await load();
    emit('success');
  } finally {
    submitting.value = false;
  }
}

async function doClose() {
  submitting.value = true;
  try {
    await closeEmergencyEvent(
      event.value.id,
      reportContent.value || undefined,
      approver.value || undefined,
      await signPadRef.value?.commit(),
    );
    signPadRef.value?.clear();
    message.success('事件已闭环');
    await load();
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
    event.value = modalApi.getData<any>() ?? {};
    wastes.value = [];
    buckets.value = [];
    handler.value = '';
    disposeNote.value = '';
    disposePhotoUrl.value = '';
    reportContent.value = '';
    approver.value = '';
    await load();
    reportContent.value = event.value.reportContent ?? '';
    if (canDispose.value) {
      addBucket();
    }
  },
});
</script>

<template>
  <Modal title="应急处置 · 过秤贴签入台账 · 闭环" class="w-3/5">
    <div class="mx-4">
      <Descriptions :column="3" size="small" bordered class="mb-3">
        <DescriptionsItem label="事件编号">
          {{ event.eventNo ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="类型">
          {{ EVENT_TYPE_MAP[event.eventType ?? ''] ?? event.eventType ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="场景">
          {{ SCENARIO_MAP[event.scenario ?? ''] ?? event.scenario ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="发生时间">
          {{ event.occurTime ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="地点">
          {{ event.location ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="涉事化学品">
          {{ event.chemicalCode ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="泄漏量">
          {{ event.leakQuantity ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="上报人/时间">
          {{ event.reportUser ?? '-' }} / {{ event.reportTime ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="状态">
          <Tag :color="EVENT_STATUS_MAP[st ?? '']?.color ?? 'default'">
            {{ EVENT_STATUS_MAP[st ?? '']?.text ?? st ?? '-' }}
          </Tag>
        </DescriptionsItem>
        <DescriptionsItem label="处置人/派发">
          {{ event.handler ?? '-' }} / {{ event.dispatchTime ?? '-' }}
        </DescriptionsItem>
        <DescriptionsItem label="应急废物">
          {{ event.wasteCount ?? 0 }} 桶 / {{ event.wasteQuantity ?? 0 }} kg
        </DescriptionsItem>
        <DescriptionsItem label="负责人/签字">
          {{ event.approver ?? '-' }} / {{ event.approveTime ?? '-' }}
        </DescriptionsItem>
      </Descriptions>

      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="应急废物一律过秤贴签入危废台账（应急不绕过管控）"
        description="一桶一行填全：桶码 + 危废类别 + 过秤净重。提交后服务端为每桶各写一条称重记录（过秤证据）与一条危废台账行（HJ1276 贴签），三者同事务。桶数与合计净重由服务端按明细算，不认手填的合计。本次确未产生废物时，不填桶直接提交即可。"
      />

      <Space v-if="canDispatch" class="mb-3">
        <Input
          v-model:value="handler"
          style="width: 220px"
          placeholder="处置人（留空取当前登录人）"
          allow-clear
        />
        <Button type="primary" :loading="submitting" @click="doDispatch">
          派发任务
        </Button>
      </Space>

      <div v-if="canDispose">
        <div class="mb-2 flex items-center justify-between">
          <span class="font-medium">本次应急废物逐桶登记</span>
          <Button size="small" @click="addBucket">加一桶</Button>
        </div>
        <div
          v-for="(b, i) in buckets"
          :key="i"
          class="mb-2 flex items-center gap-2"
        >
          <Input
            v-model:value="b.containerCode"
            style="width: 170px"
            placeholder="桶码"
          />
          <Input
            v-model:value="b.wasteCode"
            style="width: 90px"
            placeholder="类别"
          />
          <Input
            v-model:value="b.wasteName"
            style="width: 180px"
            placeholder="危废名称"
          />
          <InputNumber
            v-model:value="b.netWeight"
            style="width: 120px"
            :min="0"
            :step="0.1"
            placeholder="净重kg"
          />
          <Input
            v-model:value="b.storageLocation"
            style="width: 150px"
            placeholder="暂存库位"
          />
          <Button size="small" danger @click="removeBucket(i)">删</Button>
        </div>

        <div class="mt-3 mb-1 text-gray-500">处置说明</div>
        <Input.TextArea
          v-model:value="disposeNote"
          :rows="2"
          placeholder="如：干沙围堵后收集，未用水冲；现场已通风"
        />
        <div class="mt-2 mb-1 text-gray-500">处置照片地址</div>
        <Input v-model:value="disposePhotoUrl" placeholder="http://…" allow-clear />
      </div>

      <div v-if="wastes.length" class="mt-3">
        <div class="mb-2 font-medium">已登记应急废物</div>
        <Table
          :columns="wasteColumns"
          :data-source="wastes"
          :pagination="false"
          row-key="id"
          size="small"
        />
      </div>

      <div v-if="st === 'PENDING_REPORT'" class="mt-3">
        <div class="mb-1 text-gray-500">
          事件报告（原因 / 数量 / 处置 / 危废桶码 / 整改）
        </div>
        <Input.TextArea
          v-model:value="reportContent"
          :rows="4"
          placeholder="如：稀释剂桶倾倒约 3kg；沙土围堵吸附收集，2 桶过秤共 4.2kg 入危废台账 EM-…；无人员伤害；整改：桶架加装防倒挡块"
        />
        <div class="mt-2 mb-1 text-gray-500">签发负责人（留空取当前登录人）</div>
        <Input v-model:value="approver" style="width: 240px" allow-clear />
        <SignaturePad ref="signPadRef" class="mt-3" />
      </div>

      <div v-if="st === 'CLOSED'" class="mt-3">
        <div class="font-medium">事件报告</div>
        <div class="mt-1 whitespace-pre-wrap text-gray-600">
          {{ event.reportContent || '（空）' }}
        </div>
      </div>

      <div class="mt-4">
        <AttachmentPanel
          biz-type="EMERGENCY"
          :biz-no="event.eventNo"
          :readonly="st === 'CLOSED'"
        />
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
        <Button v-if="canDispose" type="primary" :loading="submitting" @click="doDispose">
          提交处置
        </Button>
        <Button v-if="canClose" type="primary" :loading="submitting" @click="doClose">
          签发闭环
        </Button>
      </Space>
    </template>
  </Modal>
</template>
