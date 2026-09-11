import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEmergencyDrillApi {
  /** 年度演练覆盖情况（设计文档 §八.2「每年至少 1 次演练」的判定） */
  export interface DrillCoverage {
    year?: number; // 年份
    closedCount?: number; // 已闭环次数（判定依据）
    totalCount?: number; // 当年演练总数
    satisfied?: boolean; // 是否达标
    drills?: EmergencyDrill[]; // 当年演练明细
  }

  /** MES 安全环保检测-应急演练 */
  export interface EmergencyDrill {
    id?: number; // 编号
    drillNo?: string; // 演练编号
    drillName?: string; // 演练名称
    planId?: number; // 关联预案编号（须为已发布/已备案的预案）
    planVersion?: string; // 演练时的预案版本快照
    drillType?: string; // 演练类型：COMPREHENSIVE(综合)/SPECIAL(专项)/ONSITE(现场处置)
    drillDate?: string; // 演练日期
    participantCount?: number; // 参加人数
    participants?: string; // 参加人员
    signSheetUrl?: string; // 签到表附件
    photoUrl?: string; // 演练照片
    videoUrl?: string; // 演练视频
    evaluation?: string; // 演练评估
    rectifyRequirement?: string; // 整改要求（填写后整改状态转 PENDING）
    rectifyStatus?: string; // 整改状态：NONE(无需整改)/PENDING(待整改)/DONE(已整改)
    rectifyDoneDate?: string; // 整改完成日期
    closedDate?: string; // 闭环日期
    status?: string; // 状态：PLANNED(已计划)/DONE(已演练)/CLOSED(已闭环)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询应急演练分页 */
export function getEmergencyDrillPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEmergencyDrillApi.EmergencyDrill>>(
    '/mes/safety-env/emergency-drill/page',
    { params },
  );
}

/** 查询应急演练详情 */
export function getEmergencyDrill(id: number) {
  return requestClient.get<MesSetEmergencyDrillApi.EmergencyDrill>(
    `/mes/safety-env/emergency-drill/get?id=${id}`,
  );
}

/** 新增应急演练 */
export function createEmergencyDrill(data: MesSetEmergencyDrillApi.EmergencyDrill) {
  return requestClient.post<number>('/mes/safety-env/emergency-drill/create', data);
}

/** 修改应急演练 */
export function updateEmergencyDrill(data: MesSetEmergencyDrillApi.EmergencyDrill) {
  return requestClient.put('/mes/safety-env/emergency-drill/update', data);
}

/** 删除应急演练 */
export function deleteEmergencyDrill(id: number) {
  return requestClient.delete(`/mes/safety-env/emergency-drill/delete?id=${id}`);
}

// ==================== 完成 / 闭环（共用 close 权限） ====================

/**
 * 演练完成：已计划→已演练。签到表/照片/视频至少留一项且评估结论非空，否则后端拒。
 */
export function finishEmergencyDrill(id: number, evaluation?: string) {
  return requestClient.post('/mes/safety-env/emergency-drill/finish', null, {
    params: { id, evaluation },
  });
}

/** 演练闭环：已演练→已闭环。有整改要求未整改完成时后端拒。 */
export function closeEmergencyDrill(id: number) {
  return requestClient.post('/mes/safety-env/emergency-drill/close', null, {
    params: { id },
  });
}

/** 年度演练覆盖情况 */
export function getEmergencyDrillYearCoverage(year?: number) {
  return requestClient.get<MesSetEmergencyDrillApi.DrillCoverage>(
    '/mes/safety-env/emergency-drill/year-coverage',
    { params: { year } },
  );
}
