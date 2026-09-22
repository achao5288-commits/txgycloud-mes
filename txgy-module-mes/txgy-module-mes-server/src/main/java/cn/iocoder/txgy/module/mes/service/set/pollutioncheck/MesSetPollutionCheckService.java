package cn.iocoder.txgy.module.mes.service.set.pollutioncheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesFieldSignRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesPollutionLegalBasisRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAiRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckAmendReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckReviewReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesPollutionCheckLogDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 安全环保检测-污染判定记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPollutionCheckService {

    /**
     * 创建污染判定记录（AI 初筛自动回填，落为待复核）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPollutionCheck(@Valid MesSetPollutionCheckSaveReqVO createReqVO);

    /**
     * 更新污染判定记录（仅待复核；重新 AI 初筛）
     *
     * @param updateReqVO 更新信息
     */
    void updatePollutionCheck(@Valid MesSetPollutionCheckSaveReqVO updateReqVO);

    /**
     * 发起变更：对一张**已收口**的判定开一张变更单承接改动。
     *
     * 原单内容一行不动（本来就冻死了），只回填 superseded_by；新单按新编号落库、回到待复核，
     * 复核后才产生新的台账/批次戳投影。追溯链上原单与新单各写一条节点 —— 追溯查询只按
     * (biz_type, biz_no) 等值、不走 parent_code，所以两边都得有自己的节点才看得见。
     *
     * @param amendReqVO 变更信息（originId + 变更事由 + 新的内容字段 + 签名）
     * @return 新单编号
     */
    Long amendPollutionCheck(@Valid MesSetPollutionCheckAmendReqVO amendReqVO);

    /**
     * 删除污染判定记录（仅待复核）
     *
     * @param id 编号
     */
    void deletePollutionCheck(Long id);

    /**
     * 人工复核（终态二值：CLEAN/POLLUTED，收口 AI 的不确定）
     *
     * @param reviewReqVO 复核信息
     */
    void reviewPollutionCheck(@Valid MesSetPollutionCheckReviewReqVO reviewReqVO);

    /**
     * AI 初筛预览（不落库，供录入界面提前展示建议）
     *
     * @param reqVO 基础信息
     * @return AI 建议
     */
    MesSetPollutionCheckAiRespVO prescreenPollutionCheck(@Valid MesSetPollutionCheckSaveReqVO reqVO);

    /**
     * 获得污染判定记录
     *
     * @param id 编号
     * @return 污染判定记录
     */
    MesSetPollutionCheckDO getPollutionCheck(Long id);

    /**
     * 获得污染判定记录分页
     *
     * @param pageReqVO 分页查询
     * @return 污染判定记录分页
     */
    PageResult<MesSetPollutionCheckDO> getPollutionCheckPage(MesSetPollutionCheckPageReqVO pageReqVO);

    /**
     * 获得某条污染判定记录的完整履历（创建→改单→复核，时间正序；AI/人工结果快照只增不改）
     *
     * @param checkId 判定记录ID
     * @return 履历
     */
    List<MesPollutionCheckLogDO> getPollutionCheckLogList(Long checkId);

    /**
     * 获得人工复核可引用的法规依据清单（AI 初筛与人工复核共用同一份白名单）
     *
     * 白名单是静态常量表（{@link PollutionLegalBasis#CATALOG}），法规修订时只改那一个文件。
     * 下发的是全量 8 条：现场特征只能**收窄**候选，收窄到空时必须回落到全量，否则专员无处可选。
     *
     * @return 法规依据选项（label 短、value 为落库全文）
     */
    List<MesPollutionLegalBasisRespVO> getLegalBasisList();

    /**
     * 获得现场污染特征清单（操作员按眼见的事实勾选；每项带命中的法条候选供复核收窄）
     *
     * @return 现场特征选项（value 为落库的 code）
     */
    List<MesFieldSignRespVO> getFieldSignList();

}
