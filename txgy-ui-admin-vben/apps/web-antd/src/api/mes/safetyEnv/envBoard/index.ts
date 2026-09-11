import { requestClient } from '#/api/request';

export namespace MesEnvBoardApi {
  /** 证载有效期 */
  export interface PermitItem {
    permitNo?: string;
    enterpriseName?: string;
    endDate?: string;
    daysLeft?: number;
    level?: string; // NORMAL / EXPIRING / OVERDUE
  }

  /** 年总量水位 */
  export interface QuotaItem {
    outletId?: number;
    outletCode?: string;
    pollutantCode?: string;
    /** 许可年总量(t)。为 null 表示该口未配置——**没配不等于没超**，界面要显示「未配置」而不是绿灯 */
    annualLimit?: number;
    used?: number;
    ratio?: number;
    level?: string; // NORMAL / WARN(≥80%) / RED(≥100%)
  }

  export interface FacilityItem {
    id?: number;
    facilityNo?: string;
    facilityName?: string;
    runStatus?: string;
    shutdownStatus?: string;
    nextReplaceDate?: string;
    daysToReplace?: number;
  }

  export interface StorageItem {
    id?: number;
    sourceRecordNo?: string;
    itemName?: string;
    weight?: number;
    location?: string;
    storedAt?: number;
    daysStored?: number;
    daysLeft?: number; // 负数＝已超期
    level?: string; // OVERDUE / SOON / NORMAL
  }

  export interface ManifestItem {
    id?: number;
    manifestNo?: string;
    wasteName?: string;
    quantity?: number;
    status?: string;
    declareDeadline?: number;
    hoursLeft?: number; // 负数＝已逾期
  }

  export interface MaterialItem {
    id?: number;
    materialNo?: string;
    materialName?: string;
    quantity?: number;
    unit?: string;
    expireDate?: string;
    status?: string; // OUT(缺货) / EXPIRED(过期) / EXPIRING(待检)
  }

  /** 环保看板汇总 */
  export interface EnvBoard {
    generatedAt?: number;
    quota?: {
      permits?: PermitItem[];
      items?: QuotaItem[];
      warnCount?: number;
      redCount?: number;
    };
    facility?: {
      total?: number;
      running?: number;
      stopped?: number;
      shutdownDeclared?: number;
      dueReplace?: FacilityItem[];
      stoppedWithoutApproval?: FacilityItem[];
    };
    storage?: {
      /** 本看板采用的贮存期限(天)。GB18597 一年期，是看板默认口径，不是配置值 */
      limitDays?: number;
      overdueCount?: number;
      soonCount?: number;
      items?: StorageItem[];
    };
    manifest?: {
      total?: number;
      countByStatus?: Record<string, number>;
      dueSoon?: ManifestItem[];
    };
    material?: {
      alertCount?: number;
      alerts?: MaterialItem[];
    };
  }
}

/** 获得环保看板汇总(只读聚合，一次拿全五块) */
export function getEnvBoard() {
  return requestClient.get<MesEnvBoardApi.EnvBoard>('/mes/safety-env/env-board/summary');
}
