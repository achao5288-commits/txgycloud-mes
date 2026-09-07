/**
 * 11 类检测记录的类型编码 -> 路由名 / 菜单名映射。
 * 路由名 = system_menu.component_name（动态路由 name），经查库核对。
 */
export interface DetectTypeInfo {
  type: string; // 类型编码，与后端 recordStats.type 对应
  typeName: string; // 菜单名
  route: string; // 对应记录列表页路由 name
}

export const DETECT_RECORD_TYPES: DetectTypeInfo[] = [
  { type: 'gas', typeName: '气体检测记录', route: 'MesSetGasRecord' },
  { type: 'fire', typeName: '消防设施检测', route: 'MesSetFireCheck' },
  { type: 'chemical', typeName: '危化品安全管理', route: 'MesSetChemicalSafety' },
  { type: 'noise', typeName: '噪声检测记录', route: 'MesSetNoiseRecord' },
  { type: 'dust', typeName: '粉尘浓度检测', route: 'MesSetDustRecord' },
  { type: 'electrical', typeName: '电气安全检测', route: 'MesSetElectricalRecord' },
  { type: 'pressure', typeName: '压力容器检测', route: 'MesSetPressureVessel' },
  { type: 'ppe', typeName: 'PPE防护检查', route: 'MesSetPpeCheck' },
  { type: 'occupational', typeName: '职业病危害检测', route: 'MesSetOccupationalHazard' },
  { type: 'exhaust', typeName: '废气排放检测', route: 'MesSetExhaustGas' },
  { type: 'wastewater', typeName: '废水排放检测', route: 'MesSetWastewater' },
];

/** 台账/计划类目标列表页路由 name */
export const LEDGER_ROUTE = {
  plan: 'MesSetPlan',
  standard: 'MesSetStandard',
  outlet: 'MesSetEmissionOutlet',
  permit: 'MesSetPollutionPermit',
  waste: 'MesSetHazardousWaste',
  envReport: 'MesSetEnvReport',
  carbon: 'MesSetCarbonEmission',
} as const;
