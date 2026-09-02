import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPlanApi {
  /** MES 安全环保检测-检测计划管理 */
  export interface Plan {
    id?: number; // 编号
    planNo?: string; // 计划编号
    planName?: string; // 计划名称
    planType?: string; // 触发类型
    periodType?: string; // 周期类型(周期型)
    startDate?: string; // 生效开始日期
    endDate?: string; // 生效结束日期
    machineryId?: number; // 关联设备编号
    operationId?: number; // 关联工序编号
    standardId?: number; // 关联检测标准编号
    assigneeId?: number; // 责任人(执行人)编号
    status?: string; // 状态
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询检测计划管理分页 */
export function getPlanPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPlanApi.Plan>>(
    '/mes/safety-env/plan/page',
    { params },
  );
}

/** 查询检测计划管理详情 */
export function getPlan(id: number) {
  return requestClient.get<MesSetPlanApi.Plan>(
    `%s/get?id=${id}`,
  );
}

/** 新增检测计划管理 */
export function createPlan(data: MesSetPlanApi.Plan) {
  return requestClient.post<number>('/mes/safety-env/plan/create', data);
}

/** 修改检测计划管理 */
export function updatePlan(data: MesSetPlanApi.Plan) {
  return requestClient.put('/mes/safety-env/plan/update', data);
}

/** 删除检测计划管理 */
export function deletePlan(id: number) {
  return requestClient.delete(`/mes/safety-env/plan/delete?id=${id}`);
}