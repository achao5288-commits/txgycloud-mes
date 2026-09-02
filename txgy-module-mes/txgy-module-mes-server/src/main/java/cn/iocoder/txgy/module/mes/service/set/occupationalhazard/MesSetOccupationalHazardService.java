package cn.iocoder.txgy.module.mes.service.set.occupationalhazard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.occupationalhazard.vo.MesSetOccupationalHazardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.occupationalhazard.MesSetOccupationalHazardDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-职业病危害因素检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetOccupationalHazardService {

    /**
     * 创建职业病危害因素检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOccupationalHazard(@Valid MesSetOccupationalHazardSaveReqVO createReqVO);

    /**
     * 更新职业病危害因素检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateOccupationalHazard(@Valid MesSetOccupationalHazardSaveReqVO updateReqVO);

    /**
     * 删除职业病危害因素检测记录
     *
     * @param id 编号
     */
    void deleteOccupationalHazard(Long id);

    /**
     * 校验职业病危害因素检测记录存在
     *
     * @param id 编号
     */
    void validateOccupationalHazardExists(Long id);

    /**
     * 获得职业病危害因素检测记录
     *
     * @param id 编号
     * @return 职业病危害因素检测记录
     */
    MesSetOccupationalHazardDO getOccupationalHazard(Long id);

    /**
     * 获得职业病危害因素检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 职业病危害因素检测记录分页
     */
    PageResult<MesSetOccupationalHazardDO> getOccupationalHazardPage(MesSetOccupationalHazardPageReqVO pageReqVO);

}
