import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetExhaustGasApi {
  /** MES 安全环保检测-废气监测记录 */
  export interface ExhaustGas {
    id?: number; // 编号
    recordNo?: string; // 记录编号 EXGAS-YYYYMMDD-NNN
    outletId?: number; // 关联排放口编号
    woId?: number; // 关联工单编号
    pollutantCode?: string; // 污染物：SO2/NOX/PM/VOCs/HCL/HF等
    concentration?: number; // 排放浓度 mg/m3
    unit?: string; // 单位 mg/m3等
    flowRate?: number; // 标态干烟气流量 m3/h
    emissionAmount?: number; // 折算排放速率 kg/h
    limitValue?: number; // 限值
    result?: string; // 结果：PASS/FAIL
    collectionMode?: string; // 采集方式：CEMS_AUTO/MANUAL
    monitorTime?: string; // 监测时间
    instrumentNo?: string; // CEMS设备编号/采样仪器号
    inspector?: string; // 监测人(手工时)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询废气监测记录分页 */
export function getExhaustGasPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetExhaustGasApi.ExhaustGas>>(
    '/mes/safety-env/exhaust-gas/page',
    { params },
  );
}

/** 查询废气监测记录详情 */
export function getExhaustGas(id: number) {
  return requestClient.get<MesSetExhaustGasApi.ExhaustGas>(
    `/mes/safety-env/exhaust-gas/get?id=${id}`,
  );
}

/** 新增废气监测记录 */
export function createExhaustGas(data: MesSetExhaustGasApi.ExhaustGas) {
  return requestClient.post<number>('/mes/safety-env/exhaust-gas/create', data);
}

/** 修改废气监测记录 */
export function updateExhaustGas(data: MesSetExhaustGasApi.ExhaustGas) {
  return requestClient.put('/mes/safety-env/exhaust-gas/update', data);
}

/** 删除废气监测记录 */
export function deleteExhaustGas(id: number) {
  return requestClient.delete(`/mes/safety-env/exhaust-gas/delete?id=${id}`);
}
