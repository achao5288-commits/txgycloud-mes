package cn.iocoder.txgy.module.mes.dal.mysql.set.standard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-检测标准 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetStandardMapper extends BaseMapperX<MesSetStandardDO> {

    default PageResult<MesSetStandardDO> selectPage(MesSetStandardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetStandardDO>()
                .likeIfPresent(MesSetStandardDO::getStandardName, reqVO.getStandardName())
                .eqIfPresent(MesSetStandardDO::getDomain, reqVO.getDomain())
                .eqIfPresent(MesSetStandardDO::getTestType, reqVO.getTestType())
                .eqIfPresent(MesSetStandardDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetStandardDO::getId));
    }

    default Long selectCountByStandardNo(String standardNo) {
        return selectCount(MesSetStandardDO::getStandardNo, standardNo);
    }

    default List<MesSetStandardDO> selectListByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<MesSetStandardDO>()
                .eq(MesSetStandardDO::getStatus, status)
                .orderByAsc(MesSetStandardDO::getStandardName));
    }

}
