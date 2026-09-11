import { requestClient } from '#/api/request';

export namespace MesSetEmergencySopApi {
  /** 泄漏应急处置卡（PDA 扫码即看） */
  export interface SopCard {
    scenario?: string; // 场景代码
    title?: string; // 场景/化学品名称
    source?: string; // PRESET / CHEMICAL_PROFILE / PRESET+CHEMICAL_PROFILE
    chemicalCode?: string;
    chemicalName?: string;
    msdsUrl?: string;
    storageZone?: string;
    incompatibleGroups?: string; // 禁配物
    ppe?: string[]; // 个体防护，按穿戴顺序
    steps?: string[]; // 处置步骤，按执行顺序
    forbidden?: string[]; // 红线动作
    wasteCode?: string; // 产废类别
    wasteName?: string;
    emergencyMeasure?: string; // 档案自带的应急处置文本
  }

  /** 场景清单项：[代码, 名称] */
  export type ScenarioOption = [string, string];
}

/**
 * 取处置卡。`scenario` 与 `chemicalCode` 至少给一个：
 * 只给 scenario = 内置三场景处置卡；只给 chemicalCode = 该化学品的 MSDS 与应急处置措施；
 * 两个都给 = 预设步骤 + 该化学品的身份信息（PDA 扫黑料桶就是这个用法）。
 */
export function getEmergencySopCard(params: {
  scenario?: string;
  chemicalCode?: string;
}) {
  return requestClient.get<MesSetEmergencySopApi.SopCard>(
    '/mes/safety-env/emergency-sop/card',
    { params },
  );
}

/** 内置泄漏场景清单 */
export function getEmergencySopScenarios() {
  return requestClient.get<MesSetEmergencySopApi.ScenarioOption[]>(
    '/mes/safety-env/emergency-sop/scenarios',
  );
}
