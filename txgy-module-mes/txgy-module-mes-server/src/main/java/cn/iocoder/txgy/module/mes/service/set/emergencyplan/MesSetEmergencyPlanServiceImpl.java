package cn.iocoder.txgy.module.mes.service.set.emergencyplan;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencyplan.vo.MesSetEmergencyPlanSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyplan.MesSetEmergencyPlanDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyplan.MesSetEmergencyPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_ALREADY_FILED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_FILED_IMMUTABLE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_FILING_BEFORE_PUBLISH;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_FILING_NO_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_PUBLISH_DATE_REQUIRED;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_PLAN_TYPE_INVALID;

/**
 * MES 安全环保检测-应急预案 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmergencyPlanServiceImpl implements MesSetEmergencyPlanService {

    /**
     * 预案类型：综合/专项/现场处置
     */
    private static final Set<String> PLAN_TYPES =
            new HashSet<>(Arrays.asList("COMPREHENSIVE", "SPECIAL", "ONSITE"));

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_FILED = "FILED";

    /**
     * 发布后 20 个工作日内报备案（《突发环境事件应急预案管理办法》）
     */
    private static final int FILING_WORKDAYS = 20;

    /**
     * 每 3 年评估修订一次
     */
    private static final int REVIEW_YEARS = 3;

    @Resource
    private MesSetEmergencyPlanMapper emergencyplanMapper;

    @Override
    public Long createEmergencyPlan(MesSetEmergencyPlanSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetEmergencyPlanDO obj = BeanUtils.toBean(createReqVO, MesSetEmergencyPlanDO.class);
        // 备案信息与评估日期只能由 publishPlan/filePlan/reviewPlan 写，建档时一律不认表单值
        obj.setFilingNo(null);
        obj.setFilingDate(null);
        obj.setLastReviewDate(null);
        obj.setReviewReason(null);
        applyPublishDerived(obj);
        emergencyplanMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmergencyPlan(MesSetEmergencyPlanSaveReqVO updateReqVO) {
        MesSetEmergencyPlanDO exist = validateEmergencyPlanExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getPlanNo());

        // 已备案的预案：发布日/版本是备案号的锚点，直接改就等于"备案号对不上正文"。
        // 要改只能走 reviewPlan（修订后重新备案）。
        if (STATUS_FILED.equals(exist.getStatus())
                && (!Objects.equals(updateReqVO.getPublishDate(), exist.getPublishDate())
                    || !Objects.equals(updateReqVO.getVersion(), exist.getVersion()))) {
            throw exception(SET_EMERGENCY_PLAN_FILED_IMMUTABLE);
        }

        MesSetEmergencyPlanDO obj = BeanUtils.toBean(updateReqVO, MesSetEmergencyPlanDO.class);
        // 置 null = 不参与 update（MyBatis-Plus 默认忽略 null），即这几个字段改不走表单
        obj.setFilingNo(null);
        obj.setFilingDate(null);
        obj.setLastReviewDate(null);
        obj.setReviewReason(null);
        obj.setFilingDeadline(null);
        obj.setNextReviewDate(null);
        obj.setStatus(null);
        if (obj.getPublishDate() != null && !Objects.equals(obj.getPublishDate(), exist.getPublishDate())) {
            // 发布日变了 = 重新起算备案时限与评估周期
            applyPublishDerived(obj);
        }
        emergencyplanMapper.updateById(obj);
    }

    @Override
    public void deleteEmergencyPlan(Long id) {
        validateEmergencyPlanExists(id);
        emergencyplanMapper.deleteById(id);
    }

    @Override
    public MesSetEmergencyPlanDO getEmergencyPlan(Long id) {
        return emergencyplanMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEmergencyPlanDO> getEmergencyPlanPage(MesSetEmergencyPlanPageReqVO pageReqVO) {
        return emergencyplanMapper.selectPage(pageReqVO);
    }

    @Override
    public void publishPlan(Long id, LocalDate publishDate) {
        MesSetEmergencyPlanDO exist = validateEmergencyPlanExists(id);
        if (STATUS_FILED.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_PLAN_FILED_IMMUTABLE);
        }
        MesSetEmergencyPlanDO update = new MesSetEmergencyPlanDO();
        update.setId(id);
        update.setPublishDate(publishDate == null ? LocalDate.now() : publishDate);
        applyPublishDerived(update);
        emergencyplanMapper.updateById(update);
    }

    @Override
    public void filePlan(Long id, String filingNo, LocalDate filingDate) {
        MesSetEmergencyPlanDO exist = validateEmergencyPlanExists(id);
        if (StrUtil.isBlank(filingNo)) {
            throw exception(SET_EMERGENCY_PLAN_FILING_NO_REQUIRED);
        }
        if (exist.getPublishDate() == null) {
            throw exception(SET_EMERGENCY_PLAN_PUBLISH_DATE_REQUIRED);
        }
        if (STATUS_FILED.equals(exist.getStatus())) {
            throw exception(SET_EMERGENCY_PLAN_ALREADY_FILED);
        }
        LocalDate filed = filingDate == null ? LocalDate.now() : filingDate;
        if (filed.isBefore(exist.getPublishDate())) {
            throw exception(SET_EMERGENCY_PLAN_FILING_BEFORE_PUBLISH);
        }
        MesSetEmergencyPlanDO update = new MesSetEmergencyPlanDO();
        update.setId(id);
        update.setFilingNo(filingNo);
        update.setFilingDate(filed);
        update.setStatus(STATUS_FILED);
        emergencyplanMapper.updateById(update);
    }

    @Override
    public void reviewPlan(Long id, String reason) {
        MesSetEmergencyPlanDO exist = validateEmergencyPlanExists(id);
        if (exist.getPublishDate() == null) {
            throw exception(SET_EMERGENCY_PLAN_PUBLISH_DATE_REQUIRED);
        }
        LocalDate today = LocalDate.now();
        MesSetEmergencyPlanDO update = new MesSetEmergencyPlanDO();
        update.setId(id);
        update.setLastReviewDate(today);
        update.setNextReviewDate(today.plusYears(REVIEW_YEARS));
        update.setReviewReason(reason);
        // 修订即换版：回到"已发布未备案"，须重新备案（备案号在下次 filePlan 时覆盖）
        update.setStatus(STATUS_PUBLISHED);
        emergencyplanMapper.updateById(update);
    }

    @Override
    public List<MesSetEmergencyPlanDO> listDueReview(int days) {
        return emergencyplanMapper.selectDueReview(LocalDate.now().plusDays(days));
    }

    private MesSetEmergencyPlanDO validateEmergencyPlanExists(Long id) {
        MesSetEmergencyPlanDO obj = emergencyplanMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EMERGENCY_PLAN_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、预案类型枚举
     */
    private void validateBase(MesSetEmergencyPlanSaveReqVO reqVO, String origin) {
        MesSetEmergencyPlanDO exist = emergencyplanMapper.selectByPlanNo(reqVO.getPlanNo());
        if (exist != null && !exist.getPlanNo().equals(origin)) {
            throw exception(SET_EMERGENCY_PLAN_NO_DUPLICATE);
        }
        if (reqVO.getPlanType() != null && !PLAN_TYPES.contains(reqVO.getPlanType())) {
            throw exception(SET_EMERGENCY_PLAN_TYPE_INVALID);
        }
    }

    /**
     * 按发布日期派生：状态 + 备案截止日 + 下次评估日。没发布就是草稿，两个日期都留空
     * ——**不拿建档日凑**（那是"没发布"被写成"已发布"的经典来源）。
     */
    private void applyPublishDerived(MesSetEmergencyPlanDO obj) {
        if (obj.getPublishDate() == null) {
            obj.setStatus(STATUS_DRAFT);
            obj.setFilingDeadline(null);
            obj.setNextReviewDate(null);
            return;
        }
        obj.setStatus(STATUS_PUBLISHED);
        obj.setFilingDeadline(plusWorkdays(obj.getPublishDate(), FILING_WORKDAYS));
        obj.setNextReviewDate(obj.getPublishDate().plusYears(REVIEW_YEARS));
    }

    /**
     * 发布日之后第 {@code days} 个工作日。
     * ponytail: 只跳周末，**法定节假日没接日历表** —— 遇上春节/国庆会把截止日算早几天。
     * 要精确就接一张节假日表再改这里，别在调用点各算各的。
     */
    static LocalDate plusWorkdays(LocalDate start, int days) {
        LocalDate d = start;
        int left = days;
        while (left > 0) {
            d = d.plusDays(1);
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                left--;
            }
        }
        return d;
    }

}
