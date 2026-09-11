import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetDustRecordApi {
  /** MES 安全环保检测-粉尘检测记录 */
  export interface DustRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号 DUST-YYYYMMDD-NNN
    planId?: number; // 关联检测计划编号
    woId?: number; // 关联工单编号
    operationId?: number; // 关联工序编号
    deviceId?: number; // 关联设备编号(除尘/产尘设备)
    location?: string; // 检测位置/作业区域
    dustType?: string; // 检测参数：TOTAL_DUST/RESPIRABLE_DUST/SIO2
    concentration?: number; // 检测浓度/含量
    sio2Content?: number; // 游离SiO2含量%(总尘且需矽尘分级时)
    unit?: string; // 单位 mg/m3/%
    limitValue?: number; // 限值
    refStandard?: string; // 引用国标
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

/** 查询粉尘检测记录分页 */
export function getDustRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetDustRecordApi.DustRecord>>(
    '/mes/safety-env/dust-record/page',
    { params },
  );
}

/** 查询粉尘检测记录详情 */
export function getDustRecord(id: number) {
  return requestClient.get<MesSetDustRecordApi.DustRecord>(
    `/mes/safety-env/dust-record/get?id=${id}`,
  );
}

/** 新增粉尘检测记录 */
export function createDustRecord(data: MesSetDustRecordApi.DustRecord) {
  return requestClient.post<number>('/mes/safety-env/dust-record/create', data);
}

/** 修改粉尘检测记录 */
export function updateDustRecord(data: MesSetDustRecordApi.DustRecord) {
  return requestClient.put('/mes/safety-env/dust-record/update', data);
}

/** 删除粉尘检测记录 */
export function deleteDustRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/dust-record/delete?id=${id}`);
}
