import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetCarbonEmissionApi {
  /** MES 安全环保检测-碳排放核算 */
  export interface CarbonEmission {
    id?: number; // 编号
    calcNo?: string; // 核算批次号
    periodType?: string; // 核算周期
    periodStart?: string; // 周期开始日期
    periodEnd?: string; // 周期结束日期
    woId?: number; // 关联工单编号
    sourceRecordId?: number; // 关联能耗记录编号
    energyType?: string; // 能源类型
    consumption?: number; // 能源消耗量
    emissionFactor?: number; // 排放因子
    carbonEmission?: number; // 碳排放量=consumption*factor
    unit?: string; // 单位 tCO2
    processEmission?: number; // 工艺排放
    totalEmission?: number; // 合计
    createTime?: number; // 创建时间
  }
}

/** 查询碳排放核算分页 */
export function getCarbonEmissionPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetCarbonEmissionApi.CarbonEmission>>(
    '/mes/safety-env/carbon-emission/page',
    { params },
  );
}

/** 查询碳排放核算详情 */
export function getCarbonEmission(id: number) {
  return requestClient.get<MesSetCarbonEmissionApi.CarbonEmission>(
    `%s/get?id=${id}`,
  );
}

/** 新增碳排放核算 */
export function createCarbonEmission(data: MesSetCarbonEmissionApi.CarbonEmission) {
  return requestClient.post<number>('/mes/safety-env/carbon-emission/create', data);
}

/** 修改碳排放核算 */
export function updateCarbonEmission(data: MesSetCarbonEmissionApi.CarbonEmission) {
  return requestClient.put('/mes/safety-env/carbon-emission/update', data);
}

/** 删除碳排放核算 */
export function deleteCarbonEmission(id: number) {
  return requestClient.delete(`/mes/safety-env/carbon-emission/delete?id=${id}`);
}