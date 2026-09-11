package cn.iocoder.txgy.module.mes.dal.mysql.set.emergencyevent;

import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencyevent.MesSetEmergencyWasteDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-应急废物明细 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetEmergencyWasteMapper extends BaseMapperX<MesSetEmergencyWasteDO> {

    default List<MesSetEmergencyWasteDO> selectListByEventId(Long eventId) {
        return selectList(MesSetEmergencyWasteDO::getEventId, eventId);
    }

    /**
     * 按桶码查明细——追溯反查用："这只桶是哪个应急事件产生的"。
     */
    default List<MesSetEmergencyWasteDO> selectListByContainerCode(String containerCode) {
        return selectList(MesSetEmergencyWasteDO::getContainerCode, containerCode);
    }

}
