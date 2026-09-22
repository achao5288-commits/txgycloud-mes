package cn.iocoder.txgy.module.mes.service.set.signrecord;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.txgy.framework.common.exception.ErrorCode;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.signrecord.MesSetSignRecordMapper;
import cn.iocoder.txgy.module.system.api.user.AdminUserApi;
import cn.iocoder.txgy.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * MES 安全环保检测-签字记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetSignRecordServiceImpl implements MesSetSignRecordService {

    @Resource
    private MesSetSignRecordMapper signRecordMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public Long createSignRecord(MesSetSignRecordDO record) {
        signRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public boolean isSigned(String bizType, String bizNo) {
        if (StrUtil.hasBlank(bizType, bizNo)) {
            return false;
        }
        return !signRecordMapper.selectListByBiz(bizType, bizNo).isEmpty();
    }

    @Override
    public Long createManualSign(MesSetSignRecordSaveReqVO reqVO) {
        // 签字人昵称按账号现查：前端只给 id。信前端传的名字，就会出现「选了 A 存成 B」
        // 或者重名两人分不开的情况——签字记录是追责依据，姓名必须由账号反解。
        AdminUserRespDTO user = adminUserApi.selectById(reqVO.getSignUserId());
        String signUser = StrUtil.blankToDefault(
                user == null ? null : user.getNickname(), String.valueOf(reqVO.getSignUserId()));

        return createSignRecord(MesSetSignRecordDO.builder()
                .bizType(reqVO.getBizType())
                .bizNo(reqVO.getBizNo())
                .signRole(reqVO.getSignRole())
                .signUser(signUser)
                .signUserId(reqVO.getSignUserId())
                .operatorUserId(getLoginUserId())
                .signTime(LocalDateTime.now())
                .location(reqVO.getLocation())
                .opinion(reqVO.getOpinion())
                .signImg(reqVO.getSignImg())
                .build());
    }

    @Override
    public PageResult<MesSetSignRecordDO> getSignRecordPage(MesSetSignRecordPageReqVO pageReqVO) {
        return signRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public void requireSigned(SignPayload payload, ErrorCode missingCode) {
        // 注意这里只能拦"没带签名的请求"，拦不住"带了一张假 URL"——sign_img 存的是 infra 文件
        // 服务返回的地址，图是前端画完传上去的。要证明"人手画过"得上服务端校验图片内容，本片不做。
        if (StrUtil.isBlank(payload.signImg())) {
            throw exception(missingCode);
        }
        Long operatorId = getLoginUserId();
        // 这些入口没有代签场景（谁点的谁签），故 signUserId = operatorUserId = 登录人；
        // 昵称按账号现查，与 createManualSign 同口径——签字记录是追责依据，姓名不能信前端。
        AdminUserRespDTO user = adminUserApi.selectById(operatorId);
        createSignRecord(MesSetSignRecordDO.builder()
                .bizType(payload.bizType())
                .bizNo(payload.bizNo())
                .signRole(payload.signRole())
                .signUser(StrUtil.blankToDefault(
                        user == null ? null : user.getNickname(), String.valueOf(operatorId)))
                .signUserId(operatorId)
                .operatorUserId(operatorId)
                .signTime(LocalDateTime.now())
                .opinion(payload.opinion())
                .signImg(payload.signImg())
                .build());
    }

}
