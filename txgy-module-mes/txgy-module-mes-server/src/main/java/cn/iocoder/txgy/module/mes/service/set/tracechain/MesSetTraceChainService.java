package cn.iocoder.txgy.module.mes.service.set.tracechain;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceChainPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceReverseRespVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;

/**
 * MES 安全环保检测-追溯链节点 Service 接口
 *
 * 追溯链为系统写入的审计数据：只由复核/处置流转等内部逻辑追加节点(createTraceNode)，
 * 对前端仅暴露只读 page/get。
 *
 * @author OPENLAB BS
 */
public interface MesSetTraceChainService {

    /**
     * 追加追溯节点（内部调用：复核收口、台账处置流转，须在调用方事务内）
     *
     * @param node 节点（trace_code/biz_type/biz_no/node_action 等须由调用方组装）
     * @return 编号
     */
    Long createTraceNode(MesSetTraceChainDO node);

    /**
     * 获得追溯链节点
     *
     * @param id 编号
     * @return 追溯链节点
     */
    MesSetTraceChainDO getTraceNode(Long id);

    /**
     * 获得追溯链节点分页
     *
     * @param pageReqVO 分页查询
     * @return 追溯链节点分页
     */
    PageResult<MesSetTraceChainDO> getTraceNodePage(MesSetTraceChainPageReqVO pageReqVO);

    /**
     * 反向查来源：给一只危废桶（桶码或联单号），倒推它是哪来的
     * （设计文档 §9 物料衡算："危废可由源头工单反推投料批，否则报无源"）。
     *
     * @param containerCode 危废桶码（HJ1276 贴签上那个）
     * @param manifestNo    联单号，二者至少给一个
     * @return 台账行 + 每行的源头判定 + 过秤证据 + 时间轴 + 签字
     */
    MesSetTraceReverseRespVO reverseTrace(String containerCode, String manifestNo);

    /**
     * 导出全程追溯报告（Markdown 文本，调用方直接作为下载内容回给浏览器）。
     * 设计文档 §10：「报告含时间轴+衡算表+签字+整改+应急」；§12 三期「监管检查一键导出」。
     *
     * @param containerCode 危废桶码
     * @param manifestNo    联单号，二者至少给一个
     * @return 报告全文
     */
    String exportTraceReport(String containerCode, String manifestNo);

}
