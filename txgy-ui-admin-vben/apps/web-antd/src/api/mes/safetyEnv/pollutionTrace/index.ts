import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MesPollutionTraceApi {
  /** MES 安全环保检测-追溯链节点 */
  export interface TraceNode {
    id?: number;
    traceCode?: string; // 追溯对象码(现=判定 recordNo)
    traceType?: string; // CHECK(污染判定)/LEDGER(台账处置)
    parentCode?: string; // 上游关联码
    bizType?: string;
    bizNo?: string; // 业务关联单号(判定 recordNo)
    nodeStage?: string; // 环节或 DISPOSAL
    nodeAction?: string; // 中文动作(时间轴主展示)
    batchStatus?: string; // 节点时刻对象状态
    operatorName?: string;
    nodeTime?: number;
    extra?: string;
    createTime?: number;
  }

  /** MES 安全环保检测-签字记录 */
  export interface SignRecord {
    id?: number;
    bizType?: string;
    bizNo?: string;
    signRole?: string; // REVIEWER/OPERATOR/APPROVER
    signUser?: string;
    signTime?: number;
    location?: string;
    opinion?: string;
    signImg?: string;
    createTime?: number;
  }

  /** 手工登记称重参数（净重服务端算） */
  export interface WeighSavePayload {
    weighType: string; // PRODUCE(产废)/FACTORY(出厂)
    bizType?: string;
    bizNo?: string;
    containerCode?: string;
    batchCode?: string;
    deviceCode?: string;
    grossWeight: number; // 毛重(kg)
    tareWeight?: number; // 皮重(kg)
    plateNo?: string;
    photo?: string;
    reason?: string;
    weighTime?: string;
  }

  /** MES 安全环保检测-称重记录 */
  export interface WeighRecord {
    id?: number;
    weighType?: string; // PRODUCE(产废)/FACTORY(出厂)
    bizType?: string;
    bizNo?: string;
    containerCode?: string;
    batchCode?: string;
    deviceCode?: string;
    grossWeight?: number;
    tareWeight?: number;
    netWeight?: number;
    dataSource?: string; // AUTO/MANUAL
    plateNo?: string;
    photo?: string;
    reason?: string;
    operatorName?: string;
    weighTime?: number;
    createTime?: number;
  }
}

export namespace MesPollutionTraceApi {
  /** 危废台账行 + 它反查出来的源头 */
  export interface ReverseLedger {
    id?: number;
    manifestNo?: string;
    wasteCode?: string;
    wasteName?: string;
    quantity?: number;
    quantityUnit?: string;
    stage?: string; // GENERATED/STORED/TRANSFERRED/DISPOSED
    status?: string;
    storageLocation?: string;
    containerCode?: string;
    counterparty?: string;
    /** 关联工单编号。**只回 ID**：工单侧目前没有环保联结字段，反推投料批要等它补上关联列 */
    woId?: number;
    handleTime?: number;
    handler?: string;
    /** EMERGENCY(应急处置产生) / FACILITY_CARBON(治理设施换炭) / UNSOURCED(无源) */
    sourceType?: string;
    sourceDocNo?: string;
    sourceName?: string;
    /** 源头说明，或**无源原因**，直接给人看 */
    sourceDetail?: string;
    sourceId?: number;
  }

  /** 危废反向查来源结果。核心是 sourced 这个判断题：答不出「这只桶哪来的」就是无源异常 */
  export interface TraceReverse {
    containerCode?: string;
    manifestNo?: string;
    found?: boolean;
    sourced?: boolean;
    unsourcedReason?: string;
    ledgers?: ReverseLedger[];
    weighRecords?: WeighRecord[];
    traceNodes?: TraceNode[];
    signRecords?: SignRecord[];
  }
}

/** 追溯链节点分页(可按追溯对象/业务单号/类型/环节筛选；batchNo 给出时按批次聚合该批全部判定的链) */
export function getTraceChainPage(
  params: PageParam & {
    traceCode?: string;
    bizNo?: string;
    bizType?: string;
    nodeStage?: string;
    batchNo?: string;
  },
) {
  return requestClient.get<PageResult<MesPollutionTraceApi.TraceNode>>(
    '/mes/safety-env/pollution-trace/page',
    { params },
  );
}

/** 签字记录分页(按业务键反查) */
export function getSignRecordPage(params: PageParam & { bizType?: string; bizNo?: string; signUser?: string }) {
  return requestClient.get<PageResult<MesPollutionTraceApi.SignRecord>>(
    '/mes/safety-env/pollution-sign/page',
    { params },
  );
}

/** 称重记录分页(按业务键反查；无电子秤直采前为空) */
export function getWeighRecordPage(params: PageParam & { weighType?: string; bizType?: string; bizNo?: string }) {
  return requestClient.get<PageResult<MesPollutionTraceApi.WeighRecord>>(
    '/mes/safety-env/pollution-weigh/page',
    { params },
  );
}

/** 手工登记称重(净重由服务端按 毛重-皮重 计算，dataSource=MANUAL) */
export function createWeighRecord(data: MesPollutionTraceApi.WeighSavePayload) {
  return requestClient.post('/mes/safety-env/pollution-weigh/create', data);
}

/** 反向查来源：给一只危废桶(桶码或联单号)，倒推它是哪来的。查不到源头会如实报"无源"，不是查询失败 */
export function reverseTrace(params: { containerCode?: string; manifestNo?: string }) {
  return requestClient.get<MesPollutionTraceApi.TraceReverse>(
    '/mes/safety-env/pollution-trace/reverse',
    { params },
  );
}

/** 导出一只桶的全程追溯报告(Markdown 下载件：时间轴+衡算表+签字+整改+应急) */
export function exportTraceReport(params: { containerCode?: string; manifestNo?: string }) {
  return requestClient.download('/mes/safety-env/pollution-trace/report', { params });
}
