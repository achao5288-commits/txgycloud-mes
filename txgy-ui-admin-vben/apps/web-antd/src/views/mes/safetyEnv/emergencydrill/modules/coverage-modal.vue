<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Empty, Spin, Table, Tag } from 'ant-design-vue';
import dayjs from 'dayjs';

import { getEmergencyDrillYearCoverage } from '#/api/mes/safetyEnv/emergencydrill';

import { DRILL_STATUS_MAP, DRILL_TYPE_MAP } from '../data';

const year = ref(dayjs().year());
const data = ref<any>({});
const loading = ref(false);

const columns = [
  { dataIndex: 'drillNo', key: 'drillNo', title: '演练编号', width: 150 },
  { dataIndex: 'drillName', key: 'drillName', title: '名称', width: 160 },
  {
    dataIndex: 'drillType',
    key: 'drillType',
    title: '类型',
    width: 110,
    customRender: ({ text }: any) => DRILL_TYPE_MAP[text] ?? text ?? '-',
  },
  { dataIndex: 'drillDate', key: 'drillDate', title: '日期', width: 110 },
  {
    dataIndex: 'status',
    key: 'status',
    title: '状态',
    width: 100,
    customRender: ({ record }: any) => {
      const m = DRILL_STATUS_MAP[record.status];
      return m ? `${m.text}` : (record.status ?? '-');
    },
  },
];

async function load() {
  loading.value = true;
  try {
    data.value = await getEmergencyDrillYearCoverage(year.value);
  } finally {
    loading.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      data.value = {};
      return;
    }
    year.value = dayjs().year();
    await load();
  },
});
</script>

<template>
  <Modal title="年度演练覆盖" class="w-1/2">
    <div class="mx-4">
      <Alert
        class="mb-3"
        :type="data.satisfied ? 'success' : 'warning'"
        show-icon
        :message="
          data.satisfied
            ? `${data.year} 年度演练已达标`
            : `${data.year} 年度尚无已闭环演练`
        "
        description="设计文档 §八.2 要求每年至少 1 次演练。这里按「已闭环」计数，不是「已计划」——只有计划没有记录与评估，等于没演练。演练记录（照片/视频/签到）与评估结论入执行报告。"
      />

      <div class="mb-2">
        <span class="mr-3">
          年度：<b>{{ data.year ?? year }}</b>
        </span>
        <span class="mr-3">已闭环 <b>{{ data.closedCount ?? 0 }}</b> 次</span>
        <span>合计 <b>{{ data.totalCount ?? 0 }}</b> 次</span>
        <Tag class="ml-3" :color="data.satisfied ? 'success' : 'error'">
          {{ data.satisfied ? '达标' : '未达标' }}
        </Tag>
      </div>

      <Spin :spinning="loading">
        <Empty v-if="!data.drills?.length" description="当年无演练记录" />
        <Table
          v-else
          :columns="columns"
          :data-source="data.drills"
          :pagination="false"
          row-key="id"
          size="small"
          :scroll="{ y: 360 }"
        />
      </Spin>
    </div>

    <template #footer>
      <Button @click="load">刷新</Button>
      <Button type="primary" @click="modalApi.close()">关闭</Button>
    </template>
  </Modal>
</template>
