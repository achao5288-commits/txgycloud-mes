package cn.iocoder.txgy.module.mes.dal.mysql.set.signrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-签字记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetSignRecordMapper extends BaseMapperX<MesSetSignRecordDO> {

    /**
     * 按业务键取全部签字（危废联单四方会签校验用）。
     */
    default List<MesSetSignRecordDO> selectListByBiz(String bizType, String bizNo) {
        return selectList(new LambdaQueryWrapperX<MesSetSignRecordDO>()
                .eq(MesSetSignRecordDO::getBizType, bizType)
                .eq(MesSetSignRecordDO::getBizNo, bizNo)
                .orderByAsc(MesSetSignRecordDO::getId));
    }

    default PageResult<MesSetSignRecordDO> selectPage(MesSetSignRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetSignRecordDO> query = new LambdaQueryWrapperX<MesSetSignRecordDO>()
                .eqIfPresent(MesSetSignRecordDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MesSetSignRecordDO::getBizNo, reqVO.getBizNo())
                .likeIfPresent(MesSetSignRecordDO::getSignUser, reqVO.getSignUser())
                .orderByDesc(MesSetSignRecordDO::getSignTime)
                .orderByDesc(MesSetSignRecordDO::getId);
        return selectPage(reqVO, query);
    }

}
