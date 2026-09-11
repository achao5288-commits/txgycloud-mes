import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetGasRecordApi {
  /** MES 安全环保检测-气体检测记录 */
  export interface GasRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    woId?: number; // 关联工单编号（事件触发型）
    operationId?: number; // 关联工序编号
    permitId?: number; // 关联作业许可编号
    location?: string; // 检测位置
    gasType?: string; // 气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2
    concentration?: number; // 检测浓度值
    unit?: string; // 单位：mg/m3 / % / %LEL
    limitValue?: number; // 限值
    result?: string; // 结果：PASS/FAIL
    collectionMode?: string; // 采集方式：IOT_AUTO/MANUAL
    instrumentNo?: string; // 检测仪器编号
    inspector?: string; // 检测人
    inspectTime?: string; // 检测时间
    photoUrls?: string; // 检测照片URL(逗号分隔)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询气体检测记录分页 */
export function getGasRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetGasRecordApi.GasRecord>>(
    '/mes/safety-env/gas-record/page',
    { params },
  );
}

/** 查询气体检测记录详情 */
export function getGasRecord(id: number) {
  return requestClient.get<MesSetGasRecordApi.GasRecord>(
    `/mes/safety-env/gas-record/get?id=${id}`,
  );
}

/** 新增气体检测记录 */
export function createGasRecord(data: MesSetGasRecordApi.GasRecord) {
  return requestClient.post<number>('/mes/safety-env/gas-record/create', data);
}

/** 修改气体检测记录 */
export function updateGasRecord(data: MesSetGasRecordApi.GasRecord) {
  return requestClient.put('/mes/safety-env/gas-record/update', data);
}

/** 删除气体检测记录 */
export function deleteGasRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/gas-record/delete?id=${id}`);
}
