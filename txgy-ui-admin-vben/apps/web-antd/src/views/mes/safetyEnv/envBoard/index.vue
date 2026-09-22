<script lang="ts" setup>
import type { MesEnvBoardApi } from '#/api/mes/safetyEnv/envBoard';
import type { MesMonitorApi } from '#/api/mes/safetyEnv/monitorBoard';

import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import { Alert, Button, Card, Empty, Spin, Table, Tag } from 'ant-design-vue';

import { getEnvBoard } from '#/api/mes/safetyEnv/envBoard';
import { getMonitorBoard } from '#/api/mes/safetyEnv/monitorBoard';

const router = useRouter();

const loading = ref(true);
const board = ref<MesEnvBoardApi.EnvBoard>({});
/** 在线监测那一段：本页只看四个数，细节在「在线监控看板」页 */
const monitor = ref<MesMonitorApi.Board>({});

async function load() {
  loading.value = true;
  try {
    // 两块一起拿：本看板是先看"今天有没有事"，在线监测那四个数是其中最急的一类，
    // 串行发请求只会让首屏多等一个往返
    const [env, mon] = await Promise.all([
      getEnvBoard(),
      getMonitorBoard({ range: '24h' }).catch(() => ({}) as MesMonitorApi.Board),
    ]);
    board.value = env;
    monitor.value = mon;
  } finally {
    loading.value = false;
  }
}

onMounted(load);

const LEVEL_MAP: Record<string, { color: string; text: string }> = {
  RED: { color: 'red', text: '红线' },
  WARN: { color: 'orange', text: '黄警' },
  NORMAL: { color: 'green', text: '正常' },
  OVERDUE: { color: 'red', text: '已超期' },
  SOON: { color: 'orange', text: '临近' },
  EXPIRING: { color: 'orange', text: '即将届满' },
  OUT: { color: 'red', text: '缺货' },
  EXPIRED: { color: 'red', text: '已过期' },
};

/** 顶部待办条：只看"今天该有人去处理几件事"——总量是给汇报看的，待办数才是给干活的人看的 */
const todos = computed(() => {
  const q = board.value.quota ?? {};
  const f = board.value.facility ?? {};
  const s = board.value.storage ?? {};
  const m = board.value.manifest ?? {};
  return [
    {
      label: '许可水位待办',
      value: (q.redCount ?? 0) + (q.warnCount ?? 0),
      hint: `红线 ${q.redCount ?? 0} · 黄警 ${q.warnCount ?? 0}`,
      danger: (q.redCount ?? 0) > 0,
    },
    {
      label: '设施待办',
      value: (f.stoppedWithoutApproval?.length ?? 0) + (f.dueReplace?.length ?? 0),
      hint: `未批先停 ${f.stoppedWithoutApproval?.length ?? 0} · 换炭到期 ${f.dueReplace?.length ?? 0}`,
      danger: (f.stoppedWithoutApproval?.length ?? 0) > 0,
    },
    {
      label: '暂存超期',
      value: s.overdueCount ?? 0,
      hint: `按 ${s.limitDays ?? '-'} 天口径 · 临近 ${s.soonCount ?? 0}`,
      danger: (s.overdueCount ?? 0) > 0,
    },
    {
      label: '联单待办',
      value: m.dueSoon?.length ?? 0,
      hint: `在册 ${m.total ?? 0} 张`,
      danger: false,
    },
    {
      label: '应急物资待办',
      value: board.value.material?.alertCount ?? 0,
      hint: '缺货 / 过期 / 待检',
      danger: false,
    },
  ];
});

function fmt(t?: number) {
  return t ? new Date(t).toLocaleString('zh-CN', { hour12: false }) : '-';
}

/**
 * 在线监测只取四个数：本页的定位是"今天有没有事"，
 * 排放浓度的小时曲线、达标率明细、超标派单闭环都在「在线监控看板」里做。
 * 这里把整块搬过来只会让两个页面互相抄，改一处漏一处。
 */
const monitorKpis = computed(() => {
  const m = monitor.value;
  return [
    {
      label: '排口在线率',
      value: m.onlineRate == null ? '-' : `${m.onlineRate}%`,
      danger: (m.outletTotal ?? 0) > 0 && m.onlineRate != null && m.onlineRate < 90,
    },
    {
      label: '当前超标排口',
      value: m.exceedCount ?? 0,
      danger: (m.exceedCount ?? 0) > 0,
    },
    {
      label: '今日超标次数',
      value: m.todayExceedTimes ?? 0,
      danger: (m.todayExceedTimes ?? 0) > 0,
    },
    {
      label: '数据完整率',
      value: m.dataCompleteRate == null ? '-' : `${m.dataCompleteRate}%`,
      danger: m.dataCompleteRate != null && m.dataCompleteRate < 90,
    },
  ];
});

/** 该租户没授过「在线监控看板」时按钮点了会 403，不如根本不显示 */
const canViewMonitor = computed(() => (monitor.value.outletTotal ?? 0) > 0);

