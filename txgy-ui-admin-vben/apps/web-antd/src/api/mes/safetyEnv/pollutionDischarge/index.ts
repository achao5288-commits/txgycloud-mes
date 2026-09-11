import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesPollutionDischargeApi {
  /** MES 安全环保检测-排放合规流水（台账 已排放 流转自动落档，只读） */
  export interface DischargeRecord {
    id?: number;
    ledgerId?: number; // 来源台账编号
    sourceRecordNo?: string; // 来源判定记录号(PC-...)
    stage?: string; // 环节
    batchNo?: string; // 批次号
    itemCode?: string; // 物料/产品编码
    itemName?: string; // 物料/产品名称
    itemSpec?: string; // 规格
    destination?: string; // 排放去向
    standard?: string; // 执行排放标准
    dischargeTime?: number; // 排放(登记)时间
    remark?: string; // 备注
    creator?: string; // 登记人
    createTime?: number;
  }
}

/** 排放合规流水分页（按来源记录/环节/批次/物料/去向筛选） */
export function getPollutionDischargePage(
  params: PageParam & { sourceRecordNo?: string; stage?: string; batchNo?: string; itemName?: string; destination?: string },
) {
  return requestClient.get<PageResult<MesPollutionDischargeApi.DischargeRecord>>(
    '/mes/safety-env/pollution-discharge/page',
    { params },
  );
}
