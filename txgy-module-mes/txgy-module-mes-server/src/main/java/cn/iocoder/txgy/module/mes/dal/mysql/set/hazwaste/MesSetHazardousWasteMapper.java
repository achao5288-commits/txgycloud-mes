package cn.iocoder.txgy.module.mes.dal.mysql.set.hazwaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.MesSetHazardousWastePageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazardousWasteDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-危废台账 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetHazardousWasteMapper extends BaseMapperX<MesSetHazardousWasteDO> {

    default MesSetHazardousWasteDO selectByManifestNo(String manifestNo) {
        return selectOne(MesSetHazardousWasteDO::getManifestNo, manifestNo);
    }

    /**
     * 按桶码取台账行（**可能多行**：同一只桶多次进出，如先暂存后转移）。
     * 反向查来源的入口就在这里——现场只有桶码可读（HJ1276 贴签），由它倒推这批废物是哪来的。
     */
    default List<MesSetHazardousWasteDO> selectListByContainerCode(String containerCode) {
        return selectList(new LambdaQueryWrapperX<MesSetHazardousWasteDO>()
                .eq(MesSetHazardousWasteDO::getContainerCode, containerCode)
                .orderByAsc(MesSetHazardousWasteDO::getId));
    }

    default PageResult<MesSetHazardousWasteDO> selectPage(MesSetHazardousWastePageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetHazardousWasteDO> query = new LambdaQueryWrapperX<MesSetHazardousWasteDO>()
                .likeIfPresent(MesSetHazardousWasteDO::getManifestNo, reqVO.getManifestNo())
                .eqIfPresent(MesSetHazardousWasteDO::getWasteCode, reqVO.getWasteCode())
                .likeIfPresent(MesSetHazardousWasteDO::getWasteName, reqVO.getWasteName())
                .eqIfPresent(MesSetHazardousWasteDO::getStage, reqVO.getStage())
                .eqIfPresent(MesSetHazardousWasteDO::getContainerCode, reqVO.getContainerCode())
                .eqIfPresent(MesSetHazardousWasteDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetHazardousWasteDO::getId);
        return selectPage(reqVO, query);
    }

}
