import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetNoiseRecordApi {
  /** MES 安全环保检测-噪声检测记录 */
  export interface NoiseRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号 NOISE-YYYYMMDD-NNN
    planId?: number; // 关联检测计划编号
    woId?: number; // 关联工单编号
    operationId?: number; // 关联工序编号
    deviceId?: number; // 关联设备编号
    empId?: number; // 关联人员编号(个体剂量计佩戴人)
    sourceType?: string; // 监测类型：STATIONARY(固定式声级计)/PERSONAL(个体剂量计)
    location?: string; // 检测位置/区域
    collectionMode?: string; // 采集方式：IOT_AUTO/MANUAL
    lex8h?: number; // 8小时等效声级 dB(A)
    lpeak?: number; // 峰值声级 dB(C)
    limitLex8h?: number; // 限值 dB(A)≤85
    limitLpeak?: number; // 限值 dB(C)≤140
    spectrum?: string; // 频谱分析(倍频程，JSON文本)
    standardId?: number; // 关联检测标准编号
    result?: string; // 结果：PASS/FAIL
    instrumentNo?: string; // 检测仪器编号
    inspector?: string; // 检测人
    inspectTime?: string; // 检测时间
    photoUrls?: string; // 检测照片URL(逗号分隔)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询噪声检测记录分页 */
export function getNoiseRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetNoiseRecordApi.NoiseRecord>>(
    '/mes/safety-env/noise-record/page',
    { params },
  );
}

/** 查询噪声检测记录详情 */
export function getNoiseRecord(id: number) {
  return requestClient.get<MesSetNoiseRecordApi.NoiseRecord>(
    `/mes/safety-env/noise-record/get?id=${id}`,
  );
}

/** 新增噪声检测记录 */
export function createNoiseRecord(data: MesSetNoiseRecordApi.NoiseRecord) {
  return requestClient.post<number>('/mes/safety-env/noise-record/create', data);
}

/** 修改噪声检测记录 */
export function updateNoiseRecord(data: MesSetNoiseRecordApi.NoiseRecord) {
  return requestClient.put('/mes/safety-env/noise-record/update', data);
}

/** 删除噪声检测记录 */
export function deleteNoiseRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/noise-record/delete?id=${id}`);
}
