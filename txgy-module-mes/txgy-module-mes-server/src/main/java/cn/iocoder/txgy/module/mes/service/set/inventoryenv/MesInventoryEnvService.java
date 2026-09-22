package cn.iocoder.txgy.module.mes.service.set.inventoryenv;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvBatchProfileRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvDashboardRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvInspectSummaryRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvRespVO;

/**
 * MES 在库环保视图 Service
 *
 * 读-only：这个视图不做任何写操作，行上的动作（判定/冻结/标记）都落到既有的接口上。
 * 「全库体检」也是读：它只是把同一个判据集在全库上跑一遍并计数，不落体检批次表。
 */
public interface MesInventoryEnvService {

    /** 检测状态：已检 */
    String INSPECT_STATUS_CHECKED = "CHECKED";
    /** 检测状态：未检 */
    String INSPECT_STATUS_NOT_CHECKED = "NOT_CHECKED";
    /** 检测状态：超期未检 */
    String INSPECT_STATUS_OVERDUE = "OVERDUE";
    /** 检测状态：积压 */
    String INSPECT_STATUS_STOCKPILE = "STOCKPILE";
    /** 检测状态：待检（命中未检/超期/积压/混放任一项，即待检清单） */
    String INSPECT_STATUS_PENDING = "PENDING";

    PageResult<MesInventoryEnvRespVO> getInventoryEnvPage(MesInventoryEnvPageReqVO pageReqVO);

    /**
     * 全库环保体检：对全部在库行跑一遍四项判据并计数。
     * 纯读、可重复调用、同一时刻同结果——不落库存档，所以没有「上次体检」的概念。
     */
    MesInventoryEnvInspectSummaryRespVO inspectAll();

    /** 合规看板：体检汇总 + 覆盖率/异常率 + 待复核积压 + 处置时效 + 分维度下钻 */
    MesInventoryEnvDashboardRespVO getDashboard();

    /** 批次档案：前向/后向链 + 每个节点的环保档案注解 */
    MesInventoryEnvBatchProfileRespVO getBatchProfile(String batchCode);

}
