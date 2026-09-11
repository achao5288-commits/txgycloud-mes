import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetPpeCheckApi {
  /** MES 安全环保检测-劳保用品检查 */
  export interface PpeCheck {
    id?: number; // 编号
    recordNo?: string; // 记录编号 PPE-YYYYMMDD-NNN
    empId?: number; // 关联人员编号(佩戴校验对象)
    woId?: number; // 关联工单编号
    operationId?: number; // 关联工序编号
    ppeType?: string; // PPE类别：HELMET/GOGGLES/RESPIRATOR/ANTISTATIC_CLOTHING/EARPLUGS/GLOVES/SAFETY_SHOES等
    checkMode?: string; // 检查方式：AI_VISION/MANUAL
    wearingOk?: boolean; // 佩戴完整性：1是/0否
    gradeMatchOk?: boolean; // 防护等级匹配性：1是/0否
    validOk?: boolean; // 有效期/损坏检查：1是/0否
    expiryDate?: string; // PPE到期日期
    result?: string; // 结果：PASS/FAIL
    blockFlag?: boolean; // 是否阻断开工(FAIL时=1)
    deviceNo?: string; // AI识别设备编号
    checkerName?: string; // 检查人(人工检查时)
    checkTime?: string; // 检查时间
    photoUrls?: string; // 检查照片/AI抓拍URL(逗号分隔)
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询劳保用品检查分页 */
export function getPpeCheckPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetPpeCheckApi.PpeCheck>>(
    '/mes/safety-env/ppe-check/page',
    { params },
  );
}

/** 查询劳保用品检查详情 */
export function getPpeCheck(id: number) {
  return requestClient.get<MesSetPpeCheckApi.PpeCheck>(
    `/mes/safety-env/ppe-check/get?id=${id}`,
  );
}

/** 新增劳保用品检查 */
export function createPpeCheck(data: MesSetPpeCheckApi.PpeCheck) {
  return requestClient.post<number>('/mes/safety-env/ppe-check/create', data);
}

/** 修改劳保用品检查 */
export function updatePpeCheck(data: MesSetPpeCheckApi.PpeCheck) {
  return requestClient.put('/mes/safety-env/ppe-check/update', data);
}

/** 删除劳保用品检查 */
export function deletePpeCheck(id: number) {
  return requestClient.delete(`/mes/safety-env/ppe-check/delete?id=${id}`);
}
