import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesSetEmergencyMaterialApi {
  /** MES 安全环保检测-应急物资 */
  export interface EmergencyMaterial {
    id?: number; // 编号
    materialNo?: string; // 物资编号
    materialName?: string; // 物资名称
    materialType?: string; // 物资类型：DRY_SAND(干沙)/OIL_ABSORBENT(吸油毡)/CHEM_SUIT(防化服)/GAS_MASK(防毒面具)/EXPLOSION_TOOL(防爆工具)/SANDBAG(围堰沙袋)/EYE_WASH(洗眼器)/DRY_POWDER(干粉灭火器)
    spec?: string; // 规格型号
    unit?: string; // 计量单位
    quantity?: number; // 在库数量
    storageLocation?: string; // 定点存放位置
    produceDate?: string; // 生产日期
    expireDate?: string; // 有效期至（临期预警依据）
    lastCheckDate?: string; // 最近检查日期
    status?: string; // 状态：NORMAL(正常)/EXPIRING(临期)/EXPIRED(过期)/OUT(缺货)，派生值不进表单
    remark?: string; // 备注
    createTime?: number; // 创建时间
  }
}

/** 查询应急物资分页 */
export function getEmergencyMaterialPage(params: PageParam) {
  return requestClient.get<PageResult<MesSetEmergencyMaterialApi.EmergencyMaterial>>(
    '/mes/safety-env/emergency-material/page',
    { params },
  );
}

/** 查询应急物资详情 */
export function getEmergencyMaterial(id: number) {
  return requestClient.get<MesSetEmergencyMaterialApi.EmergencyMaterial>(
    `/mes/safety-env/emergency-material/get?id=${id}`,
  );
}

/** 新增应急物资 */
export function createEmergencyMaterial(data: MesSetEmergencyMaterialApi.EmergencyMaterial) {
  return requestClient.post<number>('/mes/safety-env/emergency-material/create', data);
}

/** 修改应急物资 */
export function updateEmergencyMaterial(data: MesSetEmergencyMaterialApi.EmergencyMaterial) {
  return requestClient.put('/mes/safety-env/emergency-material/update', data);
}

/** 删除应急物资 */
export function deleteEmergencyMaterial(id: number) {
  return requestClient.delete(`/mes/safety-env/emergency-material/delete?id=${id}`);
}

/**
 * 物资预警清单：缺货 → 过期 → 临期（30 天内到期）排前面。
 * 状态由后端按数量与有效期重算，前端只负责显示。
 */
export function getEmergencyMaterialAlerts() {
  return requestClient.get<MesSetEmergencyMaterialApi.EmergencyMaterial[]>(
    '/mes/safety-env/emergency-material/alerts',
  );
}
