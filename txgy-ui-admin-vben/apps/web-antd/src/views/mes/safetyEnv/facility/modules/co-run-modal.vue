<script lang="ts" setup>
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Button,
  Empty,
  Radio,
  Space,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  checkCoRun,
  getFacility,
  listStoppedWithoutApproval,
} from '#/api/mes/safetyEnv/facility';

const facilityId = ref<number>();
const facility = ref<MesFacilityApi.TreatmentFacility>({});
const productionRunning = ref(true);
const result = ref<MesFacilityApi.CoRunResult | null>(null);
const stoppedList = ref<MesFacilityApi.TreatmentFacility[]>([]);

async function doCheck() {
  if (!facilityId.value) {
    return;
  }
  result.value = await checkCoRun({
    id: facilityId.value,
    productionRunning: productionRunning.value,
  });
}

async function loadStopped() {
  stoppedList.value = await listStoppedWithoutApproval();
}

const columns = [
  { dataIndex: 'facilityNo', title: '设施编号', width: 140 },
  { dataIndex: 'facilityName', title: '设施名称', width: 160 },
  { dataIndex: 'lineCode', title: '产线', width: 120 },
  { dataIndex: 'shutdownStatus', title: '停运申报', width: 110 },
];

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      facilityId.value = undefined;
      facility.value = {};
      productionRunning.value = true;
      result.value = null;
      stoppedList.value = [];
      return;
    }
    const data = modalApi.getData<{ id?: number }>();
    if (data?.id) {
      facilityId.value = data.id;
      facility.value = await getFacility(data.id);
      await doCheck();
    }
    await loadStopped();
  },
});
</script>

<template>
  <Modal title="同开同停校验 / 未批先停清单" class="w-3/4">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="产线运行中，配套治污设施不得停运"
        description="产线在跑而治污设施停运 = 废气直排，属违规。若设施停运还没走完审批（申报状态非「已批准」），是另一性质的违规——未批先停，会单独点出来。产线运行信号需要人工给：工单表没有产线/设施列，系统不会替你猜。"
      />

      <div v-if="facility.id" class="mb-3">
        <div class="mb-2">
          <span class="font-medium">{{ facility.facilityName }}</span>
          <span class="ml-2 text-gray-500">
            {{ facility.facilityNo }} · 产线 {{ facility.lineCode || '(未填)' }} ·
            设施当前
            <Tag :color="facility.runStatus === 'RUNNING' ? 'success' : 'error'">
              {{ facility.runStatus === 'RUNNING' ? '运行' : '停运' }}
            </Tag>
          </span>
        </div>
        <Space class="mb-2">
          <span>产线是否正在运行：</span>
          <Radio.Group v-model:value="productionRunning" button-style="solid">
            <Radio.Button :value="true">运行中</Radio.Button>
            <Radio.Button :value="false">已停机</Radio.Button>
          </Radio.Group>
          <Button @click="doCheck">重新校验</Button>
        </Space>

        <div v-if="result">
          <Tag :color="result.passed ? 'success' : 'error'">
            {{ result.passed ? '通过' : '违规' }}
          </Tag>
          <div v-if="result.reasons?.length" class="mt-1 text-red-500">
            <div v-for="r in result.reasons" :key="r">· {{ r }}</div>
          </div>
          <div v-else class="mt-1 text-green-600">同开同停满足</div>
        </div>
      </div>

      <div class="mt-4">
        <div class="mb-2 font-medium">未批先停清单</div>
        <Table
          v-if="stoppedList.length"
          :columns="columns"
          :data-source="stoppedList"
          :pagination="false"
          row-key="id"
          size="small"
          :scroll="{ x: 'max-content' }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'shutdownStatus'">
              <Tag color="error">{{ record.shutdownStatus }}</Tag>
            </template>
          </template>
        </Table>
        <Empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" description="无未批先停设施" />
      </div>
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
      </Space>
    </template>
  </Modal>
</template>
