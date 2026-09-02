import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPressureVesselApi {
  /** MES 安全环保检测-压力容器检测记录 */
  export interface PressureVessel {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    deviceId?: number; // 关联设备编号
    vesselRegNo?: string; // 压力容器使用登记证号
    wallThickness?: number; // 壁厚测定最小壁厚 mm
    ndtMethods?: string; // 无损检测方法 UT/RT/MT/PT
    ndtResults?: string; // 无损检测结果 JSON 文本
    safetyValveOk?: number; // 安全阀校验合格 1是/0否
    pressureTestValue?: number; // 耐压试验压力 MPa
    pressureTestResult?: string; // 耐压试验结果 PASS/FAIL
    processPressure?: number; // 工艺允许压力 MPa
    result?: string; // 综合结论 PASS/FAIL
    inspectOrg?: string; // 检验机构
    nextInspectDate?: string; // 下次检验日期
    reportFileUrl?: string; // 检验报告文件 URL
    inspector?: string; // 检验人
    inspectTime?: Date | number; // 检验时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询压力容器检测记录分页 */
export function getPressureVesselPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPressureVesselApi.PressureVessel>>(
    '/mes/safety-env/pressure-vessel/page',
    { params },
  );
}

/** 查询压力容器检测记录详情 */
export function getPressureVessel(id: number) {
  return requestClient.get<MesSetPressureVesselApi.PressureVessel>(
    `/mes/safety-env/pressure-vessel/get?id=${id}`,
  );
}

/** 新增压力容器检测记录 */
export function createPressureVessel(data: MesSetPressureVesselApi.PressureVessel) {
  return requestClient.post<number>('/mes/safety-env/pressure-vessel/create', data);
}

/** 修改压力容器检测记录 */
export function updatePressureVessel(data: MesSetPressureVesselApi.PressureVessel) {
  return requestClient.put('/mes/safety-env/pressure-vessel/update', data);
}

/** 删除压力容器检测记录 */
export function deletePressureVessel(id: number) {
  return requestClient.delete(`/mes/safety-env/pressure-vessel/delete?id=${id}`);
}
