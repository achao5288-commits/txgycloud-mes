package cn.iocoder.txgy.module.mes.service.set.emergencyplan;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan.MesSetEmergencyPlanDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * MES 安全环保检测-应急预案 Service 接口
 *
 * 设计文档 §八.1：编制发布后 20 个工作日内报生态环境部门备案，登记备案号 + 附件；
 * 每 3 年或工艺/物料/法规变化时评估修订。故本服务把"发布→备案→修订"三个动作显式化，
 * **备案号/备案日期/评估日期不接受表单直填**（只能在对应动作里写）。
 *
 * @author OPENLAB BS
 */
public interface MesSetEmergencyPlanService {

    Long createEmergencyPlan(@Valid MesSetEmergencyPlanSaveReqVO createReqVO);

    void updateEmergencyPlan(@Valid MesSetEmergencyPlanSaveReqVO updateReqVO);

    void deleteEmergencyPlan(Long id);

    MesSetEmergencyPlanDO getEmergencyPlan(Long id);

    PageResult<MesSetEmergencyPlanDO> getEmergencyPlanPage(@Valid MesSetEmergencyPlanPageReqVO pageReqVO);

    /**
     * 发布预案：定发布日期，并据此派生备案截止日（+20 工作日）与下次评估日（+3 年）。
     *
     * @param id          编号
     * @param publishDate 发布日期，缺省今天
     */
    void publishPlan(Long id, LocalDate publishDate);

    /**
     * 备案登记：写备案号 + 备案日期，状态转 FILED。
     * **逾期仍允许备案**（现实里晚报是常态，卡住只会逼人改日期），逾期与否由
     * `filingDate > filingDeadline` 现算，不落冗余列。
     */
    void filePlan(Long id, String filingNo, LocalDate filingDate);

    /**
     * 评估修订：记修订日期与原因，下次评估日顺延 3 年；预案回到 PUBLISHED，
     * 需重新备案（备案号在下一次 filePlan 时覆盖）。
     */
    void reviewPlan(Long id, String reason);

    /**
     * 待评估修订清单（含已逾期）：nextReviewDate &lt;= today+days。
     */
    List<MesSetEmergencyPlanDO> listDueReview(int days);

}
