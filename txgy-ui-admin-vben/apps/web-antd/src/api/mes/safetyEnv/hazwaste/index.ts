import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesHazwasteApi {
  /** 危废台账（环节只进不退：产生→贮存→转移→处置） */
  export interface HazardousWaste {
    id?: number;
    manifestNo?: string; // 危废联单号（与国家固废系统联单同键）
    wasteCode?: string; // 危废代码 HW08/HW49...
    wasteName?: string;
    quantity?: number; // 数量
    quantityUnit?: string; // 数量单位
    stage?: string; // 环节 GENERATED/STORED/TRANSFERRED/DISPOSED
    storageLocation?: string; // 贮存地点
    containerCode?: string; // 容器码（一桶一码）
    labelUrl?: string; // HJ1276 标签归档 URL
    counterparty?: string; // 交接方/接收单位
    woId?: number; // 来源工单编号
    handleTime?: number; // 交接/处理时间（epoch 毫秒）
    handler?: string; // 经办人
    status?: string; // DRAFT/APPROVED/REJECTED
    remark?: string;
    creator?: string;
    createTime?: number;
  }

  /** 国家固废系统电子转移联单（五方 + 时限） */
  export interface HazwasteManifest {
    id?: number;
    manifestNo?: string;
    wasteCode?: string;
    wasteName?: string;
    quantity?: number;
    quantityUnit?: string;
    generateUnit?: string; // 产生单位
    carrierUnit?: string; // 运输单位
    receiveUnit?: string; // 接收单位
    storageUnit?: string; // 贮存单位
    disposeUnit?: string; // 处置单位
    carrierConfirmTime?: number; // 运输方确认时间
    receiveConfirmTime?: number; // 接收方确认时间
    storageConfirmTime?: number;
    disposeConfirmTime?: number;
    declaredTime?: number; // 申报时间
    declareDeadline?: number; // 申报/确认时限
    vehicleNo?: string; // 车牌号
    netWeight?: number; // 地磅净重
    transferTime?: number; // 启运出厂时间
    gateReleaseTime?: number; // 门卫放行时间
    gateGuard?: string; // 放行门卫
    status?: string; // DRAFT/DECLARED/EFFECTIVE/TRANSFERRED/CLOSED
    remark?: string;
  }

  /** 签字记录（只增不改删） */
  export interface SignRecord {
    id?: number;
    bizType?: string;
    bizNo?: string;
    signRole?: string; // HANDOVER/DRIVER/RECEIVER/GUARD
    signUser?: string;
    signTime?: number;
    opinion?: string;
  }

  /** 门卫校验/放行结果 */
  export interface GateCheckResult {
    passed?: boolean;
    manifestNo?: string;
    wasteName?: string;
    status?: string;
    effective?: boolean;
    vehicleMatched?: boolean;
    missingSignRoles?: string[];
    reasons?: string[];
    gateReleaseTime?: number;
    gateGuard?: string;
  }

  /** HJ1276 标签字段集 */
  export interface HazwasteLabel {
    title?: string;
    wasteName?: string;
    wasteCode?: string;
    hazardTraits?: string;
    generateUnit?: string;
    containerCode?: string;
    quantity?: number;
    quantityUnit?: string;
    labelDate?: string;
    qrContent?: string;
    labelUrl?: string;
  }
}

// ==================== 危废台账 ====================

export function getHazwastePage(
  params: PageParam & {
    manifestNo?: string;
    wasteCode?: string;
    wasteName?: string;
    stage?: string;
    containerCode?: string;
    status?: string;
  },
) {
  return requestClient.get<PageResult<MesHazwasteApi.HazardousWaste>>(
    '/mes/safety-env/hazwaste/page',
    { params },
  );
}

export function getHazwaste(id: number) {
  return requestClient.get<MesHazwasteApi.HazardousWaste>(
    `/mes/safety-env/hazwaste/get?id=${id}`,
  );
}

export function createHazwaste(data: MesHazwasteApi.HazardousWaste) {
  return requestClient.post<number>('/mes/safety-env/hazwaste/create', data);
}

export function updateHazwaste(data: MesHazwasteApi.HazardousWaste) {
  return requestClient.put('/mes/safety-env/hazwaste/update', data);
}

export function deleteHazwaste(id: number) {
  return requestClient.delete(`/mes/safety-env/hazwaste/delete?id=${id}`);
}

/** 推进环节（只进不退） */
export function advanceHazwasteStage(id: number, stage: string, handler?: string) {
  return requestClient.post('/mes/safety-env/hazwaste/advance-stage', null, {
    params: { id, stage, handler },
  });
}

// ==================== HJ1276 标签 ====================

export function getHazwasteLabel(id: number) {
  return requestClient.get<MesHazwasteApi.HazwasteLabel>(
    `/mes/safety-env/hazwaste/label?id=${id}`,
  );
}

export function archiveHazwasteLabel(id: number) {
  return requestClient.post<string>(
    `/mes/safety-env/hazwaste/label/archive?id=${id}`,
  );
}

// ==================== 危废转移联单 ====================

export function getManifestByNo(manifestNo: string) {
  return requestClient.get<MesHazwasteApi.HazwasteManifest>(
    '/mes/safety-env/hazwaste/manifest/get-by-no',
    { params: { manifestNo } },
  );
}

export function createManifest(data: MesHazwasteApi.HazwasteManifest) {
  return requestClient.post<number>(
    '/mes/safety-env/hazwaste/manifest/create',
    data,
  );
}

export function updateManifest(data: MesHazwasteApi.HazwasteManifest) {
  return requestClient.put('/mes/safety-env/hazwaste/manifest/update', data);
}

/** 产生方申报（草稿→已申报） */
export function declareManifest(manifestNo: string) {
  return requestClient.post('/mes/safety-env/hazwaste/manifest/declare', null, {
    params: { manifestNo },
  });
}

/** 五方之一确认 */
export function confirmManifestParty(manifestNo: string, partyRole: string) {
  return requestClient.post('/mes/safety-env/hazwaste/manifest/confirm', null, {
    params: { manifestNo, partyRole },
  });
}

/** 回执归档（已出厂→已归档） */
export function closeManifest(manifestNo: string) {
  return requestClient.post('/mes/safety-env/hazwaste/manifest/close', null, {
    params: { manifestNo },
  });
}

/** 四方会签 */
export function signManifest(data: {
  manifestNo: string;
  signRole: string;
  opinion?: string;
  signImg?: string; // 手写签名图片 URL（手写板上传后回填，留空不存）
}) {
  return requestClient.post('/mes/safety-env/hazwaste/manifest/sign', data);
}

// ==================== 门卫硬放行 ====================

export function checkHazwasteGate(data: {
  manifestNo: string;
  vehicleNo?: string;
}) {
  return requestClient.post<MesHazwasteApi.GateCheckResult>(
    '/mes/safety-env/hazwaste/gate-check',
    data,
  );
}

export function releaseHazwasteGate(data: {
  manifestNo: string;
  vehicleNo?: string;
  signImg?: string; // 门卫手写签名图片 URL（手写板上传后回填，留空不存）
}) {
  return requestClient.post<MesHazwasteApi.GateCheckResult>(
    '/mes/safety-env/hazwaste/gate-release',
    data,
  );
}
