package cn.iocoder.txgy.module.mes.service.set.pollutionpermit;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutionpermit.vo.MesSetPollutionPermitSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutionpermit.MesSetPollutionPermitDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-排污许可证 Service 接口
 *
 * 排污许可 = 环保数据基准源：限值取自证、总量卡在证、报告照着证。
 * 本期提供许可证主数据维护（CRUD）；在线监测比对/总量红线/执行报告自动汇总为后续迭代。
 *
 * @author OPENLAB BS
 */
public interface MesSetPollutionPermitService {

    /**
     * 创建排污许可证
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPollutionPermit(@Valid MesSetPollutionPermitSaveReqVO createReqVO);

    /**
     * 更新排污许可证
     *
     * @param updateReqVO 更新信息
     */
    void updatePollutionPermit(@Valid MesSetPollutionPermitSaveReqVO updateReqVO);

    /**
     * 删除排污许可证
     *
     * @param id 编号
     */
    void deletePollutionPermit(Long id);

    /**
     * 获得排污许可证
     *
     * @param id 编号
     * @return 排污许可证
     */
    MesSetPollutionPermitDO getPollutionPermit(Long id);

    /**
     * 获得排污许可证分页
     *
     * @param pageReqVO 分页查询
     * @return 排污许可证分页
     */
    PageResult<MesSetPollutionPermitDO> getPollutionPermitPage(MesSetPollutionPermitPageReqVO pageReqVO);

}
