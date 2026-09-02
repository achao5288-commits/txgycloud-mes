import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPpeCheckApi {
  /** MES 安全环保检测-PPE防护检查 */
  export interface PpeCheck {
    id?: number; // 编号
    recordNo?: string; // 记录编号
    empId?: number; // 关联人员编号
    woId?: number; // 关联工单编号
    operationId?: number; // 关联工序编号
    ppeType?: string; // PPE类别
    checkMode?: string; // 检查方式
    wearingOk?: number; // 佩戴完整性
    gradeMatchOk?: number; // 防护等级匹配性
    validOk?: number; // 有效期/损坏检查
    expiryDate?: string; // PPE到期日期
    result?: string; // 结果
    blockFlag?: number; // 是否阻断开工
    deviceNo?: string; // AI识别设备编号
    checkerName?: string; // 检查人(人工检查时)
    checkTime?: Date | number; // 检查时间
    photoUrls?: string; // 检查照片/AI抓拍URL
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询PPE防护检查分页 */
export function getPpeCheckPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPpeCheckApi.PpeCheck>>(
    '/mes/safety-env/ppe-check/page',
    { params },
  );
}

/** 查询PPE防护检查详情 */
export function getPpeCheck(id: number) {
  return requestClient.get<MesSetPpeCheckApi.PpeCheck>(
    `%s/get?id=${id}`,
  );
}

/** 新增PPE防护检查 */
export function createPpeCheck(data: MesSetPpeCheckApi.PpeCheck) {
  return requestClient.post<number>('/mes/safety-env/ppe-check/create', data);
}

/** 修改PPE防护检查 */
export function updatePpeCheck(data: MesSetPpeCheckApi.PpeCheck) {
  return requestClient.put('/mes/safety-env/ppe-check/update', data);
}

/** 删除PPE防护检查 */
export function deletePpeCheck(id: number) {
  return requestClient.delete(`/mes/safety-env/ppe-check/delete?id=${id}`);
}