package cn.iocoder.txgy.module.mes.service.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-排污许可管理 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPollutionPermitService {

    /**
     * 创建排污许可
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPollutionPermit(@Valid MesSetPollutionPermitSaveReqVO createReqVO);

    /**
     * 更新排污许可
     *
     * @param updateReqVO 更新信息
     */
    void updatePollutionPermit(@Valid MesSetPollutionPermitSaveReqVO updateReqVO);

    /**
     * 删除排污许可
     *
     * @param id 编号
     */
    void deletePollutionPermit(Long id);

    /**
     * 校验排污许可存在
     *
     * @param id 编号
     */
    void validatePollutionPermitExists(Long id);

    /**
     * 获得排污许可
     *
     * @param id 编号
     * @return 排污许可
     */
    MesSetPollutionPermitDO getPollutionPermit(Long id);

    /**
     * 获得排污许可分页
     *
     * @param pageReqVO 分页查询
     * @return 排污许可分页
     */
    PageResult<MesSetPollutionPermitDO> getPollutionPermitPage(MesSetPollutionPermitPageReqVO pageReqVO);

}
