<script lang="ts" setup>
import type { MesHazwasteApi } from '#/api/mes/safetyEnv/hazwaste';

import { computed, ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import {
  Alert,
  Button,
  Descriptions,
  DescriptionsItem,
  Empty,
  message,
  Space,
  Tag,
} from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  closeManifest,
  confirmManifestParty,
  createManifest,
  declareManifest,
  getManifestByNo,
  signManifest,
  updateManifest,
} from '#/api/mes/safetyEnv/hazwaste';
import { getSignRecordPage } from '#/api/mes/safetyEnv/pollutionTrace';

import AttachmentPanel from '../../components/AttachmentPanel.vue';
import SignaturePad from '../../components/SignaturePad.vue';
import { MANIFEST_STATUS_MAP, SIGN_ROLE_MAP, useManifestFormSchema } from '../data';

const emit = defineEmits(['success']);

const manifestNo = ref<string>('');
const waste = ref<MesHazwasteApi.HazardousWaste | null>(null);
const manifest = ref<MesHazwasteApi.HazwasteManifest | null>(null);
const signs = ref<any[]>([]);
const editing = ref(false);
const loading = ref(false);
/** 会签手写板：会签时才上传，没签返回 undefined（各角色可各签一次，签完清板） */
const signPadRef = ref<InstanceType<typeof SignaturePad>>();

const statusText = computed(
  () => MANIFEST_STATUS_MAP[manifest.value?.status ?? '']?.text ?? manifest.value?.status ?? '-',
);
const statusColor = computed(
  () => MANIFEST_STATUS_MAP[manifest.value?.status ?? '']?.color ?? 'default',
);
const signedRoles = computed(() => new Set(signs.value.map((s) => s.signRole)));
/** 已出厂 / 已归档后联单字段只读：出厂信息是追溯证据，不该再改 */
const locked = computed(() =>
  ['CLOSED', 'TRANSFERRED'].includes(manifest.value?.status ?? ''),
);
/** 终态联单不签的理由，写在界面上：入口静默消失时无从判断是"坏了"还是"本来就不能签" */
const lockedTip = computed(() =>
  manifest.value?.status === 'CLOSED'
    ? '该联单已回执归档，签字与编辑入口已关闭（历史签字与附件仍可查阅）。'
    : '该联单已启运出厂，签字与编辑入口已关闭（门卫放行时自动补签）。',
);

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: { class: 'w-full' },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  layout: 'horizontal',
  schema: useManifestFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

async function reload() {
  loading.value = true;
  try {
    manifest.value = await getManifestByNo(manifestNo.value);
    signs.value = manifest.value
      ? ((await getSignRecordPage({ pageNo: 1, pageSize: 100, bizType: 'HAZWASTE_MANIFEST', bizNo: manifestNo.value })).list ?? [])
      : [];
  } finally {
    loading.value = false;
  }
}

async function run(action: () => Promise<any>, ok: string) {
  try {
    await action();
    message.success(ok);
    await reload();
    emit('success');
  } catch {
    // 拦截原因由全局错误提示展示
  }
}

function handleDeclare() {
  run(() => declareManifest(manifestNo.value), '已申报');
}

function handleConfirm(partyRole: string, label: string) {
  run(() => confirmManifestParty(manifestNo.value, partyRole), `${label}已确认`);
}

async function handleSign(signRole: string) {
  const signImg = await signPadRef.value?.commit();
  // 手写签名必画：没签就不提交。后端 signImg 可空（系统补签也走这个接口），
  // 卡口必须在前端，否则"会签"会落一条没有签名图的记录。
  if (!signImg) {
    message.warning('请先在手写板上签名，再点会签');
    return;
  }
  await run(
    () => signManifest({
      manifestNo: manifestNo.value,
      signRole,
      opinion: `${SIGN_ROLE_MAP[signRole]}核验`,
      signImg,
    }),
    `${SIGN_ROLE_MAP[signRole]}已签字`,
  );
  signPadRef.value?.clear();
}

