import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEmergencyEventApi {
  /** 应急废物逐桶明细（桶码 ↔ 称重记录 ↔ 危废台账行） */
  export interface EmergencyWaste {
    id?: number;
    eventId?: number;
    eventNo?: string;
    containerCode?: string; // 危废桶码
    wasteCode?: string; // 危废类别
    wasteName?: string;
    netWeight?: number; // 过秤净重 kg
    storageLocation?: string; // 入库暂存库位
    weighRecordId?: number; // 称重记录编号
    hazwasteId?: number; // 危废台账行编号
    manifestNo?: string; // 台账业务号
  }

  /** 事件详情：本体 + 逐桶明细 */
  export interface EmergencyEventDetail {
    event?: EmergencyEvent;
    wastes?: EmergencyWaste[];
  }

  /** 处置请求：逐桶登记，不接受手填合计 */
  export interface DisposeReq {
    id: number;
    disposeNote?: string;
    disposePhotoUrl?: string;
    wastes?: {
      containerCode?: string;
      wasteCode?: string;
      wasteName?: string;
      netWeight?: number;
      storageLocation?: string;
    }[];
  }

  /** MES 安全环保检测-应急事件 */
  export interface EmergencyEvent {
    id?: number; // 编号
    eventNo?: string; // 事件编号
    eventType?: string; // 事件类型：LEAK(泄漏)/EXCEED(超标)/FACILITY_FAULT(设施故障)/OTHER(其他)
    scenario?: string; // 泄漏场景（对应处置卡）：MDI_LEAK/THINNER_LEAK/WASTE_OIL_LEAK
    occurTime?: string; // 发生时间
    location?: string; // 发生地点
    chemicalCode?: string; // 涉事化学品编码（关联危化品档案取 MSDS/禁配）
    leakQuantity?: number; // 泄漏量
    impactScope?: string; // 影响范围
    reportUser?: string; // 上报人
    reportTime?: string; // 上报时间（PDA 一键上报）
    handler?: string; // 处置人（派发时指定）
    dispatchTime?: string; // 派发时间
    disposeNote?: string; // 处置说明
    disposePhotoUrl?: string; // 处置照片
    wasteCount?: number; // 应急废物桶数（处置时按明细回填，非人工填写）
    wasteQuantity?: number; // 应急废物合计净重 kg（处置时按明细回填，非人工填写）
    reportContent?: string; // 事件报告（原因/数量/处置/整改）
    approver?: string; // 负责人
    approveTime?: string; // 签字时间
    closedTime?: string; // 闭环时间
    status?: string; // 状态：REPORTED(已上报)/DISPOSING(处置中)/PENDING_REPORT(待报告)/CLOSED(已闭环)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询应急事件分页 */
export function getEmergencyEventPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEmergencyEventApi.EmergencyEvent>>(
    '/mes/safety-env/emergency-event/page',
    { params },
  );
}

/** 查询应急事件详情 */
export function getEmergencyEvent(id: number) {
  return requestClient.get<MesSetEmergencyEventApi.EmergencyEvent>(
    `/mes/safety-env/emergency-event/get?id=${id}`,
  );
}

/** 新增应急事件 */
export function createEmergencyEvent(data: MesSetEmergencyEventApi.EmergencyEvent) {
  return requestClient.post<number>('/mes/safety-env/emergency-event/create', data);
}

/** 修改应急事件 */
export function updateEmergencyEvent(data: MesSetEmergencyEventApi.EmergencyEvent) {
  return requestClient.put('/mes/safety-env/emergency-event/update', data);
}

/** 删除应急事件 */
export function deleteEmergencyEvent(id: number) {
  return requestClient.delete(`/mes/safety-env/emergency-event/delete?id=${id}`);
}

// ============ 状态机「上报 → 处置中 → 待报告 → 闭环」（共用 close 权限） ============

/** 事件详情：本体 + 应急废物逐桶明细 */
export function getEmergencyEventDetail(id: number) {
  return requestClient.get<MesSetEmergencyEventApi.EmergencyEventDetail>(
    `/mes/safety-env/emergency-event/detail?id=${id}`,
  );
}

/** 应急任务派发：已上报→处置中 */
export function dispatchEmergencyEvent(id: number, handler?: string) {
  return requestClient.post('/mes/safety-env/emergency-event/dispatch', null, {
    params: { id, handler },
  });
}

/** 应急处置：逐桶过秤贴签入危废台账 → 待报告 */
export function disposeEmergencyEvent(
  data: MesSetEmergencyEventApi.DisposeReq,
) {
  return requestClient.post('/mes/safety-env/emergency-event/dispose', data);
}

/** 事件报告签发闭环：待报告→已闭环 */
export function closeEmergencyEvent(
  id: number,
  reportContent?: string,
  approver?: string,
  signImg?: string, // 手写签名图片 URL（手写板上传后回填，留空不存）
) {
  return requestClient.post('/mes/safety-env/emergency-event/close', null, {
    params: { id, reportContent, approver, signImg },
  });
}
