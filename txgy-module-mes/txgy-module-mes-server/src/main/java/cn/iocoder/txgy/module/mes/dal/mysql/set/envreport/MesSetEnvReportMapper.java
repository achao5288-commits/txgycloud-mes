package cn.iocoder.txgy.module.mes.dal.mysql.set.envreport;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.envreport.vo.MesSetEnvReportPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.envreport.MesSetEnvReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安全环保检测-环保检测报告 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEnvReportMapper extends BaseMapperX<MesSetEnvReportDO> {

    default PageResult<MesSetEnvReportDO> selectPage(MesSetEnvReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesSetEnvReportDO>()
                .eqIfPresent(MesSetEnvReportDO::getReportType, reqVO.getReportType())
                .eqIfPresent(MesSetEnvReportDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEnvReportDO::getId));
    }

    default MesSetEnvReportDO selectByReportNo(String reportNo) {
        return selectOne(MesSetEnvReportDO::getReportNo, reportNo);
    }

}
