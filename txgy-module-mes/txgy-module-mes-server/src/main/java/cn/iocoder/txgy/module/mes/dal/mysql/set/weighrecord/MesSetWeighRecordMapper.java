package cn.iocoder.txgy.module.mes.dal.mysql.set.weighrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-称重记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetWeighRecordMapper extends BaseMapperX<MesSetWeighRecordDO> {

    /**
     * 按桶码取过秤证据（反向查来源：这只桶"称过没有、称了多少"必须能单独答，
     * 不能因为台账行上有 quantity 就假定称过——那可能只是手填的）。
     */
    default List<MesSetWeighRecordDO> selectListByContainerCode(String containerCode) {
        return selectList(new LambdaQueryWrapperX<MesSetWeighRecordDO>()
                .eq(MesSetWeighRecordDO::getContainerCode, containerCode)
                .orderByAsc(MesSetWeighRecordDO::getId));
    }

    default PageResult<MesSetWeighRecordDO> selectPage(MesSetWeighRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetWeighRecordDO> query = new LambdaQueryWrapperX<MesSetWeighRecordDO>()
                .eqIfPresent(MesSetWeighRecordDO::getWeighType, reqVO.getWeighType())
                .eqIfPresent(MesSetWeighRecordDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MesSetWeighRecordDO::getBizNo, reqVO.getBizNo())
                .orderByDesc(MesSetWeighRecordDO::getWeighTime)
                .orderByDesc(MesSetWeighRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
