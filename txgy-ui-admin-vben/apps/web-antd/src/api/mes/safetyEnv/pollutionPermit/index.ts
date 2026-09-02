import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPollutionPermitApi {
  /** MES 安全环保检测-排污许可管理 */
  export interface PollutionPermit {
    id?: number; // 编号
    permitNo?: string; // 排污许可证编号
    enterpriseName?: string; // 企业名称
    issuingAuthority?: string; // 发证机关
    issueDate?: string; // 发证日期
    startDate?: string; // 有效期开始日期
    endDate?: string; // 有效期截止日期
    outletCodes?: string; // 排污口编码
    annualLimits?: string; // 年度许可排放量
    annualReports?: string; // 年度执行报告记录
    status?: string; // 状态
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询排污许可管理分页 */
export function getPollutionPermitPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPollutionPermitApi.PollutionPermit>>(
    '/mes/safety-env/pollution-permit/page',
    { params },
  );
}

/** 查询排污许可管理详情 */
export function getPollutionPermit(id: number) {
  return requestClient.get<MesSetPollutionPermitApi.PollutionPermit>(
    `%s/get?id=${id}`,
  );
}

/** 新增排污许可管理 */
export function createPollutionPermit(data: MesSetPollutionPermitApi.PollutionPermit) {
  return requestClient.post<number>('/mes/safety-env/pollution-permit/create', data);
}

/** 修改排污许可管理 */
export function updatePollutionPermit(data: MesSetPollutionPermitApi.PollutionPermit) {
  return requestClient.put('/mes/safety-env/pollution-permit/update', data);
}

/** 删除排污许可管理 */
export function deletePollutionPermit(id: number) {
  return requestClient.delete(`/mes/safety-env/pollution-permit/delete?id=${id}`);
}