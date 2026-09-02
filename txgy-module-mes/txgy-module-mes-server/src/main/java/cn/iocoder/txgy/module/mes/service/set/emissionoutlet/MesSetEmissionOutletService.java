package cn.iocoder.txgy.module.mes.service.set.emissionoutlet;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emissionoutlet.vo.MesSetEmissionOutletSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emissionoutlet.MesSetEmissionOutletDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-排放口 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetEmissionOutletService {

    /**
     * 创建排放口
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmissionOutlet(@Valid MesSetEmissionOutletSaveReqVO createReqVO);

    /**
     * 更新排放口
     *
     * @param updateReqVO 更新信息
     */
    void updateEmissionOutlet(@Valid MesSetEmissionOutletSaveReqVO updateReqVO);

    /**
     * 删除排放口
     *
     * @param id 编号
     */
    void deleteEmissionOutlet(Long id);

    /**
     * 校验排放口存在
     *
     * @param id 编号
     */
    void validateEmissionOutletExists(Long id);

    /**
     * 获得排放口
     *
     * @param id 编号
     * @return 排放口
     */
    MesSetEmissionOutletDO getEmissionOutlet(Long id);

    /**
     * 获得排放口分页
     *
     * @param pageReqVO 分页查询
     * @return 排放口分页
     */
    PageResult<MesSetEmissionOutletDO> getEmissionOutletPage(MesSetEmissionOutletPageReqVO pageReqVO);

}
