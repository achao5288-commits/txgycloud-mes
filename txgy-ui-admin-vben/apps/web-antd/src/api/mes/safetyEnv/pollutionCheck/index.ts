import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPollutionCheckApi {
  /** MES 安全环保检测-污染判定记录 */
  export interface PollutionCheck {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    originRecordNo?: string; // 被变更的原单号：空=首版；非空=本单是变更单
    amendReason?: string; // 变更事由（仅变更单有值）
    supersededBy?: string; // 替代本单的变更单号：空=未被替代，故本单仍是有效结论
    stage?: string; // 环节
    bizNo?: string; // 关联单号
    batchId?: number; // 批次编号（mes_wm_batch.id）——判定锚定的权威批次；空=历史手工录入的未关联记录
    batchNo?: string; // 批次号（显示快照，权威值见 batchId）
    itemCode?: string; // 物料/产品编码
    itemName?: string; // 物料/产品名称
    itemSpec?: string; // 规格
    fieldSigns?: string; // 现场污染特征(多选)：SIGN_* code，换行分隔
    weight?: number; // 重量——单位见 unitName
    unitName?: string; // 重量单位：KG=已换算的质量，其余为物料原单位(个/箱/米…)
    aiResult?: string; // AI 初筛结果 CLEAN/POLLUTED/UNCERTAIN
    aiConfidence?: number; // AI 置信度(%)
    aiReason?: string; // AI 判定理由（自然语言）
    aiBasis?: string; // AI 援引的法规依据（法规名+条款号+要点）
    suggestedStorage?: string; // AI 推荐存储方法
    reviewResult?: string; // 人工复核结果 CLEAN/POLLUTED，空=待复核
    finishedResult?: string; // 成品达标分支 QUALIFIED/REWORK/SCRAPPED，仅成品环节有值
    reviewBasis?: string; // 人工复核的判定依据（复选法条，换行分隔；不强制，未填为 null）
    storageMethod?: string; // 最终存储方法
    disposition?: string; // 处置方式
    location?: string; // 去向/库位（库位名称快照）
    locationId?: number; // 受控库位编号
    marked?: boolean; // 是否标记
    reviewBy?: string; // 复核人
    reviewTime?: number; // 复核时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
    // 以下两个只在请求侧有值：**发起检测**与**发起变更**都是人为主张，后端缺 signImg 直接拒
    //（同码 1040818019），通过后与判定同事务落 mes_set_sign_record。改单、AI 初筛预览都不校验。
    // 变更单的签名键是**服务端新生成的那个单号**，不是原单号——所以发起变更走 amendPollutionCheck，
    // 不能拿 createPollutionCheck 顶替（前端无从知道新单号，签出来的字会挂到别的单上）。
    signImg?: string; // 手写签名图 URL（前端传到 infra 后回填）
    opinion?: string; // 签名说明（落签字记录的 opinion）
  }

  /** 发起变更参数：在原单之外另交一份新内容 + 事由 */
  export interface AmendPayload extends PollutionCheck {
    /** 被变更的原单 id（后端反查原单号；须是已复核且尚未被替代的判定） */
    originId: number;
    /** 变更事由：写清为什么要改。后端必校验（1040818022），空则拒 */
    amendReason: string;
    /** 手写签名图 URL。同 changePollutionControlLocation 的理由，故意不设可选，漏传编译就不过 */
    signImg: string;
  }

  /** AI 初筛建议 */
  export interface AiSuggestion {
    aiResult?: string;
    aiConfidence?: number;
    aiReason?: string;
    aiBasis?: string; // 援引的法规依据
    suggestedStorage?: string;
  }

  /** 法规依据选项：label 短标签用于展示，value 为落库全文 */
  export interface LegalBasisOption {
    label: string;
    value: string;
  }

  /** 现场污染特征选项：value 为落库的 SIGN_* code；articles 为该特征命中的法条（可能为空） */
  export interface FieldSignOption {
    label: string;
    value: string;
    articles: LegalBasisOption[];
  }

  /** 人工复核参数 */
  export interface ReviewPayload {
    id: number;
    reviewResult: string;
    finishedResult?: string; // 成品环节必填：QUALIFIED/REWORK/SCRAPPED（SCRAPPED 整批锁定，必带 locationId）
    reviewBasis?: string; // 判定依据：多选法条以**换行**拼成一个字符串（不强制，留空即不记录）
    storageMethod?: string;
    disposition?: string;
    location?: string;
    locationId?: number; // 受控库位编号：有污染必填且须为污染管控库位（名称由后端回写）
    marked?: boolean;
    remark?: string;
    signImg?: string; // 手写签名图片 URL（手写板上传后回填，留空不存）
  }

  /** 判定履历行：一次操作(创建/改单/复核)后的 AI+人工 完整快照(只增不改) */
  export interface CheckHistoryLog {
    id?: number;
    checkId?: number;
    recordNo?: string;
    opType?: string; // CREATE/UPDATE/REVIEW
    itemName?: string;
    fieldSigns?: string; // 当时的现场污染特征快照（换行分隔）
    aiResult?: string;
    aiConfidence?: number;
    aiReason?: string;
    aiBasis?: string; // 当时 AI 援引的法规依据
    suggestedStorage?: string;
    reviewResult?: string;
    reviewBasis?: string; // 人工复核的判定依据（仅 REVIEW 行有值）
    finishedResult?: string;
    storageMethod?: string;
    disposition?: string;
    location?: string;
    marked?: boolean;
    remark?: string;
    opBy?: string; // 操作人
    opTime?: number; // 操作时间
    createTime?: number;
  }
}

