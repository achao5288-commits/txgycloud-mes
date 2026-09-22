import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesPollutionLedgerApi {
  /** MES 安全环保检测-污染/危废暂存台账 */
  export interface Ledger {
    id?: number; // 编号
    sourceCheckId?: number; // 来源判定记录ID
    sourceRecordNo?: string; // 来源判定记录编号(PC-...)
    stage?: string; // 环节
    bizNo?: string; // 关联单号
    batchNo?: string; // 批次号
    itemCode?: string; // 物料/产品编码
    itemName?: string; // 物料/产品名称
    itemSpec?: string; // 规格
    weight?: number; // 重量——单位见 unitName，由源判定行带入
    unitName?: string; // 重量单位：KG=已换算的质量，其余为物料原单位(个/箱/米…)
    disposition?: string; // 处置方式
    storageMethod?: string; // 最终存储方法
    location?: string; // 去向/库位
    marked?: boolean; // 是否标记
    status?: string; // 台账状态 STORED/PROCESSING/REUSED/DISCHARGED/DISPOSED
    statusBy?: string; // 最近流转人
    statusTime?: number; // 最近流转时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }

  /** 处置流转参数(目标=已排放时另登记 排放去向/执行标准) */
  export interface StatusPayload {
    id: number;
    status: string;
    /** 流转备注（与签字说明是两回事：这个是业务说明，opinion 进签字记录） */
    remark?: string;
    destination?: string;
    standard?: string;
    /** 手写签名图 URL：流转会写终态、落排放合规流水、重投影批次污染戳，后端缺签名直接拒（1040819005）。必传 */
    signImg: string;
    /** 签署意见（随签字记录留档） */
    opinion?: string;
  }

  /** 标记品终审参数（环保专员） */
  export interface MarkPayload {
    id: number;
    marked: boolean;
    /** 会作为这次签字的说明落进签字记录（后端拿它当 opinion） */
    remark?: string;
    /** 手写签名图 URL：终审是人为主张，后端缺签名直接拒（1040819004）。必传，见 changePollutionControlLocation 的同一理由 */
    signImg: string;
  }

  /** 台账流转历史行：登记/处置/闭环 每步一行(只增不改) */
  export interface LedgerHistoryLog {
    id?: number;
    ledgerId?: number;
    sourceRecordNo?: string;
    fromStatus?: string; // 空=初始登记
    toStatus?: string;
    operator?: string;
    opTime?: number;
    remark?: string;
    createTime?: number;
  }
}

/** 查询台账分页(可按来源判定记录精确反查) */
export function getPollutionLedgerPage(params: PageParam & { sourceRecordNo?: string }) {
  return requestClient.get<PageResult<MesPollutionLedgerApi.Ledger>>(
    '/mes/safety-env/pollution-ledger/page',
    { params },
  );
}

/** 处置流转(终态清批次污染戳) */
export function updatePollutionLedgerStatus(data: MesPollutionLedgerApi.StatusPayload) {
  return requestClient.put('/mes/safety-env/pollution-ledger/status', data);
}

/** 查询台账流转历史(登记→处置→闭环，时间正序) */
/** 标记品终审（标记/解除标记，环保专员专属权限） */
export function updatePollutionLedgerMark(data: MesPollutionLedgerApi.MarkPayload) {
  return requestClient.put('/mes/safety-env/pollution-ledger/mark', data);
}

export function getPollutionLedgerHistory(ledgerId: number) {
  return requestClient.get<MesPollutionLedgerApi.LedgerHistoryLog[]>(
    `/mes/safety-env/pollution-ledger/history?ledgerId=${ledgerId}`,
  );
}
