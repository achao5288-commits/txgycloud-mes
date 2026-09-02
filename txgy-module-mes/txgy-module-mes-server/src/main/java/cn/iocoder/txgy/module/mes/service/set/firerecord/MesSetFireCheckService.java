package cn.iocoder.txgy.module.mes.service.set.firerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.firerecord.vo.MesSetFireCheckSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.firerecord.MesSetFireCheckDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-消防设施检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetFireCheckService {

    /**
     * 创建消防设施检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFireCheck(@Valid MesSetFireCheckSaveReqVO createReqVO);

    /**
     * 更新消防设施检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateFireCheck(@Valid MesSetFireCheckSaveReqVO updateReqVO);

    /**
     * 删除消防设施检测记录
     *
     * @param id 编号
     */
    void deleteFireCheck(Long id);

    /**
     * 校验消防设施检测记录存在
     *
     * @param id 编号
     */
    void validateFireCheckExists(Long id);

    /**
     * 获得消防设施检测记录
     *
     * @param id 编号
     * @return 消防设施检测记录
     */
    MesSetFireCheckDO getFireCheck(Long id);

    /**
     * 获得消防设施检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 消防设施检测记录分页
     */
    PageResult<MesSetFireCheckDO> getFireCheckPage(MesSetFireCheckPageReqVO pageReqVO);

}
