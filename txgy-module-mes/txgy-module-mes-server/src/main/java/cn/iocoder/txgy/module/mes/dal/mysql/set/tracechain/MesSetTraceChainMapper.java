package cn.iocoder.txgy.module.mes.dal.mysql.set.tracechain;

import cn.iocoder.txgy.framework.common.pojo.PageParam;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.tracechain.vo.MesSetTraceChainPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.tracechain.MesSetTraceChainDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * MES 安全环保检测-追溯链节点 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetTraceChainMapper extends BaseMapperX<MesSetTraceChainDO> {

    default PageResult<MesSetTraceChainDO> selectPage(MesSetTraceChainPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetTraceChainDO> query = new LambdaQueryWrapperX<MesSetTraceChainDO>()
                .likeIfPresent(MesSetTraceChainDO::getTraceCode, reqVO.getTraceCode())
                .likeIfPresent(MesSetTraceChainDO::getBizNo, reqVO.getBizNo())
                .eqIfPresent(MesSetTraceChainDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MesSetTraceChainDO::getNodeStage, reqVO.getNodeStage())
                .orderByDesc(MesSetTraceChainDO::getNodeTime)
                .orderByDesc(MesSetTraceChainDO::getId);
        return selectPage(reqVO, query);
    }

    /**
     * 按一组业务单号取全部节点（不分页）——反向查来源要的是"这只桶的完整时间轴"，
     * 分页会把链截断在中间，看到半条链比看不到更危险。
     */
    default List<MesSetTraceChainDO> selectListByBizNos(Collection<String> bizNos) {
        if (bizNos == null || bizNos.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<MesSetTraceChainDO>()
                .in(MesSetTraceChainDO::getBizNo, bizNos)
                .orderByAsc(MesSetTraceChainDO::getNodeTime)
                .orderByAsc(MesSetTraceChainDO::getId));
    }

    /**
     * 按一组业务单号分页（批次全链追溯：biz_no 落在同批各判定的 recordNo 集合内）
     */
    default PageResult<MesSetTraceChainDO> selectPageByBizNos(Collection<String> bizNos, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<MesSetTraceChainDO>()
                .in(MesSetTraceChainDO::getBizNo, bizNos)
                .orderByDesc(MesSetTraceChainDO::getNodeTime)
                .orderByDesc(MesSetTraceChainDO::getId));
    }

}
