<script lang="ts" setup>
import type { MesFacilityApi } from '#/api/mes/safetyEnv/facility';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Empty, InputNumber, Space, Table, Tag } from 'ant-design-vue';

import { listDueReplace } from '#/api/mes/safetyEnv/facility';

const days = ref(30);
const list = ref<MesFacilityApi.TreatmentFacility[]>([]);

function overdue(row: MesFacilityApi.TreatmentFacility) {
  if (!row.nextReplaceDate) {
    return false;
  }
  return new Date(row.nextReplaceDate).getTime() < Date.now();
}

async function load() {
  list.value = await listDueReplace(days.value);
}

const columns = [
  { dataIndex: 'facilityNo', title: '设施编号', width: 140 },
  { dataIndex: 'facilityName', title: '设施名称', width: 160 },
  { dataIndex: 'consumableName', title: '耗材', width: 110 },
  { dataIndex: 'replaceCycleDays', title: '周期(天)', width: 100 },
  { dataIndex: 'lastReplaceDate', title: '上次更换', width: 120 },
  { dataIndex: 'nextReplaceDate', title: '下次更换', width: 130 },
];

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      days.value = 30;
      list.value = [];
      return;
    }
    await load();
  },
});
</script>

<template>
  <Modal title="换炭到期预警" class="w-3/4">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="换炭不及时 = 吸附饱和 = 超标排放"
        description="列出下次更换日期落在预警窗口内的启用设施，**已逾期的一并列出**。没填更换周期的设施算不出到期日，不会出现在这里——先去档案里补周期。"
      />
      <Space class="mb-3">
        <span>预警窗口（天）：</span>
        <InputNumber v-model:value="days" :min="1" :max="365" />
        <Button type="primary" @click="load">刷新</Button>
      </Space>

      <Table
        v-if="list.length"
        :columns="columns"
        :data-source="list"
        :pagination="false"
        row-key="id"
        size="small"
        :scroll="{ x: 'max-content' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'nextReplaceDate'">
            <Tag :color="overdue(record) ? 'error' : 'warning'">
              {{ record.nextReplaceDate }}
              {{ overdue(record) ? '(已逾期)' : '' }}
            </Tag>
          </template>
        </template>
      </Table>
      <Empty
        v-else
        :image="Empty.PRESENTED_IMAGE_SIMPLE"
        description="窗口内无到期设施"
      />
    </div>

    <template #footer>
      <Space>
        <Button @click="modalApi.close()">关闭</Button>
      </Space>
    </template>
  </Modal>
</template>
