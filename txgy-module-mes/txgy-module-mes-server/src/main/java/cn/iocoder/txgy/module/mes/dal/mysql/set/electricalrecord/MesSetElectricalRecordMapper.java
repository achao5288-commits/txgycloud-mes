package cn.iocoder.txgy.module.mes.dal.mysql.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-电气安全检测记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetElectricalRecordMapper extends BaseMapperX<MesSetElectricalRecordDO> {

    default PageResult<MesSetElectricalRecordDO> selectPage(MesSetElectricalRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetElectricalRecordDO>()
                .eqIfPresent(MesSetElectricalRecordDO::getCheckItem, reqVO.getCheckItem())
                .eqIfPresent(MesSetElectricalRecordDO::getResult, reqVO.getResult())
                .betweenIfPresent(MesSetElectricalRecordDO::getInspectTime, reqVO.getInspectTime())
                .orderByDesc(MesSetElectricalRecordDO::getId));
    }

    default MesSetElectricalRecordDO selectByRecordNo(String recordNo) {
        return selectOne(MesSetElectricalRecordDO::getRecordNo, recordNo);
    }

}
