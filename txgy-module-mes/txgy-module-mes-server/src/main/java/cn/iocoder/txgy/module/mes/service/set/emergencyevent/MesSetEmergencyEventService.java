package cn.iocoder.txgy.module.mes.service.set.emergencyevent;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDetailRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventDisposeReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyevent.vo.MesSetEmergencyEventSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyEventDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-应急事件 Service 接口
 *
 * 设计文档 §八.4 的闭环：PDA 一键上报(create) → 应急任务派发(dispatch) → 处置拍照 +
 * **应急废物一律过秤贴签入危废台账**(dispose) → 事件报告负责人签字闭环(close)。
 *
 * @author OPENLAB BS
 */
public interface MesSetEmergencyEventService {

    /**
     * 上报事件（PDA 一键上报）。状态直接落到 REPORTED，上报人/上报时间由服务端取登录态，
     * **不接受前端填"谁几点报的"**。
     */
    Long createEmergencyEvent(@Valid MesSetEmergencyEventSaveReqVO createReqVO);

    void updateEmergencyEvent(@Valid MesSetEmergencyEventSaveReqVO updateReqVO);

    void deleteEmergencyEvent(Long id);

    MesSetEmergencyEventDO getEmergencyEvent(Long id);

    /**
     * 事件详情：本体 + 应急废物逐桶明细。
     */
    MesSetEmergencyEventDetailRespVO getEmergencyEventDetail(Long id);

    PageResult<MesSetEmergencyEventDO> getEmergencyEventPage(@Valid MesSetEmergencyEventPageReqVO pageReqVO);

    /**
     * 应急任务派发：已上报 → 处置中，指定处置人。
     */
    void dispatchEmergencyEvent(Long id, String handler);

    /**
     * 处置：已上报/处置中 → 待报告。逐桶登记应急废物，每桶同事务写
     * 称重记录（过秤）+ 危废台账行（贴签入账）+ 明细行，并回填桶数与合计净重。
     */
    void disposeEmergencyEvent(@Valid MesSetEmergencyEventDisposeReqVO reqVO);

    /**
     * 事件闭环：待报告 → 已闭环。写事件报告、负责人签字（签字表留档）与追溯节点。
     */
    void closeEmergencyEvent(Long id, String reportContent, String approver, String signImg);

}
