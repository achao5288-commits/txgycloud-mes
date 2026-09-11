import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetChemicalSafetyApi {
  /** MES 安全环保检测-危化品安全检查 */
  export interface ChemicalSafety {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    chemicalCode?: string; // 危化品编码
    chemicalName?: string; // 危化品名称
    storageLocation?: string; // 存储地点
    labelOk?: boolean; // 标识完整性：1是/0否
    msdsOk?: boolean; // MSDS有效性：1是/0否
    storageOk?: boolean; // 储存条件(温湿度/通风)合格：1是/0否
    separationOk?: boolean; // 禁忌物分离合格：1是/0否
    result?: string; // 结果：PASS/FAIL
    problemDesc?: string; // 异常/不合格描述
    inspector?: string; // 检测人
    inspectTime?: string; // 检测时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询危化品安全检查分页 */
export function getChemicalSafetyPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetChemicalSafetyApi.ChemicalSafety>>(
    '/mes/safety-env/chemical-safety/page',
    { params },
  );
}

/** 查询危化品安全检查详情 */
export function getChemicalSafety(id: number) {
  return requestClient.get<MesSetChemicalSafetyApi.ChemicalSafety>(
    `/mes/safety-env/chemical-safety/get?id=${id}`,
  );
}

/** 新增危化品安全检查 */
export function createChemicalSafety(data: MesSetChemicalSafetyApi.ChemicalSafety) {
  return requestClient.post<number>('/mes/safety-env/chemical-safety/create', data);
}

/** 修改危化品安全检查 */
export function updateChemicalSafety(data: MesSetChemicalSafetyApi.ChemicalSafety) {
  return requestClient.put('/mes/safety-env/chemical-safety/update', data);
}

/** 删除危化品安全检查 */
export function deleteChemicalSafety(id: number) {
  return requestClient.delete(`/mes/safety-env/chemical-safety/delete?id=${id}`);
}
