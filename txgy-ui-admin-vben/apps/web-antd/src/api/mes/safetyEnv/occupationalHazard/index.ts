import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetOccupationalHazardApi {
  /** MES 安全环保检测-职业病危害检测 */
  export interface OccupationalHazard {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    empId?: number; // 关联员工编号
    factorCategory?: string; // 危害因素类别
    factorCode?: string; // 危害因素编码
    workplace?: string; // 检测岗位/工作场所
    sourceRefType?: string; // 来源关联类型
    sourceRecordId?: number; // 来源记录编号
    measuredValue?: number; // 测量值
    unit?: string; // 单位
    limitType?: string; // 限值类型
    oelValue?: number; // 职业接触限值(OEL)
    refStandard?: string; // 参考标准
    result?: string; // 结果
    inspector?: string; // 检测人
    inspectTime?: Date | number; // 检测时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询职业病危害检测分页 */
export function getOccupationalHazardPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetOccupationalHazardApi.OccupationalHazard>>(
    '/mes/safety-env/occupational-hazard/page',
    { params },
  );
}

/** 查询职业病危害检测详情 */
export function getOccupationalHazard(id: number) {
  return requestClient.get<MesSetOccupationalHazardApi.OccupationalHazard>(
    `%s/get?id=${id}`,
  );
}

/** 新增职业病危害检测 */
export function createOccupationalHazard(data: MesSetOccupationalHazardApi.OccupationalHazard) {
  return requestClient.post<number>('/mes/safety-env/occupational-hazard/create', data);
}

/** 修改职业病危害检测 */
export function updateOccupationalHazard(data: MesSetOccupationalHazardApi.OccupationalHazard) {
  return requestClient.put('/mes/safety-env/occupational-hazard/update', data);
}

/** 删除职业病危害检测 */
export function deleteOccupationalHazard(id: number) {
  return requestClient.delete(`/mes/safety-env/occupational-hazard/delete?id=${id}`);
}