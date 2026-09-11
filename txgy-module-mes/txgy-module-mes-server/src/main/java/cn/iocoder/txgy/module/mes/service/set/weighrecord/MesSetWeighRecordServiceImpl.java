package cn.iocoder.txgy.module.mes.service.set.weighrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.weighrecord.MesSetWeighRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.txgy.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.MES_SET_WEIGH_NET_WEIGHT_INVALID;

/**
 * MES 安全环保检测-称重记录 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetWeighRecordServiceImpl implements MesSetWeighRecordService {

    /**
     * 数据来源：人工登记（电子秤直采为 AUTO，待硬件接入）
     */
    private static final String DATA_SOURCE_MANUAL = "MANUAL";

    @Resource
    private MesSetWeighRecordMapper weighRecordMapper;

    @Override
    public PageResult<MesSetWeighRecordDO> getWeighRecordPage(MesSetWeighRecordPageReqVO pageReqVO) {
        return weighRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createWeighRecord(MesSetWeighRecordSaveReqVO reqVO) {
        BigDecimal tare = reqVO.getTareWeight() == null ? BigDecimal.ZERO : reqVO.getTareWeight();
        BigDecimal net = reqVO.getGrossWeight().subtract(tare);
        if (net.signum() <= 0) {
            throw exception(MES_SET_WEIGH_NET_WEIGHT_INVALID);
        }
        MesSetWeighRecordDO record = MesSetWeighRecordDO.builder()
                .weighType(reqVO.getWeighType())
                .bizType(reqVO.getBizType())
                .bizNo(reqVO.getBizNo())
                .containerCode(reqVO.getContainerCode())
                .batchCode(reqVO.getBatchCode())
                .deviceCode(reqVO.getDeviceCode())
                .grossWeight(reqVO.getGrossWeight())
                .tareWeight(tare)
                .netWeight(net)
                .dataSource(DATA_SOURCE_MANUAL)
                .plateNo(reqVO.getPlateNo())
                .photo(reqVO.getPhoto())
                .reason(reqVO.getReason())
                .operatorName(getCurrentUserName())
                .weighTime(reqVO.getWeighTime() == null ? LocalDateTime.now() : reqVO.getWeighTime())
                .build();
        weighRecordMapper.insert(record);
        return record.getId();
    }

    /**
     * 当前登录人昵称，取不到则回退为登录用户 id
     */
    private String getCurrentUserName() {
        String nickname = getLoginUserNickname();
        if (nickname == null || nickname.isEmpty()) {
            return String.valueOf(getLoginUserId());
        }
        return nickname;
    }

}
