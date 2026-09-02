import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetChemicalSafetyApi {
  /** MES 安全环保检测-危化品安全管理 */
  export interface ChemicalSafety {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    chemicalCode?: string; // 危化品代码
    chemicalName?: string; // 危化品名称
    storageLocation?: string; // 储存地点
    labelOk?: number; // 标识标签齐全
    msdsOk?: number; // MSDS
    storageOk?: number; // 储存条件符合要求
    separationOk?: number; // 分类存放/隔离存放合规
    result?: string; // 结果
    problemDesc?: string; // 异常/不合格描述
    inspector?: string; // 巡查人
    inspectTime?: Date | number; // 巡查时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询危化品安全管理分页 */
export function getChemicalSafetyPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetChemicalSafetyApi.ChemicalSafety>>(
    '/mes/safety-env/chemical-safety/page',
    { params },
  );
}

/** 查询危化品安全管理详情 */
export function getChemicalSafety(id: number) {
  return requestClient.get<MesSetChemicalSafetyApi.ChemicalSafety>(
    `%s/get?id=${id}`,
  );
}

/** 新增危化品安全管理 */
export function createChemicalSafety(data: MesSetChemicalSafetyApi.ChemicalSafety) {
  return requestClient.post<number>('/mes/safety-env/chemical-safety/create', data);
}

/** 修改危化品安全管理 */
export function updateChemicalSafety(data: MesSetChemicalSafetyApi.ChemicalSafety) {
  return requestClient.put('/mes/safety-env/chemical-safety/update', data);
}

/** 删除危化品安全管理 */
export function deleteChemicalSafety(id: number) {
  return requestClient.delete(`/mes/safety-env/chemical-safety/delete?id=${id}`);
}