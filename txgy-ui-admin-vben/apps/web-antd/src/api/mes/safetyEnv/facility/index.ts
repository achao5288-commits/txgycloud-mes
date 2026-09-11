import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesFacilityApi {
  /** 治污设施（设计文档 §6.3：台账 / 换炭→HW49 / 停运申报审批 / 同开同停） */
  export interface TreatmentFacility {
    id?: number;
    facilityNo?: string; // 设施编号
    facilityName?: string;
    facilityType?: string; // ACTIVATED_CARBON/CATALYTIC_COMBUSTION/BAG_FILTER
    outletCode?: string; // 对应排放口
    lineCode?: string; // 对应产线（同开同停判定用）
    runStatus?: string; // RUNNING/STOPPED —— 只能经申报审批改，改档案改不动
    designAirVolume?: number; // 设计风量
    consumableName?: string; // 耗材名称（如 活性炭）
    replaceCycleDays?: number; // 更换周期（天）
    lastReplaceDate?: string; // 上次更换日 yyyy-MM-dd
    nextReplaceDate?: string; // 下次更换日（由 上次+周期 派生，不手填）
    shutdownStatus?: string; // NONE/PENDING/APPROVED/REJECTED
    shutdownReason?: string;
    shutdownPlanStart?: number; // epoch 毫秒
    shutdownPlanEnd?: number;
    shutdownDeclaredAt?: number;
    shutdownApprover?: string;
    shutdownApprovedAt?: number;
    status?: string; // ENABLED/DISABLED
    remark?: string;
    createTime?: number;
  }

  /** 换炭结果（换炭与废活性炭 HW49 登记同一事务） */
  export interface ReplaceResult {
    facilityId?: number;
    facilityName?: string;
    wasteLedgerId?: number; // 同时产生的危废台账行 id
    wasteManifestNo?: string;
    wasteCode?: string;
    quantity?: number;
    replaceDate?: string;
    nextReplaceDate?: string;
    message?: string;
  }

  /** 同开同停校验结果 */
  export interface CoRunResult {
    passed?: boolean;
    facilityId?: number;
    facilityName?: string;
    lineCode?: string;
    runStatus?: string;
    productionRunning?: boolean;
    reasons?: string[];
  }
}

// ==================== 台账 ====================

export function getFacilityPage(
  params: PageParam & {
    facilityNo?: string;
    facilityName?: string;
    facilityType?: string;
    outletCode?: string;
    runStatus?: string;
    status?: string;
  },
) {
  return requestClient.get<PageResult<MesFacilityApi.TreatmentFacility>>(
    '/mes/safety-env/facility/page',
    { params },
  );
}

export function getFacility(id: number) {
  return requestClient.get<MesFacilityApi.TreatmentFacility>(
    `/mes/safety-env/facility/get?id=${id}`,
  );
}

export function createFacility(data: MesFacilityApi.TreatmentFacility) {
  return requestClient.post<number>('/mes/safety-env/facility/create', data);
}

/** 注意：runStatus/shutdownStatus 经此接口改不动，须走停运申报审批 */
export function updateFacility(data: MesFacilityApi.TreatmentFacility) {
  return requestClient.put('/mes/safety-env/facility/update', data);
}

export function deleteFacility(id: number) {
  return requestClient.delete(`/mes/safety-env/facility/delete?id=${id}`);
}

// ==================== 换炭 → HW49 ====================

/** 换炭：同一事务内登记废活性炭 HW49 入桶并顺延下次更换日期。量必须 > 0。 */
export function replaceConsumable(data: {
  id: number;
  quantity: number;
  replaceDate?: string;
  containerCode?: string;
  storageLocation?: string;
  handler?: string;
}) {
  return requestClient.post<MesFacilityApi.ReplaceResult>(
    '/mes/safety-env/facility/replace-consumable',
    data,
  );
}

/** 换炭到期预警（含已逾期） */
export function listDueReplace(days = 30) {
  return requestClient.get<MesFacilityApi.TreatmentFacility[]>(
    '/mes/safety-env/facility/due-replace',
    { params: { days } },
  );
}

// ==================== 停运申报 + 审批 ====================

export function declareShutdown(data: {
  id: number;
  shutdownReason: string;
  shutdownPlanStart?: number;
  shutdownPlanEnd?: number;
}) {
  return requestClient.post<MesFacilityApi.TreatmentFacility>(
    '/mes/safety-env/facility/shutdown-declare',
    data,
  );
}

export function approveShutdown(data: {
  id: number;
  approved: boolean;
  opinion?: string;
  signImg?: string; // 手写签名图片 URL（手写板上传后回填，留空不存）
}) {
  return requestClient.post<MesFacilityApi.TreatmentFacility>(
    '/mes/safety-env/facility/shutdown-approve',
    data,
  );
}

export function resumeFacility(id: number) {
  return requestClient.post<MesFacilityApi.TreatmentFacility>(
    `/mes/safety-env/facility/resume?id=${id}`,
  );
}

/** 未批先停清单 */
export function listStoppedWithoutApproval() {
  return requestClient.get<MesFacilityApi.TreatmentFacility[]>(
    '/mes/safety-env/facility/stopped-without-approval',
  );
}

// ==================== 同开同停 ====================

/**
 * 只读校验。productionRunning 由调用方给 —— 工单表没有产线/设施列，
 * 后端取不到这个信号（见后端 CoRunReqVO 注释），不编。
 */
export function checkCoRun(data: { id: number; productionRunning: boolean }) {
  return requestClient.post<MesFacilityApi.CoRunResult>(
    '/mes/safety-env/facility/co-run-check',
    data,
  );
}
