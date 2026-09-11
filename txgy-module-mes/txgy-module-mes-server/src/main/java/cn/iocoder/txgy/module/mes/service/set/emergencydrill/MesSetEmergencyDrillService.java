package cn.iocoder.txgy.module.mes.service.set.emergencydrill;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillCoverageRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencydrill.vo.MesSetEmergencyDrillSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencydrill.MesSetEmergencyDrillDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-应急演练 Service 接口
 *
 * 设计文档 §八.2：每年至少 1 次演练，闭环为 计划→记录(照片视频签到)→评估→整改闭环。
 * 状态机 计划(PLANNED)→已演练(DONE)→已闭环(CLOSED)，不得回退；
 * **有整改要求未完成不能闭环**，且演练必须挂在一份已发布/已备案的预案上。
 *
 * @author OPENLAB BS
 */
public interface MesSetEmergencyDrillService {

    Long createEmergencyDrill(@Valid MesSetEmergencyDrillSaveReqVO createReqVO);

    void updateEmergencyDrill(@Valid MesSetEmergencyDrillSaveReqVO updateReqVO);

    void deleteEmergencyDrill(Long id);

    MesSetEmergencyDrillDO getEmergencyDrill(Long id);

    PageResult<MesSetEmergencyDrillDO> getEmergencyDrillPage(@Valid MesSetEmergencyDrillPageReqVO pageReqVO);

    /**
     * 演练完成：计划 → 已演练。要求记录与评估齐全（签到/照片/视频至少一项 + 评估结论）。
     */
    void finishEmergencyDrill(Long id, String evaluation);

    /**
     * 演练闭环：已演练 → 已闭环。有整改要求且未整改完成的拒绝。
     */
    void closeEmergencyDrill(Long id);

    /**
     * 年度演练覆盖情况：设计文档 §八.2 要求**每年至少 1 次**，这里直接给结论而不是让调用方数。
     */
    MesSetEmergencyDrillCoverageRespVO getYearCoverage(Integer year);

}
