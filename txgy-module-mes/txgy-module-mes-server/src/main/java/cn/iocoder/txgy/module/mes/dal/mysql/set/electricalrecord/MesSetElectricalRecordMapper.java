package cn.iocoder.txgy.module.mes.dal.mysql.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-电气安全检查 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetElectricalRecordMapper extends BaseMapperX<MesSetElectricalRecordDO> {

    default MesSetElectricalRecordDO selectByRecordNo(String record_no) {
        return selectOne(MesSetElectricalRecordDO::getRecordNo, record_no);
    }

    default PageResult<MesSetElectricalRecordDO> selectPage(MesSetElectricalRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetElectricalRecordDO> query = new LambdaQueryWrapperX<MesSetElectricalRecordDO>()
                .likeIfPresent(MesSetElectricalRecordDO::getRecordNo, reqVO.getRecordNo())
                .likeIfPresent(MesSetElectricalRecordDO::getLocation, reqVO.getLocation())
                .likeIfPresent(MesSetElectricalRecordDO::getCheckItem, reqVO.getCheckItem())
                .likeIfPresent(MesSetElectricalRecordDO::getResult, reqVO.getResult())
                .orderByDesc(MesSetElectricalRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
