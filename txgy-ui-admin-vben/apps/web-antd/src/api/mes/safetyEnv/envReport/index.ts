import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEnvReportApi {
  /** MES 安全环保检测-环保检测报告 */
  export interface EnvReport {
    id?: number; // 编号
    reportNo?: string; // 报告编号 EP-YYYYMMDD-NNN
    reportName?: string; // 报告名称
    reportType?: string; // 报告来源：THIRD_PARTY/INTERNAL
    reportCategory?: string; // 类别：EXHAUST_GAS/WASTEWATER/NOISE/SOLID_WASTE/AMBIENT/COMPREHENSIVE等
    templateId?: number; // 复用质检报告模板编号
    periodStart?: string; // 报告统计期起
    periodEnd?: string; // 报告统计期止
    reportDate?: string; // 报告日期
    dataSummary?: string; // 检测结果摘要JSON文本
    fileUrl?: string; // 报告文件URL(第三方导入PDF)
    signUrl?: string; // 电子签名文件URL
    formId?: number; // 自定义表单配置id
    status?: string; // 状态：DRAFT/APPROVED/REJECTED/ARCHIVED
    auditBy?: string; // 审核人(终审)
    auditTime?: string; // 审核时间
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
    `/mes/safety-env/env-report/get?id=${id}`,
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
