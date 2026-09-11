<script lang="ts" setup>
import type { UploadRequestOption } from 'ant-design-vue/lib/vc-upload/interface';

import type { MesSetAttachmentApi } from '#/api/mes/safetyEnv/attachment';

import { ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { Button, Empty, message, Upload } from 'ant-design-vue';

import { uploadFile } from '#/api/infra/file';
import {
  createAttachment,
  deleteAttachment,
  getAttachmentList,
} from '#/api/mes/safetyEnv/attachment';

defineOptions({ name: 'MesAttachmentPanel' });

const props = defineProps<{
  /** 业务关联类型：CHECK/LEDGER/MANIFEST/FACILITY/EMERGENCY 等 */
  bizType: string;
  /** 业务关联单号 */
  bizNo?: string;
  /** 只读（已归档单据不给再传/删） */
  readonly?: boolean;
}>();

const list = ref<MesSetAttachmentApi.Attachment[]>([]);
const uploading = ref(false);

async function load() {
  if (!props.bizNo) {
    list.value = [];
    return;
  }
  try {
    list.value = await getAttachmentList(props.bizType, props.bizNo);
  } catch {
    // 拉不到就空着，不挡单据本身的展示
    list.value = [];
  }
}

watch(() => [props.bizType, props.bizNo], load, { immediate: true });

/** 走 infra 文件服务传字节，回来再把地址登记到业务单上——本服务不碰文件流 */
async function customRequest(info: UploadRequestOption) {
  const file = info.file as File;
  uploading.value = true;
  try {
    const url = await uploadFile({ file });
    await createAttachment({
      bizType: props.bizType,
      bizNo: props.bizNo!,
      fileName: file.name,
      fileUrl: url,
    });
    info.onSuccess!(url);
    await load();
    message.success('附件已上传');
  } catch (error: any) {
    info.onError!(error);
  } finally {
    uploading.value = false;
  }
}

async function remove(id?: number) {
  if (!id) {
    return;
  }
  await deleteAttachment(id);
  await load();
  message.success('已删除');
}
</script>

<template>
  <div class="flex flex-col">
    <div class="mb-1 flex items-center justify-between">
      <span class="text-gray-500">附件（{{ list.length }}）</span>
      <Upload
        v-if="!readonly && bizNo"
        :show-upload-list="false"
        :custom-request="customRequest"
        multiple
      >
        <Button size="small" :loading="uploading">上传附件</Button>
      </Upload>
    </div>

    <Empty
      v-if="list.length === 0"
      :image="Empty.PRESENTED_IMAGE_SIMPLE"
      description="暂无附件"
    />
    <div
      v-for="a in list"
      :key="a.id"
      class="flex items-center justify-between border-b border-solid border-gray-100 py-1 text-sm"
    >
      <a :href="a.fileUrl" target="_blank" rel="noopener" class="flex items-center gap-1 truncate">
        <IconifyIcon icon="lucide:paperclip" class="size-3.5 shrink-0" />
        <span class="truncate">{{ a.fileName }}</span>
      </a>
      <div class="ml-2 flex shrink-0 items-center gap-2 text-gray-400">
        <span>{{ a.createTime ? new Date(a.createTime).toLocaleString() : '' }}</span>
        <Button
          v-if="!readonly"
          type="link"
          size="small"
          danger
          @click="remove(a.id)"
        >
          删
        </Button>
      </div>
    </div>
  </div>
</template>
