package cn.iocoder.txgy.module.mes.service.set.pressurevessel;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pressurevessel.vo.MesSetPressureVesselSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel.MesSetPressureVesselDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-压力容器检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPressureVesselService {

    /**
     * 创建压力容器检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPressureVessel(@Valid MesSetPressureVesselSaveReqVO createReqVO);

    /**
     * 更新压力容器检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePressureVessel(@Valid MesSetPressureVesselSaveReqVO updateReqVO);

    /**
     * 删除压力容器检测记录
     *
     * @param id 编号
     */
    void deletePressureVessel(Long id);

    /**
     * 校验压力容器检测记录存在
     *
     * @param id 编号
     */
    void validatePressureVesselExists(Long id);

    /**
     * 获得压力容器检测记录
     *
     * @param id 编号
     * @return 压力容器检测记录
     */
    MesSetPressureVesselDO getPressureVessel(Long id);

    /**
     * 获得压力容器检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 压力容器检测记录分页
     */
    PageResult<MesSetPressureVesselDO> getPressureVesselPage(MesSetPressureVesselPageReqVO pageReqVO);

}
