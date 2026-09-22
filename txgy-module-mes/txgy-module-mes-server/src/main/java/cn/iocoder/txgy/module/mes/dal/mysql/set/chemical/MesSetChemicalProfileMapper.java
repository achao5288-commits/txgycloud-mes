package cn.iocoder.txgy.module.mes.dal.mysql.set.chemical;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo.MesSetChemicalProfilePageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * MES 安全环保检测-危化品档案 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetChemicalProfileMapper extends BaseMapperX<MesSetChemicalProfileDO> {

    default MesSetChemicalProfileDO selectByProfileNo(String profileNo) {
        return selectOne(MesSetChemicalProfileDO::getProfileNo, profileNo);
    }

    default PageResult<MesSetChemicalProfileDO> selectPage(MesSetChemicalProfilePageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetChemicalProfileDO> query = new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                .likeIfPresent(MesSetChemicalProfileDO::getProfileNo, reqVO.getProfileNo())
                .likeIfPresent(MesSetChemicalProfileDO::getChemicalName, reqVO.getChemicalName())
                .eqIfPresent(MesSetChemicalProfileDO::getChemicalCode, reqVO.getChemicalCode())
                .eqIfPresent(MesSetChemicalProfileDO::getCompatGroup, reqVO.getCompatGroup())
                .eqIfPresent(MesSetChemicalProfileDO::getStorageZone, reqVO.getStorageZone())
                .eqIfPresent(MesSetChemicalProfileDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesSetChemicalProfileDO::getId);
        return selectPage(reqVO, query);
    }

    /**
     * 按化学品代码取档案（应急事件/处置卡按代码反查 MSDS 与应急处置文本）。
     *
     * ponytail: 同代码多档案时 selectOne 会炸——档案表没有唯一键，靠服务层保证一码一档；
     * 真出现重复时这里报错比静默取第一条好。
     */
    default MesSetChemicalProfileDO selectByChemicalCode(String chemicalCode) {
        return selectOne(MesSetChemicalProfileDO::getChemicalCode, chemicalCode);
    }

    /**
     * 同一库位上已启用的其他危化品（禁配判定的比对集）。
     */
    default List<MesSetChemicalProfileDO> selectEnabledByLocation(String storageLocation, Long excludeId) {
        return selectList(new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                .eq(MesSetChemicalProfileDO::getStorageLocation, storageLocation)
                .eq(MesSetChemicalProfileDO::getStatus, "ENABLED")
                .neIfPresent(MesSetChemicalProfileDO::getId, excludeId));
    }

    /**
     * 按物料取已启用档案（在库侧入口：批次 → 物料 → 是否按危化品管）。
     *
     * ponytail: 同一物料绑多条启用档案时 selectOne 会炸。档案表加不了唯一键（item_id 可空且历史行为空），
     * 所以靠服务层在 create/update 时挡住重复绑定；真漏了这里报错比静默取第一条好。
     */
    default MesSetChemicalProfileDO selectEnabledByItemId(Long itemId) {
        if (itemId == null) {
            return null;
        }
        return selectOne(new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                .eq(MesSetChemicalProfileDO::getItemId, itemId)
                .eq(MesSetChemicalProfileDO::getStatus, "ENABLED"));
    }

    /**
     * 批量版本（在库侧比对集：一个库位上在库物料的全部档案，一次查完不做 N+1）。
     */
    default List<MesSetChemicalProfileDO> selectEnabledByItemIds(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                .in(MesSetChemicalProfileDO::getItemId, itemIds)
                .eq(MesSetChemicalProfileDO::getStatus, "ENABLED"));
    }

    /**
     * 同一个物料是否已被别的档案绑定（绑定唯一性校验，excludeId 传自身 id）。
     */
    default MesSetChemicalProfileDO selectByItemId(Long itemId, Long excludeId) {
        if (itemId == null) {
            return null;
        }
        return selectOne(new LambdaQueryWrapperX<MesSetChemicalProfileDO>()
                .eq(MesSetChemicalProfileDO::getItemId, itemId)
                .neIfPresent(MesSetChemicalProfileDO::getId, excludeId));
    }

}
