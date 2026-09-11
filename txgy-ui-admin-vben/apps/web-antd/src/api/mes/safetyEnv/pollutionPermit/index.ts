import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPollutionPermitApi {
  /** MES 安全环保检测-排污许可证 */
  export interface Permit {
    id?: number; // 编号
    permitNo?: string; // 排污许可证编号
    enterpriseName?: string; // 持证单位名称
    issuingAuthority?: string; // 发证机关
    issueDate?: string; // 发证日期
    startDate?: string; // 许可有效期起始
    endDate?: string; // 许可有效期止
    outletCodes?: string; // 绑定的排放口编号(逗号分隔)
    annualLimits?: string; // 许可年排放总量 JSON
    annualReports?: string; // 执行报告配置 JSON
    status?: string; // 状态 ACTIVE/EXPIRED/REVOKED
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询排污许可证分页 */
export function getPollutionPermitPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPollutionPermitApi.Permit>>(
    '/mes/safety-env/pollution-permit/page',
    { params },
  );
}

/** 查询排污许可证详情 */
export function getPollutionPermit(id: number) {
  return requestClient.get<MesSetPollutionPermitApi.Permit>(
    `/mes/safety-env/pollution-permit/get?id=${id}`,
  );
}

/** 新增排污许可证 */
export function createPollutionPermit(data: MesSetPollutionPermitApi.Permit) {
  return requestClient.post<number>('/mes/safety-env/pollution-permit/create', data);
}

/** 修改排污许可证 */
export function updatePollutionPermit(data: MesSetPollutionPermitApi.Permit) {
  return requestClient.put('/mes/safety-env/pollution-permit/update', data);
}

/** 删除排污许可证 */
export function deletePollutionPermit(id: number) {
  return requestClient.delete(`/mes/safety-env/pollution-permit/delete?id=${id}`);
}
