package cn.iocoder.txgy.module.mes.service.set.gasrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.gasrecord.vo.MesSetGasRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.gasrecord.MesSetGasRecordDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-作业环境气体检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetGasRecordService {

    /**
     * 创建气体检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGasRecord(@Valid MesSetGasRecordSaveReqVO createReqVO);

    /**
     * 更新气体检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateGasRecord(@Valid MesSetGasRecordSaveReqVO updateReqVO);

    /**
     * 删除气体检测记录
     *
     * @param id 编号
     */
    void deleteGasRecord(Long id);

    /**
     * 校验气体检测记录存在
     *
     * @param id 编号
     */
    void validateGasRecordExists(Long id);

    /**
     * 获得气体检测记录
     *
     * @param id 编号
     * @return 气体检测记录
     */
    MesSetGasRecordDO getGasRecord(Long id);

    /**
     * 获得气体检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 气体检测记录分页
     */
    PageResult<MesSetGasRecordDO> getGasRecordPage(MesSetGasRecordPageReqVO pageReqVO);

}
