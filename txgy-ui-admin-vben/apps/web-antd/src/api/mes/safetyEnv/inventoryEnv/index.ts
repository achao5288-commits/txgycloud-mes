import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesInventoryEnvApi {
  /** MES 在库环保视图行（库存行 × 批次污染投影 × 最近判定 × 四项判据） */
  export interface InventoryEnv {
    id?: number; // 库存行编号
    itemTypeId?: number; // 物料分类编号
    itemId?: number; // 物料编号
    itemCode?: string; // 物料编码
    itemName?: string; // 物料名称
    specification?: string; // 规格型号
    unitMeasureName?: string; // 计量单位名称
    batchId?: number; // 批次编号：空=该库存行无批次，发起不了在库检测
    batchCode?: string; // 批次号
    warehouseId?: number; // 仓库编号
    warehouseName?: string; // 仓库名称
    locationId?: number; // 库区编号
    locationName?: string; // 库区名称
    areaId?: number; // 库位编号
    areaName?: string; // 库位名称
    areaPollutionControl?: boolean; // 库位是否受控
    quantity?: number; // 在库数量
    receiptTime?: number; // 入库时间
    stockDays?: number; // 在库天数
    frozen?: boolean; // 是否冻结
    // 污染投影（批次主数据）
    pollutionStatus?: string; // POLLUTED / 其它 / null=未判定
    pollutionLocation?: string; // 污染物隔离位置
    pollutionMarked?: boolean; // 批次是否标记
    pollutionSrcRecord?: string; // 源判定记录编号
    // 最近一次判定
    checkId?: number; // 空=该批次从未检测
    checkRecordNo?: string; // 判定记录号
    aiResult?: string; // AI 初筛结论
    aiConfidence?: number; // AI 置信度
    reviewResult?: string; // 人工复核结论，空=待复核（不算有效判定）
    reviewTime?: number; // 复核时间
    disposition?: string; // 去向/处置方式
    checkLocation?: string; // 受控库位名称快照
    checkLocationId?: number; // 受控库位编号
    checkMarked?: boolean; // 判定是否标记
    ledgerId?: number; // 台账行编号（标记终审用）
    ledgerMarked?: boolean; // 台账是否已标记终审
    checkByBatchNo?: boolean; // 判定只能按批次号字符串匹配（历史记录无 batchId）
    // 四项体检判据（服务端算）
    notChecked?: boolean; // 未检测
    overdue?: boolean; // 超期未检
    stockpiled?: boolean; // 积压
    mixed?: boolean; // 混放
  }

  /** 全库体检汇总：各判据是并列命中数，一行可同时命中多项，加起来大于 stockRows 不是 bug */
  export interface InspectSummary {
    stockRows?: number; // 在库库存行总数
    batchCount?: number; // 涉及批次数
    coveredBatchCount?: number; // 已复核批次数
    pollutedBatchCount?: number; // 复核为「有污染」的批次数
    notChecked?: number; // 未检测行数
    overdue?: number; // 超期未检行数
    stockpiled?: number; // 积压行数
    mixed?: number; // 混放行数
    pollutedRows?: number; // 污染投影为 POLLUTED 的行数（卡片值）
    pending?: number; // 待检行数（命中任一项，去重后）
  }

  /** 看板 - 单个维度分档 */
  export interface DashboardItem extends InspectSummary {
    dimId?: number; // 仓库编号或物料分类编号
    dimName?: string; // 维度名称（主数据缺失时为空）
  }

  /** 看板 - 待复核积压 */
  export interface PendingReview {
    count?: number; // 待复核判定条数
    maxWaitDays?: number; // 最长已等待天数
  }

  /** 看板 - 处置时效 */
  export interface LedgerDuration {
    closedCount?: number; // 已闭环台账行数
    avgDays?: number; // 平均处置天数
    maxDays?: number; // 最长处置天数
  }

  /** 合规看板 */
  export interface Dashboard {
    summary?: InspectSummary;
    coverRate?: number; // 检测覆盖率 0~1
    pollutedRate?: number; // 异常率 0~1
    pendingReview?: PendingReview;
    ledgerDuration?: LedgerDuration;
    byWarehouse?: DashboardItem[];
    byItemType?: DashboardItem[];
  }

  /** 批次档案 - 链上单个节点 */
  export interface BatchProfileNode {
    batchId?: number;
    batchCode?: string;
    itemId?: number;
    itemName?: string;
    productionDate?: number;
    expireDate?: number;
    qualityStatus?: number;
    // 环保档案：pollutionStatus 是「当前」投影，lastReviewResult 是「最近一次判定结论」，
    // 两者可以不一致（判过 POLLUTED 但台账已处置闭环 → 投影清空），页面上要分开显示
    pollutionStatus?: string;
    pollutionLocation?: string;
    pollutionMarked?: boolean;
    pollutionSrcRecord?: string;
    lastReviewResult?: string;
    lastCheckRecordNo?: string;
    lastCheckTime?: number;
    judged?: boolean; // 有已复核判定
    inStock?: boolean; // 当前在库
    focus?: boolean; // 是否查询起点
  }

  /** 批次档案 */
  export interface BatchProfile {
    focus?: BatchProfileNode;
    forward?: BatchProfileNode[];
    backward?: BatchProfileNode[];
    unjudgedCount?: number; // 链上未判定批次数
    pollutedCount?: number; // 链上污染批次数
  }
}

/** 查询在库环保视图分页（待检清单：带 inspectStatus=NOT_CHECKED/OVERDUE/STOCKPILE 即待检） */
export function getInventoryEnvPage(params: PageParam) {
  return requestClient.get<PageResult<MesInventoryEnvApi.InventoryEnv>>(
    '/mes/safety-env/inventory-env/page',
    { params },
  );
}

/** 全库环保体检（只读重算，不落体检批次） */
export function inspectInventoryEnv() {
  return requestClient.get<MesInventoryEnvApi.InspectSummary>(
    '/mes/safety-env/inventory-env/inspect',
  );
}

/** 合规看板 */
export function getInventoryEnvDashboard() {
  return requestClient.get<MesInventoryEnvApi.Dashboard>(
    '/mes/safety-env/inventory-env/dashboard',
  );
}

/** 批次档案（前向/后向链 + 每节点环保档案） */
export function getBatchProfile(batchCode: string) {
  return requestClient.get<MesInventoryEnvApi.BatchProfile>(
    '/mes/safety-env/inventory-env/batch-profile',
    { params: { batchCode } },
  );
}
