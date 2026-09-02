import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetHazardousWasteApi {
  /** MES 安全环保检测-危险废物管理 */
  export interface HazardousWaste {
    id?: number; // 编号
    manifestNo?: string; // 转移联单编号
    wasteCode?: string; // 危废代码
    wasteName?: string; // 危废名称
    quantity?: number; // 数量
    quantityUnit?: string; // 数量单位
    stage?: string; // 状态阶段
    storageLocation?: string; // 储存地点
    counterparty?: string; // 接收方/处置方
    woId?: number; // 关联工单编号
    handleTime?: Date | number; // 处理时间
    handler?: string; // 处理人
    status?: string; // 状态
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询危险废物管理分页 */
export function getHazardousWastePage(params: PageParam) {
  return requestClient.get<PageResult<MesSetHazardousWasteApi.HazardousWaste>>(
    '/mes/safety-env/hazardous-waste/page',
    { params },
  );
}

/** 查询危险废物管理详情 */
export function getHazardousWaste(id: number) {
  return requestClient.get<MesSetHazardousWasteApi.HazardousWaste>(
    `%s/get?id=${id}`,
  );
}

/** 新增危险废物管理 */
export function createHazardousWaste(data: MesSetHazardousWasteApi.HazardousWaste) {
  return requestClient.post<number>('/mes/safety-env/hazardous-waste/create', data);
}

/** 修改危险废物管理 */
export function updateHazardousWaste(data: MesSetHazardousWasteApi.HazardousWaste) {
  return requestClient.put('/mes/safety-env/hazardous-waste/update', data);
}

/** 删除危险废物管理 */
export function deleteHazardousWaste(id: number) {
  return requestClient.delete(`/mes/safety-env/hazardous-waste/delete?id=${id}`);
}