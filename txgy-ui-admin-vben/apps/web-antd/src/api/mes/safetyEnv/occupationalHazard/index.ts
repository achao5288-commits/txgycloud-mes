import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetOccupationalHazardApi {
  /** MES 安全环保检测-职业危害检测 */
  export interface OccupationalHazard {
    id?: number; // 编号
    recordNo?: string; // 记录编号 OH-YYYYMMDD-NNN
    planId?: number; // 关联检测计划编号
    empId?: number; // 关联人员编号(个体暴露监测时；岗位检测可空)
    factorCategory?: string; // 因素类别：CHEMICAL/PHYSICAL/BIOLOGICAL
    factorCode?: string; // 具体因素：TOXIC/DUST/NOISE/RADIATION/HEAT/VIBRATION/BIOAGENT
    workplace?: string; // 检测岗位/工作场所
    sourceRefType?: string; // 数据来源：REUSE/ORIGINAL
    sourceRecordId?: number; // 来源安全检测记录id(复用气体/噪声/粉尘)
    measuredValue?: number; // 实测浓度/强度
    unit?: string; // 单位 mg/m3/dB(A)/mSv/C/m-s2等
    limitType?: string; // 接触限值类型：MAC/PC-TWA/PC-STEL
    oelValue?: number; // 职业接触限值
    refStandard?: string; // 引用国标
    result?: string; // 结果：PASS/FAIL
    inspector?: string; // 检测人
    inspectTime?: string; // 检测时间
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询职业危害检测分页 */
export function getOccupationalHazardPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetOccupationalHazardApi.OccupationalHazard>>(
    '/mes/safety-env/occupational-hazard/page',
    { params },
  );
}

/** 查询职业危害检测详情 */
export function getOccupationalHazard(id: number) {
  return requestClient.get<MesSetOccupationalHazardApi.OccupationalHazard>(
    `/mes/safety-env/occupational-hazard/get?id=${id}`,
  );
}

/** 新增职业危害检测 */
export function createOccupationalHazard(data: MesSetOccupationalHazardApi.OccupationalHazard) {
  return requestClient.post<number>('/mes/safety-env/occupational-hazard/create', data);
}

/** 修改职业危害检测 */
export function updateOccupationalHazard(data: MesSetOccupationalHazardApi.OccupationalHazard) {
  return requestClient.put('/mes/safety-env/occupational-hazard/update', data);
}

/** 删除职业危害检测 */
export function deleteOccupationalHazard(id: number) {
  return requestClient.delete(`/mes/safety-env/occupational-hazard/delete?id=${id}`);
}
