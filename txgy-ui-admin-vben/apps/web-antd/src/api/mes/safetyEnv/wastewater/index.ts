import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetWastewaterApi {
  /** MES 安全环保检测-废水监测记录 */
  export interface Wastewater {
    id?: number; // 编号
    recordNo?: string; // 记录编号 WW-YYYYMMDD-NNN
    outletId?: number; // 关联排放口编号
    woId?: number; // 关联工单编号
    sampleNo?: string; // 实验室样品编号(手工检测时)
    pollutantCode?: string; // 污染物：PH/COD/BOD5/NH3_N/TP/PETROLEUM/SS/HEAVY_METAL等
    concentration?: number; // 检测浓度 mg/L(pH无量纲)
    unit?: string; // 单位 mg/L(pH留空)
    flowRate?: number; // 排放流量 m3/h
    emissionAmount?: number; // 排放量 kg/h
    limitValue?: number; // 限值
    result?: string; // 结果：PASS/FAIL
    collectionMode?: string; // 采集方式：ONLINE_AUTO/LAB_MANUAL
    monitorTime?: string; // 监测时间
    instrumentNo?: string; // 在线监测仪/化验设备编号
    inspector?: string; // 化验员(手工时)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询废水监测记录分页 */
export function getWastewaterPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetWastewaterApi.Wastewater>>(
    '/mes/safety-env/wastewater/page',
    { params },
  );
}

/** 查询废水监测记录详情 */
export function getWastewater(id: number) {
  return requestClient.get<MesSetWastewaterApi.Wastewater>(
    `/mes/safety-env/wastewater/get?id=${id}`,
  );
}

/** 新增废水监测记录 */
export function createWastewater(data: MesSetWastewaterApi.Wastewater) {
  return requestClient.post<number>('/mes/safety-env/wastewater/create', data);
}

/** 修改废水监测记录 */
export function updateWastewater(data: MesSetWastewaterApi.Wastewater) {
  return requestClient.put('/mes/safety-env/wastewater/update', data);
}

/** 删除废水监测记录 */
export function deleteWastewater(id: number) {
  return requestClient.delete(`/mes/safety-env/wastewater/delete?id=${id}`);
}
