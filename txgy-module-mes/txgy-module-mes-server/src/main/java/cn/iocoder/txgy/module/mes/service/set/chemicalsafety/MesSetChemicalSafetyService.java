package cn.iocoder.txgy.module.mes.service.set.chemicalsafety;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetyPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemicalsafety.vo.MesSetChemicalSafetySaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemicalsafety.MesSetChemicalSafetyDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-危化品安全巡查记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetChemicalSafetyService {

    /**
     * 创建危化品安全巡查记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChemicalSafety(@Valid MesSetChemicalSafetySaveReqVO createReqVO);

    /**
     * 更新危化品安全巡查记录
     *
     * @param updateReqVO 更新信息
     */
    void updateChemicalSafety(@Valid MesSetChemicalSafetySaveReqVO updateReqVO);

    /**
     * 删除危化品安全巡查记录
     *
     * @param id 编号
     */
    void deleteChemicalSafety(Long id);

    /**
     * 校验危化品安全巡查记录存在
     *
     * @param id 编号
     */
    void validateChemicalSafetyExists(Long id);

    /**
     * 获得危化品安全巡查记录
     *
     * @param id 编号
     * @return 危化品安全巡查记录
     */
    MesSetChemicalSafetyDO getChemicalSafety(Long id);

    /**
     * 获得危化品安全巡查记录分页
     *
     * @param pageReqVO 分页查询
     * @return 危化品安全巡查记录分页
     */
    PageResult<MesSetChemicalSafetyDO> getChemicalSafetyPage(MesSetChemicalSafetyPageReqVO pageReqVO);

}