function handleClose() {
  run(() => closeManifest(manifestNo.value), '回执已归档');
}

function startCreate() {
  editing.value = true;
  formApi.setValues({
    manifestNo: manifestNo.value,
    wasteCode: waste.value?.wasteCode,
    wasteName: waste.value?.wasteName,
    quantity: waste.value?.quantity,
    quantityUnit: waste.value?.quantityUnit,
    generateUnit: waste.value?.counterparty,
  });
}

function startEdit() {
  editing.value = true;
  formApi.setValues(manifest.value ?? {});
}

async function submitForm() {
  const { valid } = await formApi.validate();
  if (!valid) {
    return;
  }
  const data = await formApi.getValues();
  try {
    await (manifest.value ? updateManifest(data) : createManifest(data));
    message.success('保存成功');
    editing.value = false;
    await reload();
    emit('success');
  } catch {
    // 编号重复等由全局提示
  }
}

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      manifest.value = null;
      signs.value = [];
      editing.value = false;
      return;
    }
    const data = drawerApi.getData<{ manifestNo: string; waste?: MesHazwasteApi.HazardousWaste }>();
    manifestNo.value = data.manifestNo ?? '';
    waste.value = data.waste ?? null;
    await reload();
    // 台账有、联单还没有：直接进新建，省掉一次"先点新建"
    editing.value = !manifest.value;
    if (!manifest.value) {
      startCreate();
    }
  },
});
</script>

