import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetDustRecordApi {
  /** MES 安全环保检测-粉尘浓度检测 */
  export interface DustRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    woId?: number; // 关联工单编号
    operationId?: number; // 关联工序编号
    deviceId?: number; // 关联设备编号
    location?: string; // 检测位置
    dustType?: string; // 粉尘类型
    concentration?: number; // 粉尘浓度值
    sio2Content?: number; // 游离二氧化硅含量(%)
    unit?: string; // 单位
    limitValue?: number; // 限值(mg/m3)
    refStandard?: string; // 执行标准
    result?: string; // 结果
    collectionMode?: string; // 采集方式
    instrumentNo?: string; // 检测仪器编号
    inspector?: string; // 检测人
    inspectTime?: Date | number; // 检测时间
    photoUrls?: string; // 检测照片 URL
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询粉尘浓度检测分页 */
export function getDustRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetDustRecordApi.DustRecord>>(
    '/mes/safety-env/dust-record/page',
    { params },
  );
}

/** 查询粉尘浓度检测详情 */
export function getDustRecord(id: number) {
  return requestClient.get<MesSetDustRecordApi.DustRecord>(
    `%s/get?id=${id}`,
  );
}

/** 新增粉尘浓度检测 */
export function createDustRecord(data: MesSetDustRecordApi.DustRecord) {
  return requestClient.post<number>('/mes/safety-env/dust-record/create', data);
}

/** 修改粉尘浓度检测 */
export function updateDustRecord(data: MesSetDustRecordApi.DustRecord) {
  return requestClient.put('/mes/safety-env/dust-record/update', data);
}

/** 删除粉尘浓度检测 */
export function deleteDustRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/dust-record/delete?id=${id}`);
}