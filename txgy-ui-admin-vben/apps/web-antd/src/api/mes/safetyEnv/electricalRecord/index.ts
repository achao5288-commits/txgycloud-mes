import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetElectricalRecordApi {
  /** MES 安全环保检测-电气安全检查 */
  export interface ElectricalRecord {
    id?: number; // 编号
    recordNo?: string; // 记录编号 ELEC-YYYYMMDD-NNN
    planId?: number; // 关联检测计划编号
    deviceId?: number; // 关联设备/配电设施编号
    location?: string; // 检测位置(配电柜/线路区域)
    checkItem?: string; // 检测项目：INSULATION_RESISTANCE/GROUND_RESISTANCE/LEAKAGE_ACTION_CURRENT/LEAKAGE_ACTION_TIME/WITHSTAND_VOLTAGE
    measuredValue?: number; // 实测值
    unit?: string; // 单位 MΩ/Ω/mA/ms/V
    limitValue?: number; // 标准限值
    result?: string; // 结果：PASS/FAIL
    instrumentNo?: string; // 检测仪器编号
    instrumentCalibOk?: boolean; // 仪器校准状态快照：1已校准/0未校准
    inspector?: string; // 检测人
    inspectTime?: string; // 检测时间
    photoUrls?: string; // 检测照片URL(逗号分隔)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询电气安全检查分页 */
export function getElectricalRecordPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetElectricalRecordApi.ElectricalRecord>>(
    '/mes/safety-env/electrical-record/page',
    { params },
  );
}

/** 查询电气安全检查详情 */
export function getElectricalRecord(id: number) {
  return requestClient.get<MesSetElectricalRecordApi.ElectricalRecord>(
    `/mes/safety-env/electrical-record/get?id=${id}`,
  );
}

/** 新增电气安全检查 */
export function createElectricalRecord(data: MesSetElectricalRecordApi.ElectricalRecord) {
  return requestClient.post<number>('/mes/safety-env/electrical-record/create', data);
}

/** 修改电气安全检查 */
export function updateElectricalRecord(data: MesSetElectricalRecordApi.ElectricalRecord) {
  return requestClient.put('/mes/safety-env/electrical-record/update', data);
}

/** 删除电气安全检查 */
export function deleteElectricalRecord(id: number) {
  return requestClient.delete(`/mes/safety-env/electrical-record/delete?id=${id}`);
}