<template>
  <Drawer :title="`危废转移联单 ${manifestNo}`" class="w-3/5" :loading="loading">
    <div v-if="editing" class="mx-4">
      <Alert
        class="mb-3"
        type="info"
        show-icon
        message="联单对齐国家固废管理信息系统五方流程：填好五方后，先「申报」，再由运输方、接收方确认，联单才生效。"
      />
      <Form />
      <div class="mt-4 flex justify-end gap-2">
        <Button v-if="manifest" @click="editing = false">取消</Button>
        <Button type="primary" @click="submitForm">保存</Button>
      </div>
    </div>

    <template v-else-if="manifest">
      <div class="mx-4">
        <div class="mb-3 flex items-center gap-2">
          <Tag :color="statusColor">{{ statusText }}</Tag>
          <span class="text-gray-500">
            生效判据：已申报 且 运输方、接收方均已确认
          </span>
        </div>

        <Descriptions bordered size="small" :column="2">
          <DescriptionsItem label="危废代码">{{ manifest.wasteCode ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="危废名称">{{ manifest.wasteName ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="申报转移量">
            {{ manifest.quantity ?? '-' }} {{ manifest.quantityUnit ?? '' }}
          </DescriptionsItem>
          <DescriptionsItem label="车牌号">{{ manifest.vehicleNo ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="产生单位">{{ manifest.generateUnit ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="运输单位">{{ manifest.carrierUnit ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="接收单位">{{ manifest.receiveUnit ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="贮存单位">{{ manifest.storageUnit ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="处置单位">{{ manifest.disposeUnit ?? '-' }}</DescriptionsItem>
          <DescriptionsItem label="地磅净重">
            {{ manifest.netWeight == null ? '-' : `${manifest.netWeight} 吨` }}
          </DescriptionsItem>
          <DescriptionsItem label="申报时间">
            {{ manifest.declaredTime ? new Date(manifest.declaredTime).toLocaleString() : '-' }}
          </DescriptionsItem>
          <DescriptionsItem label="申报/确认时限">
            {{ manifest.declareDeadline ? new Date(manifest.declareDeadline).toLocaleString() : '-' }}
          </DescriptionsItem>
          <DescriptionsItem label="启运出厂">
            {{ manifest.transferTime ? new Date(manifest.transferTime).toLocaleString() : '-' }}
          </DescriptionsItem>
          <DescriptionsItem label="门卫放行">
            {{ manifest.gateReleaseTime ? `${new Date(manifest.gateReleaseTime).toLocaleString()} / ${manifest.gateGuard ?? ''}` : '-' }}
          </DescriptionsItem>
        </Descriptions>

        <div class="mt-4">
          <div class="mb-2 font-medium">
            四方会签（移交环保员 / 押运司机 / 接收经手人 / 门卫；门卫在放行时自动补签）
          </div>
          <Space wrap>
            <Tag
v-for="role in ['HANDOVER', 'DRIVER', 'RECEIVER', 'GUARD']" :key="role"
                 :color="signedRoles.has(role) ? 'success' : 'default'"
>
              {{ SIGN_ROLE_MAP[role] }}{{ signedRoles.has(role) ? ' ✔' : ' （待签）' }}
            </Tag>
          </Space>
        </div>

        <!-- 签字区紧跟在「四方会签」状态后面，画板紧邻会签按钮 —— 两处都不能挪：
             画板放到明细/附件后面时，按钮会被顶到抽屉可视区外（实测窗外 120px），
             用户画完签名满屏找提交入口，就是现场报的"签字签不了"。 -->
        <div v-if="!locked" class="mt-4 rounded-md border border-solid border-gray-200 p-3">
          <SignaturePad ref="signPadRef" />
          <div class="mt-3 flex flex-wrap gap-2">
            <Button
              v-for="role in ['HANDOVER', 'DRIVER', 'RECEIVER']"
              :key="role"
              @click="handleSign(role)"
            >
              会签·{{ SIGN_ROLE_MAP[role] }}
            </Button>
          </div>
        </div>
        <!-- 已归档/已出厂不签：入口静默消失时用户只能对着"（待签）"标签干找 -->
        <Alert v-else class="mt-4" type="info" show-icon :message="lockedTip" />

        <div class="mt-4">
          <div class="mb-2 font-medium">签字明细</div>
          <Empty v-if="signs.length === 0" :image="Empty.PRESENTED_IMAGE_SIMPLE" description="尚无签字" />
          <div v-for="s in signs" :key="s.id" class="mb-1 text-sm">
            {{ SIGN_ROLE_MAP[s.signRole] ?? s.signRole }} · {{ s.signUser }} ·
            {{ s.signTime ? new Date(s.signTime).toLocaleString() : '' }} · {{ s.opinion ?? '' }}
            <img v-if="s.signImg" :src="s.signImg" class="mt-1 h-12 border border-solid border-gray-200" alt="签名" />
          </div>
        </div>

        <div class="mt-4">
          <AttachmentPanel
            biz-type="HAZWASTE_MANIFEST"
            :biz-no="manifest.manifestNo"
            :readonly="locked"
          />
        </div>

        <div class="mt-4 flex flex-wrap gap-2">
          <Button v-if="manifest.status === 'DRAFT'" type="primary" @click="handleDeclare">
            产生方申报
          </Button>
          <Button v-if="!locked" @click="handleConfirm('CARRIER', '运输方')">运输方确认</Button>
          <Button v-if="!locked" @click="handleConfirm('RECEIVER_PARTY', '接收方')">接收方确认</Button>
          <Button v-if="!locked" @click="handleConfirm('STORAGE', '贮存方')">贮存方确认</Button>
          <Button v-if="!locked" @click="handleConfirm('DISPOSER', '处置方')">处置方确认</Button>
          <Button v-if="!locked" @click="handleSign('HANDOVER')">会签·移交环保员</Button>
          <Button v-if="!locked" @click="handleSign('DRIVER')">会签·押运司机</Button>
          <Button v-if="!locked" @click="handleSign('RECEIVER')">会签·接收经手人</Button>
          <Button v-if="manifest.status === 'TRANSFERRED'" @click="handleClose">回执归档</Button>
          <Button v-if="!locked" @click="startEdit">编辑联单</Button>
        </div>
      </div>
    </template>

    <Empty v-else description="该联单号尚未建联单" />
  </Drawer>
</template>
