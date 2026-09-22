<script lang="ts" setup>
import { ref } from 'vue';

import { Alert, Input, message, Modal } from 'ant-design-vue';

import SignaturePad from './SignaturePad.vue';

/** 一次签名确认的结果；用户取消/直接关窗时为 null */
interface SignResult {
  opinion?: string;
  signImg: string;
}

const visible = ref(false);
const title = ref('');
const content = ref('');
const opinion = ref('');
/** 手写签名板：确定时才上传，空白返回 undefined */
const padRef = ref<InstanceType<typeof SignaturePad>>();
/** 本次弹窗的结清回调；非空即"这一笔还没结" */
let resolve: ((v: null | SignResult) => void) | null = null;

/**
 * 弹一次签字确认，等用户画完再往下走。
 *
 * 用法：`const sign = await signRef.value?.open('标题', '说明'); if (!sign) return;`
 * 返回 null = 用户取消 —— 调用方必须当"放弃"处理，**不要**退化成不签名照提交
 *（后端那四个接口缺签名一律拒，退化只会换来一个看不懂的错误码）。
 *
 * 用 antd 自己的 Modal 而不是 vben 的：这个弹窗要**命令式**地返回一个值，
 * 挂在页面常驻（不随父弹窗开关销毁），也就能从 vben 弹窗内部再叠一层
 *（judge-modal 里就是这种套法，仓里复核弹窗同款）。
 */
function ask(titleText: string, contentText?: string) {
  title.value = titleText;
  content.value = contentText ?? '';
  opinion.value = '';
  padRef.value?.clear(); // 上一次的笔迹不能当这一次的签名
  visible.value = true;
  return new Promise<null | SignResult>((r) => {
    resolve = r;
  });
}

/** 唯一出口：签成、取消、关窗都从这里结清，免得 promise 永远挂着 */
function finish(v: null | SignResult) {
  const r = resolve;
  resolve = null;
  visible.value = false;
  r?.(v);
}

async function handleOk() {
  const signImg = await padRef.value?.commit();
  if (!signImg) {
    // "必画"只能在前端拦：后端只看得见"带没带签名 URL"
    message.warning('请先在签名板上手写签名（空白签名不算数）');
    return;
  }
  finish({ opinion: opinion.value.trim() || undefined, signImg });
}

defineExpose({ open: ask });
</script>

<template>
  <Modal
    v-model:open="visible"
    :title="title"
    :width="520"
    ok-text="签名确认"
    cancel-text="取消"
    @cancel="finish(null)"
    @ok="handleOk"
  >
    <Alert v-if="content" class="mb-3" type="warning" show-icon :message="content" />
    <div class="mb-1 text-sm">签名说明（可留空，会随签名一起存进签字记录）</div>
    <Input.TextArea
      v-model:value="opinion"
      :rows="2"
      class="mb-3"
      placeholder="例如：现场确认无污染 / 倒库后复核 / 人工覆盖冻结"
    />
    <SignaturePad ref="padRef" label="手写签名（必画，空白不放行）" />
  </Modal>
</template>
