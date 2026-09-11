package cn.iocoder.txgy.module.mes.dal.mysql.set.standard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-检测标准 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetStandardMapper extends BaseMapperX<MesSetStandardDO> {

    default MesSetStandardDO selectByStandardNo(String standard_no) {
        return selectOne(MesSetStandardDO::getStandardNo, standard_no);
    }

    default PageResult<MesSetStandardDO> selectPage(MesSetStandardPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetStandardDO> query = new LambdaQueryWrapperX<MesSetStandardDO>()
                .likeIfPresent(MesSetStandardDO::getStandardNo, reqVO.getStandardNo())
                .likeIfPresent(MesSetStandardDO::getStandardName, reqVO.getStandardName())
                .eqIfPresent(MesSetStandardDO::getDomain, reqVO.getDomain())
                .likeIfPresent(MesSetStandardDO::getTestType, reqVO.getTestType())
                .eqIfPresent(MesSetStandardDO::getPeriodType, reqVO.getPeriodType())
                .likeIfPresent(MesSetStandardDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetStandardDO::getId);
        return selectPage(reqVO, query);
    }

}
