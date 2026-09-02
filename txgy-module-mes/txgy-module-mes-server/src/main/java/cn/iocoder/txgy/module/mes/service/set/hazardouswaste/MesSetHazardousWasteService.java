package cn.iocoder.txgy.module.mes.service.set.hazardouswaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWastePageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazardouswaste.vo.MesSetHazardousWasteSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazardouswaste.MesSetHazardousWasteDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-危废台账 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetHazardousWasteService {

    /**
     * 创建危废台账
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHazardousWaste(@Valid MesSetHazardousWasteSaveReqVO createReqVO);

    /**
     * 更新危废台账
     *
     * @param updateReqVO 更新信息
     */
    void updateHazardousWaste(@Valid MesSetHazardousWasteSaveReqVO updateReqVO);

    /**
     * 删除危废台账
     *
     * @param id 编号
     */
    void deleteHazardousWaste(Long id);

    /**
     * 校验危废台账存在
     *
     * @param id 编号
     */
    void validateHazardousWasteExists(Long id);

    /**
     * 获得危废台账
     *
     * @param id 编号
     * @return 危废台账
     */
    MesSetHazardousWasteDO getHazardousWaste(Long id);

    /**
     * 获得危废台账分页
     *
     * @param pageReqVO 分页查询
     * @return 危废台账分页
     */
    PageResult<MesSetHazardousWasteDO> getHazardousWastePage(MesSetHazardousWastePageReqVO pageReqVO);

}
