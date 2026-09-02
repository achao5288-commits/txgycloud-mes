import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetElectricalRecordApi {
  /** MES 安全环保检测-电气安全检测 */
  export interface ElectricalRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    deviceId?: number; // 关联设备编号
    location?: string; // 检测位置
    checkItem?: string; // 检测项目
    measuredValue?: number; // 测量值
    unit?: string; // 单位
    limitValue?: number; // 限值
    result?: string; // 结果
    instrumentNo?: string; // 检测仪器编号
    instrumentCalibOk?: number; // 仪器校准状态
    inspector?: string; // 检测人
    inspectTime?: Date | number; // 检测时间
    photoUrls?: string; // 检测照片 URL
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询电气安全检测分页 */
export function getElectricalRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetElectricalRecordApi.ElectricalRecord>>(
    '/mes/safety-env/electrical-record/page',
    { params },
  );
}

/** 查询电气安全检测详情 */
export function getElectricalRecord(id: number) {
  return requestClient.get<MesSetElectricalRecordApi.ElectricalRecord>(
    `%s/get?id=${id}`,
  );
}

/** 新增电气安全检测 */
export function createElectricalRecord(data: MesSetElectricalRecordApi.ElectricalRecord) {
  return requestClient.post<number>('/mes/safety-env/electrical-record/create', data);
}

/** 修改电气安全检测 */
export function updateElectricalRecord(data: MesSetElectricalRecordApi.ElectricalRecord) {
  return requestClient.put('/mes/safety-env/electrical-record/update', data);
}

/** 删除电气安全检测 */
export function deleteElectricalRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/electrical-record/delete?id=${id}`);
}