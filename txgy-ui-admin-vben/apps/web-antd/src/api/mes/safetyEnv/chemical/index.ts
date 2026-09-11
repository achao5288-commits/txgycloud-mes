import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesChemicalApi {
  /** 危化品档案（设计文档 §5.2：分级储存 / 禁配 / 储量上限） */
  export interface ChemicalProfile {
    id?: number;
    profileNo?: string; // 档案编号
    chemicalCode?: string; // 危化品编码
    chemicalName?: string;
    casNo?: string; // CAS 号
    compatGroup?: string; // 相容组 ISOCYANATE/POLYOL/THINNER/EPOXY/WATER/ALCOHOL/AMINE
    hazardClass?: string; // 危险性类别
    storageZone?: string; // 储存专区 GENERAL/EXPLOSION_PROOF/ISOLATION/SPECIAL
    storageLocation?: string; // 具体库位
    storageLimit?: number; // 储量上限
    storageUnit?: string; // 储量单位
    stockQuantity?: number; // 当前存量（只由入库累加，改档案不覆盖）
    incompatibleGroups?: string; // 人工追加的禁配组，逗号分隔
    explosionProof?: boolean; // 是否须防爆
    msdsUrl?: string; // MSDS 挂载地址
    msdsExpireDate?: string; // MSDS 到期日 yyyy-MM-dd
    expireManage?: boolean; // 是否效期管理
    shelfLifeDays?: number; // 保质期天数
    emergencyMeasure?: string; // 应急处置措施
    status?: string; // ENABLED/DISABLED
    remark?: string;
    creator?: string;
    createTime?: number;
  }

  /** 库位/入库校验结果 */
  export interface StorageCheckResult {
    passed?: boolean;
    profileNo?: string;
    chemicalName?: string;
    compatGroup?: string;
    compatGroupName?: string;
    storageLocation?: string;
    msdsOk?: boolean;
    zoneOk?: boolean;
    quotaOk?: boolean | null; // 没给本次入库量时为 null（不判，而非判过）
    incompatibleWith?: string[];
    stockQuantity?: number;
    storageLimit?: number;
    reasons?: string[];
  }

  /** 五双双人签字状态 */
  export interface DoubleSignResult {
    bizNo?: string;
    signAction?: string;
    signers?: string[];
    signCount?: number;
    complete?: boolean;
    lastSignTime?: number;
  }

  /** 预警汇总 */
  export interface ChemicalAlerts {
    overQuota?: ChemicalProfile[];
    msdsMissing?: ChemicalProfile[];
    msdsExpiring?: ChemicalProfile[];
  }
}

// ==================== 档案 ====================

export function getChemicalPage(
  params: PageParam & {
    profileNo?: string;
    chemicalName?: string;
    chemicalCode?: string;
    compatGroup?: string;
    storageZone?: string;
    status?: string;
  },
) {
  return requestClient.get<PageResult<MesChemicalApi.ChemicalProfile>>(
    '/mes/safety-env/chemical/page',
    { params },
  );
}

export function getChemical(id: number) {
  return requestClient.get<MesChemicalApi.ChemicalProfile>(
    `/mes/safety-env/chemical/get?id=${id}`,
  );
}

export function getChemicalByNo(profileNo: string) {
  return requestClient.get<MesChemicalApi.ChemicalProfile>(
    '/mes/safety-env/chemical/get-by-no',
    { params: { profileNo } },
  );
}

export function createChemical(data: MesChemicalApi.ChemicalProfile) {
  return requestClient.post<number>('/mes/safety-env/chemical/create', data);
}

export function updateChemical(data: MesChemicalApi.ChemicalProfile) {
  return requestClient.put('/mes/safety-env/chemical/update', data);
}

export function deleteChemical(id: number) {
  return requestClient.delete(`/mes/safety-env/chemical/delete?id=${id}`);
}

// ==================== 分级储存四道校验 ====================

/** 只判不写：MSDS → 专区 → 禁配 → 储量上限 */
export function checkChemicalStorage(data: {
  profileNo: string;
  storageLocation?: string;
  quantity?: number;
}) {
  return requestClient.post<MesChemicalApi.StorageCheckResult>(
    '/mes/safety-env/chemical/check-storage',
    data,
  );
}

/** 入库（过四道校验后累加存量；任一条不过即拒） */
export function stockInChemical(data: {
  profileNo: string;
  quantity: number;
  storageLocation?: string;
}) {
  return requestClient.post<MesChemicalApi.StorageCheckResult>(
    '/mes/safety-env/chemical/stock-in',
    data,
  );
}

// ==================== 五双双人签字 ====================

/** 签字（同一作业同一动作须两个不同账号各签一次） */
export function doubleSignChemical(data: {
  bizNo: string;
  signAction: string;
  opinion?: string;
  signImg?: string; // 手写签名图片 URL（手写板上传后回填，留空不存）
}) {
  return requestClient.post<MesChemicalApi.DoubleSignResult>(
    '/mes/safety-env/chemical/double-sign',
    data,
  );
}

export function getDoubleSignStatus(bizNo: string, signAction: string) {
  return requestClient.get<MesChemicalApi.DoubleSignResult>(
    '/mes/safety-env/chemical/double-sign-status',
    { params: { bizNo, signAction } },
  );
}

// ==================== 预警 ====================

export function getChemicalAlerts(days = 30) {
  return requestClient.get<MesChemicalApi.ChemicalAlerts>(
    '/mes/safety-env/chemical/alerts',
    { params: { days } },
  );
}
