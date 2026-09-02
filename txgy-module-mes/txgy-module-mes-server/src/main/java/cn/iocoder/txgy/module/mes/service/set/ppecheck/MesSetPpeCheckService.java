package cn.iocoder.txgy.module.mes.service.set.ppecheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.ppecheck.vo.MesSetPpeCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.ppecheck.MesSetPpeCheckDO;
import jakarta.validation.Valid;

/**
 * MES 安全环保检测-PPE防护检查记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetPpeCheckService {

    /**
     * 创建 PPE防护检查记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPpeCheck(@Valid MesSetPpeCheckSaveReqVO createReqVO);

    /**
     * 更新 PPE防护检查记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePpeCheck(@Valid MesSetPpeCheckSaveReqVO updateReqVO);

    /**
     * 删除 PPE防护检查记录
     *
     * @param id 编号
     */
    void deletePpeCheck(Long id);

    /**
     * 校验 PPE防护检查记录存在
     *
     * @param id 编号
     */
    void validatePpeCheckExists(Long id);

    /**
     * 获得 PPE防护检查记录
     *
     * @param id 编号
     * @return PPE防护检查记录
     */
    MesSetPpeCheckDO getPpeCheck(Long id);

    /**
     * 获得 PPE防护检查记录分页
     *
     * @param pageReqVO 分页查询
     * @return PPE防护检查记录分页
     */
    PageResult<MesSetPpeCheckDO> getPpeCheckPage(MesSetPpeCheckPageReqVO pageReqVO);

}