function goMonitor() {
  router.push({ name: 'MesSetMonitorBoard' });
}
</script>

<template>
  <Page auto-content-height>
    <Spin :spinning="loading">
      <div class="mb-3 grid grid-cols-2 gap-3 lg:grid-cols-5">
        <Card v-for="t in todos" :key="t.label" size="small">
          <div class="text-sm text-gray-500">{{ t.label }}</div>
          <div
            class="mt-1 text-2xl font-semibold"
            :class="t.danger && t.value > 0 ? 'text-red-500' : ''"
          >
            {{ t.value }}
          </div>
          <div class="text-xs text-gray-400">{{ t.hint }}</div>
        </Card>
      </div>

      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="本看板只读：所有判定复用各业务的既有规则，不另设一套阈值。"
        description="许可水位与证载有效期分开看——证过期不等于超总量。暂存倒计时按 GB18597 一年期（365 天）口径计算，该阈值目前写在服务端常量里，阈值配置页落地后改为字典项。"
      />

      <Card v-if="canViewMonitor" class="mb-3" size="small">
        <template #title>
          在线监测
          <span class="ml-2 text-xs font-normal text-gray-400">
            {{ monitor.meta?.source ?? '' }} · {{ monitor.meta?.freq ?? '' }}
          </span>
        </template>
        <template #extra>
          <Button size="small" type="primary" @click="goMonitor">
            进入在线监控看板
          </Button>
        </template>
        <div class="grid grid-cols-2 gap-3 lg:grid-cols-4">
          <div v-for="k in monitorKpis" :key="k.label">
            <div class="text-sm text-gray-500">{{ k.label }}</div>
            <div
              class="mt-1 text-2xl font-semibold"
              :class="k.danger ? 'text-red-500' : ''"
            >
              {{ k.value }}
            </div>
          </div>
        </div>
      </Card>

      <Card class="mb-3" size="small" title="许可余量（自然年累计 vs 许可年总量）">
        <Table
          size="small"
          row-key="pollutantCode"
          :columns="[
            { title: '排放口', dataIndex: 'outletCode', width: 120 },
            { title: '污染物', dataIndex: 'pollutantCode', width: 140 },
            { title: '许可年总量(t)', dataIndex: 'annualLimit', width: 130 },
            { title: '本年累计(t)', dataIndex: 'used', width: 120 },
            { title: '水位', dataIndex: 'ratio', width: 100 },
            { title: '判定', dataIndex: 'level', width: 100 },
          ]"
          :data-source="board.quota?.items ?? []"
          :pagination="false"
          :scroll="{ x: 900 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'annualLimit'">
              <!-- 没配总量不等于没超：这里必须显示「未配置」，不能给个空白让人当成绿灯 -->
              <span v-if="record.annualLimit == null" class="text-orange-500">未配置</span>
              <span v-else>{{ record.annualLimit }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'ratio'">
              <span v-if="record.ratio == null">-</span>
              <span v-else>{{ (record.ratio * 100).toFixed(1) }}%</span>
            </template>
            <template v-else-if="column.dataIndex === 'level'">
              <Tag :color="LEVEL_MAP[record.level as string]?.color ?? 'default'">
                {{ LEVEL_MAP[record.level as string]?.text ?? record.level ?? '-' }}
              </Tag>
            </template>
          </template>
        </Table>

        <div class="mt-3 mb-1 font-medium">证载有效期</div>
        <Table
          size="small"
          row-key="permitNo"
          :columns="[
            { title: '许可证号', dataIndex: 'permitNo', width: 220 },
            { title: '单位', dataIndex: 'enterpriseName' },
            { title: '有效期止', dataIndex: 'endDate', width: 130 },
            { title: '剩余天数', dataIndex: 'daysLeft', width: 110 },
            { title: '水位', dataIndex: 'level', width: 110 },
          ]"
          :data-source="board.quota?.permits ?? []"
          :pagination="false"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'level'">
              <Tag :color="LEVEL_MAP[record.level as string]?.color ?? 'default'">
                {{ LEVEL_MAP[record.level as string]?.text ?? record.level ?? '-' }}
              </Tag>
            </template>
          </template>
        </Table>
      </Card>

      <div class="grid grid-cols-1 gap-3 xl:grid-cols-2">
        <Card size="small" title="治污设施运行">
          <div class="mb-2 text-sm text-gray-600">
            在册 {{ board.facility?.total ?? 0 }} 台 · 运行 {{ board.facility?.running ?? 0 }} ·
            停运 {{ board.facility?.stopped ?? 0 }} · 已申报停运
            {{ board.facility?.shutdownDeclared ?? 0 }}
          </div>
          <div class="mb-1 font-medium text-red-600">未批先停（停运未走申报即违规）</div>
          <Table
            v-if="board.facility?.stoppedWithoutApproval?.length"
            class="mb-3"
            size="small"
            row-key="id"
            :columns="[
              { title: '设施编号', dataIndex: 'facilityNo', width: 150 },
              { title: '名称', dataIndex: 'facilityName' },
              { title: '运行状态', dataIndex: 'runStatus', width: 100 },
              { title: '申报状态', dataIndex: 'shutdownStatus', width: 110 },
            ]"
            :data-source="board.facility.stoppedWithoutApproval"
            :pagination="false"
          />
          <Empty
            v-else
            class="mb-3"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="没有未批先停"
          />
          <div class="mb-1 font-medium">换炭到期</div>
          <Table
            v-if="board.facility?.dueReplace?.length"
            size="small"
            row-key="id"
            :columns="[
              { title: '设施编号', dataIndex: 'facilityNo', width: 150 },
              { title: '名称', dataIndex: 'facilityName' },
              { title: '应换日', dataIndex: 'nextReplaceDate', width: 120 },
              { title: '剩余天数', dataIndex: 'daysToReplace', width: 100 },
            ]"
            :data-source="board.facility.dueReplace"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'daysToReplace'">
                <span :class="(record.daysToReplace ?? 0) < 0 ? 'text-red-500' : ''">
                  {{ record.daysToReplace }}
                </span>
              </template>
            </template>
          </Table>
          <Empty
            v-else
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="没有到期的换炭"
          />
        </Card>

        <Card size="small">
          <template #title>
            危废暂存倒计时
            <span class="ml-2 text-xs font-normal text-gray-400">
              按 {{ board.storage?.limitDays ?? '-' }} 天口径（GB18597 一年期）
            </span>
          </template>
          <Table
            size="small"
            row-key="id"
            :columns="[
              { title: '来源判定号', dataIndex: 'sourceRecordNo', width: 160 },
              { title: '物品', dataIndex: 'itemName' },
              { title: '重量', dataIndex: 'weight', width: 90 },
              { title: '库位', dataIndex: 'location', width: 130 },
              { title: '已存天数', dataIndex: 'daysStored', width: 100 },
              { title: '剩余', dataIndex: 'daysLeft', width: 90 },
              { title: '水位', dataIndex: 'level', width: 100 },
            ]"
            :data-source="board.storage?.items ?? []"
            :pagination="{ pageSize: 8 }"
            :scroll="{ x: 800 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'daysLeft'">
                <span :class="(record.daysLeft ?? 0) < 0 ? 'font-semibold text-red-500' : ''">
                  {{ record.daysLeft }}
                </span>
              </template>
              <template v-else-if="column.dataIndex === 'level'">
                <Tag :color="LEVEL_MAP[record.level as string]?.color ?? 'default'">
                  {{ LEVEL_MAP[record.level as string]?.text ?? record.level ?? '-' }}
                </Tag>
              </template>
            </template>
          </Table>
        </Card>

        <Card size="small" title="危废联单状态">
          <div class="mb-3 flex flex-wrap gap-2">
            <Tag
              v-for="(count, status) in board.manifest?.countByStatus ?? {}"
              :key="status"
              :color="count > 0 ? 'blue' : 'default'"
            >
              {{ status }}：{{ count }}
            </Tag>
          </div>
          <div class="mb-1 font-medium">申报时限临近（含已逾期）</div>
          <Table
            v-if="board.manifest?.dueSoon?.length"
            size="small"
            row-key="id"
            :columns="[
              { title: '联单号', dataIndex: 'manifestNo', width: 180 },
              { title: '废物', dataIndex: 'wasteName' },
              { title: '数量', dataIndex: 'quantity', width: 90 },
              { title: '申报时限', dataIndex: 'declareDeadline', width: 170 },
              { title: '剩余小时', dataIndex: 'hoursLeft', width: 110 },
            ]"
            :data-source="board.manifest.dueSoon"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'declareDeadline'">
                {{ fmt(record.declareDeadline) }}
              </template>
              <template v-else-if="column.dataIndex === 'hoursLeft'">
                <span :class="(record.hoursLeft ?? 0) < 0 ? 'font-semibold text-red-500' : ''">
                  {{ record.hoursLeft }}
                </span>
              </template>
            </template>
          </Table>
          <Empty
            v-else
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="没有临近申报时限的联单"
          />
        </Card>

        <Card size="small" title="应急物资待办">
          <Table
            v-if="board.material?.alerts?.length"
            size="small"
            row-key="id"
            :columns="[
              { title: '物资编号', dataIndex: 'materialNo', width: 160 },
              { title: '名称', dataIndex: 'materialName' },
              { title: '数量', dataIndex: 'quantity', width: 90 },
              { title: '单位', dataIndex: 'unit', width: 70 },
              { title: '有效期至', dataIndex: 'expireDate', width: 120 },
              { title: '状态', dataIndex: 'status', width: 100 },
            ]"
            :data-source="board.material.alerts"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'status'">
                <Tag :color="LEVEL_MAP[record.status as string]?.color ?? 'default'">
                  {{ LEVEL_MAP[record.status as string]?.text ?? record.status ?? '-' }}
                </Tag>
              </template>
            </template>
          </Table>
          <Empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" description="物资都在正常状态" />
        </Card>
      </div>
    </Spin>
  </Page>
</template>
