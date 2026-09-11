import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEmergencyPlanApi {
  /** MES 安全环保检测-应急预案 */
  export interface EmergencyPlan {
    id?: number; // 编号
    planNo?: string; // 预案编号
    planName?: string; // 预案名称
    planType?: string; // 预案类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)
    version?: string; // 版本号
    publishDate?: string; // 发布日期（备案时限与评估周期的起算点）
    filingDeadline?: string; // 备案截止日（发布 + 20 个工作日，派生值不进表单）
    filingNo?: string; // 备案号（报生态环境部门后登记）
    filingDate?: string; // 备案日期
    attachUrl?: string; // 预案附件地址
    lastReviewDate?: string; // 上次评估修订日期
    nextReviewDate?: string; // 下次评估修订日期（发布或上次修订 + 3 年，派生值不进表单）
    reviewReason?: string; // 修订原因（工艺/物料/法规变化）
    status?: string; // 状态：DRAFT(草稿)/PUBLISHED(已发布)/FILED(已备案)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询应急预案分页 */
export function getEmergencyPlanPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEmergencyPlanApi.EmergencyPlan>>(
    '/mes/safety-env/emergency-plan/page',
    { params },
  );
}

/** 查询应急预案详情 */
export function getEmergencyPlan(id: number) {
  return requestClient.get<MesSetEmergencyPlanApi.EmergencyPlan>(
    `/mes/safety-env/emergency-plan/get?id=${id}`,
  );
}

/** 新增应急预案 */
export function createEmergencyPlan(data: MesSetEmergencyPlanApi.EmergencyPlan) {
  return requestClient.post<number>('/mes/safety-env/emergency-plan/create', data);
}

/** 修改应急预案 */
export function updateEmergencyPlan(data: MesSetEmergencyPlanApi.EmergencyPlan) {
  return requestClient.put('/mes/safety-env/emergency-plan/update', data);
}

/** 删除应急预案 */
export function deleteEmergencyPlan(id: number) {
  return requestClient.delete(`/mes/safety-env/emergency-plan/delete?id=${id}`);
}

// ==================== 发布 / 备案 / 评估修订（共用 file 权限） ====================

/**
 * 发布预案：草稿→已发布。
 * 发布日是备案时限（+20 工作日）与评估周期（+3 年）的起算点，所以后端一定要这一天。
 */
export function publishEmergencyPlan(id: number, publishDate?: string) {
  return requestClient.post('/mes/safety-env/emergency-plan/publish', null, {
    params: { id, publishDate },
  });
}

/** 备案登记：已发布→已备案。逾期仍允许备案（后端只登记不拦截）。 */
export function fileEmergencyPlan(
  id: number,
  filingNo: string,
  filingDate?: string,
) {
  return requestClient.post('/mes/safety-env/emergency-plan/file', null, {
    params: { id, filingNo, filingDate },
  });
}

/** 评估修订：工艺/物料/法规变化或 3 年到期后重新评估，回到已发布（需重新备案） */
export function reviewEmergencyPlan(id: number, reason?: string) {
  return requestClient.post('/mes/safety-env/emergency-plan/review', null, {
    params: { id, reason },
  });
}

/** 查询临近评估修订期限的预案 */
export function getEmergencyPlanDueReview(days?: number) {
  return requestClient.get<MesSetEmergencyPlanApi.EmergencyPlan[]>(
    '/mes/safety-env/emergency-plan/due-review',
    { params: { days } },
  );
}
