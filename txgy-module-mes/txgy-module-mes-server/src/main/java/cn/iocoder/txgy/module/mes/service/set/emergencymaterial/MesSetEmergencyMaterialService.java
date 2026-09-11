package cn.iocoder.txgy.module.mes.service.set.emergencymaterial;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial.MesSetEmergencyMaterialDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 安全环保检测-应急物资 Service 接口
 *
 * 设计文档 §八.2：物资台账（干沙/吸油毡/防化服/防毒面具/防爆工具/围堰沙袋/洗眼器/干粉灭火器）
 * 定点存放 + 临期预警。**状态是按数量与有效期现算的派生值，不接受人工填**。
 *
 * @author OPENLAB BS
 */
public interface MesSetEmergencyMaterialService {

    Long createEmergencyMaterial(@Valid MesSetEmergencyMaterialSaveReqVO createReqVO);

    void updateEmergencyMaterial(@Valid MesSetEmergencyMaterialSaveReqVO updateReqVO);

    void deleteEmergencyMaterial(Long id);

    MesSetEmergencyMaterialDO getEmergencyMaterial(Long id);

    PageResult<MesSetEmergencyMaterialDO> getEmergencyMaterialPage(@Valid MesSetEmergencyMaterialPageReqVO pageReqVO);

    /**
     * 应急物资告警：已过期 / 临期 / 缺货三类，按严重度排序。
     */
    List<MesSetEmergencyMaterialDO> getAlerts();

}
