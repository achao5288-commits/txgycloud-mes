package cn.iocoder.txgy.module.mes.service.set.exhaustgas;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.exhaustgas.vo.MesSetExhaustGasSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.exhaustgas.MesSetExhaustGasDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-废气排放检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetExhaustGasService {

    /**
     * 创建废气排放检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExhaustGas(@Valid MesSetExhaustGasSaveReqVO createReqVO);

    /**
     * 更新废气排放检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateExhaustGas(@Valid MesSetExhaustGasSaveReqVO updateReqVO);

    /**
     * 删除废气排放检测记录
     *
     * @param id 编号
     */
    void deleteExhaustGas(Long id);

    /**
     * 校验废气排放检测记录存在
     *
     * @param id 编号
     */
    void validateExhaustGasExists(Long id);

    /**
     * 获得废气排放检测记录
     *
     * @param id 编号
     * @return 废气排放检测记录
     */
    MesSetExhaustGasDO getExhaustGas(Long id);

    /**
     * 获得废气排放检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 废气排放检测记录分页
     */
    PageResult<MesSetExhaustGasDO> getExhaustGasPage(MesSetExhaustGasPageReqVO pageReqVO);

}
