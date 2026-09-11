package cn.iocoder.txgy.module.mes.service.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-劳保用品检查 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPpeCheckService {

    /**
     * 创建劳保用品检查
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPpeCheck(@Valid MesSetPpeCheckSaveReqVO createReqVO);

    /**
     * 更新劳保用品检查
     *
     * @param updateReqVO 更新信息
     */
    void updatePpeCheck(@Valid MesSetPpeCheckSaveReqVO updateReqVO);

    /**
     * 删除劳保用品检查
     *
     * @param id 编号
     */
    void deletePpeCheck(Long id);

    /**
     * 获得劳保用品检查
     *
     * @param id 编号
     * @return 劳保用品检查
     */
    MesSetPpeCheckDO getPpeCheck(Long id);

    /**
     * 获得劳保用品检查分页
     *
     * @param pageReqVO 分页查询
     * @return 劳保用品检查分页
     */
    PageResult<MesSetPpeCheckDO> getPpeCheckPage(MesSetPpeCheckPageReqVO pageReqVO);

}
