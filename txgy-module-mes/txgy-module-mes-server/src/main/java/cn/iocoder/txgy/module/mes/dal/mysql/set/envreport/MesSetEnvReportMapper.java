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

    default MesSetEnvReportDO selectByReportNo(String report_no) {
        return selectOne(MesSetEnvReportDO::getReportNo, report_no);
    }

    default PageResult<MesSetEnvReportDO> selectPage(MesSetEnvReportPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetEnvReportDO> query = new LambdaQueryWrapperX<MesSetEnvReportDO>()
                .likeIfPresent(MesSetEnvReportDO::getReportNo, reqVO.getReportNo())
                .likeIfPresent(MesSetEnvReportDO::getReportName, reqVO.getReportName())
                .eqIfPresent(MesSetEnvReportDO::getReportType, reqVO.getReportType())
                .likeIfPresent(MesSetEnvReportDO::getReportCategory, reqVO.getReportCategory())
                .eqIfPresent(MesSetEnvReportDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetEnvReportDO::getId);
        return selectPage(reqVO, query);
    }

}
