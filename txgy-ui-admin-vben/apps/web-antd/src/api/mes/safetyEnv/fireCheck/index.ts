import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetFireCheckApi {
  /** MES 安全环保检测-消防检查记录 */
  export interface FireCheck {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    planId?: number; // 关联检测计划编号
    location?: string; // 区域/位置
    facilityName?: string; // 设施名称(灭火器/消火栓/烟感/温感/应急照明/疏散指示等)
    facilityCode?: string; // 设施编号(资产编号)
    checkTime?: string; // 检测时间
    result?: string; // 结果：PASS/FAIL
    problemDesc?: string; // 异常/不合格描述
    inspector?: string; // 检测人
    photoUrls?: string; // 检测照片URL(逗号分隔)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询消防检查记录分页 */
export function getFireCheckPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetFireCheckApi.FireCheck>>(
    '/mes/safety-env/fire-check/page',
    { params },
  );
}

/** 查询消防检查记录详情 */
export function getFireCheck(id: number) {
  return requestClient.get<MesSetFireCheckApi.FireCheck>(
    `/mes/safety-env/fire-check/get?id=${id}`,
  );
}

/** 新增消防检查记录 */
export function createFireCheck(data: MesSetFireCheckApi.FireCheck) {
  return requestClient.post<number>('/mes/safety-env/fire-check/create', data);
}

/** 修改消防检查记录 */
export function updateFireCheck(data: MesSetFireCheckApi.FireCheck) {
  return requestClient.put('/mes/safety-env/fire-check/update', data);
}

/** 删除消防检查记录 */
export function deleteFireCheck(id: number) {
  return requestClient.delete(`/mes/safety-env/fire-check/delete?id=${id}`);
}
