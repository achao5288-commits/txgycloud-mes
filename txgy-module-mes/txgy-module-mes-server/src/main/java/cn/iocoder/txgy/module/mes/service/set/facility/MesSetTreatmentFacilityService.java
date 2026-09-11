package cn.iocoder.txgy.module.mes.service.set.facility;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.facility.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.facility.MesSetTreatmentFacilityDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 安全环保检测-治污设施运行管控 Service（设计文档 §6.3）
 *
 * 台账 + 换炭→HW49 + 停运申报审批 + 同开同停。
 *
 * @author OPENLAB BS
 */
public interface MesSetTreatmentFacilityService {

    /**
     * 设施类型（设计文档 §6.3 / §3.3 点名的三类）
     */
    String[] FACILITY_TYPES = {"ACTIVATED_CARBON", "CATALYTIC_COMBUSTION", "BAG_FILTER"};

    /**
     * 运行状态：运行 / 停运
     */
    String[] RUN_STATUSES = {"RUNNING", "STOPPED"};

    /**
     * 停运申报状态：无 / 待审批 / 已批准 / 已驳回
     */
    String[] SHUTDOWN_STATUSES = {"NONE", "PENDING", "APPROVED", "REJECTED"};

    /**
     * 换炭产出的危废：设计文档 §3.3「喷涂 VOCs = 活性炭吸附+催化燃烧（换炭→HW49）」
     */
    String CONSUMABLE_WASTE_CODE = "HW49";

    String CONSUMABLE_WASTE_NAME = "废活性炭";

    /**
     * 停运审批的留痕业务类型（复用 mes_set_sign_record）
     */
    String SHUTDOWN_SIGN_BIZ_TYPE = "FACILITY_SHUTDOWN";

    // ==================== 台账 ====================

    Long createFacility(@Valid MesSetTreatmentFacilitySaveReqVO createReqVO);

    void updateFacility(@Valid MesSetTreatmentFacilitySaveReqVO updateReqVO);

    void deleteFacility(Long id);

    MesSetTreatmentFacilityDO getFacility(Long id);

    PageResult<MesSetTreatmentFacilityDO> getFacilityPage(@Valid MesSetTreatmentFacilityPageReqVO pageReqVO);

    // ==================== 换炭 → HW49 ====================

    /**
     * 耗材更换（换炭）：**同一个事务内**把换下的废活性炭登记成 HW49 危废台账入桶，
     * 并顺延下次更换日期。杜绝"换了炭不记危废"。
     */
    MesSetFacilityReplaceRespVO replaceConsumable(@Valid MesSetFacilityReplaceReqVO reqVO);

    /**
     * 换炭到期预警：下次更换日期不晚于 now+days 的启用设施（含已逾期）。
     */
    List<MesSetTreatmentFacilityDO> listDueReplace(Integer days);

    // ==================== 停运申报 + 审批 ====================

    /**
     * 停运申报：NONE/REJECTED → PENDING。**申报不等于停运**——设施此时仍在 RUNNING，
     * 批准后才转 STOPPED（未批先停要拦，见 {@link #checkCoRun}）。
     */
    MesSetTreatmentFacilityDO declareShutdown(@Valid MesSetFacilityShutdownDeclareReqVO reqVO);

    /**
     * 停运审批：PENDING → APPROVED（并转 STOPPED）/ REJECTED（保持 RUNNING）。
     * 审批留痕写 mes_set_sign_record。
     */
    MesSetTreatmentFacilityDO approveShutdown(@Valid MesSetFacilityShutdownApproveReqVO reqVO);

    /**
     * 复运：STOPPED → RUNNING，并清掉本次停运申报（回到 NONE）。
     */
    MesSetTreatmentFacilityDO resumeFacility(Long id);

    /**
     * 未批先停清单：已停运但停运申报不是 APPROVED 的设施。
     */
    List<MesSetTreatmentFacilityDO> listStoppedWithoutApproval();

    // ==================== 同开同停 ====================

    /**
     * 同开同停校验（只读）：产线在跑而配套治污设施停运 → 违规 1040831007。
     *
     * 产线运行信号由调用方给出，因为工单表没有产线/设施列（见 ReqVO 注释）。
     */
    MesSetFacilityCoRunRespVO checkCoRun(@Valid MesSetFacilityCoRunReqVO reqVO);

}
