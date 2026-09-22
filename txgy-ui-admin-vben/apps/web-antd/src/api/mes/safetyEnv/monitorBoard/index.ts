import { requestClient } from '#/api/request';

export namespace MesMonitorApi {
  /** 环比（较昨日同期）。正数表示超标类变差；比率类由前端按语义着色 */
  export interface Delta {
    exceedCount?: number;
    todayExceedTimes?: number;
    dataCompleteRate?: number;
    onlineRate?: number;
  }

  export interface Meta {
    source?: string;
    freq?: string;
    caliber?: string;
  }

  export interface HourTrend {
    hour?: string;
    /** 该小时无有效读数时为 null——**不是 0**，画成 0 等于凭空报了一个「浓度为零」 */
    value?: null | number;
    limit?: number;
  }

  export interface OutletRate {
    outletNo?: string;
    outletName?: string;
    /** 分子分母均排除掉线/数据延迟排口；无可考核时段为 null */
    passRate?: null | number;
    exceedCount?: number;
  }

  export interface Metric {
    pollutantName?: string;
    value?: number;
    limit?: number;
  }

  export interface ExceedEvent {
    t?: number;
    kind?: string; // DISPATCH / CLOSE
    title?: string;
    desc?: string;
  }

  export interface ExceedRecord {
    id?: number;
    outletNo?: string;
    outletName?: string;
    pollutantName?: string;
    value?: number;
    limit?: number;
    /** 后端算好下发；前端不重算 */
    multiple?: number;
    occurTime?: number;
    durationMin?: number;
    handleStatus?: string; // PENDING / PROCESSING / CLOSED
    handler?: string;
    events?: ExceedEvent[];
  }

  export interface OutletLive {
    outletNo?: string;
    outletName?: string;
    pollutantName?: string;
    /** 掉线/数据延迟为 null，不能显示为 0，也不能显示为达标 */
    value?: null | number;
    limit?: number;
    todayExceed?: number;
    lastReport?: string;
    status?: string; // ONLINE / STALE / OFFLINE
  }

  /** 在线监控看板（大屏 read-only 同结构） */
  export interface Board {
    outletTotal?: number;
    outletOnline?: number;
    exceedCount?: number;
    todayExceedTimes?: number;
    dataCompleteRate?: null | number;
    onlineRate?: null | number;
    delta?: Delta;
    meta?: Meta;
    hourTrend?: HourTrend[];
    outletRates?: OutletRate[];
    metrics?: Metric[];
    exceedRecords?: ExceedRecord[];
    outletLive?: OutletLive[];
  }
}

export interface MonitorQuery {
  /** 24h / 7d / 30d */
  range?: string;
  /** 监控因子筛选，空则由后端取读数最多的因子 */
  factor?: string;
}

/** 获得在线监控看板 */
export function getMonitorBoard(params?: MonitorQuery) {
  return requestClient.get<MesMonitorApi.Board>('/mes/safety-env/monitor/board', {
    params,
  });
}

/** 实时监控大屏轮询（每 2 秒一次，返回结构与看板一致） */
export function getMonitorLive(params?: MonitorQuery) {
  return requestClient.get<MesMonitorApi.Board>('/mes/safety-env/monitor/read-only', {
    params,
  });
}

/** 超标派单（处置人由后端按当前登录人回填） */
export function dispatchMonitorExceed(id: number) {
  return requestClient.post(`/mes/safety-env/monitor/exceed/${id}/dispatch`);
}

/** 超标闭环 */
export function closeMonitorExceed(id: number) {
  return requestClient.post(`/mes/safety-env/monitor/exceed/${id}/close`);
}
