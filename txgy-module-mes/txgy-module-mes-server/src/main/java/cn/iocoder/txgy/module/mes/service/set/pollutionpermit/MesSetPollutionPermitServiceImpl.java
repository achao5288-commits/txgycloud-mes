package cn.iocoder.txgy.module.mes.service.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.pollutionpermit.MesSetPollutionPermitMapper;
import cn.iocoder.txgy.module.mes.service.set.permitcompliance.MesSetPermitComplianceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_DATE_INVALID;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_OUTLET_MISSING;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_POLLUTION_PERMIT_STATUS_INVALID;

/**
 * MES 安全环保检测-排污许可证 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetPollutionPermitServiceImpl implements MesSetPollutionPermitService {

    /**
     * 状态：有效
     */
    public static final String STATUS_ACTIVE = "ACTIVE";
    /**
     * 状态：已过期
     */
    public static final String STATUS_EXPIRED = "EXPIRED";
    /**
     * 状态：已注销
     */
    public static final String STATUS_REVOKED = "REVOKED";

    private static final Set<String> STATUSES = new HashSet<>(
            Arrays.asList(STATUS_ACTIVE, STATUS_EXPIRED, STATUS_REVOKED));

    @Resource
    private MesSetPollutionPermitMapper pollutionPermitMapper;

    @Resource
    private MesSetPermitComplianceService permitComplianceService;

    @Override
    public Long createPollutionPermit(MesSetPollutionPermitSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        // 1. 组装
        MesSetPollutionPermitDO permit = BeanUtils.toBean(createReqVO, MesSetPollutionPermitDO.class);
        if (permit.getStatus() == null) {
            permit.setStatus(STATUS_ACTIVE);
        }
        // 2. 插入
        pollutionPermitMapper.insert(permit);
        return permit.getId();
    }

    @Override
    public void updatePollutionPermit(MesSetPollutionPermitSaveReqVO updateReqVO) {
        // 1. 校验存在
        MesSetPollutionPermitDO exist = validatePollutionPermitExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getPermitNo());
        // 2. 更新
        MesSetPollutionPermitDO updateObj = BeanUtils.toBean(updateReqVO, MesSetPollutionPermitDO.class);
        pollutionPermitMapper.updateById(updateObj);
    }

    @Override
    public void deletePollutionPermit(Long id) {
        validatePollutionPermitExists(id);
        pollutionPermitMapper.deleteById(id);
    }

    @Override
    public MesSetPollutionPermitDO getPollutionPermit(Long id) {
        return pollutionPermitMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetPollutionPermitDO> getPollutionPermitPage(MesSetPollutionPermitPageReqVO pageReqVO) {
        return pollutionPermitMapper.selectPage(pageReqVO);
    }

    private MesSetPollutionPermitDO validatePollutionPermitExists(Long id) {
        MesSetPollutionPermitDO permit = pollutionPermitMapper.selectById(id);
        if (permit == null) {
            throw exception(SET_POLLUTION_PERMIT_NOT_EXISTS);
        }
        return permit;
    }

    /**
     * 基础校验：证号唯一(改单时排除自身)、有效期先后、状态枚举、排放口必填、总量 JSON 合法
     */
    private void validateBase(MesSetPollutionPermitSaveReqVO reqVO, String originPermitNo) {
        // 1. 证号唯一（uk_permit_no 全库唯一不带租户，跨租户须加 -H 后缀由录入方保证）
        MesSetPollutionPermitDO existByNo = pollutionPermitMapper.selectByPermitNo(reqVO.getPermitNo());
        if (existByNo != null && !existByNo.getPermitNo().equals(originPermitNo)) {
            throw exception(SET_POLLUTION_PERMIT_NO_DUPLICATE);
        }
        // 2. 有效期先后
        if (reqVO.getEndDate() != null && reqVO.getStartDate() != null
                && reqVO.getEndDate().isBefore(reqVO.getStartDate())) {
            throw exception(SET_POLLUTION_PERMIT_DATE_INVALID);
        }
        // 3. 状态枚举
        if (reqVO.getStatus() != null && !STATUSES.contains(reqVO.getStatus())) {
            throw exception(SET_POLLUTION_PERMIT_STATUS_INVALID);
        }
        // 4. 排放口必填（按证排污的最小抓手：无排放口则无从比对）
        if (reqVO.getOutletCodes() == null || reqVO.getOutletCodes().trim().isEmpty()) {
            throw exception(SET_POLLUTION_PERMIT_OUTLET_MISSING);
        }
        // 5. 许可年排放总量 JSON 合法性（结构：[{pollutantCode,pollutantName,annualLimitT,annualUsedT}]）
        //    校验口径与合规判定同源：不光要"是个 JSON 数组"，还要每条都有 pollutantCode 且 annualLimitT>0。
        //    否则一串合法但无意义的 JSON 能过校验，然后在红线判定里静默失效——那等于没有红线。
        permitComplianceService.validateAnnualLimits(reqVO.getAnnualLimits());
    }

}
