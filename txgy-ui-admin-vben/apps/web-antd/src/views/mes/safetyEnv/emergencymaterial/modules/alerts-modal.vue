<script lang="ts" setup>
import { h, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, Button, Empty, message, Spin, Table, Tag } from 'ant-design-vue';

import { getEmergencyMaterialAlerts } from '#/api/mes/safetyEnv/emergencymaterial';

import { MATERIAL_STATUS_MAP, MATERIAL_TYPE_MAP } from '../data';

const rows = ref<any[]>([]);
const loading = ref(false);

const columns = [
  { dataIndex: 'materialNo', key: 'materialNo', title: '物资编号', width: 160 },
  {
    dataIndex: 'materialType',
    key: 'materialType',
    title: '类型',
    width: 110,
    customRender: ({ text }: any) => MATERIAL_TYPE_MAP[text] ?? text ?? '-',
  },
  { dataIndex: 'materialName', key: 'materialName', title: '名称', width: 150 },
  {
    dataIndex: 'quantity',
    key: 'quantity',
    title: '在库',
    width: 90,
    customRender: ({ record }: any) =>
      `${record.quantity ?? 0} ${record.unit ?? ''}`,
  },
  { dataIndex: 'expireDate', key: 'expireDate', title: '有效期至', width: 120 },
  {
    dataIndex: 'storageLocation',
    key: 'storageLocation',
    title: '存放位置',
    width: 150,
  },
  {
    dataIndex: 'status',
    key: 'status',
    title: '预警',
    width: 90,
    customRender: ({ text }: any) => {
      const m = MATERIAL_STATUS_MAP[text];
      return m ? h(Tag, { color: m.color }, () => m.text) : (text ?? '-');
    },
  },
];

async function load() {
  loading.value = true;
  try {
    rows.value = await getEmergencyMaterialAlerts();
  } catch {
    message.error('加载应急物资预警失败');
  } finally {
    loading.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      rows.value = [];
      return;
    }
    await load();
  },
});
</script>

<template>
  <Modal title="应急物资预警" class="w-3/5">
    <div class="mx-4">
      <Alert
        class="mb-3"
        type="warning"
        show-icon
        message="缺货 → 过期 → 临期（30 天内到期）"
        description="状态按「在库数量 + 有效期」当场重算，不是入库时定的死值：缺货排在过期前面——现场没沙袋比沙袋临期更急。过期与缺货的物资不得用于应急，请安排补充/更换。"
      />
      <Spin :spinning="loading">
        <Empty v-if="!rows.length" description="暂无临期/过期/缺货物资" />
        <Table
          v-else
          :columns="columns"
          :data-source="rows"
          :pagination="false"
          row-key="id"
          size="small"
          :scroll="{ y: 420 }"
        />
      </Spin>
    </div>

    <template #footer>
      <Button @click="load">刷新</Button>
      <Button type="primary" @click="modalApi.close()">关闭</Button>
    </template>
  </Modal>
</template>