/** 查询污染判定分页 */
export function getPollutionCheckPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPollutionCheckApi.PollutionCheck>>(
    '/mes/safety-env/pollution-check/page',
    { params },
  );
}

/** 查询污染判定详情 */
export function getPollutionCheck(id: number) {
  return requestClient.get<MesSetPollutionCheckApi.PollutionCheck>(
    `/mes/safety-env/pollution-check/get?id=${id}`,
  );
}

/** 新增污染判定 */
export function createPollutionCheck(data: MesSetPollutionCheckApi.PollutionCheck) {
  return requestClient.post<number>('/mes/safety-env/pollution-check/create', data);
}

/** 修改污染判定(仅待复核) */
export function updatePollutionCheck(data: MesSetPollutionCheckApi.PollutionCheck) {
  return requestClient.put('/mes/safety-env/pollution-check/update', data);
}

/**
 * 发起变更(仅已复核的判定)
 *
 * 收口后的判定内容冻死，改动一律新开一份记录：本接口新开一行承接改动，原单回填 supersededBy 并
 * 从此只读。新单同样回到待复核——变更后的结论要重新复核，不继承原单的结论。
 *
 * 与 update 的区别不在字段而在单号：update 改的是**同一行**，amend 生成**新行**。
 * 所以它返回的是新单 id（Ledger 那边要按新单号重新串），不是原单 id。
 */
export function amendPollutionCheck(data: MesSetPollutionCheckApi.AmendPayload) {
  return requestClient.post<number>('/mes/safety-env/pollution-check/amend', data);
}

/** AI 初筛预览(不落库) */
export function prescreenPollutionCheck(data: MesSetPollutionCheckApi.PollutionCheck) {
  return requestClient.post<MesSetPollutionCheckApi.AiSuggestion>(
    '/mes/safety-env/pollution-check/prescreen',
    data,
  );
}

/** 人工复核(终态收口：无污染/有污染) */
export function reviewPollutionCheck(data: MesSetPollutionCheckApi.ReviewPayload) {
  return requestClient.post('/mes/safety-env/pollution-check/review', data);
}

/**
 * 受控库位调整(仅已复核)
 *
 * 判定复核后即终态不可改，但受控库位是物理事实（倒库/危废间扩容）必须能调整：
 * 后端会同步判定行 + 台账行 + 批次污染戳三处，所以这里只能走这个接口，不能改用 update。
 */
export function changePollutionControlLocation(data: {
  id: number;
  locationId: number;
  /**
   * 手写签名图 URL。库位是环保专员手改的物理事实，后端缺签名直接拒（1040818019）。
   * 故意**不设可选**：这个动作的所有入口由 TS 兜着，漏传签名编译就不过。
   */
  signImg: string;
  opinion?: string;
}) {
  return requestClient.put('/mes/safety-env/pollution-check/control-location', data);
}

/** 删除污染判定(仅待复核) */
export function deletePollutionCheck(id: number) {
  return requestClient.delete(`/mes/safety-env/pollution-check/delete?id=${id}`);
}

/**
 * 查询可引用的法规依据白名单（AI 初筛与人工复核共用同一份）
 *
 * label 是短标签（法规简称+条款号+要点），value 是落库全文——两者不能混用：
 * 落库要能对外引用，展示要在一行里看得下。
 */
export function getLegalBasisList() {
  return requestClient.get<MesSetPollutionCheckApi.LegalBasisOption[]>(
    '/mes/safety-env/pollution-check/legal-basis',
  );
}

/** 查询现场污染特征清单（每项带命中的法条候选，供复核收窄依据候选） */
export function getFieldSignList() {
  return requestClient.get<MesSetPollutionCheckApi.FieldSignOption[]>(
    '/mes/safety-env/pollution-check/field-signs',
  );
}

/** 查询污染判定履历(创建→改单→复核，时间正序) */
export function getPollutionCheckHistory(checkId: number) {
  return requestClient.get<MesSetPollutionCheckApi.CheckHistoryLog[]>(
    `/mes/safety-env/pollution-check/history?checkId=${checkId}`,
  );
}

/** 导出污染判定记录(供环保检查/追溯，导出列与列表口径一致) */
export function exportPollutionCheck(params: any) {
  return requestClient.download('/mes/safety-env/pollution-check/export-excel', {
    params,
  });
}
