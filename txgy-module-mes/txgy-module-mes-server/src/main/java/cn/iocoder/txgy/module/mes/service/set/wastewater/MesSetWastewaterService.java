package cn.iocoder.txgy.module.mes.service.set.wastewater;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.wastewater.vo.MesSetWastewaterSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.wastewater.MesSetWastewaterDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-废水排放检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetWastewaterService {

    /**
     * 创建废水排放检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWastewater(@Valid MesSetWastewaterSaveReqVO createReqVO);

    /**
     * 更新废水排放检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateWastewater(@Valid MesSetWastewaterSaveReqVO updateReqVO);

    /**
     * 删除废水排放检测记录
     *
     * @param id 编号
     */
    void deleteWastewater(Long id);

    /**
     * 校验废水排放检测记录存在
     *
     * @param id 编号
     */
    void validateWastewaterExists(Long id);

    /**
     * 获得废水排放检测记录
     *
     * @param id 编号
     * @return 废水排放检测记录
     */
    MesSetWastewaterDO getWastewater(Long id);

    /**
     * 获得废水排放检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 废水排放检测记录分页
     */
    PageResult<MesSetWastewaterDO> getWastewaterPage(MesSetWastewaterPageReqVO pageReqVO);

}
