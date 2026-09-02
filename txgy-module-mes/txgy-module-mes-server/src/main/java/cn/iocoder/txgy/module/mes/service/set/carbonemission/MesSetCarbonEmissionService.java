package cn.iocoder.txgy.module.mes.service.set.carbonemission;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.carbonemission.vo.MesSetCarbonEmissionSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.carbonemission.MesSetCarbonEmissionDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-碳排放核算记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetCarbonEmissionService {

    /**
     * 创建碳排放核算记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCarbonEmission(@Valid MesSetCarbonEmissionSaveReqVO createReqVO);

    /**
     * 更新碳排放核算记录
     *
     * @param updateReqVO 更新信息
     */
    void updateCarbonEmission(@Valid MesSetCarbonEmissionSaveReqVO updateReqVO);

    /**
     * 删除碳排放核算记录
     *
     * @param id 编号
     */
    void deleteCarbonEmission(Long id);

    /**
     * 校验碳排放核算记录存在
     *
     * @param id 编号
     */
    void validateCarbonEmissionExists(Long id);

    /**
     * 获得碳排放核算记录
     *
     * @param id 编号
     * @return 碳排放核算记录
     */
    MesSetCarbonEmissionDO getCarbonEmission(Long id);

    /**
     * 获得碳排放核算记录分页
     *
     * @param pageReqVO 分页查询
     * @return 碳排放核算记录分页
     */
    PageResult<MesSetCarbonEmissionDO> getCarbonEmissionPage(MesSetCarbonEmissionPageReqVO pageReqVO);

}
