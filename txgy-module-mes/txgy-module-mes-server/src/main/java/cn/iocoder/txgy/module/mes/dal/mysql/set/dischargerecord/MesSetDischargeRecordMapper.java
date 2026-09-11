package cn.iocoder.txgy.module.mes.dal.mysql.set.dischargerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord.vo.MesSetDischargeRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord.MesSetDischargeRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-排放合规流水 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetDischargeRecordMapper extends BaseMapperX<MesSetDischargeRecordDO> {

    default PageResult<MesSetDischargeRecordDO> selectPage(MesSetDischargeRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetDischargeRecordDO> query = new LambdaQueryWrapperX<MesSetDischargeRecordDO>()
                .eqIfPresent(MesSetDischargeRecordDO::getSourceRecordNo, reqVO.getSourceRecordNo())
                .eqIfPresent(MesSetDischargeRecordDO::getStage, reqVO.getStage())
                .eqIfPresent(MesSetDischargeRecordDO::getBatchNo, reqVO.getBatchNo())
                .likeIfPresent(MesSetDischargeRecordDO::getItemName, reqVO.getItemName())
                .likeIfPresent(MesSetDischargeRecordDO::getDestination, reqVO.getDestination())
                .orderByDesc(MesSetDischargeRecordDO::getDischargeTime)
                .orderByDesc(MesSetDischargeRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
