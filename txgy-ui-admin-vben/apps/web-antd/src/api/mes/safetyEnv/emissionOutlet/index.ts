import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEmissionOutletApi {
  /** MES 安全环保检测-排放口管理 */
  export interface EmissionOutlet {
    id?: number; // 编号
    outletCode?: string; // 排放口编号
    outletName?: string; // 排放口名称
    outletType?: string; // 排放类型
    pollutantCodes?: string; // 主要污染物列表(JSON/CSV文本)
    location?: string; // 位置描述
    longitude?: number; // 经度
    latitude?: number; // 纬度
    stackHeight?: number; // 排气筒高度 m
    monitorMethod?: string; // 在线监测方式
    permitNo?: string; // 关联排污许可证号
    permitLimits?: string; // 许可排放限值JSON文本
    isKeyOutlet?: number; // 是否重点/国控排放口
    status?: string; // 状态
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询排放口管理分页 */
export function getEmissionOutletPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEmissionOutletApi.EmissionOutlet>>(
    '/mes/safety-env/emission-outlet/page',
    { params },
  );
}

/** 查询排放口管理详情 */
export function getEmissionOutlet(id: number) {
  return requestClient.get<MesSetEmissionOutletApi.EmissionOutlet>(
    `%s/get?id=${id}`,
  );
}

/** 新增排放口管理 */
export function createEmissionOutlet(data: MesSetEmissionOutletApi.EmissionOutlet) {
  return requestClient.post<number>('/mes/safety-env/emission-outlet/create', data);
}

/** 修改排放口管理 */
export function updateEmissionOutlet(data: MesSetEmissionOutletApi.EmissionOutlet) {
  return requestClient.put('/mes/safety-env/emission-outlet/update', data);
}

/** 删除排放口管理 */
export function deleteEmissionOutlet(id: number) {
  return requestClient.delete(`/mes/safety-env/emission-outlet/delete?id=${id}`);
}