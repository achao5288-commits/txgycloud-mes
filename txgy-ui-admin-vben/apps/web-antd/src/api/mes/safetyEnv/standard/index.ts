import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetStandardApi {
  /** MES 安全环保检测-检测标准 */
  export interface Standard {
    id?: number; // 编号
    standardNo?: string; // 标准编号
    standardName?: string; // 标准名称
    domain?: string; // 检测域：SAFETY/ENV/HEALTH
    testType?: string; // 检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等
    refStandard?: string; // 引用国标编号（GBZ/GB/T）
    limitsConfig?: string; // 限值配置(JSON文本，如 {CO:{mac,pcTWA,unit}})
    method?: string; // 检测方法描述
    periodType?: string; // 周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT
    triggerConfig?: string; // 事件触发配置(JSON文本)
    applicableArea?: string; // 适用区域/工序(JSON文本)
    status?: string; // 状态：DRAFT/ACTIVE/OBSOLETE
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询检测标准分页 */
export function getStandardPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetStandardApi.Standard>>(
    '/mes/safety-env/standard/page',
    { params },
  );
}

/** 查询检测标准详情 */
export function getStandard(id: number) {
  return requestClient.get<MesSetStandardApi.Standard>(
    `/mes/safety-env/standard/get?id=${id}`,
  );
}

/** 新增检测标准 */
export function createStandard(data: MesSetStandardApi.Standard) {
  return requestClient.post<number>('/mes/safety-env/standard/create', data);
}

/** 修改检测标准 */
export function updateStandard(data: MesSetStandardApi.Standard) {
  return requestClient.put('/mes/safety-env/standard/update', data);
}

/** 删除检测标准 */
export function deleteStandard(id: number) {
  return requestClient.delete(`/mes/safety-env/standard/delete?id=${id}`);
}
