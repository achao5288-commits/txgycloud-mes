import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetStandardApi {
  /** MES 安全环保检测-检测标准管理 */
  export interface Standard {
    id?: number; // 编号
    standardNo?: string; // 标准编号
    standardName?: string; // 标准名称
    domain?: string; // 检测域
    testType?: string; // 检测类型
    refStandard?: string; // 引用国标编号
    limitsConfig?: string; // 限值配置(JSON 文本)
    method?: string; // 检测方法描述
    periodType?: string; // 周期类型
    triggerConfig?: string; // 事件触发配置(JSON 文本)
    applicableArea?: string; // 适用区域/工序(JSON 文本)
    status?: string; // 状态
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询检测标准管理分页 */
export function getStandardPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetStandardApi.Standard>>(
    '/mes/safety-env/standard/page',
    { params },
  );
}

/** 查询检测标准管理详情 */
export function getStandard(id: number) {
  return requestClient.get<MesSetStandardApi.Standard>(
    `%s/get?id=${id}`,
  );
}

/** 新增检测标准管理 */
export function createStandard(data: MesSetStandardApi.Standard) {
  return requestClient.post<number>('/mes/safety-env/standard/create', data);
}

/** 修改检测标准管理 */
export function updateStandard(data: MesSetStandardApi.Standard) {
  return requestClient.put('/mes/safety-env/standard/update', data);
}

/** 删除检测标准管理 */
export function deleteStandard(id: number) {
  return requestClient.delete(`/mes/safety-env/standard/delete?id=${id}`);
}