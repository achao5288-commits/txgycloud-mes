import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEnvReportApi {
  /** MES 安全环保检测-环保检测报告 */
  export interface EnvReport {
    id?: number; // 编号
    reportNo?: string; // 报告编号
    reportName?: string; // 报告名称
    reportType?: string; // 报告类型
    reportCategory?: string; // 报告类别
    templateId?: number; // 关联报告模板编号
    periodStart?: string; // 报告周期开始日期
    periodEnd?: string; // 报告周期结束日期
    reportDate?: string; // 报告生成日期
    dataSummary?: string; // 数据汇总摘要
    fileUrl?: string; // 报告文件 URL
    signUrl?: string; // 签章文件 URL
    formId?: number; // 关联表单编号
    status?: string; // 状态
    auditBy?: string; // 审核人
    auditTime?: Date | number; // 审核时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询环保检测报告分页 */
export function getEnvReportPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEnvReportApi.EnvReport>>(
    '/mes/safety-env/env-report/page',
    { params },
  );
}

/** 查询环保检测报告详情 */
export function getEnvReport(id: number) {
  return requestClient.get<MesSetEnvReportApi.EnvReport>(
    `%s/get?id=${id}`,
  );
}

/** 新增环保检测报告 */
export function createEnvReport(data: MesSetEnvReportApi.EnvReport) {
  return requestClient.post<number>('/mes/safety-env/env-report/create', data);
}

/** 修改环保检测报告 */
export function updateEnvReport(data: MesSetEnvReportApi.EnvReport) {
  return requestClient.put('/mes/safety-env/env-report/update', data);
}

/** 删除环保检测报告 */
export function deleteEnvReport(id: number) {
  return requestClient.delete(`/mes/safety-env/env-report/delete?id=${id}`);
}