<script lang="ts" setup>
import type { MesChemicalApi } from '#/api/mes/safetyEnv/chemical';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Empty, Tabs } from 'ant-design-vue';

import { getChemicalAlerts } from '#/api/mes/safetyEnv/chemical';

import { COMPAT_GROUP_MAP, STORAGE_ZONE_MAP } from '../data';

const alerts = ref<MesChemicalApi.ChemicalAlerts>({});

/** 三类预警各自的行 */
function rows(list?: MesChemicalApi.ChemicalProfile[]) {
  return list ?? [];
}

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      alerts.value = {};
      return;
    }
    modalApi.lock();
    try {
      alerts.value = await getChemicalAlerts(30);
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal title="危化品预警（超量 / MSDS 缺失 / MSDS 将到期）" class="w-2/3">
    <div class="mx-4">
      <Tabs>
        <Tabs.TabPane :key="'over'">
          <template #tab>
            超量预警 ({{ rows(alerts.overQuota).length }})
          </template>
          <Empty v-if="rows(alerts.overQuota).length === 0" description="无超量" />
          <div v-for="p in rows(alerts.overQuota)" :key="p.id" class="leading-7">
            <span class="font-medium">{{ p.chemicalName }}</span>
            <span class="ml-2 text-gray-500">{{ p.profileNo }}</span>
            <span class="ml-2 text-red-500">
              现有 {{ p.stockQuantity }} / 上限 {{ p.storageLimit }} {{ p.storageUnit ?? '' }}
            </span>
          </div>
        </Tabs.TabPane>

        <Tabs.TabPane :key="'missing'">
          <template #tab>
            MSDS 缺失 ({{ rows(alerts.msdsMissing).length }})
          </template>
          <Empty v-if="rows(alerts.msdsMissing).length === 0" description="全部已挂载" />
          <div v-for="p in rows(alerts.msdsMissing)" :key="p.id" class="leading-7">
            <span class="font-medium">{{ p.chemicalName }}</span>
            <span class="ml-2 text-gray-500">{{ p.profileNo }}</span>
            <span class="ml-2 text-gray-500">
              {{ COMPAT_GROUP_MAP[p.compatGroup ?? ''] ?? p.compatGroup ?? '-' }}
              · {{ STORAGE_ZONE_MAP[p.storageZone ?? ''] ?? p.storageZone ?? '-' }}
            </span>
            <span class="ml-2 text-red-500">无 MSDS 不得入库</span>
          </div>
        </Tabs.TabPane>

        <Tabs.TabPane :key="'expiring'">
          <template #tab>
            MSDS 将到期 ({{ rows(alerts.msdsExpiring).length }})
          </template>
          <Empty v-if="rows(alerts.msdsExpiring).length === 0" description="近 30 天无到期" />
          <div v-for="p in rows(alerts.msdsExpiring)" :key="p.id" class="leading-7">
            <span class="font-medium">{{ p.chemicalName }}</span>
            <span class="ml-2 text-gray-500">{{ p.profileNo }}</span>
            <span class="ml-2 text-orange-500">到期日 {{ p.msdsExpireDate }}</span>
          </div>
        </Tabs.TabPane>
      </Tabs>
    </div>
  </Modal>
</template>
